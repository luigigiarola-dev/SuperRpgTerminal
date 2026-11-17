package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Arqueiro extends Heroi {
    private final List<Habilidade> hab = Arrays.asList(
        new Habilidade("Flecha Precisa", "Físico x1.8", 10, 0, Elemento.FISICO, 1.8),
        new Habilidade("Flecha Flamejante", "Fogo x1.4 e queima", 20, 1, Elemento.FOGO, 1.4)
            .alvo(new Efeito(TipoEfeito.QUEIMADURA, 3, 9)),
        new Habilidade("Chuva de Flechas", "Físico x2.6 e sangramento", 30, 3, Elemento.FISICO, 2.6)
            .alvo(new Efeito(TipoEfeito.SANGRAMENTO, 3, 8)));
    Arqueiro(String n) { super(n, 120, 60, 36, 8, 16); }
    @Override String getClasse() { return "Arqueiro"; }
    @Override String cor() { return Cor.VERDE; }
    @Override List<Habilidade> habilidades() { return hab; }
    @Override void atacar(Monstro alvo) {
        Eventos.texto("\n🏹 " + getNome() + " dispara uma flecha!");

        alvo.receberDano(calcularAtaque(1.0), elementoArma(), true);
    }
    @Override String[] sprite() {
        return new String[] { "      .-^-.      /|", "     ( -_- )    / |", "    /|=====|\\  (  |",
            "   / |  |  | \\  \\ |", "     |_/ \\_|    \\|", "     /_/ \\_\\       " };
    }
}
