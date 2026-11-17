package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class PergaminhoRaio extends Item {
    PergaminhoRaio() { super("Pergaminho do Trovão", "Raro", "Consumível", "45 de dano de Raio + atordoa 1 turno", 55); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        Eventos.texto("📜 " + h.getNome() + " lê o Pergaminho do Trovão!");

        alvo.receberDano(45, Elemento.RAIO, false);
        alvo.adicionarEfeito(new Efeito(TipoEfeito.ATORDOADO, 1, 0));
        return true;
    }
}
