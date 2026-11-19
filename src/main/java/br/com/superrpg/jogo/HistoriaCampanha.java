package br.com.superrpg.jogo;

import java.util.List;

/** Textos e resumo imutáveis, sem dependência da interface nem efeitos na partida. */
public final class HistoriaCampanha {
    private HistoriaCampanha() {}

    public record Fala(String personagem, String texto) {}
    public record Encerramento(boolean venceu, String titulo, String texto, String nome,
            String classe, int nivel, int dias, int abates, int missoes, int totalMissoes,
            int honra, boolean hardcore, int pontuacao) {}

    static List<Fala> introducao(Heroi h) {
        return List.of(
            new Fala("Narrador", "Há muito tempo, o Olho do Caos despertou na Torre e o Dragão Ignis voltou a rugir nas montanhas."),
            new Fala("Narrador", h.getNome() + ", " + h.getClasse() + " sem fama, chega à Vila Aurora com uma espada enferrujada e muita coragem."),
            new Fala("Capitã Mira", "Viajante! Fale comigo e com os moradores. Há muito trabalho — e muito perigo — por aqui."));
    }

    static Encerramento encerrar(Heroi h, Diario diario, int dia, boolean hardcore, boolean venceu) {
        String texto = !venceu ? "Sua lenda termina aqui. Aurora guardará a memória de sua jornada."
            : h.getHonra() >= 2 ? "O povo canta seu nome: " + h.getNome() + " " + h.titulo() + ", salvador do reino!"
            : h.getHonra() <= -2 ? "Venceu o Dragão, mas as histórias contam que seu coração é mais frio que o gelo..."
            : "O reino respira aliviado. " + h.getNome() + " desaparece pela estrada, sem alarde.";
        int pontos = Math.max(0, h.getNivel() * 100 + h.getOuro() + h.abates * 10
            + diario.entregues() * 150 + (venceu ? 3000 : 0) + h.getHonra() * 20 - dia * 5);
        return new Encerramento(venceu, venceu ? "Aurora verá outro amanhecer" : "Uma lenda chega ao fim",
            texto, h.getNome(), h.getClasse(), h.getNivel(), dia, h.abates,
            diario.entregues(), diario.missoes.size(), h.getHonra(), hardcore, pontos);
    }
}
