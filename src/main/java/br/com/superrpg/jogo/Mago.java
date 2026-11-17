package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Mago extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Bola de Fogo", "Fogo x2.0 e queima", 25, 0, Elemento.FOGO, 2.0)
            .alvo(new Efeito(TipoEfeito.QUEIMADURA, 3, 10)),
        new Habilidade("Relâmpago", "Raio x1.8", 20, 0, Elemento.RAIO, 1.8),
        new Habilidade("Prisão Arcana", "Atordoa por 2 turnos", 35, 5, Elemento.RAIO, 0)
            .alvo(new Efeito(TipoEfeito.ATORDOADO, 2, 0)));
    Mago(String n) { super(n, 90, 120, 40, 4, 9); }
    @Override String getClasse() { return "Mago"; }
    @Override String cor() { return Cor.AZUL; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override String[] sprite() {
        return new String[] { "        /\\         *", "       /__\\       -|-", "      ( ^_^ )      |",
            "     /|~~~~~|\\     |", "    / |~~~~~| \\    |", "      /_/ \\_\\          " };
    }
}
