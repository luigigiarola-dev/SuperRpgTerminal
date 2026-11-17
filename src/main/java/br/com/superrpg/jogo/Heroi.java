package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

abstract class Heroi extends Entidade {
    protected int xp = 0, ouro = 60, honra = 0;
    final ArrayList<Item> inv = new ArrayList<>();
    final Equipamento[] equip = new Equipamento[3];
    final Map<String, Integer> recargas = new HashMap<>();
    Aliado aliado = null;
    int abates = 0;
    int talentosPendentes = 0;

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
        Eventos.texto("\n⚔️  " + getNome() + " ataca com " + (equip[0] != null ? equip[0].getNome() : "os punhos") + "!");
        Elemento el = elementoArma();

        alvo.receberDano(calcularAtaque(1.0), el, true);
    }

    boolean podeUsar(Habilidade h) { return mana >= h.custo && recargas.getOrDefault(h.nome, 0) == 0; }

    void usarHabilidade(Habilidade h, Monstro alvo) {
        modificarMana(-h.custo);
        if (h.recarga > 0) recargas.put(h.nome, h.recarga + 1);
        Eventos.texto("\n✨ " + getNome() + " usa " + Cor.c(Cor.NEGRITO, h.nome.toUpperCase()) + "!");
        if (h.mult > 0) {

            int dano = alvo.receberDano(calcularAtaque(h.mult), h.elem, true);
            if (h.rouba && dano > 0) {
                Eventos.texto(Cor.c(Cor.VERDE, "🩸 Drenando essência... você recupera " + (dano / 2) + " HP!"));
                curar(dano / 2);
            }
            if (h.efeitoAlvo != null && alvo.estaVivo()) alvo.adicionarEfeito(h.efeitoAlvo);
        } else if (h.efeitoAlvo != null) {

            alvo.adicionarEfeito(h.efeitoAlvo);
        }
        if (h.cura > 0) { Eventos.texto(Cor.c(Cor.VERDE, "💚 Você recupera " + h.cura + " HP!")); curar(h.cura); }
        if (h.efeitoSelf != null) {

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
        Eventos.texto("🎽 " + getNome() + " equipou " + e.getNome() + ".");
    }

    void restaurarTotal() { hp = hpMax; mana = manaMax; limparEfeitos(); recargas.clear(); }

    /** Ganha XP e sobe de nível com escolha de talento. Retorna níveis ganhos. */
    int ganharXp(int ganho) {
        xp += ganho;
        Eventos.texto(Cor.c(Cor.MAGENTA, "✨ +" + ganho + " XP."));
        int subidas = 0;
        while (xp >= xpProximo() && nivel < 15) {
            xp -= xpProximo();
            nivel++;
            hpMax += 25; manaMax += 8; atk += 4; def += 2; agi += 1;
            hp = hpMax; mana = manaMax;
            subidas++;

            Eventos.texto(Cor.c(Cor.VERDE + Cor.NEGRITO, "🆙 NÍVEL " + nivel + "! HP/Mana restaurados."));
            talentosPendentes++;
        }
        if (nivel >= 15) xp = 0;
        return subidas;
    }

    void aprenderTalento(int opcao) {
        if (talentosPendentes <= 0 || opcao < 1 || opcao > 4) throw new IllegalArgumentException("Talento inválido");
        talentosPendentes--;
        switch (opcao) {
            case 1: hpMax += 30; hp = hpMax; break;
            case 2: atk += 6; break;
            case 3: manaMax += 25; mana = manaMax; break;
            default: agi += 4; def += 2;
        }
        Eventos.texto(Cor.c(Cor.VERDE, "   Talento aprendido!"));
    }

    String ficha() {
        return getNome() + " " + titulo() + " — " + getClasse() + " Nv " + nivel;
    }
}
