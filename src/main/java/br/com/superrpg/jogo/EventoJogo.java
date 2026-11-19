package br.com.superrpg.jogo;

/** Resultado imutável de uma ação; não conhece componentes gráficos. */
public record EventoJogo(Tipo tipo, String alvo, int valor, String texto, Impacto impacto) {
    public EventoJogo(Tipo tipo,String alvo,int valor,String texto) { this(tipo,alvo,valor,texto,null); }
    public record Impacto(boolean critico, String elemento, double afinidade, boolean guarda, int absorvido) {}
    public enum Tipo { TEXTO, DANO, CURA, ESQUIVA, BLOQUEIO, VITORIA, DERROTA, FUGA, NIVEL }
}
