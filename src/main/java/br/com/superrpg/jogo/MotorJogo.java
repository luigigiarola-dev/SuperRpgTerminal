package br.com.superrpg.jogo;

import java.util.*;
import static br.com.superrpg.jogo.EstadoJogo.*;

/** Sessão independente. Cada comando termina imediatamente; nenhuma leitura ou espera. */
public final class MotorJogo {
    final Heroi heroi;
    final Diario diario = new Diario();
    Local local = Local.VILA;
    int dia = 1, periodo, rodada;
    final boolean hardcore;
    Monstro inimigo;
    Fase fase = Fase.EXPLORACAO;
    private boolean heroiPrimeiro;
    private int andarTorre;
    private final Random sorte;

    public MotorJogo(String nome, String classe, boolean hardcore) { this(nome, classe, hardcore, new Random()); }
    MotorJogo(String nome, String classe, boolean hardcore, Random sorte) {
        this.sorte = sorte;
        this.hardcore = hardcore;
        String n = nome == null || nome.isBlank() ? "Viajante" : nome.strip();
        if (n.length() > 30) n = n.substring(0, 30);
        heroi = switch (classe) {
            case "Paladino" -> new Paladino(n);
            case "Necromante" -> new Necromante(n);
            case "Arqueiro" -> new Arqueiro(n);
            case "Mago" -> new Mago(n);
            case "Ladino" -> new Ladino(n);
            default -> throw new IllegalArgumentException("Classe desconhecida");
        };
        heroi.inv.add(new Pocao()); heroi.inv.add(new Pocao()); heroi.inv.add(new Elixir());
        heroi.equip[0] = (Equipamento) Fabrica.criar("Espada Enferrujada");
    }

    public List<EventoJogo> executar(AcaoJogo acao) {
        List<EventoJogo> eventos = new ArrayList<>();
        Eventos.capturar(eventos::add, () -> processar(acao));
        return List.copyOf(eventos);
    }

    private void processar(AcaoJogo a) {
        if (heroi.talentosPendentes > 0) {
            if (a.tipo() == AcaoJogo.Tipo.TALENTO && a.indice() >= 1 && a.indice() <= 4) heroi.aprenderTalento(a.indice());
            else Eventos.texto("Escolha seu talento antes de continuar.");
            return;
        }
        if (fase == Fase.COMBATE) { comandoCombate(a); return; }
        if (fase == Fase.VITORIA || fase == Fase.DERROTA) return;
        if (fase == Fase.RECOMPENSA) {
            if (a.tipo() == AcaoJogo.Tipo.CONTINUAR) {
                fase = Fase.EXPLORACAO; inimigo = null;
                if (andarTorre > 0 && andarTorre < 3) { andarTorre++; iniciarCombate(andarTorre == 3 ? chefeTorre() : Bestiario.sortear(Local.TORRE, heroi.nivel, noite())); }
                else andarTorre = 0;
            }
            return;
        }
        switch (a.tipo()) {
            case VIAJAR -> viajar(a.indice());
            case EXPLORAR -> explorar();
            case COLETAR -> coletar();
            case ACAMPAR -> acampar();
            case DESCANSAR -> {
                if (local != Local.VILA) return;
                if (!heroi.gastarOuro(preco(20))) { Eventos.texto("Ouro insuficiente para descansar."); return; }
                heroi.restaurarTotal();
                while (periodo != 0) avancar();
                Eventos.texto("Uma noite sob o teto de Gus. Vida e mana restauradas.");
            }
            case MISSAO -> missao(a.indice());
            case ITEM -> usarFora(a.indice());
            case EQUIPAR -> {
                if (indiceValido(a.indice(), heroi.inv.size()) && heroi.inv.get(a.indice()) instanceof Equipamento e) heroi.equipar(e);
            }
            case COMPRAR -> comprar(a.indice());
            default -> { }
        }
    }

    private void viajar(int indice) {
        if (!indiceValido(indice, Local.values().length)) return;
        Local destino = Local.values()[indice];
        if (destino == local || bloqueio(destino) != null) return;
        avancar();
        local = destino;
        Eventos.texto("Você chega a " + local.nome + ".");
        if (local != Local.VILA && local != Local.TORRE && local != Local.COVIL && sorte.nextInt(100) < 25) encontro();
    }

    private void explorar() {
        if (local == Local.VILA) return;
        if (local == Local.COVIL) {
            DragaoAncestral d = new DragaoAncestral();
            if (heroi.nivel > 9) d.escalar(1 + .05 * (heroi.nivel - 9));
            iniciarCombate(d); return;
        }
        avancar();
        if (local == Local.TORRE) { andarTorre = 1; encontro(); return; }
        // Primeira fatia: encontros garantidos. Eventos narrativos serão migrados separadamente.
        encontro();
    }

    private Beholder chefeTorre() {
        Beholder b = new Beholder();
        if (heroi.nivel > 6) b.escalar(1 + .05 * (heroi.nivel - 6));
        return b;
    }

    private void coletar() {
        if (local.material() == null) return;
        avancar();
        int r = sorte.nextInt(100);
        if (r < 25) { encontro(); return; }
        if (r >= 90) { Eventos.texto("Nenhum recurso encontrado desta vez."); return; }
        int qtd = 1 + sorte.nextInt(2) + (local == Local.FLORESTA && noite() ? 1 : 0);
        for (int i = 0; i < qtd; i++) heroi.inv.add(Fabrica.criar(local.material()));
        Eventos.texto("Coletado: " + qtd + " × " + local.material());
    }

    private void acampar() {
        if (local.material() == null) return;
        avancar();
        if (sorte.nextInt(100) < (noite() ? 35 : 15)) { encontro(); return; }
        heroi.curar(heroi.hpMax * 30 / 100); heroi.modificarMana(heroi.manaMax * 30 / 100);
        heroi.removerNegativos(); Eventos.texto("A fogueira aquece a noite. +30% de vida e mana.");
    }

    private void encontro() { iniciarCombate(Bestiario.sortear(local, heroi.nivel, noite())); }

    void iniciarCombate(Monstro monstro) {
        inimigo = monstro; fase = Fase.COMBATE; rodada = 0;
        heroi.limparEfeitos(); heroi.recargas.clear();
        Eventos.texto(monstro.getNome() + " bloqueia seu caminho.");
        prepararRodada();
    }

    private void prepararRodada() {
        // Atordoamento pode pular ações, mas não cria espera nem recursão de menus.
        while (fase == Fase.COMBATE) {
            rodada++;
            heroiPrimeiro = heroi.getAgilidade() >= inimigo.getAgilidade();
            if (!heroiPrimeiro) { turnoInimigo(); if (encerrarCombate()) return; }
            heroi.setDefendendo(false); heroi.tickRecargas();
            boolean podeAgir = heroi.iniciarTurno();
            if (encerrarCombate()) return;
            if (podeAgir) return;
            Eventos.texto("Você está atordoado e perde a ação.");
            if (heroiPrimeiro) { turnoInimigo(); if (encerrarCombate()) return; }
        }
    }

    private void comandoCombate(AcaoJogo a) {
        switch (a.tipo()) {
            case ATACAR -> heroi.atacar(inimigo);
            case DEFENDER -> { heroi.setDefendendo(true); heroi.modificarMana(8); Eventos.texto("Guarda erguida. +8 de mana."); }
            case HABILIDADE -> {
                if (!indiceValido(a.indice(), heroi.habilidades().size())) return;
                Habilidade h = heroi.habilidades().get(a.indice());
                if (!heroi.podeUsar(h)) { Eventos.texto("Mana insuficiente ou habilidade em recarga."); return; }
                heroi.usarHabilidade(h, inimigo);
            }
            case ITEM -> {
                if (!indiceValido(a.indice(), heroi.inv.size())) return;
                Item item = heroi.inv.get(a.indice());
                if (!item.usavelEmCombate() || !item.usar(heroi, inimigo)) return;
                heroi.inv.remove(a.indice());
            }
            case FUGIR -> {
                if (inimigo.chefe) { Eventos.texto("Não há saída deste confronto."); return; }
                int chance = Math.max(15, Math.min(85, 45 + (heroi.getAgilidade() - inimigo.getAgilidade()) * 2));
                if (sorte.nextInt(100) < chance) {
                    heroi.limparEfeitos(); fase = Fase.EXPLORACAO; inimigo = null; andarTorre = 0;
                    Eventos.emitir(new EventoJogo(EventoJogo.Tipo.FUGA, "", 0, "Você escapou do confronto.")); return;
                }
                Eventos.texto("O inimigo bloqueou sua fuga.");
            }
            default -> { return; }
        }
        if (inimigo.estaVivo() && heroi.aliado != null) heroi.aliado.agir(heroi, inimigo);
        heroi.modificarMana(4);
        if (encerrarCombate()) return;
        if (heroiPrimeiro) { turnoInimigo(); if (encerrarCombate()) return; }
        prepararRodada();
    }

    private void turnoInimigo() {
        boolean agir = inimigo.iniciarTurno();
        if (!inimigo.estaVivo()) return;
        if (agir) inimigo.turno(heroi);
        else {
            Eventos.texto(inimigo.getNome() + " está atordoado.");
            if (inimigo.carregando) { inimigo.carregando = false; Eventos.texto("O golpe carregado foi interrompido!"); }
        }
    }

    private boolean encerrarCombate() {
        if (!heroi.estaVivo()) {
            andarTorre = 0;
            Eventos.emitir(new EventoJogo(EventoJogo.Tipo.DERROTA, heroi.getNome(), 0, "Você caiu em combate."));
            if (hardcore) fase = Fase.DERROTA;
            else {
                int perda = heroi.ouro / 4; heroi.gastarOuro(perda); heroi.restaurarTotal(); heroi.curar(-heroi.hpMax / 2);
                local = Local.VILA; periodo = 3; avancar(); fase = Fase.RECOMPENSA;
                Eventos.texto("Lira o acolheu na vila. Você perdeu " + perda + " de ouro.");
            }
            return true;
        }
        if (inimigo.estaVivo()) return false;
        heroi.limparEfeitos(); heroi.abates++;
        int ouro = noite() ? (int)(inimigo.ouroRec * 1.5) : inimigo.ouroRec;
        heroi.ganharOuro(ouro);
        if (inimigo.dropNome != null && sorte.nextInt(100) < inimigo.dropChance) {
            heroi.inv.add(Fabrica.criar(inimigo.dropNome)); Eventos.texto("Encontrado: " + inimigo.dropNome);
        }
        if (sorte.nextInt(100) < 12) heroi.inv.add(Fabrica.criar(new String[]{"Poção de Cura", "Antídoto", "Ensopado Quente"}[sorte.nextInt(3)]));
        diario.registrarAbate(Bestiario.id(inimigo));
        if (inimigo instanceof Beholder) heroi.inv.add(Fabrica.criar("Chave do Dragão"));
        fase = inimigo instanceof DragaoAncestral ? Fase.VITORIA : Fase.RECOMPENSA;
        if (fase == Fase.VITORIA && diario.obter("dragao").estado == 1) diario.obter("dragao").estado = 2;
        heroi.ganharXp(inimigo.xpRec);
        Eventos.emitir(new EventoJogo(EventoJogo.Tipo.VITORIA, inimigo.getNome(), ouro,
                "Vitória! +" + ouro + " de ouro · +" + inimigo.xpRec + " XP"));
        return true;
    }

    private void missao(int indice) {
        if (local != Local.VILA || !indiceValido(indice, diario.missoes.size())) return;
        Missao m = diario.missoes.get(indice);
        if (m.pronta(heroi)) {
            if (m.tipo == Missao.Tipo.COLETAR) heroi.consumir(m.alvo, m.qtd);
            m.estado = 2; heroi.ganharOuro(m.ouro); heroi.mudarHonra(1);
            if (m.itemRec != null) heroi.inv.add(Fabrica.criar(m.itemRec));
            heroi.ganharXp(m.xp); Eventos.texto("Missão concluída: " + m.titulo);
        } else if (disponivel(m)) { m.estado = 1; Eventos.texto("Missão aceita: " + m.titulo); }
    }

    private boolean disponivel(Missao m) { return m.estado == 0 && heroi.nivel >= m.nivelMin && (m.requer == null || diario.concluida(m.requer)); }
    private void usarFora(int i) {
        if (!indiceValido(i, heroi.inv.size())) return;
        Item item = heroi.inv.get(i);
        if (!(item instanceof Equipamento) && item.usavelFora() && item.usar(heroi, null)) heroi.inv.remove(i);
    }
    private List<String> catalogo() {
        List<String> nomes = new ArrayList<>(List.of(Fabrica.CONSUMIVEIS)); nomes.addAll(List.of(Fabrica.EQUIPAMENTOS_LOJA)); return nomes;
    }
    private void comprar(int i) {
        if (local != Local.VILA || noite() || !indiceValido(i, catalogo().size())) return;
        Item item = Fabrica.criar(catalogo().get(i));
        if (heroi.gastarOuro(preco(item.getPreco()))) { heroi.inv.add(item); Eventos.texto("Comprado: " + item.getNome()); }
        else Eventos.texto("Ouro insuficiente.");
    }
    private int preco(int base) { return Math.max(1, (int)Math.round(base * (heroi.honra >= 2 ? .9 : heroi.honra <= -2 ? 1.15 : 1))); }
    private boolean noite() { return periodo >= 2; }
    private void avancar() {
        periodo = (periodo + 1) % 4; if (periodo == 0) dia++;
        heroi.curar(5); heroi.modificarMana(8);
    }
    private String bloqueio(Local l) {
        if (l == Local.TORRE && !diario.concluida("seita")) return "Conclua A Seita do Olho";
        if (l == Local.COVIL && heroi.contar("Chave do Dragão") == 0) return "Requer a Chave do Dragão";
        return null;
    }
    private static boolean indiceValido(int i, int tamanho) { return i >= 0 && i < tamanho; }

    public EstadoJogo estado() {
        List<Opcao> hs = new ArrayList<>(), inv = new ArrayList<>(), locais = new ArrayList<>(), ms = new ArrayList<>(), loja = new ArrayList<>();
        for (int i = 0; i < heroi.habilidades().size(); i++) {
            Habilidade h = heroi.habilidades().get(i); int rc = heroi.recargas.getOrDefault(h.nome, 0);
            hs.add(new Opcao(i, h.nome, h.custo + " MP · " + h.elem.nome + " · " + h.desc + (rc > 0 ? " · recarga " + rc : ""), heroi.podeUsar(h)));
        }
        for (int i = 0; i < heroi.inv.size(); i++) {
            Item it = heroi.inv.get(i);
            inv.add(new Opcao(i, it.getNome(), it.getCategoria() + " · " + it.getDescricao(), fase == Fase.COMBATE ? it.usavelEmCombate() : it.usavelFora()));
        }
        for (Local l : Local.values()) locais.add(new Opcao(l.ordinal(), l.nome, "Nível " + l.nivelRec + (bloqueio(l) == null ? "" : " · " + bloqueio(l)), bloqueio(l) == null && l != local));
        for (int i = 0; i < diario.missoes.size(); i++) {
            Missao m = diario.missoes.get(i);
            String status = m.estado == 2 ? "Concluída" : m.pronta(heroi) ? "Entregar" : m.estado == 1 ? "Em andamento" : disponivel(m) ? "Aceitar" : "Bloqueada";
            ms.add(new Opcao(i, m.titulo, status + " · " + m.doador + "\n" + m.desc + "\n" + m.objetivo(heroi) + "\n" + m.recompensa(), local == Local.VILA && (disponivel(m) || m.pronta(heroi))));
        }
        List<String> nomes = catalogo();
        for (int i = 0; i < nomes.size(); i++) {
            Item it = Fabrica.criar(nomes.get(i)); int p = preco(it.getPreco());
            loja.add(new Opcao(i, it.getNome(), p + " ouro · " + it.getDescricao(), local == Local.VILA && !noite() && heroi.ouro >= p));
        }
        List<String> eq = Arrays.stream(heroi.equip).map(e -> e == null ? "Vazio" : e.getNome() + " · " + e.getDescricao()).toList();
        return new EstadoJogo(fase, personagem(heroi), inimigo == null ? null : personagem(inimigo), heroi.getClasse(), local.nome, local.name(),
                dia, SuperRpgTerminal.PERIODOS[periodo], heroi.ouro, heroi.xp, heroi.xpProximo(), heroi.honra, heroi.talentosPendentes, hardcore, rodada,
                List.copyOf(hs), List.copyOf(inv), List.copyOf(locais), List.copyOf(ms), List.copyOf(loja), eq);
    }
    private Personagem personagem(Entidade e) {
        String afinidades = e.afinidade.entrySet().stream().map(a -> a.getKey().nome + " ×" + a.getValue()).reduce((a,b) -> a + " · " + b).orElse("Sem afinidades especiais");
        return new Personagem(e.getNome(), e instanceof Heroi ? heroi.getClasse() : Bestiario.id((Monstro)e), e.hp, e.hpMax, e.mana, e.manaMax,
                e.nivel, e.getAtaque(), e.getDefesa(), e.getAgilidade(), e.resumoEfeitos(), e instanceof Monstro m && m.carregando, afinidades);
    }
}
