package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class IrmaLira extends Aliado {
    IrmaLira() { super("Lira, a Curandeira", 200); }
    @Override String descricao() { return "Cura o herói e remove males de vez em quando"; }
    @Override String[] sprite() {
        return new String[] { "     .-.      ", "    (o.o)  +  ", "    /|+|\\     ", "     / \\      " };
    }
    @Override void agir(Heroi h, Monstro m) {
        if (h.getHp() < h.getHpMax() * 0.7 || Entidade.RNG.nextBoolean()) {
            Eventos.texto("\n💖 " + nome + " conjura uma bênção de cura!");
            h.curar(20 + h.getNivel() * 3);
            if (Entidade.RNG.nextInt(100) < 40) h.removerNegativos();
        } else {
            Eventos.texto("\n🙏 " + nome + " reza por você.");
            h.modificarMana(8);
        }
    }
}

// =========================================================================
// PARTE 4 - MONSTROS, CHEFES E BESTIÁRIO
// =========================================================================
