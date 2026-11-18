package br.com.superrpg.jogo;

public record AcaoJogo(Tipo tipo, int indice) {
    public enum Tipo { ATACAR, HABILIDADE, DEFENDER, ITEM, FUGIR, TALENTO, VIAJAR, EXPLORAR, COLETAR, ACAMPAR, DESCANSAR, MISSAO, COMPRAR, EQUIPAR, CONTINUAR,
        VENDER, MELHORAR, CRIAR, CURAR, DOAR, CONTRATAR, DISPENSAR }
    public static AcaoJogo de(Tipo tipo) { return new AcaoJogo(tipo, 0); }
}
