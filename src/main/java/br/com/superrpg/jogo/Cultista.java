package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

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
        Eventos.texto("\n🔮 O Cultista dispara um raio sombrio!");

        h.receberDano(calcularAtaque(1.0), Elemento.SOMBRA, true);
    }
    @Override void especial(Heroi h) {
        Eventos.texto("\n🌀 O Cultista lança uma MALDIÇÃO LENTA!");

        h.receberDano(calcularAtaque(0.7), Elemento.SOMBRA, true);
        h.adicionarEfeito(new Efeito(TipoEfeito.LENTIDAO, 3, 0));
    }
}
