package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

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
        Eventos.texto("\n🐺 O Lobo salta com as presas à mostra!");

        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        Eventos.texto("\n🩸 O Lobo crava os dentes na sua carne!");
        if (h.receberDano(calcularAtaque(0.9), Elemento.FISICO, true) > 0)
            h.adicionarEfeito(new Efeito(TipoEfeito.SANGRAMENTO, 3, 6));
    }
}
