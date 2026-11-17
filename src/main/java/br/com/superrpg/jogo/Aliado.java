package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

abstract class Aliado {
    final String nome;
    final int preco;
    Aliado(String nome, int preco) { this.nome = nome; this.preco = preco; }
    abstract String[] sprite();
    abstract String descricao();
    abstract void agir(Heroi h, Monstro m);
}
