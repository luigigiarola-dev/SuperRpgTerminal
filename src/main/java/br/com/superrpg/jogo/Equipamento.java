package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

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
