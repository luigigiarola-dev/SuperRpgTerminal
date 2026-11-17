package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

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
        Eventos.texto("\n🟢 A Gosma se arremessa contra você!");

        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        Eventos.texto("\n🧪 A Gosma cospe ácido corrosivo!");
        h.receberDano(calcularAtaque(1.0) + 8, Elemento.SOMBRA, true);
        h.modificarMana(-10);
    }
}
