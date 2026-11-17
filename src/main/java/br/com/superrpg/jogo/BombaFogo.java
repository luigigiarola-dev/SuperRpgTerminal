package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class BombaFogo extends Item {
    BombaFogo() { super("Bomba de Fogo", "Épico", "Consumível", "70 de dano de Fogo + queimadura", 60); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        Eventos.texto("💣 " + h.getNome() + " arremessa a Bomba de Fogo!");

        alvo.receberDano(70, Elemento.FOGO, false);
        alvo.adicionarEfeito(new Efeito(TipoEfeito.QUEIMADURA, 3, 8));
        return true;
    }
}
