package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Ensopado extends Item {
    Ensopado() { super("Ensopado Quente", "Comum", "Consumível", "Restaura 40 HP e 10 Mana (só fora de combate)", 20); }
    @Override boolean usavelFora() { return true; }
    @Override boolean usar(Heroi h, Monstro alvo) {
        Eventos.texto("🍲 O ensopado aquece o corpo. (+40 HP, +10 Mana)");
        h.curar(40); h.modificarMana(10);
        return true;
    }
}
