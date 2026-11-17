package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

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
        Eventos.texto("\n🦴 O Esqueleto desfere um golpe de espada enferrujada!");

        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        Eventos.texto("\n💀 O Esqueleto realiza um ATAQUE DUPLO!");
        h.receberDano(calcularAtaque(0.8), Elemento.FISICO, true);
        if (h.estaVivo()) h.receberDano(calcularAtaque(0.8), Elemento.FISICO, true);
    }
}
