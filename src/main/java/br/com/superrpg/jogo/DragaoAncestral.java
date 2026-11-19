package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class DragaoAncestral extends Monstro {
    DragaoAncestral() {
        super("Ignis, o Dragão Ancestral", 480, 46, 18, 9, 9, 600, 500, true, null, 0);
        afinidade.put(Elemento.FOGO, 0.25); afinidade.put(Elemento.RAIO, 1.5); afinidade.put(Elemento.LUZ, 1.25);
    }
    @Override String[] sprite() {
        return new String[] { "        /\\    /\\         ", "   _   /  \\__/  \\   _    ", "  / \\_|  O    O  |_/ \\   ",
            " /    \\    ^^    /    \\  ", " \\_    \\  VVVV  /    _/  ", "   \\____\\_/||\\_/____/    ",
            "      /_/ /||\\ \\_\\       ", "     ~~~ /_/  \\_\\ ~~~    " };
    }
    @Override String cor() { return Cor.VERMELHO; }
    @Override String aviso() { return "Ignis inspira fundo... o ar ao redor começa a queimar!"; }
    @Override void ataqueBasico(Heroi h) {
        Eventos.texto("\n🐉 O Dragão açoita com suas garras colossais!");

        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        Eventos.texto("\n🔥 Ignis cospe uma rajada de brasas!");

        h.receberDano(calcularAtaque(1.0).somar(10), Elemento.FOGO, true);
        h.adicionarEfeito(new Efeito(TipoEfeito.QUEIMADURA, 3, 10));
    }
    @Override void golpePesado(Heroi h) {
        Eventos.texto(Cor.c(Cor.VERMELHO + Cor.NEGRITO, "\n🔥🔥 SOPRO DE FOGO ANCESTRAL!"));

        h.receberDano(calcularAtaque(1.0).somar(55), Elemento.FOGO, false);
        h.adicionarEfeito(new Efeito(TipoEfeito.QUEIMADURA, 3, 12));
    }
}
