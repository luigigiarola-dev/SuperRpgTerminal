package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Ladino extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Golpe Furtivo", "Físico x2.2", 10, 2, Elemento.FISICO, 2.2),
        new Habilidade("Lâmina Envenenada", "Físico x1.0 e veneno", 15, 1, Elemento.FISICO, 1.0)
            .alvo(new Efeito(TipoEfeito.VENENO, 4, 10)),
        new Habilidade("Dança das Lâminas", "Físico x2.4 e sangramento", 25, 3, Elemento.FISICO, 2.4)
            .alvo(new Efeito(TipoEfeito.SANGRAMENTO, 3, 10)));
    Ladino(String n) { super(n, 110, 60, 35, 7, 20); }
    @Override String getClasse() { return "Ladino"; }
    @Override String cor() { return Cor.AMARELO; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override String[] sprite() {
        return new String[] { "      .---.       /", "     ( ò_ó )     /", "    /|=====|\\   /",
            "   /_|     |_\\ /", "     |_/ \\_|       ", "     /_/ \\_\\       " };
    }
}

// =========================================================================
// ALIADOS (AGEM A CADA TURNO DO HERÓI)
// =========================================================================
