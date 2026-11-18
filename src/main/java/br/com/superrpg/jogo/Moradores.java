package br.com.superrpg.jogo;

import java.util.List;

/** Catálogo imutável das conversas da vila, preservadas do terminal. */
public final class Moradores {
    private Moradores() {}
    public record Morador(String id, String nome, List<String> falas) {}
    public static final List<Morador> TODOS = List.of(
        new Morador("Mira", "Capitã Mira", List.of(
            "A vila está segura enquanto eu estiver de pé. Mas preciso de braços fortes.",
            "Os monstros estão mais ousados a cada lua. Quem me ajudar será bem recompensado.",
            "Um herói de verdade se prova nas estradas, não nas tavernas.")),
        new Morador("Lira", "Irmã Lira", List.of(
            "Que a luz o guie, viajante. Cuide bem dos seus ferimentos.",
            "As ervas lunares crescem na floresta, mas só brilham à noite.",
            "Se tiver doações, o templo sempre as aceita.")),
        new Morador("Borin", "Borin, o Ferreiro", List.of(
            "Aço de verdade pede fogo de verdade! Pedras de fogo, é isso que preciso.",
            "Traga sua arma e eu a deixo afiada. Armadura boa salva mais vidas que feitiço.",
            "Golens têm o coração de pedra de fogo, sabia? Raio neles!")),
        new Morador("Aldric", "Sábio Aldric", List.of(
            "Um Olho antigo observa tudo da Torre. E sob a montanha, Ignis aguarda.",
            "Só a Chave do Dragão abre o Covil. E só o Observador a guarda.",
            "Luz fere os mortos e os cultistas. Raio racha o ferro dos golens.")),
        new Morador("Gus", "Gus, o Taverneiro", List.of(
            "Uma cerveja e uma boa história! O que vai ser?",
            "Dizem que à noite os monstros carregam mais ouro... e mais dentes.",
            "Sábio Aldric sabe de coisas que nem os reis sabem."))
    );
}
