package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

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

    void curar(int q) {
        int anterior = hp;
        hp = Math.max(0, Math.min(hpMax, hp + q));
        if (hp > anterior) Eventos.emitir(new EventoJogo(EventoJogo.Tipo.CURA, nome, hp - anterior, ""));
    }
    void modificarMana(int q) { mana = Math.max(0, Math.min(manaMax, mana + q)); }
    void perderHp(int q) {
        int anterior = hp;
        hp = Math.max(0, hp - Math.max(0, q));
        if (anterior > hp) Eventos.emitir(new EventoJogo(EventoJogo.Tipo.DANO, nome, anterior - hp, ""));
    }

    boolean temEfeito(TipoEfeito t) { return obterEfeito(t) != null; }
    Efeito obterEfeito(TipoEfeito t) {
        for (Efeito e : efeitos) if (e.tipo == t) return e;
        return null;
    }

    void adicionarEfeito(Efeito novo) {
        Efeito antigo = obterEfeito(novo.tipo);
        if (antigo != null) efeitos.remove(antigo);
        efeitos.add(novo.copia());
        Eventos.texto(Cor.c(novo.negativo() ? Cor.MAGENTA : Cor.CIANO,
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
                    Eventos.texto(Cor.c(Cor.MAGENTA, "   " + e.tipo.rotulo + ": " + nome + " perde " + e.valor + " HP!"));
                    break;
                case ATORDOADO: atordoado = true; break;
                default: break;
            }
            e.turnos--;
            if (e.turnos <= 0 || (e.tipo == TipoEfeito.ESCUDO && e.valor <= 0)) {
                it.remove();
                Eventos.texto(Cor.c(Cor.CINZA, "   (" + e.tipo.rotulo + " de " + nome + " acabou)"));
            }
        }
        return estaVivo() && !atordoado;
    }

    /** Aplica dano considerando esquiva, afinidade elemental, defesa, guarda e escudo. */
    int receberDano(int bruto, Elemento elem, boolean podeEsquivar) {
        if (podeEsquivar && RNG.nextInt(100) < Math.min(30, getAgilidade() / 2)) {
            Eventos.texto(Cor.c(Cor.CIANO, "💨 " + nome + " esquivou do ataque!"));
            return 0;
        }
        double m = afinidade.getOrDefault(elem, 1.0);
        int dano = (int) (bruto * m) - getDefesa();
        if (m > 1.0) Eventos.texto(Cor.c(Cor.AMARELO, "🎯 Fraqueza a " + elem.nome + "! Dano aumentado!"));
        if (m < 1.0) Eventos.texto(Cor.c(Cor.CINZA, "🧱 " + nome + " resiste a " + elem.nome + "."));
        if (defendendo) {
            dano /= 2;
            Eventos.texto("🛡️  " + nome + " está em guarda e reduz o impacto!");
        }
        dano = Math.max(1, dano);
        Efeito esc = obterEfeito(TipoEfeito.ESCUDO);
        if (esc != null && esc.valor > 0) {
            int absorvido = Math.min(dano, esc.valor);
            esc.valor -= absorvido;
            dano -= absorvido;
            Eventos.texto(Cor.c(Cor.CIANO, "🛡️  O escudo absorveu " + absorvido + " de dano!"));
            if (dano == 0) return 0;
        }
        perderHp(dano);
        Eventos.texto(Cor.c(Cor.VERMELHO, "💥 " + nome + " sofreu " + dano + " de dano!"));
        return dano;
    }

    int calcularAtaque(double mult) {
        int base = (int) ((getAtaque() + RNG.nextInt(7) - 3) * mult);
        if (RNG.nextInt(100) < 8 + getAgilidade() / 2) {
            Eventos.texto(Cor.c(Cor.AMARELO + Cor.NEGRITO, "⭐ ACERTO CRÍTICO!"));
            base = (int) (base * 1.5);
        }
        return Math.max(1, base);
    }
}

// =========================================================================
// HERÓI BASE (INVENTÁRIO, EQUIPAMENTOS, NÍVEIS E TALENTOS)
// =========================================================================
