package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

abstract class Item {
    private final String nome, raridade, descricao, categoria;
    private final int preco;

    Item(String nome, String raridade, String categoria, String descricao, int preco) {
        this.nome = nome; this.raridade = raridade; this.categoria = categoria;
        this.descricao = descricao; this.preco = preco;
    }

    String getNome() { return nome; }
    String getRaridade() { return raridade; }
    String getCategoria() { return categoria; }
    String getDescricao() { return descricao; }
    int getPreco() { return preco; }
    int getPrecoVenda() { return preco / 2; }

    String raridadeColorida() {
        switch (raridade) {
            case "Raro": return Cor.c(Cor.AZUL, raridade);
            case "Épico": return Cor.c(Cor.MAGENTA, raridade);
            case "Lendário": return Cor.c(Cor.AMARELO, raridade);
            case "Comum": return Cor.c(Cor.CINZA, raridade);
            default: return Cor.c(Cor.VERDE, raridade);
        }
    }

    boolean usavelEmCombate() { return false; }
    boolean usavelFora() { return false; }

    /** Retorna true se o item foi consumido. alvo pode ser null fora de combate. */
    boolean usar(Heroi h, Monstro alvo) { return false; }

    String linha() { return getNome() + " [" + raridadeColorida() + "] - " + getDescricao(); }
}
