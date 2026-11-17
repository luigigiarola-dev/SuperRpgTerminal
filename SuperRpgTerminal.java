import java.io.*;
import java.util.*;

// =========================================================================
// PARTE 1 - BASE VISUAL: ELEMENTOS, CORES, TELA E ARTES
// =========================================================================
enum Elemento {
    FISICO("Físico", "⚔️"), LUZ("Luz", "✨"), SOMBRA("Sombra", "🌑"), FOGO("Fogo", "🔥"), RAIO("Raio", "⚡");
    final String nome, icone;
    Elemento(String n, String i) { nome = n; icone = i; }
}

final class Cor {
    static boolean ativo = true;
    static final String RESET = "\u001B[0m", NEGRITO = "\u001B[1m";
    static final String VERMELHO = "\u001B[31m", VERDE = "\u001B[32m", AMARELO = "\u001B[33m";
    static final String AZUL = "\u001B[34m", MAGENTA = "\u001B[35m", CIANO = "\u001B[36m";
    static final String CINZA = "\u001B[90m", BRANCO = "\u001B[97m";
    static String c(String cor, String texto) { return ativo ? cor + texto + RESET : texto; }
}

final class Tela {
    static final int LARGURA = 66;
    static boolean rapido = false;
    static final Scanner IN = new Scanner(System.in);

    static String pad(String s, int w) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    static String linha(char ch) { return String.valueOf(ch).repeat(LARGURA); }

    static void titulo(String texto, String cor) {
        System.out.println(Cor.c(cor, linha('=')));
        int esq = Math.max(0, (LARGURA - texto.length()) / 2);
        System.out.println(Cor.c(cor + Cor.NEGRITO, " ".repeat(esq) + texto));
        System.out.println(Cor.c(cor, linha('=')));
    }

    static void caixa(String titulo, String... linhas) {
        int w = titulo.length() + 1;
        for (String l : linhas) w = Math.max(w, l.length());
        System.out.println("┌─ " + titulo + " " + "─".repeat(w - titulo.length() - 1) + "┐");
        for (String l : linhas) System.out.println("│ " + pad(l, w) + " │");
        System.out.println("└" + "─".repeat(w + 2) + "┘");
    }

    static String barra(int atual, int max, int tam, String cor) {
        int cheio = max > 0 ? (int) Math.round((double) atual / max * tam) : 0;
        cheio = Math.max(0, Math.min(tam, cheio));
        if (atual > 0 && cheio == 0) cheio = 1;
        return "[" + Cor.c(cor, "█".repeat(cheio)) + Cor.c(Cor.CINZA, "░".repeat(tam - cheio)) + "]";
    }

    static String corHp(int hp, int max) {
        double p = (double) hp / Math.max(1, max);
        return p > 0.5 ? Cor.VERDE : (p > 0.25 ? Cor.AMARELO : Cor.VERMELHO);
    }

    static void esperar(int ms) {
        if (rapido) return;
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    static void mostrarArte(String[] arte, String cor) {
        for (String l : arte) System.out.println(Cor.c(cor, l));
    }

    /** Fala de NPC com efeito de máquina de escrever. */
    static void fala(String npc, String texto) {
        System.out.println();
        System.out.println(Cor.c(Cor.AMARELO + Cor.NEGRITO, "💬 " + npc + ":"));
        System.out.print("   «");
        if (rapido) System.out.print(texto);
        else for (char c : texto.toCharArray()) { System.out.print(c); System.out.flush(); esperar(12); }
        System.out.println("»");
    }

    static void sair() {
        System.out.println("\nEntrada encerrada. Saindo do jogo.");
        System.exit(0);
    }

    static String lerTexto() {
        try { return IN.nextLine().trim(); }
        catch (NoSuchElementException e) { sair(); return ""; }
    }

    static int ler(int min, int max) {
        while (true) {
            String s = lerTexto();
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) return v;
                System.out.print("Fora do alcance (" + min + " a " + max + "): ");
            } catch (NumberFormatException e) {
                System.out.print("Digite apenas números: ");
            }
        }
    }

    static void enter() {
        if (rapido) return;
        System.out.print(Cor.c(Cor.CINZA, "  [Enter para continuar] "));
        lerTexto();
    }
}

// =========================================================================
// ARTES ASCII (LOCAIS, EVENTOS E EFEITOS)
// =========================================================================
final class Arte {
    static final String[] ESPADA = {
        "          /\\          ", "         /  \\         ", "         |  |         ",
        "         |  |         ", "         |  |         ", "     ____|  |____     ",
        "    '----|  |----'    ", "         |  |         ", "         |__|         ", "          \\/          " };
    static final String[] VILA = {
        "          |>               ", "    /\\    |    /\\    /\\    ", "   /  \\  / \\  /  \\  /  \\   ",
        "  /____\\/___\\/____\\/____\\  ", "  |[]  ||[ ]||  []||[]  |  ", "  |__ _||_|_||_ __||_ __|  ",
        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~" };
    static final String[] FLORESTA = {
        "     /\\      /\\     /\\    ", "    /  \\    /  \\   /  \\   ", "   /____\\  /____\\ /____\\  ",
        "   /    \\  /    \\ /    \\  ", "     ||      ||     ||    ", "  ,,,||,,,,,,||,,,,,||,,, " };
    static final String[] CAVERNA = {
        "    _.-'''''''''''-._     ", "  .'  \\ \\  |  / /  '.   ", " /     \\ \\ | / /     \\  ",
        "|   .-._\\_\\|/_/_.-.   | ", "|  (  O O  ) web (O O)| ", " \\__'-'_____|_____'-'__/ " };
    static final String[] RUINAS = {
        "   _    _     _    _    ", "  | |  | |   | |  | |   ", "  |_|__|_|___|_|__|_|   ",
        "  | |  | | ? | |  | |   ", " _|_|__|_|___|_|__|_|_  ", "/_____________________\\ " };
    static final String[] MONTANHA = {
        "          /\\            ", "     /\\  /  \\    /\\     ", "    /  \\/    \\  /  \\    ",
        "   / /\\  /\\   \\/ /\\ \\   ", "  / /  \\/  \\   \\/  \\ \\  ", " /_/___________\\_____\\_\\ " };
    static final String[] TORRE = {
        "        _|^|_          ", "       |  O  |         ", "       |  |  |         ",
        "      _|__|__|_        ", "     |   [O]   |       ", "     |___________|      ", "   ##################   " };
    static final String[] COVIL = {
        "     .  *  .  *  .     ", "   .-'~~~~~~~~~~~'-.   ", "  /  /\\  /\\  /\\    \\  ",
        " |  /  \\/  \\/  \\   |  ", " |  ( ) ( ) ( ) ( )  | ", "  \\__ 🔥  🔥  🔥  __/   " };
    static final String[] TAVERNA = {
        "      ____________       ", "   __/ TAVERNA    \\__    ", "  |  _  _  _  _  _  _ |   ",
        "  | |_||_||_||_||_||_||   ", "  |   [  ]   🍺      |   ", "  |_______|__|_______|   " };
    static final String[] LOJA = {
        "   ___________________   ", "  /$ $ $ $ $ $ $ $ $ \\  ", " /___________________\\ ",
        " |  [^]  [~]  [!]   | ", " |_____________  ___| ", "               |__|     " };
    static final String[] FORJA = {
        "       ____       (  )   ", "   ___/    \\___  ) (    ", "  [____________]  ( )    ",
        "   |  ====  |  .-'-.     ", "  _|__________|_/_____\\_  " };
    static final String[] TEMPLO = {
        "        _/\\_         ", "      _/ || \\_       ", "     /___||___\\      ",
        "     |  |  |  |      ", "     |  |  |  |      ", "    _|__|__|__|_     " };
    static final String[] ALQUIMIA = {
        "      __     __        ", "     (  )   (  )       ", "    /    \\ /    \\      ",
        "   | ~~~~ | ~~~~ |     ", "    \\______/\\______/    " };
    static final String[] FONTE = {
        "        .  *  .       ", "      .-'~~~~~'-.     ", "     (  ~ ~~~ ~  )    ",
        "      '-._____.-'     ", "       ~~ | | ~~      ", "     ____/___\\____    " };
    static final String[] ARMADILHA = {
        "      /\\ /\\ /\\ /\\     ", "     /  V  V  V  \\    ", "    ^^^^^^^^^^^^^^^   ", "   ################   " };
    static final String[] BAU = {
        "       _.--------._   ", "      /  ______    \\  ", "     |__|______|___|  ",
        "     |    [  ]    |   ", "     |____________|   " };
    static final String[] FOGUEIRA = {
        "          )  (        ", "         (    )  )    ", "          ) /(  (     ",
        "        .-'---'-.     ", "     ___\\_/\\_/\\_/___  " };
    static final String[] CAVEIRA = {
        "        _______       ", "     .-'       '-.    ", "    /   _     _   \\   ",
        "   |   (_)   (_)   |  ", "    \\      ^      /   ", "     '-.  VVV  .-'    ", "        '-----'       " };
    static final String[] TROFEU = {
        "      ___________      ", "     '._==_==_=_.'     ", "     .-\\:      /-.     ",
        "    | (|:.     |) |    ", "     '-|:.     |-'     ", "       \\::.    /       ",
        "        '::. .'        ", "          ) (          ", "        _.' '._        ", "       '-------'       " };
    static final String[] DADOS = {
        "   .-------.    .-------.   ", "   | o   o |    | o     |   ", "   |   o   |    |   o   |   ",
        "   | o   o |    |     o |   ", "   '-------'    '-------'   " };
    static final String[] NOITE = {
        "     .  *   _    .   *   ", "  *      .-'  '-.   .    ", "   .    (   ☾    )    *  ",
        "         '-.___.-'         " };
    static final String[] PERGAMINHO = {
        "   .-----------------.   ", "  (  MISSAO            )  ", "   |  ~~~~~~~~~~~~~~~  |   ",
        "   |  ~~~~~~~~~~~~     |   ", "  (___________________)  " };
}

final class Efeitos {
    static final String[] CORTE = { "                 //   ", "              ///     ", "          ////  CORTE!", "       ///            " };
    static final String[] LUZ = { "        \\  |  /       ", "      -- ( * ) --     ", "        /  |  \\   LUZ!" };
    static final String[] SOMBRA = { "      ~ ~ ~ ~ ~       ", "     ~ (  @  ) ~   SOMBRA!", "      ~ ~ ~ ~ ~       " };
    static final String[] IMPACTO = { "       \\ | /          ", "      -- BAM! --      ", "       / | \\          " };
    static final String[] FOGO = { "      (  )   (   )    ", "     )  (  ) )  (  FOGO!", "    (  ) ( (  ) )     " };
    static final String[] RAIO = { "         /\\           ", "        /  \\  /\\      ", "       /___ \\/  \\  RAIO!", "           \\/         " };
    static final String[] FLECHA = { "   >>>--------->  *      ", "   >>>--------->    FLECHA!" };
    static final String[] VENENO = { "      .  o  .  O       ", "     o  VENENO  o      ", "      O  .  o  .       " };
    static final String[] GELO = { "      *  *  *  *       ", "     * PRISAO  * *     ", "      *  *  *  *       " };
    static final String[] ESCUDO = { "      .-------.      ", "     /  [###]  \\     ", "    |   [###]   |    ", "     \\   \\ /   /     ", "      '-._V_.-'      " };

    static void tocar(String[] arte, String cor) {
        Tela.mostrarArte(arte, cor);
        Tela.esperar(450);
    }

    static String[] doElemento(Elemento e) {
        switch (e) {
            case LUZ: return LUZ;
            case SOMBRA: return SOMBRA;
            case FOGO: return FOGO;
            case RAIO: return RAIO;
            default: return CORTE;
        }
    }

    static String corDoElemento(Elemento e) {
        switch (e) {
            case LUZ: return Cor.AMARELO;
            case SOMBRA: return Cor.MAGENTA;
            case FOGO: return Cor.VERMELHO;
            case RAIO: return Cor.CIANO;
            default: return Cor.BRANCO;
        }
    }
}

// =========================================================================
// PARTE 2 - EFEITOS DE STATUS, ITENS, EQUIPAMENTOS E FÁBRICA
// =========================================================================
enum TipoEfeito {
    VENENO("☠️ Veneno"), QUEIMADURA("🔥 Queimadura"), SANGRAMENTO("🩸 Sangramento"),
    ATORDOADO("💫 Atordoado"), ESCUDO("🛡️ Escudo"), LENTIDAO("🐌 Lentidão"), FURIA("💪 Fúria");
    final String rotulo;
    TipoEfeito(String r) { rotulo = r; }
}

class Efeito {
    final TipoEfeito tipo;
    int turnos;
    int valor;

    Efeito(TipoEfeito tipo, int turnos, int valor) { this.tipo = tipo; this.turnos = turnos; this.valor = valor; }
    Efeito copia() { return new Efeito(tipo, turnos, valor); }
    boolean negativo() { return tipo != TipoEfeito.ESCUDO && tipo != TipoEfeito.FURIA; }
}

abstract class Item {
    private final String nome, raridade, descricao, categoria;
    private final int preco;

    Item(String nome, String raridade, String categoria, String descricao, int preco) {
        this.nome = nome; this.raridade = raridade; this.categoria = categoria;
        this.descricao = descricao; this.preco = preco;
    }

    String getNome() { return nome; }
    String getRaridade() { return raridade; }
    String getCategoria() { return categoria; }
    String getDescricao() { return descricao; }
    int getPreco() { return preco; }
    int getPrecoVenda() { return preco / 2; }

    String raridadeColorida() {
        switch (raridade) {
            case "Raro": return Cor.c(Cor.AZUL, raridade);
            case "Épico": return Cor.c(Cor.MAGENTA, raridade);
            case "Lendário": return Cor.c(Cor.AMARELO, raridade);
            case "Comum": return Cor.c(Cor.CINZA, raridade);
            default: return Cor.c(Cor.VERDE, raridade);
        }
    }

    boolean usavelEmCombate() { return false; }
    boolean usavelFora() { return false; }

    /** Retorna true se o item foi consumido. alvo pode ser null fora de combate. */
    boolean usar(Heroi h, Monstro alvo) { return false; }

    String linha() { return getNome() + " [" + raridadeColorida() + "] - " + getDescricao(); }
}

class Pocao extends Item {
    Pocao() { super("Poção de Cura", "Incomum", "Consumível", "Restaura 60 HP", 30); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usavelFora() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        if (h.getHp() >= h.getHpMax()) { System.out.println("Seu HP já está cheio."); return false; }
        Tela.mostrarArte(new String[] { "      _____      ", "     [_____]     ", "      |~~~|      ",
            "     /~~~~~\\     ", "    |~~~~~~~|    ", "     \\_____/     " }, Cor.VERMELHO);
        System.out.println("🧪 " + h.getNome() + " bebeu a Poção e sentiu as feridas fecharem! (+60 HP)");
        h.curar(60);
        return true;
    }
}

class Elixir extends Item {
    Elixir() { super("Elixir de Mana", "Raro", "Consumível", "Restaura 40 de Mana", 40); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usavelFora() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        if (h.getMana() >= h.getManaMax()) { System.out.println("Sua mana já está cheia."); return false; }
        Tela.mostrarArte(new String[] { "       _|_       ", "      (   )      ", "     /  *  \\     ",
            "    |  ***  |    ", "     \\_____/     " }, Cor.AZUL);
        System.out.println("🧪 " + h.getNome() + " bebeu o Elixir! (+40 Mana)");
        h.modificarMana(40);
        return true;
    }
}

class Antidoto extends Item {
    Antidoto() { super("Antídoto", "Comum", "Consumível", "Remove veneno, queimadura e sangramento", 25); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usavelFora() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        boolean limpou = h.removerNegativos();
        if (!limpou) { System.out.println("Você não está sofrendo de nenhum mal."); return false; }
        System.out.println("💚 " + h.getNome() + " tomou o Antídoto e se sente purificado!");
        return true;
    }
}

class Ensopado extends Item {
    Ensopado() { super("Ensopado Quente", "Comum", "Consumível", "Restaura 40 HP e 10 Mana (só fora de combate)", 20); }
    @Override boolean usavelFora() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        System.out.println("🍲 O ensopado aquece o corpo. (+40 HP, +10 Mana)");
        h.curar(40); h.modificarMana(10);
        return true;
    }
}

class BombaFogo extends Item {
    BombaFogo() { super("Bomba de Fogo", "Épico", "Consumível", "70 de dano de Fogo + queimadura", 60); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        System.out.println("💣 " + h.getNome() + " arremessa a Bomba de Fogo!");
        Efeitos.tocar(Efeitos.FOGO, Cor.VERMELHO);
        alvo.receberDano(70, Elemento.FOGO, false);
        alvo.adicionarEfeito(new Efeito(TipoEfeito.QUEIMADURA, 3, 8));
        return true;
    }
}

class PergaminhoRaio extends Item {
    PergaminhoRaio() { super("Pergaminho do Trovão", "Raro", "Consumível", "45 de dano de Raio + atordoa 1 turno", 55); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        System.out.println("📜 " + h.getNome() + " lê o Pergaminho do Trovão!");
        Efeitos.tocar(Efeitos.RAIO, Cor.CIANO);
        alvo.receberDano(45, Elemento.RAIO, false);
        alvo.adicionarEfeito(new Efeito(TipoEfeito.ATORDOADO, 1, 0));
        return true;
    }
}

class Material extends Item {
    Material(String nome, int preco, String desc) { super(nome, "Comum", "Material", desc, preco); }
}

class ItemMissao extends Item {
    ItemMissao(String nome, String desc) { super(nome, "Lendário", "Missão", desc, 0); }
}

enum Slot {
    ARMA("Arma"), ARMADURA("Armadura"), AMULETO("Amuleto");
    final String nome;
    Slot(String n) { nome = n; }
}

class Equipamento extends Item {
    final Slot slot;
    final int atk, def, agi;
    final Elemento elem;
    int melhoria = 0;

    Equipamento(String nome, String raridade, int preco, Slot slot, int atk, int def, int agi, Elemento elem) {
        super(nome, raridade, "Equipamento", "", preco);
        this.slot = slot; this.atk = atk; this.def = def; this.agi = agi; this.elem = elem;
    }

    int getAtk() { return atk + (slot == Slot.ARMA ? melhoria * 2 : 0); }
    int getDef() { return def + (slot == Slot.ARMADURA ? melhoria : 0); }
    int getAgi() { return agi; }

    @Override String getDescricao() {
        StringBuilder sb = new StringBuilder(slot.nome + ":");
        if (getAtk() != 0) sb.append(" ATK+").append(getAtk());
        if (getDef() != 0) sb.append(" DEF+").append(getDef());
        if (getAgi() != 0) sb.append(" AGI+").append(getAgi());
        if (elem != Elemento.FISICO) sb.append(" ").append(elem.icone).append(elem.nome);
        return sb.toString();
    }

    @Override String getNome() { return super.getNome() + (melhoria > 0 ? " +" + melhoria : ""); }
    String getNomeBase() { return super.getNome(); }
    @Override int getPreco() { return super.getPreco() + melhoria * 40; }
    @Override boolean usavelFora() { return true; }
}

final class Fabrica {
    static final String[] CONSUMIVEIS = { "Poção de Cura", "Elixir de Mana", "Antídoto", "Ensopado Quente",
        "Bomba de Fogo", "Pergaminho do Trovão" };
    static final String[] EQUIPAMENTOS_LOJA = { "Lâmina de Aço", "Adaga Sombria", "Lança do Trovão", "Machado Flamejante",
        "Gibão de Couro", "Cota de Malha", "Placa Rúnica", "Anel Veloz", "Amuleto Vital" };

    static Item criar(String nome) {
        switch (nome) {
            case "Poção de Cura": return new Pocao();
            case "Elixir de Mana": return new Elixir();
            case "Antídoto": return new Antidoto();
            case "Ensopado Quente": return new Ensopado();
            case "Bomba de Fogo": return new BombaFogo();
            case "Pergaminho do Trovão": return new PergaminhoRaio();
            case "Erva Lunar": return new Material("Erva Lunar", 8, "Erva medicinal que brilha ao luar");
            case "Presa de Aranha": return new Material("Presa de Aranha", 12, "Presa repleta de veneno");
            case "Pedra de Fogo": return new Material("Pedra de Fogo", 15, "Pedra que arde sem se consumir");
            case "Essência Arcana": return new Material("Essência Arcana", 20, "Magia condensada em cristal");
            case "Gosma": return new Material("Gosma", 5, "Gosma viscosa de slime");
            case "Chave do Dragão": return new ItemMissao("Chave do Dragão", "Abre o Covil de Ignis");
            // Armas
            case "Espada Enferrujada": return new Equipamento(nome, "Comum", 15, Slot.ARMA, 3, 0, 0, Elemento.FISICO);
            case "Lâmina de Aço": return new Equipamento(nome, "Incomum", 120, Slot.ARMA, 7, 0, 0, Elemento.FISICO);
            case "Adaga Sombria": return new Equipamento(nome, "Raro", 220, Slot.ARMA, 8, 0, 4, Elemento.SOMBRA);
            case "Lança do Trovão": return new Equipamento(nome, "Raro", 260, Slot.ARMA, 10, 0, 0, Elemento.RAIO);
            case "Machado Flamejante": return new Equipamento(nome, "Épico", 380, Slot.ARMA, 14, 0, -2, Elemento.FOGO);
            case "Cajado Radiante": return new Equipamento(nome, "Épico", 400, Slot.ARMA, 12, 2, 0, Elemento.LUZ);
            // Armaduras
            case "Gibão de Couro": return new Equipamento(nome, "Comum", 60, Slot.ARMADURA, 0, 4, 1, Elemento.FISICO);
            case "Cota de Malha": return new Equipamento(nome, "Incomum", 180, Slot.ARMADURA, 0, 8, 0, Elemento.FISICO);
            case "Placa Rúnica": return new Equipamento(nome, "Épico", 420, Slot.ARMADURA, 0, 14, -2, Elemento.FISICO);
            // Amuletos
            case "Anel Veloz": return new Equipamento(nome, "Incomum", 140, Slot.AMULETO, 0, 0, 7, Elemento.FISICO);
            case "Amuleto Vital": return new Equipamento(nome, "Raro", 200, Slot.AMULETO, 4, 4, 0, Elemento.FISICO);
            default: return null;
        }
    }

    /** Receitas de alquimia: nome do resultado -> ingredientes. */
    static final String[][] RECEITAS = {
        { "Poção de Cura", "Erva Lunar", "Erva Lunar" },
        { "Antídoto", "Presa de Aranha", "Erva Lunar" },
        { "Elixir de Mana", "Essência Arcana", "Essência Arcana" },
        { "Bomba de Fogo", "Pedra de Fogo", "Pedra de Fogo" },
        { "Pergaminho do Trovão", "Essência Arcana", "Pedra de Fogo" },
        { "Ensopado Quente", "Erva Lunar", "Gosma" } };
}

// =========================================================================
// PARTE 3 - HABILIDADES, ENTIDADE BASE, HERÓIS E ALIADOS
// =========================================================================
class Habilidade {
    final String nome, desc;
    final int custo, recarga;
    final Elemento elem;
    final double mult;
    Efeito efeitoAlvo, efeitoSelf;
    int cura = 0;
    boolean rouba = false;

    Habilidade(String nome, String desc, int custo, int recarga, Elemento elem, double mult) {
        this.nome = nome; this.desc = desc; this.custo = custo; this.recarga = recarga;
        this.elem = elem; this.mult = mult;
    }
    Habilidade alvo(Efeito e) { efeitoAlvo = e; return this; }
    Habilidade self(Efeito e) { efeitoSelf = e; return this; }
    Habilidade cura(int c) { cura = c; return this; }
    Habilidade rouba() { rouba = true; return this; }
}

abstract class Entidade {
    protected static final Random RNG = new Random();
    private final String nome;
    protected int hp, hpMax, mana, manaMax, atk, def, agi;
    protected int nivel = 1;
    protected boolean defendendo = false;
    protected final ArrayList<Efeito> efeitos = new ArrayList<>();
    protected final EnumMap<Elemento, Double> afinidade = new EnumMap<>(Elemento.class);

    Entidade(String nome, int hpMax, int manaMax, int atk, int def, int agi) {
        this.nome = nome;
        this.hpMax = hpMax; this.hp = hpMax;
        this.manaMax = manaMax; this.mana = manaMax;
        this.atk = atk; this.def = def; this.agi = agi;
    }

    protected int bonusAtk() { return 0; }
    protected int bonusDef() { return 0; }
    protected int bonusAgi() { return 0; }

    String getNome() { return nome; }
    int getHp() { return hp; }
    int getHpMax() { return hpMax; }
    int getMana() { return mana; }
    int getManaMax() { return manaMax; }
    int getNivel() { return nivel; }
    boolean estaVivo() { return hp > 0; }
    void setDefendendo(boolean d) { defendendo = d; }
    boolean isDefendendo() { return defendendo; }
    int getAtaque() { return atk + bonusAtk() + (temEfeito(TipoEfeito.FURIA) ? 10 : 0); }
    int getDefesa() { return def + bonusDef(); }
    int getAgilidade() {
        int a = Math.max(1, agi + bonusAgi());
        return temEfeito(TipoEfeito.LENTIDAO) ? Math.max(1, a / 2) : a;
    }

    void curar(int q) { hp = Math.max(0, Math.min(hpMax, hp + q)); }
    void modificarMana(int q) { mana = Math.max(0, Math.min(manaMax, mana + q)); }
    void perderHp(int q) { hp = Math.max(0, hp - q); }

    boolean temEfeito(TipoEfeito t) { return obterEfeito(t) != null; }
    Efeito obterEfeito(TipoEfeito t) {
        for (Efeito e : efeitos) if (e.tipo == t) return e;
        return null;
    }

    void adicionarEfeito(Efeito novo) {
        Efeito antigo = obterEfeito(novo.tipo);
        if (antigo != null) efeitos.remove(antigo);
        efeitos.add(novo.copia());
        System.out.println(Cor.c(novo.negativo() ? Cor.MAGENTA : Cor.CIANO,
            "   ➜ " + nome + " recebeu: " + novo.tipo.rotulo + " (" + novo.turnos + " turnos)"));
    }

    boolean removerNegativos() {
        boolean tirou = false;
        Iterator<Efeito> it = efeitos.iterator();
        while (it.hasNext()) {
            Efeito e = it.next();
            if (e.negativo() && e.tipo != TipoEfeito.ATORDOADO) { it.remove(); tirou = true; }
        }
        return tirou;
    }

    void limparEfeitos() { efeitos.clear(); defendendo = false; }

    String resumoEfeitos() {
        if (efeitos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Efeito e : efeitos) sb.append(" ").append(e.tipo.rotulo).append("(").append(e.turnos).append(")");
        return sb.toString();
    }

    /** Processa efeitos no início do turno. Retorna true se a entidade pode agir. */
    boolean iniciarTurno() {
        boolean atordoado = false;
        Iterator<Efeito> it = efeitos.iterator();
        while (it.hasNext()) {
            Efeito e = it.next();
            switch (e.tipo) {
                case VENENO: case QUEIMADURA: case SANGRAMENTO:
                    perderHp(e.valor);
                    System.out.println(Cor.c(Cor.MAGENTA, "   " + e.tipo.rotulo + ": " + nome + " perde " + e.valor + " HP!"));
                    break;
                case ATORDOADO: atordoado = true; break;
                default: break;
            }
            e.turnos--;
            if (e.turnos <= 0 || (e.tipo == TipoEfeito.ESCUDO && e.valor <= 0)) {
                it.remove();
                System.out.println(Cor.c(Cor.CINZA, "   (" + e.tipo.rotulo + " de " + nome + " acabou)"));
            }
        }
        return estaVivo() && !atordoado;
    }

    /** Aplica dano considerando esquiva, afinidade elemental, defesa, guarda e escudo. */
    int receberDano(int bruto, Elemento elem, boolean podeEsquivar) {
        if (podeEsquivar && RNG.nextInt(100) < Math.min(30, getAgilidade() / 2)) {
            System.out.println(Cor.c(Cor.CIANO, "💨 " + nome + " esquivou do ataque!"));
            return 0;
        }
        double m = afinidade.getOrDefault(elem, 1.0);
        int dano = (int) (bruto * m) - getDefesa();
        if (m > 1.0) System.out.println(Cor.c(Cor.AMARELO, "🎯 Fraqueza a " + elem.nome + "! Dano aumentado!"));
        if (m < 1.0) System.out.println(Cor.c(Cor.CINZA, "🧱 " + nome + " resiste a " + elem.nome + "."));
        if (defendendo) {
            dano /= 2;
            System.out.println("🛡️  " + nome + " está em guarda e reduz o impacto!");
        }
        dano = Math.max(1, dano);
        Efeito esc = obterEfeito(TipoEfeito.ESCUDO);
        if (esc != null && esc.valor > 0) {
            int absorvido = Math.min(dano, esc.valor);
            esc.valor -= absorvido;
            dano -= absorvido;
            System.out.println(Cor.c(Cor.CIANO, "🛡️  O escudo absorveu " + absorvido + " de dano!"));
            if (dano == 0) return 0;
        }
        perderHp(dano);
        System.out.println(Cor.c(Cor.VERMELHO, "💥 " + nome + " sofreu " + dano + " de dano!"));
        return dano;
    }

    int calcularAtaque(double mult) {
        int base = (int) ((getAtaque() + RNG.nextInt(7) - 3) * mult);
        if (RNG.nextInt(100) < 8 + getAgilidade() / 2) {
            System.out.println(Cor.c(Cor.AMARELO + Cor.NEGRITO, "⭐ ACERTO CRÍTICO!"));
            base = (int) (base * 1.5);
        }
        return Math.max(1, base);
    }
}

// =========================================================================
// HERÓI BASE (INVENTÁRIO, EQUIPAMENTOS, NÍVEIS E TALENTOS)
// =========================================================================
abstract class Heroi extends Entidade {
    protected int xp = 0, ouro = 60, honra = 0;
    final ArrayList<Item> inv = new ArrayList<>();
    final Equipamento[] equip = new Equipamento[3];
    final Map<String, Integer> recargas = new HashMap<>();
    Aliado aliado = null;
    int abates = 0;

    Heroi(String nome, int hp, int mana, int atk, int def, int agi) { super(nome, hp, mana, atk, def, agi); }

    abstract String getClasse();
    abstract String[] sprite();
    abstract String cor();
    abstract List<Habilidade> habilidades();

    @Override protected int bonusAtk() { int s = 0; for (Equipamento e : equip) if (e != null) s += e.getAtk(); return s; }
    @Override protected int bonusDef() { int s = 0; for (Equipamento e : equip) if (e != null) s += e.getDef(); return s; }
    @Override protected int bonusAgi() { int s = 0; for (Equipamento e : equip) if (e != null) s += e.getAgi(); return s; }

    Elemento elementoArma() { return equip[0] != null ? equip[0].elem : Elemento.FISICO; }
    int getOuro() { return ouro; }
    void ganharOuro(int q) { ouro += q; }
    boolean gastarOuro(int q) { if (ouro < q) return false; ouro -= q; return true; }
    int getXp() { return xp; }
    int getHonra() { return honra; }
    void mudarHonra(int d) { honra = Math.max(-5, Math.min(5, honra + d)); }
    int xpProximo() { return nivel * 100; }

    String titulo() {
        if (honra >= 4) return "o Lendário Justo";
        if (honra >= 2) return "o Honrado";
        if (honra <= -4) return "o Temido";
        if (honra <= -2) return "o Oportunista";
        return "o Viajante";
    }

    void atacar(Monstro alvo) {
        System.out.println("\n⚔️  " + getNome() + " ataca com " + (equip[0] != null ? equip[0].getNome() : "os punhos") + "!");
        Elemento el = elementoArma();
        Efeitos.tocar(Efeitos.doElemento(el), Efeitos.corDoElemento(el));
        alvo.receberDano(calcularAtaque(1.0), el, true);
    }

    boolean podeUsar(Habilidade h) { return mana >= h.custo && recargas.getOrDefault(h.nome, 0) == 0; }

    void usarHabilidade(Habilidade h, Monstro alvo) {
        modificarMana(-h.custo);
        if (h.recarga > 0) recargas.put(h.nome, h.recarga + 1);
        System.out.println("\n✨ " + getNome() + " usa " + Cor.c(Cor.NEGRITO, h.nome.toUpperCase()) + "!");
        if (h.mult > 0) {
            Efeitos.tocar(Efeitos.doElemento(h.elem), Efeitos.corDoElemento(h.elem));
            int dano = alvo.receberDano(calcularAtaque(h.mult), h.elem, true);
            if (h.rouba && dano > 0) {
                System.out.println(Cor.c(Cor.VERDE, "🩸 Drenando essência... você recupera " + (dano / 2) + " HP!"));
                curar(dano / 2);
            }
            if (h.efeitoAlvo != null && alvo.estaVivo()) alvo.adicionarEfeito(h.efeitoAlvo);
        } else if (h.efeitoAlvo != null) {
            Efeitos.tocar(Efeitos.GELO, Cor.CIANO);
            alvo.adicionarEfeito(h.efeitoAlvo);
        }
        if (h.cura > 0) { System.out.println(Cor.c(Cor.VERDE, "💚 Você recupera " + h.cura + " HP!")); curar(h.cura); }
        if (h.efeitoSelf != null) {
            if (h.efeitoSelf.tipo == TipoEfeito.ESCUDO) Tela.mostrarArte(Efeitos.ESCUDO, Cor.CIANO);
            adicionarEfeito(h.efeitoSelf);
        }
    }

    void tickRecargas() {
        for (Map.Entry<String, Integer> e : recargas.entrySet()) if (e.getValue() > 0) e.setValue(e.getValue() - 1);
    }

    int contar(String nomeItem) {
        int n = 0;
        for (Item i : inv) if (i.getNome().equals(nomeItem)) n++;
        return n;
    }

    boolean consumir(String nomeItem, int qtd) {
        if (contar(nomeItem) < qtd) return false;
        for (int k = 0; k < qtd; k++) {
            for (int i = 0; i < inv.size(); i++) if (inv.get(i).getNome().equals(nomeItem)) { inv.remove(i); break; }
        }
        return true;
    }

    void equipar(Equipamento e) {
        int idx = e.slot.ordinal();
        Equipamento antigo = equip[idx];
        inv.remove(e);
        equip[idx] = e;
        if (antigo != null) inv.add(antigo);
        System.out.println("🎽 " + getNome() + " equipou " + e.getNome() + ".");
    }

    void restaurarTotal() { hp = hpMax; mana = manaMax; limparEfeitos(); recargas.clear(); }

    /** Ganha XP e sobe de nível com escolha de talento. Retorna níveis ganhos. */
    int ganharXp(int ganho) {
        xp += ganho;
        System.out.println(Cor.c(Cor.MAGENTA, "✨ +" + ganho + " XP."));
        int subidas = 0;
        while (xp >= xpProximo() && nivel < 15) {
            xp -= xpProximo();
            nivel++;
            hpMax += 25; manaMax += 8; atk += 4; def += 2; agi += 1;
            hp = hpMax; mana = manaMax;
            subidas++;
            Tela.mostrarArte(new String[] { "        /\\        ", "       /  \\       ", "      / UP \\      ",
                "     /______\\     ", "        ||        " }, Cor.VERDE);
            System.out.println(Cor.c(Cor.VERDE + Cor.NEGRITO, "🆙 NÍVEL " + nivel + "! HP/Mana restaurados."));
            escolherTalento();
        }
        if (nivel >= 15) xp = 0;
        return subidas;
    }

    void escolherTalento() {
        Tela.caixa("ESCOLHA UM TALENTO", "1. Vigor     (+30 HP máximo)", "2. Poder     (+6 Ataque)",
            "3. Arcano    (+25 Mana máxima)", "4. Reflexos  (+4 Agilidade, +2 Defesa)");
        System.out.print("Talento: ");
        switch (Tela.ler(1, 4)) {
            case 1: hpMax += 30; hp = hpMax; break;
            case 2: atk += 6; break;
            case 3: manaMax += 25; mana = manaMax; break;
            default: agi += 4; def += 2;
        }
        System.out.println(Cor.c(Cor.VERDE, "   Talento aprendido!"));
    }

    String ficha() {
        return getNome() + " " + titulo() + " — " + getClasse() + " Nv " + nivel;
    }
}

class Paladino extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Julgamento da Luz", "Dano de Luz x2.0 e cura 30 HP", 20, 0, Elemento.LUZ, 2.0).cura(30),
        new Habilidade("Escudo Divino", "Escudo de 45 por 3 turnos", 15, 3, Elemento.LUZ, 0)
            .self(new Efeito(TipoEfeito.ESCUDO, 3, 45)),
        new Habilidade("Martelo Sagrado", "Dano de Luz x1.5 e atordoa", 25, 3, Elemento.LUZ, 1.5)
            .alvo(new Efeito(TipoEfeito.ATORDOADO, 1, 0)));
    Paladino(String n) { super(n, 160, 50, 30, 15, 8); }
    @Override String getClasse() { return "Paladino"; }
    @Override String cor() { return Cor.CIANO; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override String[] sprite() {
        return new String[] { "      .-^-.        ^", "     ( o_o )       |", "    /|#####|\\     -+-",
            "   [ |#####| ]======|", "      |_| |_|      |", "      /_/ \\_\\      v" };
    }
}

class Necromante extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Sifão da Alma", "Sombra x1.4 e rouba metade do dano", 30, 0, Elemento.SOMBRA, 1.4).rouba(),
        new Habilidade("Praga Mortal", "Veneno de 12 por 4 turnos", 20, 2, Elemento.SOMBRA, 0.6)
            .alvo(new Efeito(TipoEfeito.VENENO, 4, 12)),
        new Habilidade("Explosão Necrótica", "Sombra x3.0 (devastador)", 50, 4, Elemento.SOMBRA, 3.0));
    Necromante(String n) { super(n, 100, 100, 42, 5, 10); }
    @Override String getClasse() { return "Necromante"; }
    @Override String cor() { return Cor.MAGENTA; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override String[] sprite() {
        return new String[] { "      .-~~-.        @", "     / v  v \\       |", "    ~|  /\\  |~      |",
            "   ~~|  ||  |~~     |", "     |  XX  |       |", "    /___||___\\      |" };
    }
}

class Arqueiro extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Flecha Precisa", "Físico x1.8", 10, 0, Elemento.FISICO, 1.8),
        new Habilidade("Flecha Flamejante", "Fogo x1.4 e queima", 20, 1, Elemento.FOGO, 1.4)
            .alvo(new Efeito(TipoEfeito.QUEIMADURA, 3, 9)),
        new Habilidade("Chuva de Flechas", "Físico x2.6 e sangramento", 30, 3, Elemento.FISICO, 2.6)
            .alvo(new Efeito(TipoEfeito.SANGRAMENTO, 3, 8)));
    Arqueiro(String n) { super(n, 120, 60, 36, 8, 16); }
    @Override String getClasse() { return "Arqueiro"; }
    @Override String cor() { return Cor.VERDE; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override void atacar(Monstro alvo) {
        System.out.println("\n🏹 " + getNome() + " dispara uma flecha!");
        Efeitos.tocar(Efeitos.FLECHA, Cor.VERDE);
        alvo.receberDano(calcularAtaque(1.0), elementoArma(), true);
    }
    @Override String[] sprite() {
        return new String[] { "      .-^-.      /|", "     ( -_- )    / |", "    /|=====|\\  (  |",
            "   / |  |  | \\  \\ |", "     |_/ \\_|    \\|", "     /_/ \\_\\       " };
    }
}

class Mago extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Bola de Fogo", "Fogo x2.0 e queima", 25, 0, Elemento.FOGO, 2.0)
            .alvo(new Efeito(TipoEfeito.QUEIMADURA, 3, 10)),
        new Habilidade("Relâmpago", "Raio x1.8", 20, 0, Elemento.RAIO, 1.8),
        new Habilidade("Prisão Arcana", "Atordoa por 2 turnos", 35, 5, Elemento.RAIO, 0)
            .alvo(new Efeito(TipoEfeito.ATORDOADO, 2, 0)));
    Mago(String n) { super(n, 90, 120, 40, 4, 9); }
    @Override String getClasse() { return "Mago"; }
    @Override String cor() { return Cor.AZUL; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override String[] sprite() {
        return new String[] { "        /\\         *", "       /__\\       -|-", "      ( ^_^ )      |",
            "     /|~~~~~|\\     |", "    / |~~~~~| \\    |", "      /_/ \\_\\          " };
    }
}

class Ladino extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Golpe Furtivo", "Físico x2.2", 10, 2, Elemento.FISICO, 2.2),
        new Habilidade("Lâmina Envenenada", "Físico x1.0 e veneno", 15, 1, Elemento.FISICO, 1.0)
            .alvo(new Efeito(TipoEfeito.VENENO, 4, 10)),
        new Habilidade("Dança das Lâminas", "Físico x2.4 e sangramento", 25, 3, Elemento.FISICO, 2.4)
            .alvo(new Efeito(TipoEfeito.SANGRAMENTO, 3, 10)));
    Ladino(String n) { super(n, 110, 60, 35, 7, 20); }
    @Override String getClasse() { return "Ladino"; }
    @Override String cor() { return Cor.AMARELO; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override String[] sprite() {
        return new String[] { "      .---.       /", "     ( ò_ó )     /", "    /|=====|\\   /",
            "   /_|     |_\\ /", "     |_/ \\_|       ", "     /_/ \\_\\       " };
    }
}

// =========================================================================
// ALIADOS (AGEM A CADA TURNO DO HERÓI)
// =========================================================================
abstract class Aliado {
    final String nome;
    final int preco;
    Aliado(String nome, int preco) { this.nome = nome; this.preco = preco; }
    abstract String[] sprite();
    abstract String descricao();
    abstract void agir(Heroi h, Monstro m);
}

class LoboCinzento extends Aliado {
    LoboCinzento() { super("Fenrir, o Lobo Cinzento", 150); }
    @Override String descricao() { return "Morde o inimigo todo turno e causa sangramento"; }
    @Override String[] sprite() {
        return new String[] { "    /\\_/\\     ", "   ( o.o )~~  ", "    > ^ <     ", "   /|   |\\    " };
    }
    @Override void agir(Heroi h, Monstro m) {
        System.out.println("\n🐺 " + nome + " ataca o inimigo!");
        m.receberDano(18 + h.getNivel() * 3, Elemento.FISICO, false);
        if (Entidade.RNG.nextInt(100) < 35 && m.estaVivo()) m.adicionarEfeito(new Efeito(TipoEfeito.SANGRAMENTO, 3, 7));
    }
}

class IrmaLira extends Aliado {
    IrmaLira() { super("Lira, a Curandeira", 200); }
    @Override String descricao() { return "Cura o herói e remove males de vez em quando"; }
    @Override String[] sprite() {
        return new String[] { "     .-.      ", "    (o.o)  +  ", "    /|+|\\     ", "     / \\      " };
    }
    @Override void agir(Heroi h, Monstro m) {
        if (h.getHp() < h.getHpMax() * 0.7 || Entidade.RNG.nextBoolean()) {
            System.out.println("\n💖 " + nome + " conjura uma bênção de cura!");
            h.curar(20 + h.getNivel() * 3);
            if (Entidade.RNG.nextInt(100) < 40) h.removerNegativos();
        } else {
            System.out.println("\n🙏 " + nome + " reza por você.");
            h.modificarMana(8);
        }
    }
}

// =========================================================================
// PARTE 4 - MONSTROS, CHEFES E BESTIÁRIO
// =========================================================================
abstract class Monstro extends Entidade {
    final int nivelBase;
    int xpRec, ouroRec;
    final boolean chefe;
    final String dropNome;
    final int dropChance;
    protected boolean carregando = false, fase2 = false;

    Monstro(String nome, int hp, int atk, int def, int agi, int nivelBase, int xp, int ouro,
            boolean chefe, String drop, int dropChance) {
        super(nome, hp, 0, atk, def, agi);
        this.nivelBase = nivelBase; this.xpRec = xp; this.ouroRec = ouro;
        this.chefe = chefe; this.dropNome = drop; this.dropChance = dropChance;
    }

    abstract String[] sprite();
    abstract String cor();
    abstract void ataqueBasico(Heroi h);
    abstract void especial(Heroi h);
    void golpePesado(Heroi h) { especial(h); }
    String aviso() { return getNome() + " está reunindo energia..."; }
    void entrarFase2() { atk += 8; }

    /** Escala atributos conforme o nível do herói (e a noite). */
    void escalar(double f) {
        hpMax = (int) (hpMax * f); hp = hpMax;
        atk = (int) (atk * f);
        xpRec = (int) (xpRec * f);
        ouroRec = (int) (ouroRec * f);
    }

    void turno(Heroi h) {
        if (chefe && !fase2 && hp < hpMax / 2) {
            fase2 = true;
            System.out.println(Cor.c(Cor.VERMELHO + Cor.NEGRITO, "\n⚠️  " + getNome() + " entra em FÚRIA! Seu poder aumenta!"));
            entrarFase2();
            adicionarEfeito(new Efeito(TipoEfeito.FURIA, 99, 0));
        }
        if (carregando) { carregando = false; golpePesado(h); return; }
        if (chefe && RNG.nextInt(100) < 30) {
            carregando = true;
            System.out.println(Cor.c(Cor.AMARELO + Cor.NEGRITO, "\n⚠️  " + aviso() + " (DEFENDA-SE!)"));
            return;
        }
        if (RNG.nextInt(100) < 30) especial(h); else ataqueBasico(h);
    }

    String estadoCarga() { return carregando ? Cor.c(Cor.AMARELO + Cor.NEGRITO, " ⚠️ CARREGANDO GOLPE!") : ""; }
}

class Slime extends Monstro {
    Slime() {
        super("Gosma Ácida", 60, 16, 3, 4, 1, 40, 12, false, "Gosma", 60);
        afinidade.put(Elemento.FOGO, 1.5); afinidade.put(Elemento.FISICO, 0.75);
    }
    @Override String[] sprite() {
        return new String[] { "                ", "    .----.      ", "   ( o  o )     ",
            "  (   ~~   )    ", " (__________)   ", "  ~~~~~~~~~~    " };
    }
    @Override String cor() { return Cor.VERDE; }
    @Override void ataqueBasico(Heroi h) {
        System.out.println("\n🟢 A Gosma se arremessa contra você!");
        Efeitos.tocar(Efeitos.IMPACTO, Cor.VERDE);
        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        System.out.println("\n🧪 A Gosma cospe ácido corrosivo!");
        h.receberDano(calcularAtaque(1.0) + 8, Elemento.SOMBRA, true);
        h.modificarMana(-10);
    }
}

class Lobo extends Monstro {
    Lobo() {
        super("Lobo Selvagem", 70, 22, 4, 18, 1, 50, 15, false, null, 0);
        afinidade.put(Elemento.FOGO, 1.5);
    }
    @Override String[] sprite() {
        return new String[] { "    /\\___/\\       ", "   ( o   o )__    ", "   (  =^=  )  \\   ",
            "    )     (___/   ", "   /_/   \\_\\      " };
    }
    @Override String cor() { return Cor.CINZA; }
    @Override void ataqueBasico(Heroi h) {
        System.out.println("\n🐺 O Lobo salta com as presas à mostra!");
        Efeitos.tocar(Efeitos.CORTE, Cor.CINZA);
        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        System.out.println("\n🩸 O Lobo crava os dentes na sua carne!");
        if (h.receberDano(calcularAtaque(0.9), Elemento.FISICO, true) > 0)
            h.adicionarEfeito(new Efeito(TipoEfeito.SANGRAMENTO, 3, 6));
    }
}

class Aranha extends Monstro {
    Aranha() {
        super("Aranha Gigante", 85, 24, 6, 14, 2, 65, 20, false, "Presa de Aranha", 70);
        afinidade.put(Elemento.FOGO, 1.5); afinidade.put(Elemento.SOMBRA, 0.5);
    }
    @Override String[] sprite() {
        return new String[] { "   /\\  /\\  /\\   /\\ ", "  /  \\/  \\/  \\ /  ", "     .-'''-.      ",
            "  ---( o o )---   ", "  /   '-V-'  \\    ", " /    /   \\    \\   " };
    }
    @Override String cor() { return Cor.MAGENTA; }
    @Override void ataqueBasico(Heroi h) {
        System.out.println("\n🕷️  A Aranha ataca com as quelíceras!");
        Efeitos.tocar(Efeitos.CORTE, Cor.MAGENTA);
        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        System.out.println("\n☠️  A Aranha injeta veneno com sua picada!");
        Efeitos.tocar(Efeitos.VENENO, Cor.VERDE);
        h.receberDano(calcularAtaque(0.8), Elemento.SOMBRA, true);
        h.adicionarEfeito(new Efeito(TipoEfeito.VENENO, 4, 8));
    }
}

class Bandido extends Monstro {
    private int roubado = 0;
    Bandido() { super("Bandido da Estrada", 90, 26, 9, 15, 2, 70, 40, false, null, 0); }
    @Override String[] sprite() {
        return new String[] { "      _____        ", "     /_____\\       ", "     ( -_- )  /|   ",
            "    /|[===]|\\/ |   ", "   / |     |  \\|   ", "     |_/ \\_|       " };
    }
    @Override String cor() { return Cor.AMARELO; }
    @Override void ataqueBasico(Heroi h) {
        System.out.println("\n🗡️  O Bandido ataca com a adaga!");
        Efeitos.tocar(Efeitos.CORTE, Cor.AMARELO);
        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        int r = Math.min(h.getOuro(), 15 + RNG.nextInt(20));
        System.out.println("\n💰 O Bandido tenta bater sua carteira!");
        if (r > 0 && RNG.nextInt(100) < 70) {
            h.gastarOuro(r); roubado += r; ouroRec += r;
            System.out.println(Cor.c(Cor.AMARELO, "   Ele roubou " + r + " de ouro! Derrote-o para recuperar."));
        } else System.out.println("   Você protegeu seu ouro a tempo!");
    }
}

class Esqueleto extends Monstro {
    Esqueleto() {
        super("Guerreiro Esquelético", 110, 28, 10, 8, 3, 80, 25, false, "Essência Arcana", 25);
        afinidade.put(Elemento.LUZ, 1.75); afinidade.put(Elemento.SOMBRA, 0.25); afinidade.put(Elemento.FISICO, 0.75);
    }
    @Override String[] sprite() {
        return new String[] { "     .---.    ", "    ( o o )   ", "     \\ ^ /    ", "   __|=|=|__  ",
            "  /  |=|=|  \\ ", "     |_|_|    ", "    _/   \\_   " };
    }
    @Override String cor() { return Cor.BRANCO; }
    @Override void ataqueBasico(Heroi h) {
        System.out.println("\n🦴 O Esqueleto desfere um golpe de espada enferrujada!");
        Efeitos.tocar(Efeitos.CORTE, Cor.CINZA);
        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        System.out.println("\n💀 O Esqueleto realiza um ATAQUE DUPLO!");
        h.receberDano(calcularAtaque(0.8), Elemento.FISICO, true);
        if (h.estaVivo()) h.receberDano(calcularAtaque(0.8), Elemento.FISICO, true);
    }
}

class Cultista extends Monstro {
    Cultista() {
        super("Cultista do Olho", 95, 34, 6, 11, 3, 90, 35, false, "Essência Arcana", 50);
        afinidade.put(Elemento.LUZ, 1.5); afinidade.put(Elemento.SOMBRA, 0.5);
    }
    @Override String[] sprite() {
        return new String[] { "       /\\          ", "      /  \\   *     ", "     / () \\  |     ",
            "    /______\\-+-    ", "    |  ><  |  |    ", "   /|______|\\ |    " };
    }
    @Override String cor() { return Cor.MAGENTA; }
    @Override void ataqueBasico(Heroi h) {
        System.out.println("\n🔮 O Cultista dispara um raio sombrio!");
        Efeitos.tocar(Efeitos.SOMBRA, Cor.MAGENTA);
        h.receberDano(calcularAtaque(1.0), Elemento.SOMBRA, true);
    }
    @Override void especial(Heroi h) {
        System.out.println("\n🌀 O Cultista lança uma MALDIÇÃO LENTA!");
        Efeitos.tocar(Efeitos.GELO, Cor.MAGENTA);
        h.receberDano(calcularAtaque(0.7), Elemento.SOMBRA, true);
        h.adicionarEfeito(new Efeito(TipoEfeito.LENTIDAO, 3, 0));
    }
}

class GolemFerro extends Monstro {
    GolemFerro() {
        super("Constructo de Ferro", 160, 34, 22, 3, 5, 120, 45, false, "Pedra de Fogo", 50);
        afinidade.put(Elemento.FISICO, 0.5); afinidade.put(Elemento.RAIO, 1.75); afinidade.put(Elemento.SOMBRA, 0.75);
    }
    @Override String[] sprite() {
        return new String[] { "     [=======]    ", "     |[O___O]|    ", "  [M]|#######|[M] ",
            "  [_]|#######|[_] ", "     |_|||||_|    ", "     [__] [__]    " };
    }
    @Override String cor() { return Cor.CINZA; }
    @Override void ataqueBasico(Heroi h) {
        System.out.println("\n🔨 O Golem ergue os punhos maciços e esmaga o chão!");
        Efeitos.tocar(Efeitos.IMPACTO, Cor.AMARELO);
        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        System.out.println("\n⚙️  O Golem libera uma ONDA DE CHOQUE METÁLICA!");
        Efeitos.tocar(Efeitos.IMPACTO, Cor.AMARELO);
        h.receberDano(calcularAtaque(1.5), Elemento.FISICO, false);
    }
}

class Beholder extends Monstro {
    Beholder() {
        super("Observador do Caos", 320, 38, 12, 10, 6, 350, 200, true, null, 0);
        afinidade.put(Elemento.SOMBRA, 0.5); afinidade.put(Elemento.LUZ, 1.5);
    }
    @Override String[] sprite() {
        return new String[] { "    \\  |  /     ", "   .-\\ | /-.    ", " /'  ( O )  '\\ ", "|  .-'---'-.  |",
            "|  \\  vvv  /  |", " \\  '-----'  / ", "  '-._____.-'   " };
    }
    @Override String cor() { return Cor.VERMELHO; }
    @Override String aviso() { return "O olho central do Observador começa a brilhar intensamente..."; }
    @Override void ataqueBasico(Heroi h) {
        System.out.println("\n👁️  O Observador dispara raios desintegradores!");
        Efeitos.tocar(Efeitos.RAIO, Cor.AMARELO);
        h.receberDano(calcularAtaque(1.0), Elemento.RAIO, true);
    }
    @Override void especial(Heroi h) {
        System.out.println("\n🌑 O Observador fixa o olhar e queima sua mana!");
        Efeitos.tocar(Efeitos.SOMBRA, Cor.MAGENTA);
        h.receberDano(calcularAtaque(1.0) + 10, Elemento.SOMBRA, true);
        h.modificarMana(-20);
        h.adicionarEfeito(new Efeito(TipoEfeito.LENTIDAO, 2, 0));
    }
    @Override void golpePesado(Heroi h) {
        System.out.println(Cor.c(Cor.VERMELHO + Cor.NEGRITO, "\n🔴 O GRANDE OLHO CENTRAL EMITE UM RAIO ANTIVITAL!"));
        Efeitos.tocar(Efeitos.RAIO, Cor.VERMELHO);
        h.receberDano(calcularAtaque(1.0) + 40, Elemento.RAIO, false);
        h.modificarMana(-25);
    }
}

class DragaoAncestral extends Monstro {
    DragaoAncestral() {
        super("Ignis, o Dragão Ancestral", 480, 46, 18, 9, 9, 600, 500, true, null, 0);
        afinidade.put(Elemento.FOGO, 0.25); afinidade.put(Elemento.RAIO, 1.5); afinidade.put(Elemento.LUZ, 1.25);
    }
    @Override String[] sprite() {
        return new String[] { "        /\\    /\\         ", "   _   /  \\__/  \\   _    ", "  / \\_|  O    O  |_/ \\   ",
            " /    \\    ^^    /    \\  ", " \\_    \\  VVVV  /    _/  ", "   \\____\\_/||\\_/____/    ",
            "      /_/ /||\\ \\_\\       ", "     ~~~ /_/  \\_\\ ~~~    " };
    }
    @Override String cor() { return Cor.VERMELHO; }
    @Override String aviso() { return "Ignis inspira fundo... o ar ao redor começa a queimar!"; }
    @Override void ataqueBasico(Heroi h) {
        System.out.println("\n🐉 O Dragão açoita com suas garras colossais!");
        Efeitos.tocar(Efeitos.CORTE, Cor.VERMELHO);
        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        System.out.println("\n🔥 Ignis cospe uma rajada de brasas!");
        Efeitos.tocar(Efeitos.FOGO, Cor.VERMELHO);
        h.receberDano(calcularAtaque(1.0) + 10, Elemento.FOGO, true);
        h.adicionarEfeito(new Efeito(TipoEfeito.QUEIMADURA, 3, 10));
    }
    @Override void golpePesado(Heroi h) {
        System.out.println(Cor.c(Cor.VERMELHO + Cor.NEGRITO, "\n🔥🔥 SOPRO DE FOGO ANCESTRAL!"));
        Efeitos.tocar(Efeitos.FOGO, Cor.VERMELHO);
        h.receberDano(calcularAtaque(1.0) + 55, Elemento.FOGO, false);
        h.adicionarEfeito(new Efeito(TipoEfeito.QUEIMADURA, 3, 12));
    }
}

enum Local {
    VILA("Vila Aurora", 0), FLORESTA("Floresta Sussurrante", 1), CAVERNA("Caverna das Aranhas", 2),
    RUINAS("Ruínas Esquecidas", 3), MONTANHA("Montanha de Ferro", 5), TORRE("Torre do Observador", 6),
    COVIL("Covil de Ignis", 9);
    final String nome;
    final int nivelRec;
    Local(String n, int nv) { nome = n; nivelRec = nv; }

    String[] arte() {
        switch (this) {
            case VILA: return Arte.VILA;
            case FLORESTA: return Arte.FLORESTA;
            case CAVERNA: return Arte.CAVERNA;
            case RUINAS: return Arte.RUINAS;
            case MONTANHA: return Arte.MONTANHA;
            case TORRE: return Arte.TORRE;
            default: return Arte.COVIL;
        }
    }

    String cor() {
        switch (this) {
            case FLORESTA: return Cor.VERDE;
            case CAVERNA: return Cor.MAGENTA;
            case RUINAS: return Cor.AMARELO;
            case MONTANHA: return Cor.CINZA;
            case TORRE: return Cor.AZUL;
            case COVIL: return Cor.VERMELHO;
            default: return Cor.CIANO;
        }
    }

    String material() {
        switch (this) {
            case FLORESTA: return "Erva Lunar";
            case CAVERNA: return "Presa de Aranha";
            case RUINAS: return "Essência Arcana";
            case MONTANHA: return "Pedra de Fogo";
            default: return null;
        }
    }
}

final class Bestiario {
    static Monstro criar(String id) {
        switch (id) {
            case "Slime": return new Slime();
            case "Lobo": return new Lobo();
            case "Aranha": return new Aranha();
            case "Bandido": return new Bandido();
            case "Esqueleto": return new Esqueleto();
            case "Cultista": return new Cultista();
            case "Golem": return new GolemFerro();
            case "Beholder": return new Beholder();
            default: return new DragaoAncestral();
        }
    }

    /** Identificador usado nas missões (corresponde ao id acima). */
    static String id(Monstro m) {
        if (m instanceof Slime) return "Slime";
        if (m instanceof Lobo) return "Lobo";
        if (m instanceof Aranha) return "Aranha";
        if (m instanceof Bandido) return "Bandido";
        if (m instanceof Esqueleto) return "Esqueleto";
        if (m instanceof Cultista) return "Cultista";
        if (m instanceof GolemFerro) return "Golem";
        if (m instanceof Beholder) return "Beholder";
        return "Dragao";
    }

    static Monstro sortear(Local l, int nivelHeroi, boolean noite) {
        String[] pool;
        switch (l) {
            case FLORESTA: pool = new String[] { "Slime", "Slime", "Lobo", "Lobo", "Bandido" }; break;
            case CAVERNA: pool = new String[] { "Aranha", "Aranha", "Slime", "Bandido", "Lobo" }; break;
            case RUINAS: pool = new String[] { "Esqueleto", "Esqueleto", "Cultista", "Cultista", "Bandido" }; break;
            case MONTANHA: pool = new String[] { "Golem", "Golem", "Bandido", "Esqueleto", "Cultista" }; break;
            case TORRE: pool = new String[] { "Cultista", "Cultista" }; break;
            default: pool = new String[] { "Golem" };
        }
        Monstro m = criar(pool[Entidade.RNG.nextInt(pool.length)]);
        double f = 1.0 + 0.08 * Math.max(0, nivelHeroi - m.nivelBase);
        if (noite) f *= 1.15;
        if (f > 1.0) m.escalar(f);
        return m;
    }
}

// =========================================================================
// PARTE 5 - SISTEMA DE COMBATE POR TURNOS (VELOCIDADE, STATUS, FUGA)
// =========================================================================
final class Combate {
    enum Resultado { VITORIA, DERROTA, FUGA }

    static Resultado lutar(Heroi h, Monstro m, boolean podeFugir) {
        h.limparEfeitos();
        h.recargas.clear();
        while (true) {
            boolean heroiPrimeiro = h.getAgilidade() >= m.getAgilidade();
            for (int i = 0; i < 2; i++) {
                boolean vezHeroi = (i == 0) == heroiPrimeiro;
                if (vezHeroi) {
                    if (turnoHeroi(h, m, podeFugir)) { h.limparEfeitos(); return Resultado.FUGA; }
                } else {
                    turnoMonstro(h, m);
                }
                if (!h.estaVivo()) return Resultado.DERROTA;
                if (!m.estaVivo()) { h.limparEfeitos(); return Resultado.VITORIA; }
            }
            Tela.esperar(500);
        }
    }

    private static boolean turnoHeroi(Heroi h, Monstro m, boolean podeFugir) {
        h.setDefendendo(false);
        h.tickRecargas();
        boolean agir = h.iniciarTurno();
        if (!h.estaVivo()) return false;
        if (!agir) {
            System.out.println(Cor.c(Cor.AMARELO, "💫 " + h.getNome() + " está atordoado e perde o turno!"));
            return false;
        }
        desenharCampo(h, m);
        boolean gasto = false;
        while (!gasto) {
            System.out.println("┌──────────────────────────────────────────────────────────────┐");
            System.out.println("│ 1.Atacar 2.Habilidade 3.Defender 4.Item 5.Analisar 6.Fugir   │");
            System.out.println("└──────────────────────────────────────────────────────────────┘");
            System.out.print("Seu comando: ");
            switch (Tela.ler(1, 6)) {
                case 1: h.atacar(m); gasto = true; break;
                case 2: gasto = menuHabilidades(h, m); break;
                case 3:
                    h.setDefendendo(true);
                    Efeitos.tocar(Efeitos.ESCUDO, Cor.CIANO);
                    System.out.println("🛡️  " + h.getNome() + " ergue a guarda e recupera o fôlego! (+8 Mana)");
                    h.modificarMana(8);
                    gasto = true;
                    break;
                case 4: gasto = menuItens(h, m); break;
                case 5: analisar(m); break;
                default:
                    if (!podeFugir) { System.out.println("❌ Não há como fugir desta batalha!"); break; }
                    int chance = Math.max(15, Math.min(85, 45 + (h.getAgilidade() - m.getAgilidade()) * 2));
                    if (RandomUtil.chance(chance)) {
                        System.out.println("🏃 " + h.getNome() + " escapou da batalha!");
                        return true;
                    }
                    System.out.println("❌ A fuga falhou! O inimigo bloqueou o caminho.");
                    gasto = true;
            }
        }
        if (m.estaVivo() && h.estaVivo() && h.aliado != null) h.aliado.agir(h, m);
        h.modificarMana(4);
        return false;
    }

    private static void turnoMonstro(Heroi h, Monstro m) {
        Tela.esperar(600);
        boolean agir = m.iniciarTurno();
        if (!m.estaVivo()) return;
        if (!agir) {
            System.out.println(Cor.c(Cor.AMARELO, "💫 " + m.getNome() + " está atordoado e perde o turno!"));
            if (m.carregando) {
                m.carregando = false;
                System.out.println(Cor.c(Cor.VERDE, "✅ O golpe carregado foi INTERROMPIDO!"));
            }
            return;
        }
        m.turno(h);
    }

    private static boolean menuHabilidades(Heroi h, Monstro m) {
        List<Habilidade> hs = h.habilidades();
        System.out.println("\n✨ HABILIDADES (Mana " + h.getMana() + "/" + h.getManaMax() + ")");
        for (int i = 0; i < hs.size(); i++) {
            Habilidade x = hs.get(i);
            int rc = h.recargas.getOrDefault(x.nome, 0);
            String estado = rc > 0 ? Cor.c(Cor.VERMELHO, " [recarga: " + rc + "]")
                : (h.getMana() < x.custo ? Cor.c(Cor.VERMELHO, " [sem mana]") : "");
            System.out.println((i + 1) + ". " + Tela.pad(x.nome, 19) + " " + Tela.pad(x.custo + " mana", 8) + " "
                + x.elem.icone + " " + x.desc + estado);
        }
        System.out.println("0. Voltar");
        System.out.print("Escolha: ");
        int op = Tela.ler(0, hs.size());
        if (op == 0) return false;
        Habilidade x = hs.get(op - 1);
        if (!h.podeUsar(x)) {
            System.out.println("❌ Não é possível usar " + x.nome + " agora (mana insuficiente ou em recarga).");
            return false;
        }
        h.usarHabilidade(x, m);
        return true;
    }

    private static boolean menuItens(Heroi h, Monstro m) {
        ArrayList<Item> usaveis = new ArrayList<>();
        for (Item i : h.inv) if (i.usavelEmCombate()) usaveis.add(i);
        if (usaveis.isEmpty()) { System.out.println("🎒 Você não tem itens utilizáveis em combate."); return false; }
        System.out.println("\n🎒 ITENS");
        for (int i = 0; i < usaveis.size(); i++) System.out.println((i + 1) + ". " + usaveis.get(i).linha());
        System.out.println("0. Voltar");
        System.out.print("Escolha: ");
        int op = Tela.ler(0, usaveis.size());
        if (op == 0) return false;
        Item it = usaveis.get(op - 1);
        if (it.usar(h, m)) { h.inv.remove(it); return true; }
        return false;
    }

    private static void analisar(Monstro m) {
        StringBuilder fraco = new StringBuilder(), resiste = new StringBuilder();
        for (Elemento e : Elemento.values()) {
            double a = m.afinidade.getOrDefault(e, 1.0);
            if (a > 1.0) fraco.append(e.icone).append(e.nome).append(" ");
            if (a < 1.0) resiste.append(e.icone).append(e.nome).append(" ");
        }
        Tela.caixa("ANÁLISE: " + m.getNome(),
            "HP " + m.getHp() + "/" + m.getHpMax() + "   ATK " + m.getAtaque() + "   DEF " + m.getDefesa() + "   AGI " + m.getAgilidade(),
            "Fraco a:   " + (fraco.length() == 0 ? "-" : fraco),
            "Resiste a: " + (resiste.length() == 0 ? "-" : resiste),
            m.chefe ? "CHEFE: prepara golpes devastadores. Defenda-se ou atordoe-o!" : "Criatura comum.");
    }

    static void desenharCampo(Heroi h, Monstro m) {
        System.out.println();
        Tela.titulo("CAMPO DE BATALHA", Cor.AMARELO);
        String[] sh = h.sprite();
        String[] se = m.sprite();
        int altura = Math.max(sh.length, se.length);
        int larguraH = 0;
        for (String s : sh) larguraH = Math.max(larguraH, s.length());
        larguraH += 6;
        for (int i = 0; i < altura; i++) {
            int ih = i - (altura - sh.length);
            int ie = i - (altura - se.length);
            String lh = ih >= 0 ? sh[ih] : "";
            String le = ie >= 0 ? se[ie] : "";
            System.out.println(Cor.c(h.cor(), Tela.pad(lh, larguraH)) + Cor.c(m.cor(), le));
        }
        System.out.println(Cor.c(Cor.CINZA, Tela.linha('▔')));
        System.out.println(Cor.c(h.cor() + Cor.NEGRITO, h.getNome()) + " (Nv " + h.getNivel() + " " + h.getClasse() + ")"
            + (h.isDefendendo() ? " 🛡️" : "") + h.resumoEfeitos());
        System.out.println("  HP   " + Tela.barra(h.getHp(), h.getHpMax(), 24, Tela.corHp(h.getHp(), h.getHpMax()))
            + " " + h.getHp() + "/" + h.getHpMax());
        System.out.println("  MANA " + Tela.barra(h.getMana(), h.getManaMax(), 24, Cor.AZUL)
            + " " + h.getMana() + "/" + h.getManaMax());
        if (h.aliado != null) System.out.println("  Aliado: " + h.aliado.nome);
        System.out.println(Cor.c(m.cor() + Cor.NEGRITO, m.getNome()) + m.resumoEfeitos() + m.estadoCarga());
        System.out.println("  HP   " + Tela.barra(m.getHp(), m.getHpMax(), 24, Tela.corHp(m.getHp(), m.getHpMax()))
            + " " + m.getHp() + "/" + m.getHpMax());
        System.out.println(Cor.c(Cor.AMARELO, Tela.linha('=')));
    }
}

final class RandomUtil {
    static final Random R = new Random();
    static boolean chance(int pct) { return R.nextInt(100) < pct; }
    static int entre(int min, int max) { return min + R.nextInt(max - min + 1); }
}

// =========================================================================
// PARTE 6 - SISTEMA DE MISSÕES (DIÁRIO)
// =========================================================================
class Missao {
    enum Tipo { CACAR, COLETAR, CHEFE }

    final String id, titulo, desc, doador, alvo, itemRec, requer;
    final Tipo tipo;
    final int qtd, ouro, xp, nivelMin;
    int estado = 0;      // 0 = disponível, 1 = ativa, 2 = entregue
    int progresso = 0;

    Missao(String id, String titulo, String desc, String doador, Tipo tipo, String alvo, int qtd,
           int ouro, int xp, String itemRec, int nivelMin, String requer) {
        this.id = id; this.titulo = titulo; this.desc = desc; this.doador = doador; this.tipo = tipo;
        this.alvo = alvo; this.qtd = qtd; this.ouro = ouro; this.xp = xp; this.itemRec = itemRec;
        this.nivelMin = nivelMin; this.requer = requer;
    }

    int atual(Heroi h) { return tipo == Tipo.COLETAR ? Math.min(qtd, h.contar(alvo)) : Math.min(qtd, progresso); }
    boolean pronta(Heroi h) { return estado == 1 && atual(h) >= qtd; }

    String objetivo(Heroi h) {
        String verbo = tipo == Tipo.COLETAR ? "Coletar " : (tipo == Tipo.CHEFE ? "Derrotar " : "Caçar ");
        return verbo + alvo + " (" + atual(h) + "/" + qtd + ")";
    }

    String recompensa() {
        return ouro + " ouro, " + xp + " XP" + (itemRec != null ? ", " + itemRec : "");
    }
}

final class Diario {
    final ArrayList<Missao> missoes = new ArrayList<>();
    final HashSet<String> flags = new HashSet<>();

    Diario() {
        add("praga", "Praga na Floresta", "Gosmas ácidas estão destruindo as plantações.", "Mira",
            Missao.Tipo.CACAR, "Slime", 3, 60, 80, null, 1, null);
        add("lobos", "Lobos Famintos", "Lobos atacam viajantes na Floresta.", "Mira",
            Missao.Tipo.CACAR, "Lobo", 3, 90, 100, "Gibão de Couro", 1, null);
        add("ervas", "Ervas para a Irmã Lira", "Lira precisa de Erva Lunar para seus remédios.", "Lira",
            Missao.Tipo.COLETAR, "Erva Lunar", 3, 40, 70, "Poção de Cura", 1, null);
        add("ladrao", "Ladrão da Estrada", "Bandidos assaltam comerciantes.", "Mira",
            Missao.Tipo.CACAR, "Bandido", 2, 100, 140, "Anel Veloz", 2, "lobos");
        add("teias", "Teias Sombrias", "Aranhas dominaram a Caverna.", "Mira",
            Missao.Tipo.CACAR, "Aranha", 3, 120, 160, "Antídoto", 2, "praga");
        add("fogo", "Pedras para a Forja", "Borin precisa de Pedra de Fogo para forjar uma arma.", "Borin",
            Missao.Tipo.COLETAR, "Pedra de Fogo", 2, 50, 120, "Lâmina de Aço", 2, null);
        add("ossos", "Ossos Inquietos", "Mortos-vivos vagam pelas Ruínas.", "Mira",
            Missao.Tipo.CACAR, "Esqueleto", 4, 150, 220, null, 3, "teias");
        add("seita", "A Seita do Olho", "Cultistas adoram algo na Torre.", "Aldric",
            Missao.Tipo.CACAR, "Cultista", 3, 160, 260, "Elixir de Mana", 4, "ossos");
        add("torre", "O Olho Que Tudo Vê", "Destrua o Observador do Caos no topo da Torre.", "Aldric",
            Missao.Tipo.CHEFE, "Beholder", 1, 300, 500, "Cajado Radiante", 5, "seita");
        add("dragao", "A Fúria de Ignis", "Com a Chave do Dragão, derrote Ignis no seu Covil.", "Aldric",
            Missao.Tipo.CHEFE, "Dragao", 1, 1000, 1000, null, 7, "torre");
    }

    private void add(String id, String t, String d, String doador, Missao.Tipo tipo, String alvo, int qtd,
                     int ouro, int xp, String item, int nv, String req) {
        missoes.add(new Missao(id, t, d, doador, tipo, alvo, qtd, ouro, xp, item, nv, req));
    }

    Missao obter(String id) {
        for (Missao m : missoes) if (m.id.equals(id)) return m;
        return null;
    }

    boolean concluida(String id) { Missao m = obter(id); return m != null && m.estado == 2; }

    /** Missões que o doador pode oferecer agora. */
    ArrayList<Missao> disponiveis(String doador, Heroi h) {
        ArrayList<Missao> r = new ArrayList<>();
        for (Missao m : missoes) {
            if (!m.doador.equals(doador) || m.estado != 0) continue;
            if (h.getNivel() < m.nivelMin) continue;
            if (m.requer != null && !concluida(m.requer)) continue;
            r.add(m);
        }
        return r;
    }

    ArrayList<Missao> prontas(String doador, Heroi h) {
        ArrayList<Missao> r = new ArrayList<>();
        for (Missao m : missoes) if (m.doador.equals(doador) && m.pronta(h)) r.add(m);
        return r;
    }

    int ativas() { int n = 0; for (Missao m : missoes) if (m.estado == 1) n++; return n; }
    int entregues() { int n = 0; for (Missao m : missoes) if (m.estado == 2) n++; return n; }

    /** Chamado quando um monstro é derrotado. */
    void registrarAbate(String idMonstro) {
        for (Missao m : missoes) {
            if (m.estado == 1 && (m.tipo == Missao.Tipo.CACAR || m.tipo == Missao.Tipo.CHEFE)
                && m.alvo.equals(idMonstro) && m.progresso < m.qtd) {
                m.progresso++;
                System.out.println(Cor.c(Cor.AMARELO, "📜 Missão \"" + m.titulo + "\": " + m.progresso + "/" + m.qtd
                    + (m.progresso >= m.qtd ? " — PRONTA PARA ENTREGAR!" : "")));
            }
        }
    }

    void mostrar(Heroi h) {
        System.out.println();
        Tela.mostrarArte(Arte.PERGAMINHO, Cor.AMARELO);
        boolean algum = false;
        for (Missao m : missoes) {
            if (m.estado != 1) continue;
            algum = true;
            String status = m.pronta(h) ? Cor.c(Cor.VERDE, " [ENTREGAR para " + m.doador + "]") : "";
            System.out.println(Cor.c(Cor.NEGRITO, "• " + m.titulo) + status);
            System.out.println("    " + m.objetivo(h) + "  |  Recompensa: " + m.recompensa());
        }
        if (!algum) System.out.println("Nenhuma missão ativa. Converse com os moradores da vila!");
        System.out.println(Cor.c(Cor.CINZA, "Missões concluídas: " + entregues() + "/" + missoes.size()));
    }
}

// =========================================================================
// PARTE 7 - ENGINE DO JOGO: MENUS, VILA, NPCs, LOJAS, TAVERNA E TEMPLO
// =========================================================================
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

    static void prepararJogo() { fimDeJogo = false; venceu = false; voltarMenu = false; chegou = true; }

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
            heroi.ganharXp(m.xp);
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
            heroi.ganharXp(40);
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
            heroi.ganharXp(15);
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
                    heroi.ganharXp(30);
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
        heroi.ganharXp(m.xpRec);
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
final class Persistencia {
    private static String ser(Item i) {
        if (i instanceof Equipamento) return ((Equipamento) i).getNomeBase() + "|" + ((Equipamento) i).melhoria;
        return i.getNome() + "|0";
    }

    private static Item deser(String s) {
        String[] p = s.split("\\|");
        Item it = Fabrica.criar(p[0]);
        if (it instanceof Equipamento && p.length > 1) ((Equipamento) it).melhoria = Integer.parseInt(p[1]);
        return it;
    }

    static void salvar() {
        Heroi h = SuperRpgTerminal.heroi;
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(SuperRpgTerminal.SAVE), "UTF-8"))) {
            w.println("v2");
            w.println(h.getClasse());
            w.println(h.getNome());
            w.println(h.nivel + "," + h.xp + "," + h.hp + "," + h.hpMax + "," + h.mana + "," + h.manaMax + "," + h.atk + ","
                + h.def + "," + h.agi + "," + h.ouro + "," + h.honra + "," + h.abates);
            StringBuilder eq = new StringBuilder();
            for (Equipamento e : h.equip) eq.append(e == null ? "-" : ser(e)).append(";");
            w.println(eq);
            StringBuilder inv = new StringBuilder();
            for (Item i : h.inv) inv.append(ser(i)).append(";");
            w.println(inv.length() == 0 ? "-" : inv);
            w.println(h.aliado == null ? "-" : (h.aliado instanceof LoboCinzento ? "lobo" : "lira"));
            w.println(SuperRpgTerminal.dia + "," + SuperRpgTerminal.periodo + "," + SuperRpgTerminal.local.name() + ","
                + SuperRpgTerminal.hardcore + "," + SuperRpgTerminal.charadaHoje);
            StringBuilder ms = new StringBuilder();
            for (Missao m : SuperRpgTerminal.diario.missoes) ms.append(m.id).append(":").append(m.estado).append(":").append(m.progresso).append(";");
            w.println(ms);
            System.out.println(Cor.c(Cor.VERDE, "💾 Jogo salvo em " + SuperRpgTerminal.SAVE + "."));
        } catch (Exception e) {
            System.out.println(Cor.c(Cor.VERMELHO, "❌ Não foi possível salvar (este ambiente pode não permitir gravar arquivos)."));
        }
    }

    static boolean carregar() {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(SuperRpgTerminal.SAVE), "UTF-8"))) {
            if (!"v2".equals(r.readLine())) return false;
            String classe = r.readLine();
            String nome = r.readLine();
            Heroi h;
            switch (classe) {
                case "Paladino": h = new Paladino(nome); break;
                case "Necromante": h = new Necromante(nome); break;
                case "Arqueiro": h = new Arqueiro(nome); break;
                case "Mago": h = new Mago(nome); break;
                default: h = new Ladino(nome);
            }
            String[] s = r.readLine().split(",");
            h.nivel = Integer.parseInt(s[0]); h.xp = Integer.parseInt(s[1]); h.hp = Integer.parseInt(s[2]);
            h.hpMax = Integer.parseInt(s[3]); h.mana = Integer.parseInt(s[4]); h.manaMax = Integer.parseInt(s[5]);
            h.atk = Integer.parseInt(s[6]); h.def = Integer.parseInt(s[7]); h.agi = Integer.parseInt(s[8]);
            h.ouro = Integer.parseInt(s[9]); h.honra = Integer.parseInt(s[10]); h.abates = Integer.parseInt(s[11]);
            String[] eq = r.readLine().split(";");
            for (int i = 0; i < 3 && i < eq.length; i++) if (!eq[i].equals("-")) h.equip[i] = (Equipamento) deser(eq[i]);
            String inv = r.readLine();
            if (!inv.equals("-")) for (String p : inv.split(";")) if (!p.isEmpty()) { Item it = deser(p); if (it != null) h.inv.add(it); }
            String al = r.readLine();
            if (al.equals("lobo")) h.aliado = new LoboCinzento();
            else if (al.equals("lira")) h.aliado = new IrmaLira();
            String[] t = r.readLine().split(",");
            Diario d = new Diario();
            String linhaMissoes = r.readLine();
            if (linhaMissoes != null) for (String p : linhaMissoes.split(";")) {
                if (p.isEmpty()) continue;
                String[] q = p.split(":");
                Missao m = d.obter(q[0]);
                if (m != null) { m.estado = Integer.parseInt(q[1]); m.progresso = Integer.parseInt(q[2]); }
            }
            SuperRpgTerminal.heroi = h;
            SuperRpgTerminal.diario = d;
            SuperRpgTerminal.dia = Integer.parseInt(t[0]);
            SuperRpgTerminal.periodo = Integer.parseInt(t[1]);
            SuperRpgTerminal.local = Local.valueOf(t[2]);
            SuperRpgTerminal.hardcore = Boolean.parseBoolean(t[3]);
            SuperRpgTerminal.charadaHoje = Boolean.parseBoolean(t[4]);
            System.out.println(Cor.c(Cor.VERDE, "📂 Jogo carregado: " + h.ficha()));
            return true;
        } catch (Exception e) {
            System.out.println(Cor.c(Cor.VERMELHO, "❌ Save corrompido ou inacessível."));
            return false;
        }
    }

    static int pontuacao() {
        Heroi h = SuperRpgTerminal.heroi;
        int p = h.getNivel() * 100 + h.getOuro() + h.abates * 10 + SuperRpgTerminal.diario.entregues() * 150
            + (SuperRpgTerminal.venceu ? 3000 : 0) + h.getHonra() * 20 - SuperRpgTerminal.dia * 5;
        return Math.max(0, p);
    }

    static void registrarRanking() {
        Heroi h = SuperRpgTerminal.heroi;
        try (PrintWriter w = new PrintWriter(new FileWriter(SuperRpgTerminal.RANKING, true))) {
            w.println(pontuacao() + ";" + h.getNome() + ";" + h.getClasse() + ";" + h.getNivel() + ";" + SuperRpgTerminal.dia
                + ";" + (SuperRpgTerminal.venceu ? "VENCEU" : "caiu"));
        } catch (Exception ignorada) { }
    }

    static void mostrarRanking() {
        ArrayList<String[]> linhas = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader(SuperRpgTerminal.RANKING))) {
            String l;
            while ((l = r.readLine()) != null) { String[] p = l.split(";"); if (p.length >= 6) linhas.add(p); }
        } catch (Exception ignorada) { }
        linhas.sort((a, b) -> Integer.parseInt(b[0]) - Integer.parseInt(a[0]));
        System.out.println();
        Tela.titulo("SALÃO DOS HERÓIS", Cor.AMARELO);
        if (linhas.isEmpty()) System.out.println("Nenhum herói no ranking ainda.");
        for (int i = 0; i < Math.min(5, linhas.size()); i++) {
            String[] p = linhas.get(i);
            System.out.println((i + 1) + ". " + Tela.pad(p[1], 14) + Tela.pad(p[2], 11) + "Nv " + Tela.pad(p[3], 3) + "Dia " + Tela.pad(p[4], 4)
                + Tela.pad(p[5], 8) + Cor.c(Cor.AMARELO, p[0] + " pts"));
        }
        Tela.enter();
    }

    static void telaVitoria() {
        Heroi h = SuperRpgTerminal.heroi;
        System.out.println();
        Tela.mostrarArte(Arte.TROFEU, Cor.AMARELO);
        Tela.titulo("👑 O DRAGÃO CAIU! VITÓRIA ABSOLUTA! 👑", Cor.AMARELO);
        String final1 = h.getHonra() >= 2 ? "O povo canta seu nome: " + h.getNome() + " " + h.titulo() + ", salvador do reino!"
            : (h.getHonra() <= -2 ? "Venceu o Dragão, mas as histórias contam que seu coração é mais frio que o gelo..."
            : "O reino respira aliviado. " + h.getNome() + " desaparece pela estrada, sem alarde.");
        Tela.fala("Narrador", final1);
        System.out.println("\n  Nível final: " + h.getNivel() + " | Dias: " + SuperRpgTerminal.dia + " | Abates: " + h.abates
            + " | Missões: " + SuperRpgTerminal.diario.entregues() + "/" + SuperRpgTerminal.diario.missoes.size());
        System.out.println(Cor.c(Cor.AMARELO, "  Pontuação: " + pontuacao()));
        Tela.enter();
    }

    static void telaDerrota() {
        System.out.println();
        Tela.mostrarArte(Arte.CAVEIRA, Cor.VERMELHO);
        Tela.titulo("GAME OVER - Sua lenda termina aqui...", Cor.VERMELHO);
        System.out.println("  Pontuação: " + pontuacao());
        Tela.enter();
    }
}
