package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

final class RandomUtil {
    static final Random R = new Random();
    static boolean chance(int pct) { return R.nextInt(100) < pct; }
    static int entre(int min, int max) { return min + R.nextInt(max - min + 1); }
}

// =========================================================================
// PARTE 6 - SISTEMA DE MISSÕES (DIÁRIO)
// =========================================================================
