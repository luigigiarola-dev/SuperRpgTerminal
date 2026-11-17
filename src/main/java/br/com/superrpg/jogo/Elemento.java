package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

enum Elemento {
    FISICO("Físico", "⚔️"), LUZ("Luz", "✨"), SOMBRA("Sombra", "🌑"), FOGO("Fogo", "🔥"), RAIO("Raio", "⚡");
    final String nome, icone;
    Elemento(String n, String i) { nome = n; icone = i; }
}
