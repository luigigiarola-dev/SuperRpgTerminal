package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

final class Tela {
    static final int LARGURA = 66;
    static boolean rapido = false;
    static final Scanner IN = new Scanner(System.in);

    static String pad(String s, int w) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    static String linha(char ch) { return String.valueOf(ch).repeat(LARGURA); }

    static void titulo(String texto, String cor) {
        System.out.println(Cor.c(cor, linha('=')));
        int esq = Math.max(0, (LARGURA - texto.length()) / 2);
        System.out.println(Cor.c(cor + Cor.NEGRITO, " ".repeat(esq) + texto));
        System.out.println(Cor.c(cor, linha('=')));
    }

    static void caixa(String titulo, String... linhas) {
        int w = titulo.length() + 1;
        for (String l : linhas) w = Math.max(w, l.length());
        System.out.println("┌─ " + titulo + " " + "─".repeat(w - titulo.length() - 1) + "┐");
        for (String l : linhas) System.out.println("│ " + pad(l, w) + " │");
        System.out.println("└" + "─".repeat(w + 2) + "┘");
    }

    static String barra(int atual, int max, int tam, String cor) {
        int cheio = max > 0 ? (int) Math.round((double) atual / max * tam) : 0;
        cheio = Math.max(0, Math.min(tam, cheio));
        if (atual > 0 && cheio == 0) cheio = 1;
        return "[" + Cor.c(cor, "█".repeat(cheio)) + Cor.c(Cor.CINZA, "░".repeat(tam - cheio)) + "]";
    }

    static String corHp(int hp, int max) {
        double p = (double) hp / Math.max(1, max);
        return p > 0.5 ? Cor.VERDE : (p > 0.25 ? Cor.AMARELO : Cor.VERMELHO);
    }

    static void esperar(int ms) {
        if (rapido) return;
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    static void mostrarArte(String[] arte, String cor) {
        for (String l : arte) System.out.println(Cor.c(cor, l));
    }

    /** Fala de NPC com efeito de máquina de escrever. */
    static void fala(String npc, String texto) {
        System.out.println();
        System.out.println(Cor.c(Cor.AMARELO + Cor.NEGRITO, "💬 " + npc + ":"));
        System.out.print("   «");
        if (rapido) System.out.print(texto);
        else for (char c : texto.toCharArray()) { System.out.print(c); System.out.flush(); esperar(12); }
        System.out.println("»");
    }

    static void sair() {
        System.out.println("\nEntrada encerrada. Saindo do jogo.");
        System.exit(0);
    }

    static String lerTexto() {
        try { return IN.nextLine().trim(); }
        catch (NoSuchElementException e) { sair(); return ""; }
    }

    static int ler(int min, int max) {
        while (true) {
            String s = lerTexto();
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) return v;
                System.out.print("Fora do alcance (" + min + " a " + max + "): ");
            } catch (NumberFormatException e) {
                System.out.print("Digite apenas números: ");
            }
        }
    }

    static void enter() {
        if (rapido) return;
        System.out.print(Cor.c(Cor.CINZA, "  [Enter para continuar] "));
        lerTexto();
    }
}

// =========================================================================
// ARTES ASCII (LOCAIS, EVENTOS E EFEITOS)
// =========================================================================
