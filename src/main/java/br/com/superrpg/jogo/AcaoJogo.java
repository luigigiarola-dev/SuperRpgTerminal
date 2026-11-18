package br.com.superrpg.jogo;

public record AcaoJogo(Tipo tipo, int indice, String texto) {
    public AcaoJogo(Tipo tipo, int indice) { this(tipo, indice, ""); }
    public enum Tipo { ATACAR, HABILIDADE, DEFENDER, ITEM, FUGIR, TALENTO, VIAJAR, EXPLORAR, COLETAR, ACAMPAR, DESCANSAR, MISSAO, COMPRAR, EQUIPAR, CONTINUAR,
        VENDER, MELHORAR, CRIAR, CURAR, DOAR, CONTRATAR, DISPENSAR,
        ESCOLHER_EVENTO, DADOS, RUMOR, CHARADA, RESPONDER }
    public static AcaoJogo de(Tipo tipo) { return new AcaoJogo(tipo, 0); }
}
