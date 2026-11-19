package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Beholder extends Monstro {
    Beholder() {
        super("Observador do Caos", 320, 38, 12, 10, 6, 350, 200, true, null, 0);
        afinidade.put(Elemento.SOMBRA, 0.5); afinidade.put(Elemento.LUZ, 1.5);
    }
    @Override String[] sprite() {
        return new String[] { "    \\  |  /     ", "   .-\\ | /-.    ", " /'  ( O )  '\\ ", "|  .-'---'-.  |",
            "|  \\  vvv  /  |", " \\  '-----'  / ", "  '-._____.-'   " };
    }
    @Override String cor() { return Cor.VERMELHO; }
    @Override String aviso() { return "O olho central do Observador começa a brilhar intensamente..."; }
    @Override void ataqueBasico(Heroi h) {
        Eventos.texto("\n👁️  O Observador dispara raios desintegradores!");

        h.receberDano(calcularAtaque(1.0), Elemento.RAIO, true);
    }
    @Override void especial(Heroi h) {
        Eventos.texto("\n🌑 O Observador fixa o olhar e queima sua mana!");

        h.receberDano(calcularAtaque(1.0).somar(10), Elemento.SOMBRA, true);
        h.modificarMana(-20);
        h.adicionarEfeito(new Efeito(TipoEfeito.LENTIDAO, 2, 0));
    }
    @Override void golpePesado(Heroi h) {
        Eventos.texto(Cor.c(Cor.VERMELHO + Cor.NEGRITO, "\n🔴 O GRANDE OLHO CENTRAL EMITE UM RAIO ANTIVITAL!"));

        h.receberDano(calcularAtaque(1.0).somar(40), Elemento.RAIO, false);
        h.modificarMana(-25);
    }
}
