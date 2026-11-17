package br.com.superrpg.jogo;

/** Resultado imutável de uma ação; não conhece componentes gráficos. */
public record EventoJogo(Tipo tipo, String alvo, int valor, String texto) {
    public enum Tipo { TEXTO, DANO, CURA, VITORIA, DERROTA, FUGA, NIVEL }
}
