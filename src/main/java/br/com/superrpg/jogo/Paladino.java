package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Paladino extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Julgamento da Luz", "Dano de Luz x2.0 e cura 30 HP", 20, 0, Elemento.LUZ, 2.0).cura(30),
        new Habilidade("Escudo Divino", "Escudo de 45 por 3 turnos", 15, 3, Elemento.LUZ, 0)
            .self(new Efeito(TipoEfeito.ESCUDO, 3, 45)),
        new Habilidade("Martelo Sagrado", "Dano de Luz x1.5 e atordoa", 25, 3, Elemento.LUZ, 1.5)
            .alvo(new Efeito(TipoEfeito.ATORDOADO, 1, 0)));
    Paladino(String n) { super(n, 160, 50, 30, 15, 8); }
    @Override String getClasse() { return "Paladino"; }
    @Override String cor() { return Cor.CIANO; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override String[] sprite() {
        return new String[] { "      .-^-.        ^", "     ( o_o )       |", "    /|#####|\\     -+-",
            "   [ |#####| ]======|", "      |_| |_|      |", "      /_/ \\_\\      v" };
    }
}
