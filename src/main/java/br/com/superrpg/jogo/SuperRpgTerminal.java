package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

public class SuperRpgTerminal {
    static Heroi heroi;
    static Diario diario;
    static Local local = Local.VILA;
    static int dia = 1, periodo = 0;
    static boolean fimDeJogo = false, venceu = false, voltarMenu = false, hardcore = false, chegou = true;
    static boolean charadaHoje = false;
    static final String[] PERIODOS = { "Manhã", "Tarde", "Noite", "Madrugada" };
    static final String SAVE = "rpg_save.txt", RANKING = "rpg_ranking.txt";
    static final Random RNG = new Random();

    public static void main(String[] args) {
        try { System.setOut(new PrintStream(System.out, true, "UTF-8")); } catch (Exception ignorada) { }
        for (String a : args) {
            if (a.equals("--sem-cor")) Cor.ativo = false;
            if (a.equals("--rapido")) Tela.rapido = true;
        }
        menuPrincipal();
    }

    // ------------------------------------------------------------------
    // MENU PRINCIPAL E NOVO JOGO
    // ------------------------------------------------------------------
    static void menuPrincipal() {
        while (true) {
            System.out.println();
            Tela.mostrarArte(Arte.ESPADA, Cor.CIANO);
            Tela.titulo("CHRONICLES OF POLYMORPHISM: O RPG DE TERMINAL", Cor.AMARELO);
            boolean temSave = new File(SAVE).exists();
            Tela.caixa("MENU PRINCIPAL", "1. Novo jogo", "2. Continuar jogo salvo" + (temSave ? "" : " (nenhum save)"),
                "3. Ranking dos heróis", "0. Sair");
            System.out.print("Escolha: ");
            switch (Tela.ler(0, 3)) {
                case 1: novoJogo(); break;
                case 2:
                    if (temSave && Persistencia.carregar()) { prepararJogo(); jogar(); }
                    else System.out.println("Nenhum save válido encontrado.");
                    break;
                case 3: Persistencia.mostrarRanking(); break;
                default: System.out.println("Até a próxima aventura!"); return;
            }
        }
    }

static void ganharExperiencia(int quantidade) {
    heroi.ganharXp(quantidade);
    while (heroi.talentosPendentes > 0) {
        Tela.caixa("ESCOLHA UM TALENTO", "1. Vigor (+30 HP)", "2. Poder (+6 Ataque)",
            "3. Arcano (+25 Mana)", "4. Reflexos (+4 Agilidade, +2 Defesa)");
        heroi.aprenderTalento(Tela.ler(1, 4));
    }
}

static void prepararJogo()
 { fimDeJogo = false; venceu = false; voltarMenu = false; chegou = true; }

    static void novoJogo() {
        System.out.print("\nBatize seu herói: ");
        String nome = Tela.lerTexto();
        if (nome.isEmpty()) nome = "Herói";
        Heroi[] opcoes = { new Paladino(nome), new Necromante(nome), new Arqueiro(nome), new Mago(nome), new Ladino(nome) };
        String[] resumo = { "HP alto, cura e escudo", "Dano sombrio e roubo de vida", "Rápido, flechas e sangramento",
            "Mana imensa, fogo e raios", "Veloz, veneno e golpes furtivos" };
        System.out.println("\nEscolha sua linhagem:");
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println("\n" + (i + 1) + ". " + Cor.c(opcoes[i].cor() + Cor.NEGRITO, opcoes[i].getClasse()) + " — " + resumo[i]);
            Tela.mostrarArte(opcoes[i].sprite(), opcoes[i].cor());
        }
        System.out.print("\nSua escolha: ");
        heroi = opcoes[Tela.ler(1, 5) - 1];
        System.out.println("\nModo de jogo:\n1. Normal (se cair, você acorda na vila)\n2. Hardcore (se cair, é o fim)");
        System.out.print("Escolha: ");
        hardcore = Tela.ler(1, 2) == 2;

        diario = new Diario();
        local = Local.VILA; dia = 1; periodo = 0; charadaHoje = false;
        heroi.inv.add(Fabrica.criar("Poção de Cura"));
        heroi.inv.add(Fabrica.criar("Poção de Cura"));
        heroi.inv.add(Fabrica.criar("Elixir de Mana"));
        Equipamento espada = (Equipamento) Fabrica.criar("Espada Enferrujada");
        heroi.equip[0] = espada;

        System.out.println();
        Tela.mostrarArte(Arte.VILA, Cor.CIANO);
        Tela.fala("Narrador", "Há muito tempo, o Olho do Caos despertou na Torre e o Dragão Ignis voltou a rugir nas montanhas.");
        Tela.fala("Narrador", nome + ", " + heroi.getClasse() + " sem fama, chega à Vila Aurora com uma espada enferrujada e muita coragem.");
        Tela.fala("Capitã Mira", "Viajante! Fale comigo e com os moradores. Há muito trabalho — e muito perigo — por aqui.");
        Tela.enter();
        prepararJogo();
        jogar();
    }

    static void jogar() {
        while (!fimDeJogo && !voltarMenu) {
            if (local == Local.VILA) menuVila(); else menuLocal();
        }
        if (fimDeJogo) {
            if (venceu) Persistencia.telaVitoria(); else Persistencia.telaDerrota();
            Persistencia.registrarRanking();
            new File(SAVE).delete();
        }
    }

    // ------------------------------------------------------------------
    // TEMPO E INTERFACE
    // ------------------------------------------------------------------
    static boolean noite() { return periodo >= 2; }
    static boolean aberto() { return periodo <= 1; }

    static void avancar(int n) {
        for (int i = 0; i < n; i++) {
            periodo++;
            if (periodo >= 4) { periodo = 0; dia++; charadaHoje = false; }
            heroi.curar(5);          // recuperação natural com o passar do tempo
            heroi.modificarMana(8);
        }
        if (periodo == 2) {
            System.out.println();
            Tela.mostrarArte(Arte.NOITE, Cor.AZUL);
            System.out.println(Cor.c(Cor.AZUL, "🌙 A noite cai. Os monstros ficam mais fortes (e mais generosos)..."));
        } else if (periodo == 0) {
            System.out.println(Cor.c(Cor.AMARELO, "🌅 Um novo dia amanhece. (Dia " + dia + ")"));
        }
    }

    static void cabecalho() {
        System.out.println();
        System.out.println(Cor.c(Cor.AMARELO, Tela.linha('=')));
        System.out.println(" 📅 Dia " + dia + " • " + PERIODOS[periodo] + " • 📍 " + local.nome + " • 💰 " + heroi.getOuro()
            + (hardcore ? " • ☠️ Hardcore" : ""));
        System.out.println(" " + Cor.c(heroi.cor() + Cor.NEGRITO, heroi.getNome()) + " " + heroi.titulo() + " (Nv " + heroi.getNivel() + " "
            + heroi.getClasse() + ")");
        System.out.println(" HP " + Tela.barra(heroi.getHp(), heroi.getHpMax(), 16, Tela.corHp(heroi.getHp(), heroi.getHpMax()))
            + " " + heroi.getHp() + "/" + heroi.getHpMax()
            + "  MP " + Tela.barra(heroi.getMana(), heroi.getManaMax(), 10, Cor.AZUL) + " " + heroi.getMana() + "/" + heroi.getManaMax());
        System.out.println(" XP " + Tela.barra(heroi.getXp(), heroi.xpProximo(), 16, Cor.MAGENTA) + " " + heroi.getXp() + "/" + heroi.xpProximo());
        System.out.println(Cor.c(Cor.AMARELO, Tela.linha('=')));
    }

    static int preco(int base) {
        double f = heroi.getHonra() >= 2 ? 0.9 : (heroi.getHonra() <= -2 ? 1.15 : 1.0);
        return Math.max(1, (int) Math.round(base * f));
    }

    static boolean checarAberto(String lugar) {
        if (aberto()) return true;
        System.out.println("🌙 " + lugar + " está fechado à noite. Volte de manhã — ou descanse na Taverna.");
        return false;
    }

    // ------------------------------------------------------------------
    // VILA
    // ------------------------------------------------------------------
    static void menuVila() {
        cabecalho();
        if (chegou) { Tela.mostrarArte(Arte.VILA, Cor.CIANO); chegou = false; }
        Tela.caixa("VILA AURORA - O QUE DESEJA FAZER?",
            "1. Conversar com moradores     6. Templo",
            "2. Mercador                    7. Ficha e inventário",
            "3. Ferreiro                    8. Diário de missões",
            "4. Alquimista                  9. Viajar",
            "5. Taverna                     10. Salvar jogo",
            "0. Voltar ao menu principal");
        System.out.print("Escolha: ");
        switch (Tela.ler(0, 10)) {
            case 1: menuMoradores(); break;
            case 2: menuMercador(); break;
            case 3: menuFerreiro(); break;
            case 4: menuAlquimista(); break;
            case 5: menuTaverna(); break;
            case 6: menuTemplo(); break;
            case 7: menuFicha(); break;
            case 8: diario.mostrar(heroi); Tela.enter(); break;
            case 9: menuViagem(); break;
            case 10: Persistencia.salvar(); break;
            default:
                System.out.print("Voltar ao menu? Progresso não salvo será perdido (1=sim, 0=não): ");
                if (Tela.ler(0, 1) == 1) voltarMenu = true;
        }
    }

    // ------------------------------------------------------------------
    // MORADORES E MISSÕES
    // ------------------------------------------------------------------
    static void menuMoradores() {
        Tela.caixa("MORADORES", "1. Capitã Mira (Guarda da Vila)", "2. Irmã Lira (Curandeira)", "3. Borin (Ferreiro)",
            "4. Sábio Aldric (Ancião)", "5. Gus (Taverneiro)", "0. Voltar");
        System.out.print("Falar com: ");
        switch (Tela.ler(0, 5)) {
            case 1: falarCom("Mira", "Capitã Mira", new String[] {
                "A vila está segura enquanto eu estiver de pé. Mas preciso de braços fortes.",
                "Os monstros estão mais ousados a cada lua. Quem me ajudar será bem recompensado.",
                "Um herói de verdade se prova nas estradas, não nas tavernas." }); break;
            case 2: falarCom("Lira", "Irmã Lira", new String[] {
                "Que a luz o guie, viajante. Cuide bem dos seus ferimentos.",
                "As ervas lunares crescem na floresta, mas só brilham à noite.",
                "Se tiver doações, o templo sempre as aceita." }); break;
            case 3: falarCom("Borin", "Borin, o Ferreiro", new String[] {
                "Aço de verdade pede fogo de verdade! Pedras de fogo, é isso que preciso.",
                "Traga sua arma e eu a deixo afiada. Armadura boa salva mais vidas que feitiço.",
                "Golens têm o coração de pedra de fogo, sabia? Raio neles!" }); break;
            case 4:
                if (heroi.getNivel() < 4) {
                    Tela.fala("Sábio Aldric", "Ainda é cedo, jovem. Prove seu valor ajudando a vila e volte mais forte (Nível 4).");
                    Tela.enter();
                } else {
                    falarCom("Aldric", "Sábio Aldric", new String[] {
                        "Um Olho antigo observa tudo da Torre. E sob a montanha, Ignis aguarda.",
                        "Só a Chave do Dragão abre o Covil. E só o Observador a guarda.",
                        "Luz fere os mortos e os cultistas. Raio racha o ferro dos golens." });
                }
                break;
            case 5: falarCom("Gus", "Gus, o Taverneiro", new String[] {
                "Uma cerveja e uma boa história! O que vai ser?",
                "Dizem que à noite os monstros carregam mais ouro... e mais dentes.",
                "Sábio Aldric sabe de coisas que nem os reis sabem." }); break;
            default: break;
        }
    }

    static void falarCom(String id, String nome, String[] falas) {
        Tela.fala(nome, falas[RNG.nextInt(falas.length)]);
        entregarMissoes(id, nome);
        oferecerMissoes(id, nome);
        Tela.enter();
    }

    static void entregarMissoes(String id, String nome) {
        for (Missao m : diario.prontas(id, heroi)) {
            Tela.fala(nome, "Você conseguiu! Aqui está sua recompensa por \"" + m.titulo + "\".");
            if (m.tipo == Missao.Tipo.COLETAR) heroi.consumir(m.alvo, m.qtd);
            m.estado = 2;
            heroi.ganharOuro(m.ouro);
            System.out.println(Cor.c(Cor.AMARELO, "💰 +" + m.ouro + " de ouro."));
            if (m.itemRec != null) {
                heroi.inv.add(Fabrica.criar(m.itemRec));
                System.out.println("🎁 Você recebeu: " + m.itemRec);
            }
            heroi.mudarHonra(1);
            ganharExperiencia(m.xp);
        }
    }

    static void oferecerMissoes(String id, String nome) {
        while (true) {
            ArrayList<Missao> disp = diario.disponiveis(id, heroi);
            if (disp.isEmpty()) return;
            System.out.println("\n📜 Missões oferecidas:");
            for (int i = 0; i < disp.size(); i++) {
                Missao m = disp.get(i);
                System.out.println((i + 1) + ". " + Cor.c(Cor.NEGRITO, m.titulo) + " — " + m.desc);
                System.out.println("     Objetivo: " + m.objetivo(heroi) + "  |  Recompensa: " + m.recompensa());
            }
            System.out.println("0. Agora não");
            System.out.print("Aceitar: ");
            int op = Tela.ler(0, disp.size());
            if (op == 0) return;
            Missao m = disp.get(op - 1);
            m.estado = 1;
            Tela.fala(nome, "Conto com você. Boa sorte!");
            System.out.println(Cor.c(Cor.VERDE, "✅ Missão aceita: " + m.titulo));
        }
    }

    // ------------------------------------------------------------------
    // MERCADOR
    // ------------------------------------------------------------------
    static void menuMercador() {
        if (!checarAberto("O mercado")) return;
        Tela.mostrarArte(Arte.LOJA, Cor.AMARELO);
        Tela.fala("Mercador Tobias", heroi.getHonra() >= 2 ? "Ah, o herói honrado! Desconto para você."
            : (heroi.getHonra() <= -2 ? "Hmpf... sua fama o precede. Os preços subiram." : "Tudo de melhor, pelo menor preço!"));
        while (true) {
            System.out.println("\n💰 Seu ouro: " + heroi.getOuro());
            Tela.caixa("MERCADOR", "1. Comprar suprimentos", "2. Comprar equipamentos", "3. Vender itens", "0. Sair");
            System.out.print("Escolha: ");
            int op = Tela.ler(0, 3);
            if (op == 0) return;
            if (op == 1) comprar(Fabrica.CONSUMIVEIS);
            else if (op == 2) comprar(Fabrica.EQUIPAMENTOS_LOJA);
            else vender();
        }
    }

    static void comprar(String[] nomes) {
        System.out.println();
        for (int i = 0; i < nomes.length; i++) {
            Item it = Fabrica.criar(nomes[i]);
            System.out.println((i + 1) + ". " + it.linha() + " — " + Cor.c(Cor.AMARELO, preco(it.getPreco()) + "g"));
            if (it instanceof Equipamento) {
                Equipamento eq = (Equipamento) it;
                Equipamento atual = heroi.equip[eq.slot.ordinal()];
                if (atual != null) System.out.println("      (equipado: " + atual.getNome() + " " + atual.getDescricao() + ")");
            }
        }
        System.out.println("0. Voltar");
        System.out.print("Comprar: ");
        int op = Tela.ler(0, nomes.length);
        if (op == 0) return;
        Item it = Fabrica.criar(nomes[op - 1]);
        int custo = preco(it.getPreco());
        if (!heroi.gastarOuro(custo)) { System.out.println("❌ Ouro insuficiente."); return; }
        heroi.inv.add(it);
        System.out.println("✅ Você comprou " + it.getNome() + " por " + custo + "g.");
    }

    static void vender() {
        ArrayList<Item> vendaveis = new ArrayList<>();
        for (Item i : heroi.inv) if (!i.getCategoria().equals("Missão")) vendaveis.add(i);
        if (vendaveis.isEmpty()) { System.out.println("Você não tem nada para vender."); return; }
        System.out.println();
        for (int i = 0; i < vendaveis.size(); i++)
            System.out.println((i + 1) + ". " + vendaveis.get(i).linha() + " — " + Cor.c(Cor.AMARELO, vendaveis.get(i).getPrecoVenda() + "g"));
        System.out.println("0. Voltar");
        System.out.print("Vender: ");
        int op = Tela.ler(0, vendaveis.size());
        if (op == 0) return;
        Item it = vendaveis.get(op - 1);
        heroi.inv.remove(it);
        heroi.ganharOuro(it.getPrecoVenda());
        System.out.println("💰 Vendido por " + it.getPrecoVenda() + "g.");
    }

    // ------------------------------------------------------------------
    // FERREIRO E ALQUIMISTA
    // ------------------------------------------------------------------
    static void menuFerreiro() {
        if (!checarAberto("A forja")) return;
        Tela.mostrarArte(Arte.FORJA, Cor.VERMELHO);
        Tela.fala("Borin", "Mostre-me o que tem. Eu melhoro arma e armadura — mas o metal tem limites!");
        while (true) {
            System.out.println("\n💰 Seu ouro: " + heroi.getOuro());
            Tela.caixa("FORJA", "1. Melhorar arma equipada", "2. Melhorar armadura equipada", "0. Sair");
            System.out.print("Escolha: ");
            int op = Tela.ler(0, 2);
            if (op == 0) return;
            Equipamento eq = heroi.equip[op - 1];
            if (eq == null) { System.out.println("Você não tem isso equipado."); continue; }
            if (eq.melhoria >= 5) { System.out.println("Esta peça já está no limite (+5)."); continue; }
            int custo = preco(60 * (eq.melhoria + 1));
            int chance = eq.melhoria < 2 ? 100 : (eq.melhoria == 2 ? 80 : (eq.melhoria == 3 ? 60 : 45));
            System.out.println(eq.getNome() + " → +" + (eq.melhoria + 1) + " | Custo: " + custo + "g | Chance: " + chance + "%");
            System.out.print("Confirmar (1=sim, 0=não): ");
            if (Tela.ler(0, 1) == 0) continue;
            if (!heroi.gastarOuro(custo)) { System.out.println("❌ Ouro insuficiente."); continue; }
            System.out.println("🔨 *clang* *clang* *clang*");
            Tela.esperar(800);
            if (RNG.nextInt(100) < chance) {
                eq.melhoria++;
                System.out.println(Cor.c(Cor.VERDE, "✨ Sucesso! " + eq.getNome() + " ficou melhor."));
            } else System.out.println(Cor.c(Cor.VERMELHO, "💢 O metal rachou e nada mudou. O ouro se foi..."));
        }
    }

    static void menuAlquimista() {
        if (!checarAberto("O laboratório")) return;
        Tela.mostrarArte(Arte.ALQUIMIA, Cor.VERDE);
        Tela.fala("Mara, a Alquimista", "Traga ingredientes e eu transformo em maravilhas. De graça, claro... por enquanto.");
        while (true) {
            System.out.println();
            String[][] rs = Fabrica.RECEITAS;
            for (int i = 0; i < rs.length; i++) {
                Map<String, Integer> need = new LinkedHashMap<>();
                for (int k = 1; k < rs[i].length; k++) need.merge(rs[i][k], 1, Integer::sum);
                StringBuilder sb = new StringBuilder();
                boolean ok = true;
                for (Map.Entry<String, Integer> e : need.entrySet()) {
                    int tem = heroi.contar(e.getKey());
                    if (tem < e.getValue()) ok = false;
                    sb.append(e.getValue()).append("x ").append(e.getKey()).append(" (tem ").append(tem).append(")  ");
                }
                System.out.println((i + 1) + ". " + Cor.c(ok ? Cor.VERDE : Cor.CINZA, rs[i][0]) + " ← " + sb);
            }
            System.out.println("0. Sair");
            System.out.print("Criar: ");
            int op = Tela.ler(0, rs.length);
            if (op == 0) return;
            String[] r = rs[op - 1];
            Map<String, Integer> need = new LinkedHashMap<>();
            for (int k = 1; k < r.length; k++) need.merge(r[k], 1, Integer::sum);
            boolean ok = true;
            for (Map.Entry<String, Integer> e : need.entrySet()) if (heroi.contar(e.getKey()) < e.getValue()) ok = false;
            if (!ok) { System.out.println("❌ Faltam ingredientes."); continue; }
            for (Map.Entry<String, Integer> e : need.entrySet()) heroi.consumir(e.getKey(), e.getValue());
            heroi.inv.add(Fabrica.criar(r[0]));
            Tela.mostrarArte(new String[] { "     ~ ~ ~ ~     ", "    (  ⚗️  )    ", "     ~ ~ ~ ~     " }, Cor.VERDE);
            System.out.println("⚗️  Você criou: " + r[0] + "!");
        }
    }

    // ------------------------------------------------------------------
    // TAVERNA (DESCANSO, ALIADOS, DADOS, CHARADA, RUMORES)
    // ------------------------------------------------------------------
    static void menuTaverna() {
        Tela.mostrarArte(Arte.TAVERNA, Cor.AMARELO);
        Tela.fala("Gus", "Entre, entre! A casa é sua.");
        while (true) {
            System.out.println("\n💰 Seu ouro: " + heroi.getOuro());
            Tela.caixa("TAVERNA", "1. Descansar até de manhã (20g)", "2. Contratar aliado", "3. Jogar dados",
                "4. Ouvir rumores", "5. Desafio da charada (1 por dia)", "0. Sair");
            System.out.print("Escolha: ");
            switch (Tela.ler(0, 5)) {
                case 1:
                    if (!heroi.gastarOuro(preco(20))) { System.out.println("❌ Ouro insuficiente."); break; }
                    heroi.restaurarTotal();
                    while (periodo != 0) avancar(1);
                    System.out.println("😴 Você dormiu como uma pedra. HP e Mana restaurados.");
                    break;
                case 2: contratarAliado(); break;
                case 3: jogarDados(); break;
                case 4: rumores(); break;
                case 5: charada(); break;
                default: return;
            }
        }
    }

    static void contratarAliado() {
        Aliado[] opcoes = { new LoboCinzento(), new IrmaLira() };
        System.out.println();
        for (int i = 0; i < opcoes.length; i++) {
            Tela.mostrarArte(opcoes[i].sprite(), Cor.CIANO);
            System.out.println((i + 1) + ". " + opcoes[i].nome + " — " + opcoes[i].descricao() + " (" + preco(opcoes[i].preco) + "g)");
        }
        if (heroi.aliado != null) System.out.println("Aliado atual: " + heroi.aliado.nome);
        System.out.println("3. Dispensar aliado atual\n0. Voltar");
        System.out.print("Escolha: ");
        int op = Tela.ler(0, 3);
        if (op == 0) return;
        if (op == 3) { heroi.aliado = null; System.out.println("Você segue sozinho."); return; }
        Aliado a = opcoes[op - 1];
        if (!heroi.gastarOuro(preco(a.preco))) { System.out.println("❌ Ouro insuficiente."); return; }
        heroi.aliado = a;
        System.out.println(Cor.c(Cor.VERDE, "🤝 " + a.nome + " agora luta ao seu lado!"));
    }

    static void jogarDados() {
        Tela.mostrarArte(Arte.DADOS, Cor.BRANCO);
        Tela.fala("Gus", "Dois dados cada. Quem somar mais leva a aposta. Empate devolve o ouro!");
        while (true) {
            System.out.print("\n💰 Ouro: " + heroi.getOuro() + " | Aposta (0 para sair, máx 100): ");
            int aposta = Tela.ler(0, 100);
            if (aposta == 0) return;
            if (aposta > heroi.getOuro()) { System.out.println("Você não tem tanto ouro."); continue; }
            int a = RNG.nextInt(6) + RNG.nextInt(6) + 2, b = RNG.nextInt(6) + RNG.nextInt(6) + 2;
            System.out.println("🎲 Você tirou " + a + " | Gus tirou " + b);
            if (a > b) { heroi.ganharOuro(aposta); System.out.println(Cor.c(Cor.VERDE, "Você ganhou " + aposta + "g!")); }
            else if (a < b) { heroi.gastarOuro(aposta); System.out.println(Cor.c(Cor.VERMELHO, "Você perdeu " + aposta + "g.")); }
            else System.out.println("Empate! Ninguém perde nada.");
        }
    }

    static void rumores() {
        ArrayList<String> r = new ArrayList<>();
        r.add("Dizem que slimes e lobos fogem do fogo... ou melhor, derretem nele.");
        r.add("Bandidos roubam ouro, mas quem os derrota recupera tudo e mais um pouco.");
        r.add("Chefes avisam antes de um golpe devastador. Defender ou atordoar salva vidas!");
        r.add("Honra alta dá desconto nas lojas. Roubar viajantes... nem tanto.");
        if (!diario.concluida("seita")) r.add("Cultistas vagam pelas Ruínas. Aldric quer saber o que eles adoram.");
        else if (!diario.concluida("torre")) r.add("A Torre do Observador está aberta. Leve luz e uma poção extra.");
        else r.add("A Chave do Dragão abre o Covil de Ignis. Ele resiste a fogo — mas teme raios!");
        Tela.fala("Gus", r.get(RNG.nextInt(r.size())));
    }

    static void charada() {
        if (charadaHoje) { System.out.println("Você já tentou hoje. Volte amanhã."); return; }
        String[][] c = { { "O que é, o que é: cai em pé e corre deitada?", "chuva" },
            { "O que tem coroa mas não é rei?", "abacaxi" }, { "O que quanto mais se tira, maior fica?", "buraco" },
            { "Tenho cidades sem casas e rios sem água. O que sou?", "mapa" }, { "Tem dentes mas não morde. O que é?", "pente" } };
        String[] q = c[RNG.nextInt(c.length)];
        charadaHoje = true;
        Tela.fala("Gus", q[0]);
        System.out.print("Sua resposta: ");
        if (Tela.lerTexto().toLowerCase().contains(q[1])) {
            int ouro = 30 + RNG.nextInt(40);
            heroi.ganharOuro(ouro);
            System.out.println(Cor.c(Cor.VERDE, "🎉 Correto! Você ganhou " + ouro + "g!"));
            heroi.mudarHonra(0);
            ganharExperiencia(40);
        } else System.out.println(Cor.c(Cor.VERMELHO, "Errado! A resposta era \"" + q[1] + "\"."));
    }

    // ------------------------------------------------------------------
    // TEMPLO
    // ------------------------------------------------------------------
    static void menuTemplo() {
        Tela.mostrarArte(Arte.TEMPLO, Cor.AMARELO);
        Tela.fala("Irmã Lira", "O templo acolhe a todos. A luz cura o corpo, a generosidade cura a alma.");
        while (true) {
            int custoCura = preco(5 + heroi.getNivel() * 5);
            System.out.println("\n💰 Seu ouro: " + heroi.getOuro() + " | Honra: " + heroi.getHonra() + " (" + heroi.titulo() + ")");
            Tela.caixa("TEMPLO", "1. Curar HP, Mana e males (" + custoCura + "g)", "2. Doar 50g aos necessitados (+1 Honra)", "0. Sair");
            System.out.print("Escolha: ");
            int op = Tela.ler(0, 2);
            if (op == 0) return;
            if (op == 1) {
                if (!heroi.gastarOuro(custoCura)) { System.out.println("❌ Ouro insuficiente."); continue; }
                heroi.restaurarTotal();
                Tela.mostrarArte(Efeitos.LUZ, Cor.AMARELO);
                System.out.println("✨ Você se sente renovado!");
            } else {
                if (!heroi.gastarOuro(50)) { System.out.println("❌ Ouro insuficiente."); continue; }
                heroi.mudarHonra(1);
                System.out.println(Cor.c(Cor.VERDE, "💖 Sua generosidade é reconhecida. Honra +1."));
            }
        }
    }

    // ------------------------------------------------------------------
    // FICHA E INVENTÁRIO
    // ------------------------------------------------------------------
    static void menuFicha() {
        while (true) {
            System.out.println();
            Tela.titulo("FICHA DO HERÓI", Cor.CIANO);
            Tela.mostrarArte(heroi.sprite(), heroi.cor());
            System.out.println(heroi.ficha());
            System.out.println("ATK " + heroi.getAtaque() + "  DEF " + heroi.getDefesa() + "  AGI " + heroi.getAgilidade()
                + "  Honra " + heroi.getHonra() + "  Abates " + heroi.abates + "  Ouro " + heroi.getOuro());
            System.out.println("\nEQUIPADO:");
            for (Slot s : Slot.values()) {
                Equipamento e = heroi.equip[s.ordinal()];
                System.out.println("  " + Tela.pad(s.nome, 10) + (e == null ? "—" : e.getNome() + " (" + e.getDescricao() + ")"));
            }
            System.out.println("Aliado: " + (heroi.aliado == null ? "—" : heroi.aliado.nome));
            System.out.println("\nHABILIDADES:");
            for (Habilidade h : heroi.habilidades())
                System.out.println("  " + h.elem.icone + " " + Tela.pad(h.nome, 20) + h.custo + " mana — " + h.desc);
            System.out.println("\nMOCHILA:");
            Map<String, Integer> cont = new LinkedHashMap<>();
            for (Item i : heroi.inv) cont.merge(i.getNome(), 1, Integer::sum);
            if (cont.isEmpty()) System.out.println("  (vazia)");
            for (Map.Entry<String, Integer> e : cont.entrySet()) System.out.println("  " + e.getValue() + "x " + e.getKey());
            Tela.caixa("OPÇÕES", "1. Equipar item", "2. Usar item", "0. Voltar");
            System.out.print("Escolha: ");
            int op = Tela.ler(0, 2);
            if (op == 0) return;
            if (op == 1) {
                ArrayList<Equipamento> eqs = new ArrayList<>();
                for (Item i : heroi.inv) if (i instanceof Equipamento) eqs.add((Equipamento) i);
                if (eqs.isEmpty()) { System.out.println("Nenhum equipamento na mochila."); continue; }
                for (int i = 0; i < eqs.size(); i++) System.out.println((i + 1) + ". " + eqs.get(i).linha());
                System.out.println("0. Voltar");
                System.out.print("Equipar: ");
                int k = Tela.ler(0, eqs.size());
                if (k > 0) heroi.equipar(eqs.get(k - 1));
            } else {
                ArrayList<Item> us = new ArrayList<>();
                for (Item i : heroi.inv) if (i.usavelFora() && !(i instanceof Equipamento)) us.add(i);
                if (us.isEmpty()) { System.out.println("Nada utilizável agora."); continue; }
                for (int i = 0; i < us.size(); i++) System.out.println((i + 1) + ". " + us.get(i).linha());
                System.out.println("0. Voltar");
                System.out.print("Usar: ");
                int k = Tela.ler(0, us.size());
                if (k > 0 && us.get(k - 1).usar(heroi, null)) heroi.inv.remove(us.get(k - 1));
            }
        }
    }

    // ------------------------------------------------------------------
    // VIAGEM E MAPA
    // ------------------------------------------------------------------
    static String bloqueio(Local l) {
        if (l == Local.TORRE && !diario.concluida("seita")) return "Selada — conclua \"A Seita do Olho\" para Aldric revelar o caminho";
        if (l == Local.COVIL && heroi.contar("Chave do Dragão") == 0) return "Trancado — requer a Chave do Dragão";
        return null;
    }

    static void menuViagem() {
        System.out.println();
        Tela.titulo("MAPA DO REINO", Cor.AMARELO);
        System.out.println("            [Torre]────[Montanha]────[Covil]");
        System.out.println("               │           │");
        System.out.println("  [Floresta]──[VILA]──────[Ruínas]");
        System.out.println("               │");
        System.out.println("           [Caverna]");
        System.out.println();
        Local[] ls = Local.values();
        for (int i = 0; i < ls.length; i++) {
            Local l = ls[i];
            String b = bloqueio(l);
            String atual = l == local ? Cor.c(Cor.CINZA, " (você está aqui)") : "";
            System.out.println((i + 1) + ". " + Cor.c(b == null ? l.cor() : Cor.CINZA, Tela.pad(l.nome, 24)) + " Nv rec. " + l.nivelRec
                + (b != null ? Cor.c(Cor.VERMELHO, "  🔒 " + b) : "") + atual);
        }
        System.out.println("0. Cancelar");
        System.out.print("Viajar para: ");
        int op = Tela.ler(0, ls.length);
        if (op == 0) return;
        Local dest = ls[op - 1];
        if (dest == local) { System.out.println("Você já está aqui."); return; }
        String b = bloqueio(dest);
        if (b != null) { System.out.println("🔒 " + b); return; }
        viajarPara(dest);
    }

    static void viajarPara(Local dest) {
        System.out.println("\n🧭 Você parte rumo a " + dest.nome + "...");
        avancar(1);
        Tela.esperar(700);
        if (dest != Local.VILA && dest != Local.TORRE && dest != Local.COVIL && RNG.nextInt(100) < 25) {
            System.out.println("⚠️  Algo bloqueia a estrada!");
            Combate.Resultado r = combate(Bestiario.sortear(dest, heroi.getNivel(), noite()), true);
            if (r == Combate.Resultado.DERROTA) return;
        }
        local = dest;
        chegou = true;
    }

    // ------------------------------------------------------------------
    // LOCAIS SELVAGENS
    // ------------------------------------------------------------------
    static void menuLocal() {
        cabecalho();
        if (chegou) {
            Tela.mostrarArte(local.arte(), local.cor());
            System.out.println(Cor.c(local.cor(), descricaoLocal()));
            chegou = false;
        }
        if (local == Local.TORRE) { menuTorre(); return; }
        if (local == Local.COVIL) { menuCovil(); return; }
        Tela.caixa(local.nome.toUpperCase() + " (Nv rec. " + local.nivelRec + ")", "1. Explorar", "2. Coletar " + local.material(),
            "3. Acampar", "4. Ficha e inventário", "5. Diário de missões", "6. Viajar", "7. Salvar jogo");
        System.out.print("Escolha: ");
        switch (Tela.ler(1, 7)) {
            case 1: explorar(); break;
            case 2: coletar(); break;
            case 3: acampar(); break;
            case 4: menuFicha(); break;
            case 5: diario.mostrar(heroi); Tela.enter(); break;
            case 6: menuViagem(); break;
            default: Persistencia.salvar();
        }
    }

    static String descricaoLocal() {
        switch (local) {
            case FLORESTA: return "As árvores sussurram segredos antigos. Algo se move nas sombras...";
            case CAVERNA: return "Teias grossas cobrem as paredes. O ar é úmido e pesado.";
            case RUINAS: return "Colunas quebradas e ossos espalhados. Os mortos não descansam aqui.";
            case MONTANHA: return "O vento corta como lâmina. Passos metálicos ecoam entre as rochas.";
            case TORRE: return "O Olho do Caos observa de cima. Cada degrau é uma provação.";
            default: return "O calor é insuportável. Ouro e cinzas cobrem o chão. Ignis dorme... ou finge.";
        }
    }

    static void menuTorre() {
        Tela.caixa("TORRE DO OBSERVADOR", "1. Subir a torre (2 andares + chefe)", "2. Ficha e inventário", "3. Viajar");
        System.out.print("Escolha: ");
        switch (Tela.ler(1, 3)) {
            case 1: subirTorre(); break;
            case 2: menuFicha(); break;
            default: menuViagem();
        }
    }

    static void subirTorre() {
        avancar(1);
        for (int andar = 1; andar <= 2; andar++) {
            System.out.println(Cor.c(Cor.AZUL + Cor.NEGRITO, "\n🪜 ANDAR " + andar));
            Combate.Resultado r = combate(Bestiario.sortear(Local.TORRE, heroi.getNivel(), noite()), true);
            if (r != Combate.Resultado.VITORIA) return;
            System.out.println("\n1. Continuar subindo   2. Usar item/Ver ficha   0. Descer");
            System.out.print("Escolha: ");
            int op = Tela.ler(0, 2);
            if (op == 0) { System.out.println("Você desce a torre para recuperar o fôlego."); return; }
            if (op == 2) menuFicha();
        }
        System.out.println(Cor.c(Cor.VERMELHO + Cor.NEGRITO, "\n👁️  NO TOPO, O OBSERVADOR DO CAOS ABRE SEU GRANDE OLHO!"));
        Beholder b = new Beholder();
        if (heroi.getNivel() > 6) b.escalar(1.0 + 0.05 * (heroi.getNivel() - 6));
        combate(b, false);
    }

    static void menuCovil() {
        Tela.caixa("COVIL DE IGNIS", "1. Enfrentar Ignis, o Dragão Ancestral", "2. Ficha e inventário", "3. Viajar");
        System.out.print("Escolha: ");
        switch (Tela.ler(1, 3)) {
            case 1:
                System.out.println(Cor.c(Cor.VERMELHO + Cor.NEGRITO, "\n🐉 O CHÃO TREME. IGNIS ABRE OS OLHOS DOURADOS..."));
                DragaoAncestral d = new DragaoAncestral();
                if (heroi.getNivel() > 9) d.escalar(1.0 + 0.05 * (heroi.getNivel() - 9));
                combate(d, false);
                break;
            case 2: menuFicha(); break;
            default: menuViagem();
        }
    }

    static void explorar() {
        avancar(1);
        System.out.println("\n🚶 Você explora " + local.nome + "...");
        Tela.esperar(600);
        int r = RNG.nextInt(10);
        if (r <= 3) encontro();
        else if (r == 4) bau();
        else if (r == 5) armadilha();
        else if (r == 6) fogueira();
        else eventoAleatorio();
    }

    static Combate.Resultado encontro() {
        Monstro m = Bestiario.sortear(local, heroi.getNivel(), noite());
        System.out.println(Cor.c(Cor.VERMELHO, "⚠️  Um(a) " + m.getNome() + " surge diante de você!" + (noite() ? " (a noite o fortalece)" : "")));
        return combate(m, true);
    }

    static void coletar() {
        avancar(1);
        String mat = local.material();
        int r = RNG.nextInt(100);
        if (r < 25) { System.out.println("⚠️  Uma criatura surge enquanto você coleta!"); encontro(); return; }
        if (r < 90) {
            int q = 1 + RNG.nextInt(2) + (mat.equals("Erva Lunar") && noite() ? 1 : 0);
            for (int i = 0; i < q; i++) heroi.inv.add(Fabrica.criar(mat));
            System.out.println("🌿 Você coletou " + q + "x " + mat + (mat.equals("Erva Lunar") && noite() ? " (brilham à noite!)" : "") + ".");
        } else System.out.println("Você procurou bastante, mas não achou nada útil.");
    }

    static void acampar() {
        Tela.mostrarArte(Arte.FOGUEIRA, Cor.AMARELO);
        avancar(1);
        if (RNG.nextInt(100) < (noite() ? 35 : 15)) {
            System.out.println("⚠️  Algo ataca seu acampamento!");
            if (encontro() != Combate.Resultado.VITORIA) return;
        }
        heroi.curar(heroi.getHpMax() * 30 / 100);
        heroi.modificarMana(heroi.getManaMax() * 30 / 100);
        heroi.removerNegativos();
        System.out.println("🔥 Você descansa junto ao fogo e recupera 30% de HP e Mana.");
    }

    static void danoSeguro(int q) { heroi.perderHp(Math.min(q, heroi.getHp() - 1)); }

    static void bau() {
        Tela.mostrarArte(Arte.BAU, Cor.AMARELO);
        System.out.println("📦 Você encontrou um baú esquecido!");
        int r = RNG.nextInt(100);
        if (r < 50) {
            int o = 15 + RNG.nextInt(20) * Math.max(1, local.nivelRec / 2 + 1);
            heroi.ganharOuro(o);
            System.out.println("💰 Dentro havia " + o + " de ouro!");
        } else if (r < 85) {
            String[] c = { "Poção de Cura", "Elixir de Mana", "Antídoto", "Bomba de Fogo", "Pergaminho do Trovão" };
            Item it = Fabrica.criar(c[RNG.nextInt(c.length)]);
            heroi.inv.add(it);
            System.out.println("🎁 Você achou: " + it.linha());
        } else {
            String[] e = { "Gibão de Couro", "Cota de Malha", "Lâmina de Aço", "Anel Veloz", "Amuleto Vital", "Adaga Sombria" };
            Item it = Fabrica.criar(e[RNG.nextInt(e.length)]);
            heroi.inv.add(it);
            System.out.println("🎁 Equipamento raro! " + it.linha());
        }
    }

    static void armadilha() {
        Tela.mostrarArte(Arte.ARMADILHA, Cor.VERMELHO);
        if (RNG.nextInt(100) < Math.min(60, heroi.getAgilidade() * 2)) {
            System.out.println("🥷 Você percebeu a armadilha e desarmou-a com agilidade!");
            ganharExperiencia(15);
        } else {
            int d = 8 + local.nivelRec * 2;
            danoSeguro(d);
            System.out.println("🕸️  A armadilha disparou! Você perdeu " + d + " HP.");
        }
    }

    static void fogueira() {
        Tela.mostrarArte(Arte.FOGUEIRA, Cor.AMARELO);
        System.out.println("🔥 Uma fogueira abandonada ainda queima. Você descansa e recupera 35 HP.");
        heroi.curar(35);
    }

    // ------------------------------------------------------------------
    // EVENTOS NARRATIVOS COM ESCOLHAS
    // ------------------------------------------------------------------
    static void eventoAleatorio() {
        switch (RNG.nextInt(5)) {
            case 0: eventoViajante(); break;
            case 1: eventoSantuario(); break;
            case 2: eventoCofre(); break;
            case 3: eventoMercadorErrante(); break;
            default:
                Tela.mostrarArte(Arte.FONTE, Cor.CIANO);
                System.out.println("✨ Você encontrou uma fonte de energia mística! Sua mana foi totalmente recarregada.");
                heroi.modificarMana(heroi.getManaMax());
        }
    }

    static void eventoViajante() {
        Tela.fala("Viajante ferido", "Por favor... estou sangrando. Você tem algo para me ajudar?");
        Tela.caixa("O QUE FAZER?", "1. Dar uma Poção de Cura", "2. Seguir caminho", "3. Roubar o viajante");
        System.out.print("Escolha: ");
        switch (Tela.ler(1, 3)) {
            case 1:
                if (heroi.consumir("Poção de Cura", 1)) {
                    int o = 30 + RNG.nextInt(30);
                    Tela.fala("Viajante", "Obrigado, herói! Que a sorte o acompanhe. Aceite isto.");
                    heroi.ganharOuro(o);
                    heroi.mudarHonra(1);
                    System.out.println(Cor.c(Cor.VERDE, "💖 Honra +1 e " + o + "g de recompensa."));
                    ganharExperiencia(30);
                } else System.out.println("Você não tem Poção de Cura para oferecer...");
                break;
            case 2: System.out.println("Você segue seu caminho, com o gemido ecoando atrás de você."); break;
            default:
                int o = 40 + RNG.nextInt(40);
                heroi.ganharOuro(o);
                heroi.mudarHonra(-1);
                System.out.println(Cor.c(Cor.VERMELHO, "💰 Você levou " + o + "g. Honra -1."));
        }
    }

    static void eventoSantuario() {
        System.out.println("⛩️  Um santuário antigo emana uma aura misteriosa.");
        Tela.caixa("SANTUÁRIO", "1. Rezar em silêncio", "2. Fazer oferenda de 20g", "3. Ignorar");
        System.out.print("Escolha: ");
        switch (Tela.ler(1, 3)) {
            case 1:
                if (RNG.nextInt(100) < 60) {
                    System.out.println(Cor.c(Cor.VERDE, "✨ Uma luz suave o envolve. Você recupera metade do HP e da Mana."));
                    heroi.curar(heroi.getHpMax() / 2); heroi.modificarMana(heroi.getManaMax() / 2);
                } else { System.out.println(Cor.c(Cor.VERMELHO, "🌑 Uma presença sombria drena sua vitalidade! -15 HP.")); danoSeguro(15); }
                break;
            case 2:
                if (!heroi.gastarOuro(20)) { System.out.println("Você não tem ouro."); break; }
                heroi.restaurarTotal();
                heroi.mudarHonra(1);
                System.out.println(Cor.c(Cor.VERDE, "🌟 O santuário aceita a oferenda: HP e Mana restaurados. Honra +1."));
                break;
            default: System.out.println("Você segue adiante.");
        }
    }

    static void eventoCofre() {
        Tela.mostrarArte(Arte.BAU, Cor.CINZA);
        System.out.println("🔒 Um cofre de ferro trancado, com símbolos arcanos.");
        Tela.caixa("COFRE", "1. Forçar a tranca", "2. Tentar abrir com delicadeza (usa Agilidade)", "3. Deixar para lá");
        System.out.print("Escolha: ");
        int op = Tela.ler(1, 3);
        if (op == 3) return;
        boolean sucesso = op == 1 ? RNG.nextInt(100) < 50 : RNG.nextInt(100) < Math.min(85, 30 + heroi.getAgilidade() * 2);
        if (sucesso) {
            int o = 40 + RNG.nextInt(60);
            heroi.ganharOuro(o);
            Item it = Fabrica.criar(RNG.nextBoolean() ? "Elixir de Mana" : "Bomba de Fogo");
            heroi.inv.add(it);
            System.out.println(Cor.c(Cor.VERDE, "🎉 O cofre abre! " + o + "g e " + it.getNome() + "!"));
        } else {
            int d = op == 1 ? 20 : 10;
            danoSeguro(d);
            System.out.println(Cor.c(Cor.VERMELHO, "💥 A armadilha mágica explode! -" + d + " HP. O cofre permanece fechado."));
        }
    }

    static void eventoMercadorErrante() {
        String[] c = { "Poção de Cura", "Elixir de Mana", "Antídoto", "Bomba de Fogo", "Pergaminho do Trovão" };
        Item it = Fabrica.criar(c[RNG.nextInt(c.length)]);
        int p = Math.max(1, (int) (it.getPreco() * 0.7));
        Tela.fala("Mercador Errante", "Psiu! Tenho " + it.getNome() + " em promoção, só " + p + "g!");
        System.out.print("Comprar? (1=sim, 0=não): ");
        if (Tela.ler(0, 1) == 1) {
            if (heroi.gastarOuro(p)) { heroi.inv.add(it); System.out.println("✅ Comprado!"); }
            else System.out.println("❌ Ouro insuficiente.");
        }
    }

    // ------------------------------------------------------------------
    // COMBATE (ENVOLTÓRIO COM RECOMPENSAS E DERROTA)
    // ------------------------------------------------------------------
    static Combate.Resultado combate(Monstro m, boolean podeFugir) {
        Tela.titulo("COMBATE: " + m.getNome().toUpperCase(), Cor.VERMELHO);
        Tela.esperar(600);
        Combate.Resultado r = Combate.lutar(heroi, m, podeFugir);
        if (r == Combate.Resultado.VITORIA) vitoria(m);
        else if (r == Combate.Resultado.DERROTA) derrota();
        else System.out.println(Cor.c(Cor.CINZA, "Você recua e o inimigo desaparece na escuridão."));
        return r;
    }

    static void vitoria(Monstro m) {
        System.out.println("\n" + Cor.c(Cor.AMARELO + Cor.NEGRITO, "🏆 " + m.getNome() + " foi derrotado!"));
        Tela.mostrarArte(new String[] { "       \\ | /       ", "     -- VITÓRIA --  ", "       / | \\       " }, Cor.AMARELO);
        int ouro = noite() ? (int) (m.ouroRec * 1.5) : m.ouroRec;
        heroi.ganharOuro(ouro);
        System.out.println("💰 +" + ouro + " de ouro.");
        heroi.abates++;
        if (m.dropNome != null && RNG.nextInt(100) < m.dropChance) {
            heroi.inv.add(Fabrica.criar(m.dropNome));
            System.out.println("🎁 Item coletado: " + m.dropNome);
        }
        if (RNG.nextInt(100) < 12) {
            String[] c = { "Poção de Cura", "Antídoto", "Ensopado Quente" };
            Item it = Fabrica.criar(c[RNG.nextInt(c.length)]);
            heroi.inv.add(it);
            System.out.println("🎁 O monstro deixou cair: " + it.getNome());
        }
        diario.registrarAbate(Bestiario.id(m));
        if (m instanceof Beholder) {
            heroi.inv.add(Fabrica.criar("Chave do Dragão"));
            System.out.println(Cor.c(Cor.AMARELO + Cor.NEGRITO, "🗝️  Você encontrou a CHAVE DO DRAGÃO entre os restos do Observador!"));
        }
        if (m instanceof DragaoAncestral) {
            Missao d = diario.obter("dragao");
            if (d != null && d.estado == 1) d.estado = 2;
            venceu = true;
            fimDeJogo = true;
        }
        ganharExperiencia(m.xpRec);
        Tela.esperar(800);
    }

    static void derrota() {
        if (hardcore) { fimDeJogo = true; venceu = false; return; }
        System.out.println();
        Tela.mostrarArte(Arte.CAVEIRA, Cor.VERMELHO);
        System.out.println(Cor.c(Cor.VERMELHO + Cor.NEGRITO, "💀 Você caiu em combate..."));
        int perda = heroi.getOuro() / 4;
        heroi.gastarOuro(perda);
        heroi.restaurarTotal();
        heroi.curar(-(heroi.getHpMax() / 2));
        local = Local.VILA;
        periodo = 3;
        avancar(1);
        chegou = true;
        Tela.fala("Irmã Lira", "Você está vivo! Seus aliados o trouxeram desacordado. Perdeu " + perda + "g no caminho, mas há esperança.");
        Tela.enter();
    }
}

// =========================================================================
// PARTE 9 - SAVE/LOAD, RANKING E TELAS FINAIS
// =========================================================================
