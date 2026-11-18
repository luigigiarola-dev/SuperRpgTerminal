package br.com.superrpg.jogo;

import java.util.List;

/** Fotografia imutável do jogo: a interface não recebe entidades mutáveis. */
public record EstadoJogo(Fase fase, Personagem heroi, Personagem inimigo, String classe,
        String local, String localId, int dia, String periodo, int ouro, int xp, int xpProximo,
        int honra, int talentos, boolean hardcore, int rodada, List<Opcao> habilidades,
        List<Opcao> inventario, List<Opcao> locais, List<Opcao> missoes, List<Opcao> loja,
        List<String> equipamentos, Vila vila, Narrativa narrativa, boolean charadaHoje) {
    public record Narrativa(String titulo, String texto, List<Opcao> escolhas, boolean respostaLivre) {}
    public record Vila(List<Opcao> vendas, List<Opcao> forja, List<Opcao> receitas,
            List<Opcao> cura, List<Opcao> doacao, List<Opcao> aliados, List<Opcao> dispensa,
            String aliadoAtual, List<Opcao> descanso) {}
    public enum Fase { EXPLORACAO, EVENTO, COMBATE, RECOMPENSA, VITORIA, DERROTA }
    public record Personagem(String nome, String tipo, int hp, int hpMax, int mana, int manaMax,
            int nivel, int ataque, int defesa, int agilidade, String efeitos, boolean carregando, String afinidades) {}
    public record Opcao(int id, String nome, String descricao, boolean disponivel) {}
}
