package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

enum Local {
    VILA("Vila Aurora", 0), FLORESTA("Floresta Sussurrante", 1), CAVERNA("Caverna das Aranhas", 2),
    RUINAS("Ruínas Esquecidas", 3), MONTANHA("Montanha de Ferro", 5), TORRE("Torre do Observador", 6),
    COVIL("Covil de Ignis", 9);
    final String nome;
    final int nivelRec;
    Local(String n, int nv) { nome = n; nivelRec = nv; }

    String[] arte() {
        switch (this) {
            case VILA: return Arte.VILA;
            case FLORESTA: return Arte.FLORESTA;
            case CAVERNA: return Arte.CAVERNA;
            case RUINAS: return Arte.RUINAS;
            case MONTANHA: return Arte.MONTANHA;
            case TORRE: return Arte.TORRE;
            default: return Arte.COVIL;
        }
    }

    String cor() {
        switch (this) {
            case FLORESTA: return Cor.VERDE;
            case CAVERNA: return Cor.MAGENTA;
            case RUINAS: return Cor.AMARELO;
            case MONTANHA: return Cor.CINZA;
            case TORRE: return Cor.AZUL;
            case COVIL: return Cor.VERMELHO;
            default: return Cor.CIANO;
        }
    }

    String material() {
        switch (this) {
            case FLORESTA: return "Erva Lunar";
            case CAVERNA: return "Presa de Aranha";
            case RUINAS: return "Essência Arcana";
            case MONTANHA: return "Pedra de Fogo";
            default: return null;
        }
    }
}
