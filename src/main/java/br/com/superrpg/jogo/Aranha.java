package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

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
        Eventos.texto("\n🕷️  A Aranha ataca com as quelíceras!");

        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        Eventos.texto("\n☠️  A Aranha injeta veneno com sua picada!");

        h.receberDano(calcularAtaque(0.8), Elemento.SOMBRA, true);
        h.adicionarEfeito(new Efeito(TipoEfeito.VENENO, 4, 8));
    }
}
