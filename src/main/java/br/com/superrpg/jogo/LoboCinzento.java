package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class LoboCinzento extends Aliado {
    LoboCinzento() { super("Fenrir, o Lobo Cinzento", 150); }
    @Override String descricao() { return "Morde o inimigo todo turno e causa sangramento"; }
    @Override String[] sprite() {
        return new String[] { "    /\\_/\\     ", "   ( o.o )~~  ", "    > ^ <     ", "   /|   |\\    " };
    }
    @Override void agir(Heroi h, Monstro m) {
        Eventos.texto("\n🐺 " + nome + " ataca o inimigo!");
        m.receberDano(18 + h.getNivel() * 3, Elemento.FISICO, false);
        if (Entidade.RNG.nextInt(100) < 35 && m.estaVivo()) m.adicionarEfeito(new Efeito(TipoEfeito.SANGRAMENTO, 3, 7));
    }
}
