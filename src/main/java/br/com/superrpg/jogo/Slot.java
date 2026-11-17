package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

enum Slot {
    ARMA("Arma"), ARMADURA("Armadura"), AMULETO("Amuleto");
    final String nome;
    Slot(String n) { nome = n; }
}
