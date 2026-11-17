package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Bandido extends Monstro {
    private int roubado = 0;
    Bandido() { super("Bandido da Estrada", 90, 26, 9, 15, 2, 70, 40, false, null, 0); }
    @Override String[] sprite() {
        return new String[] { "      _____        ", "     /_____\\       ", "     ( -_- )  /|   ",
            "    /|[===]|\\/ |   ", "   / |     |  \\|   ", "     |_/ \\_|       " };
    }
    @Override String cor() { return Cor.AMARELO; }
    @Override void ataqueBasico(Heroi h) {
        Eventos.texto("\n🗡️  O Bandido ataca com a adaga!");

        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        int r = Math.min(h.getOuro(), 15 + RNG.nextInt(20));
        Eventos.texto("\n💰 O Bandido tenta bater sua carteira!");
        if (r > 0 && RNG.nextInt(100) < 70) {
            h.gastarOuro(r); roubado += r; ouroRec += r;
            Eventos.texto(Cor.c(Cor.AMARELO, "   Ele roubou " + r + " de ouro! Derrote-o para recuperar."));
        } else Eventos.texto("   Você protegeu seu ouro a tempo!");
    }
}
