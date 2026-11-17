package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

class Efeito {
    final TipoEfeito tipo;
    int turnos;
    int valor;

    Efeito(TipoEfeito tipo, int turnos, int valor) { this.tipo = tipo; this.turnos = turnos; this.valor = valor; }
    Efeito copia() { return new Efeito(tipo, turnos, valor); }
    boolean negativo() { return tipo != TipoEfeito.ESCUDO && tipo != TipoEfeito.FURIA; }
}
