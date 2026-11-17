package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Elixir extends Item {
    Elixir() { super("Elixir de Mana", "Raro", "Consumível", "Restaura 40 de Mana", 40); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usavelFora() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        if (h.getMana() >= h.getManaMax()) { Eventos.texto("Sua mana já está cheia."); return false; }

        Eventos.texto("🧪 " + h.getNome() + " bebeu o Elixir! (+40 Mana)");
        h.modificarMana(40);
        return true;
    }
}
