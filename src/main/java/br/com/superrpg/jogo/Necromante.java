package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Necromante extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Sifão da Alma", "Sombra x1.4 e rouba metade do dano", 30, 0, Elemento.SOMBRA, 1.4).rouba(),
        new Habilidade("Praga Mortal", "Veneno de 12 por 4 turnos", 20, 2, Elemento.SOMBRA, 0.6)
            .alvo(new Efeito(TipoEfeito.VENENO, 4, 12)),
        new Habilidade("Explosão Necrótica", "Sombra x3.0 (devastador)", 50, 4, Elemento.SOMBRA, 3.0));
    Necromante(String n) { super(n, 100, 100, 42, 5, 10); }
    @Override String getClasse() { return "Necromante"; }
    @Override String cor() { return Cor.MAGENTA; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override String[] sprite() {
        return new String[] { "      .-~~-.        @", "     / v  v \\       |", "    ~|  /\\  |~      |",
            "   ~~|  ||  |~~     |", "     |  XX  |       |", "    /___||___\\      |" };
    }
}
