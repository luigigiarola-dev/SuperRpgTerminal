package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class GolemFerro extends Monstro {
    GolemFerro() {
        super("Constructo de Ferro", 160, 34, 22, 3, 5, 120, 45, false, "Pedra de Fogo", 50);
        afinidade.put(Elemento.FISICO, 0.5); afinidade.put(Elemento.RAIO, 1.75); afinidade.put(Elemento.SOMBRA, 0.75);
    }
    @Override String[] sprite() {
        return new String[] { "     [=======]    ", "     |[O___O]|    ", "  [M]|#######|[M] ",
            "  [_]|#######|[_] ", "     |_|||||_|    ", "     [__] [__]    " };
    }
    @Override String cor() { return Cor.CINZA; }
    @Override void ataqueBasico(Heroi h) {
        Eventos.texto("\n🔨 O Golem ergue os punhos maciços e esmaga o chão!");

        h.receberDano(calcularAtaque(1.0), Elemento.FISICO, true);
    }
    @Override void especial(Heroi h) {
        Eventos.texto("\n⚙️  O Golem libera uma ONDA DE CHOQUE METÁLICA!");

        h.receberDano(calcularAtaque(1.5), Elemento.FISICO, false);
    }
}
