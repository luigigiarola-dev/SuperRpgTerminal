package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Antidoto extends Item {
    Antidoto() { super("Antídoto", "Comum", "Consumível", "Remove veneno, queimadura e sangramento", 25); }
    @Override boolean usavelEmCombate() { return true; }
    @Override boolean usavelFora() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        boolean limpou = h.removerNegativos();
        if (!limpou) { Eventos.texto("Você não está sofrendo de nenhum mal."); return false; }
        Eventos.texto("💚 " + h.getNome() + " tomou o Antídoto e se sente purificado!");
        return true;
    }
}
