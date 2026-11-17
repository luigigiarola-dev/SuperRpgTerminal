package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

abstract class Monstro extends Entidade {
    final int nivelBase;
    int xpRec, ouroRec;
    final boolean chefe;
    final String dropNome;
    final int dropChance;
    protected boolean carregando = false, fase2 = false;

    Monstro(String nome, int hp, int atk, int def, int agi, int nivelBase, int xp, int ouro,
            boolean chefe, String drop, int dropChance) {
        super(nome, hp, 0, atk, def, agi);
        this.nivelBase = nivelBase; this.xpRec = xp; this.ouroRec = ouro;
        this.chefe = chefe; this.dropNome = drop; this.dropChance = dropChance;
    }

    abstract String[] sprite();
    abstract String cor();
    abstract void ataqueBasico(Heroi h);
    abstract void especial(Heroi h);
    void golpePesado(Heroi h) { especial(h); }
    String aviso() { return getNome() + " está reunindo energia..."; }
    void entrarFase2() { atk += 8; }

    /** Escala atributos conforme o nível do herói (e a noite). */
    void escalar(double f) {
        hpMax = (int) (hpMax * f); hp = hpMax;
        atk = (int) (atk * f);
        xpRec = (int) (xpRec * f);
        ouroRec = (int) (ouroRec * f);
    }

    void turno(Heroi h) {
        if (chefe && !fase2 && hp < hpMax / 2) {
            fase2 = true;
            Eventos.texto(Cor.c(Cor.VERMELHO + Cor.NEGRITO, "\n⚠️  " + getNome() + " entra em FÚRIA! Seu poder aumenta!"));
            entrarFase2();
            adicionarEfeito(new Efeito(TipoEfeito.FURIA, 99, 0));
        }
        if (carregando) { carregando = false; golpePesado(h); return; }
        if (chefe && RNG.nextInt(100) < 30) {
            carregando = true;
            Eventos.texto(Cor.c(Cor.AMARELO + Cor.NEGRITO, "\n⚠️  " + aviso() + " (DEFENDA-SE!)"));
            return;
        }
        if (RNG.nextInt(100) < 30) especial(h); else ataqueBasico(h);
    }

    String estadoCarga() { return carregando ? Cor.c(Cor.AMARELO + Cor.NEGRITO, " ⚠️ CARREGANDO GOLPE!") : ""; }
}
