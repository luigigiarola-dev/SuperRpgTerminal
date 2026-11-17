package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Missao {
    enum Tipo { CACAR, COLETAR, CHEFE }

    final String id, titulo, desc, doador, alvo, itemRec, requer;
    final Tipo tipo;
    final int qtd, ouro, xp, nivelMin;
    int estado = 0;      // 0 = disponível, 1 = ativa, 2 = entregue
    int progresso = 0;

    Missao(String id, String titulo, String desc, String doador, Tipo tipo, String alvo, int qtd,
           int ouro, int xp, String itemRec, int nivelMin, String requer) {
        this.id = id; this.titulo = titulo; this.desc = desc; this.doador = doador; this.tipo = tipo;
        this.alvo = alvo; this.qtd = qtd; this.ouro = ouro; this.xp = xp; this.itemRec = itemRec;
        this.nivelMin = nivelMin; this.requer = requer;
    }

    int atual(Heroi h) { return tipo == Tipo.COLETAR ? Math.min(qtd, h.contar(alvo)) : Math.min(qtd, progresso); }
    boolean pronta(Heroi h) { return estado == 1 && atual(h) >= qtd; }

    String objetivo(Heroi h) {
        String verbo = tipo == Tipo.COLETAR ? "Coletar " : (tipo == Tipo.CHEFE ? "Derrotar " : "Caçar ");
        return verbo + alvo + " (" + atual(h) + "/" + qtd + ")";
    }

    String recompensa() {
        return ouro + " ouro, " + xp + " XP" + (itemRec != null ? ", " + itemRec : "");
    }
}
