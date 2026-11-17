package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

final class Bestiario {
    static Monstro criar(String id) {
        switch (id) {
            case "Slime": return new Slime();
            case "Lobo": return new Lobo();
            case "Aranha": return new Aranha();
            case "Bandido": return new Bandido();
            case "Esqueleto": return new Esqueleto();
            case "Cultista": return new Cultista();
            case "Golem": return new GolemFerro();
            case "Beholder": return new Beholder();
            default: return new DragaoAncestral();
        }
    }

    /** Identificador usado nas missões (corresponde ao id acima). */
    static String id(Monstro m) {
        if (m instanceof Slime) return "Slime";
        if (m instanceof Lobo) return "Lobo";
        if (m instanceof Aranha) return "Aranha";
        if (m instanceof Bandido) return "Bandido";
        if (m instanceof Esqueleto) return "Esqueleto";
        if (m instanceof Cultista) return "Cultista";
        if (m instanceof GolemFerro) return "Golem";
        if (m instanceof Beholder) return "Beholder";
        return "Dragao";
    }

    static Monstro sortear(Local l, int nivelHeroi, boolean noite) {
        String[] pool;
        switch (l) {
            case FLORESTA: pool = new String[] { "Slime", "Slime", "Lobo", "Lobo", "Bandido" }; break;
            case CAVERNA: pool = new String[] { "Aranha", "Aranha", "Slime", "Bandido", "Lobo" }; break;
            case RUINAS: pool = new String[] { "Esqueleto", "Esqueleto", "Cultista", "Cultista", "Bandido" }; break;
            case MONTANHA: pool = new String[] { "Golem", "Golem", "Bandido", "Esqueleto", "Cultista" }; break;
            case TORRE: pool = new String[] { "Cultista", "Cultista" }; break;
            default: pool = new String[] { "Golem" };
        }
        Monstro m = criar(pool[Entidade.RNG.nextInt(pool.length)]);
        double f = 1.0 + 0.08 * Math.max(0, nivelHeroi - m.nivelBase);
        if (noite) f *= 1.15;
        if (f > 1.0) m.escalar(f);
        return m;
    }
}

// =========================================================================
// PARTE 5 - SISTEMA DE COMBATE POR TURNOS (VELOCIDADE, STATUS, FUGA)
// =========================================================================
