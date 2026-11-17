package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

final class Efeitos {
    static final String[] CORTE = { "                 //   ", "              ///     ", "          ////  CORTE!", "       ///            " };
    static final String[] LUZ = { "        \\  |  /       ", "      -- ( * ) --     ", "        /  |  \\   LUZ!" };
    static final String[] SOMBRA = { "      ~ ~ ~ ~ ~       ", "     ~ (  @  ) ~   SOMBRA!", "      ~ ~ ~ ~ ~       " };
    static final String[] IMPACTO = { "       \\ | /          ", "      -- BAM! --      ", "       / | \\          " };
    static final String[] FOGO = { "      (  )   (   )    ", "     )  (  ) )  (  FOGO!", "    (  ) ( (  ) )     " };
    static final String[] RAIO = { "         /\\           ", "        /  \\  /\\      ", "       /___ \\/  \\  RAIO!", "           \\/         " };
    static final String[] FLECHA = { "   >>>--------->  *      ", "   >>>--------->    FLECHA!" };
    static final String[] VENENO = { "      .  o  .  O       ", "     o  VENENO  o      ", "      O  .  o  .       " };
    static final String[] GELO = { "      *  *  *  *       ", "     * PRISAO  * *     ", "      *  *  *  *       " };
    static final String[] ESCUDO = { "      .-------.      ", "     /  [###]  \\     ", "    |   [###]   |    ", "     \\   \\ /   /     ", "      '-._V_.-'      " };

    static void tocar(String[] arte, String cor) {
        Tela.mostrarArte(arte, cor);
        Tela.esperar(450);
    }

    static String[] doElemento(Elemento e) {
        switch (e) {
            case LUZ: return LUZ;
            case SOMBRA: return SOMBRA;
            case FOGO: return FOGO;
            case RAIO: return RAIO;
            default: return CORTE;
        }
    }

    static String corDoElemento(Elemento e) {
        switch (e) {
            case LUZ: return Cor.AMARELO;
            case SOMBRA: return Cor.MAGENTA;
            case FOGO: return Cor.VERMELHO;
            case RAIO: return Cor.CIANO;
            default: return Cor.BRANCO;
        }
    }
}

// =========================================================================
// PARTE 2 - EFEITOS DE STATUS, ITENS, EQUIPAMENTOS E FÁBRICA
// =========================================================================
