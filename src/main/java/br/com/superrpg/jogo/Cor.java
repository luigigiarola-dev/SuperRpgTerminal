package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

final class Cor {
    static boolean ativo = true;
    static final String RESET = "\u001B[0m", NEGRITO = "\u001B[1m";
    static final String VERMELHO = "\u001B[31m", VERDE = "\u001B[32m", AMARELO = "\u001B[33m";
    static final String AZUL = "\u001B[34m", MAGENTA = "\u001B[35m", CIANO = "\u001B[36m";
    static final String CINZA = "\u001B[90m", BRANCO = "\u001B[97m";
    static String c(String cor, String texto) { return ativo ? cor + texto + RESET : texto; }
}
