package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Habilidade {
    final String nome, desc;
    final int custo, recarga;
    final Elemento elem;
    final double mult;
    Efeito efeitoAlvo, efeitoSelf;
    int cura = 0;
    boolean rouba = false;

    Habilidade(String nome, String desc, int custo, int recarga, Elemento elem, double mult) {
        this.nome = nome; this.desc = desc; this.custo = custo; this.recarga = recarga;
        this.elem = elem; this.mult = mult;
    }
    Habilidade alvo(Efeito e) { efeitoAlvo = e; return this; }
    Habilidade self(Efeito e) { efeitoSelf = e; return this; }
    Habilidade cura(int c) { cura = c; return this; }
    Habilidade rouba() { rouba = true; return this; }
}
