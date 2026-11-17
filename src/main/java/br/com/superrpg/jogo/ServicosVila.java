package br.com.superrpg.jogo;

import java.util.*;
import static br.com.superrpg.jogo.AcaoJogo.Tipo.*;
import static br.com.superrpg.jogo.EstadoJogo.*;

/** Regras da vila sem interface; as mesmas condições alimentam a tela e validam comandos. */
final class ServicosVila {
    private final MotorJogo jogo;
    private final Heroi heroi;
    private final Random sorte;

    ServicosVila(MotorJogo jogo, Random sorte) {
        this.jogo = jogo;
        this.heroi = jogo.heroi;
        this.sorte = sorte;
    }

    Vila estado() {
        return new Vila(opcoes(VENDER), opcoes(MELHORAR), opcoes(CRIAR), opcoes(CURAR),
                opcoes(DOAR), opcoes(CONTRATAR), opcoes(DISPENSAR),
                heroi.aliado == null ? "Nenhum aliado" : heroi.aliado.nome, opcoes(DESCANSAR));
    }

    private Opcao opcao(int id, String nome, String descricao, boolean comercial, String impedimento) {
        String motivo = jogo.local != Local.VILA ? "Disponível apenas na vila."
                : jogo.fase != Fase.EXPLORACAO || heroi.talentosPendentes > 0 ? "Conclua a ação atual primeiro."
                : comercial && jogo.noite() ? "Fechado à noite. Descanse na taverna."
                : impedimento;
        return new Opcao(id, nome, descricao + (motivo == null ? "" : "\n" + motivo), motivo == null);
    }

    private String faltaOuro(int custo) { return heroi.ouro < custo ? "Faltam " + (custo - heroi.ouro) + " de ouro." : null; }
    private int custoForja(Equipamento e) { return jogo.preco(60 * (e.melhoria + 1)); }
    private int chance(Equipamento e) { return e.melhoria < 2 ? 100 : e.melhoria == 2 ? 80 : e.melhoria == 3 ? 60 : 45; }
    private Aliado aliado(int i) { return i == 0 ? new LoboCinzento() : new IrmaLira(); }
    private Map<String, Integer> ingredientes(int i) {
        Map<String, Integer> itens = new LinkedHashMap<>();
        String[] receita = Fabrica.RECEITAS[i];
        for (int k = 1; k < receita.length; k++) itens.merge(receita[k], 1, Integer::sum);
        return itens;
    }

    private List<Opcao> opcoes(AcaoJogo.Tipo tipo) {
        List<Opcao> lista = new ArrayList<>();
        switch (tipo) {
            case VENDER -> {
                for (int i = 0; i < heroi.inv.size(); i++) {
                    Item item = heroi.inv.get(i);
                    if (!item.getCategoria().equals("Missão")) lista.add(opcao(i, item.getNome(),
                            item.getPrecoVenda() + " ouro · " + item.getDescricao(), true, null));
                }
            }
            case MELHORAR -> {
                for (int i = 0; i < 2; i++) {
                    Equipamento e = heroi.equip[i];
                    if (e == null) lista.add(opcao(i, i == 0 ? "Arma" : "Armadura", "Nenhuma peça equipada.", true, "Equipe uma peça na mochila."));
                    else if (e.melhoria >= 5) lista.add(opcao(i, e.getNome(), e.getDescricao(), true, "Limite de melhoria: +5."));
                    else lista.add(opcao(i, e.getNome(), e.getDescricao() + "\nMelhorar para +" + (e.melhoria + 1)
                            + " · " + custoForja(e) + " ouro · " + chance(e) + "% de sucesso."
                            + "\nEm caso de falha, o ouro é consumido e a peça mantém a melhoria atual.", true, faltaOuro(custoForja(e))));
                }
            }
            case CRIAR -> {
                for (int i = 0; i < Fabrica.RECEITAS.length; i++) {
                    Map<String, Integer> itens = ingredientes(i);
                    boolean faltam = itens.entrySet().stream().anyMatch(e -> heroi.contar(e.getKey()) < e.getValue());
                    StringJoiner desc = new StringJoiner("\n", "Sem custo em ouro.\n", "");
                    itens.forEach((nome, qtd) -> desc.add(nome + ": " + heroi.contar(nome) + "/" + qtd
                            + (heroi.contar(nome) < qtd ? " · falta " + (qtd - heroi.contar(nome)) : " · disponível")));
                    lista.add(opcao(i, Fabrica.RECEITAS[i][0], desc.toString(), true, faltam ? "Ingredientes insuficientes." : null));
                }
            }
            case CURAR -> {
                int custo = jogo.preco(5 + heroi.nivel * 5);
                lista.add(opcao(0, "Bênção de Lira", custo + " ouro · Restaura vida e mana e remove os males.", false, faltaOuro(custo)));
            }
            case DESCANSAR -> lista.add(opcao(0, "Descansar na taverna", jogo.preco(20)
                    + " ouro · Restaura vida e mana e avança até a manhã, se necessário.", false, faltaOuro(jogo.preco(20))));
            case DOAR -> lista.add(opcao(0, "Doação aos necessitados", "50 ouro · +1 honra (limite +5).", false, faltaOuro(50)));
            case CONTRATAR -> {
                for (int i = 0; i < 2; i++) {
                    Aliado a = aliado(i); int custo = jogo.preco(a.preco);
                    lista.add(opcao(i, a.nome, custo + " ouro · " + a.descricao()
                            + "\nSubstitui o aliado atual, sem reembolso.", false, faltaOuro(custo)));
                }
            }
            case DISPENSAR -> lista.add(opcao(0, "Dispensar aliado", "Você seguirá sozinho, sem reembolso.", false,
                    heroi.aliado == null ? "Nenhum aliado contratado." : null));
            default -> { }
        }
        return List.copyOf(lista);
    }

    void executar(AcaoJogo acao) {
        Opcao opcao = opcoes(acao.tipo()).stream().filter(o -> o.id() == acao.indice()).findFirst().orElse(null);
        if (opcao == null) { Eventos.texto("Opção indisponível."); return; }
        if (!opcao.disponivel()) { Eventos.texto(opcao.descricao()); return; }
        switch (acao.tipo()) {
            case VENDER -> {
                Item item = heroi.inv.remove(acao.indice());
                heroi.ganharOuro(item.getPrecoVenda());
                Eventos.texto("Vendido: " + item.getNome() + " por " + item.getPrecoVenda() + " de ouro.");
            }
            case MELHORAR -> {
                Equipamento e = heroi.equip[acao.indice()];
                heroi.gastarOuro(custoForja(e));
                if (sorte.nextInt(100) < chance(e)) { e.melhoria++; Eventos.texto("Melhoria concluída: " + e.getNome()); }
                else Eventos.texto("A melhoria falhou. O ouro foi consumido, mas a peça foi preservada.");
            }
            case CRIAR -> {
                ingredientes(acao.indice()).forEach(heroi::consumir);
                String nome = Fabrica.RECEITAS[acao.indice()][0];
                heroi.inv.add(Fabrica.criar(nome)); Eventos.texto("Criado: " + nome);
            }
            case CURAR -> { heroi.gastarOuro(jogo.preco(5 + heroi.nivel * 5)); heroi.restaurarTotal(); Eventos.texto("Lira restaurou sua vida e mana e removeu os males."); }
            case DOAR -> { heroi.gastarOuro(50); heroi.mudarHonra(1); Eventos.texto("Doação recebida. Honra atual: " + heroi.honra); }
            case CONTRATAR -> { Aliado a = aliado(acao.indice()); heroi.gastarOuro(jogo.preco(a.preco)); heroi.aliado = a; Eventos.texto(a.nome + " agora luta ao seu lado."); }
            case DISPENSAR -> { heroi.aliado = null; Eventos.texto("Aliado dispensado. Você segue sozinho."); }
            default -> { }
        }
    }
}
