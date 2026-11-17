package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Pocao extends Item {
    Pocao() { super("Poção de Cura", "Incomum", "Consumível", "Restaura 60 HP", 30); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usavelFora() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        if (h.getHp() >= h.getHpMax()) { Eventos.texto("Seu HP já está cheio."); return false; }

        Eventos.texto("🧪 " + h.getNome() + " bebeu a Poção e sentiu as feridas fecharem! (+60 HP)");
        h.curar(60);
        return true;
    }
}
