package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Material extends Item {
    Material(String nome, int preco, String desc) { super(nome, "Comum", "Material", desc, preco); }
}
