package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

final class Diario {
    final ArrayList<Missao> missoes = new ArrayList<>();
    final HashSet<String> flags = new HashSet<>();

    Diario() {
        add("praga", "Praga na Floresta", "Gosmas ácidas estão destruindo as plantações.", "Mira",
            Missao.Tipo.CACAR, "Slime", 3, 60, 80, null, 1, null);
        add("lobos", "Lobos Famintos", "Lobos atacam viajantes na Floresta.", "Mira",
            Missao.Tipo.CACAR, "Lobo", 3, 90, 100, "Gibão de Couro", 1, null);
        add("ervas", "Ervas para a Irmã Lira", "Lira precisa de Erva Lunar para seus remédios.", "Lira",
            Missao.Tipo.COLETAR, "Erva Lunar", 3, 40, 70, "Poção de Cura", 1, null);
        add("ladrao", "Ladrão da Estrada", "Bandidos assaltam comerciantes.", "Mira",
            Missao.Tipo.CACAR, "Bandido", 2, 100, 140, "Anel Veloz", 2, "lobos");
        add("teias", "Teias Sombrias", "Aranhas dominaram a Caverna.", "Mira",
            Missao.Tipo.CACAR, "Aranha", 3, 120, 160, "Antídoto", 2, "praga");
        add("fogo", "Pedras para a Forja", "Borin precisa de Pedra de Fogo para forjar uma arma.", "Borin",
            Missao.Tipo.COLETAR, "Pedra de Fogo", 2, 50, 120, "Lâmina de Aço", 2, null);
        add("ossos", "Ossos Inquietos", "Mortos-vivos vagam pelas Ruínas.", "Mira",
            Missao.Tipo.CACAR, "Esqueleto", 4, 150, 220, null, 3, "teias");
        add("seita", "A Seita do Olho", "Cultistas adoram algo na Torre.", "Aldric",
            Missao.Tipo.CACAR, "Cultista", 3, 160, 260, "Elixir de Mana", 4, "ossos");
        add("torre", "O Olho Que Tudo Vê", "Destrua o Observador do Caos no topo da Torre.", "Aldric",
            Missao.Tipo.CHEFE, "Beholder", 1, 300, 500, "Cajado Radiante", 5, "seita");
        add("dragao", "A Fúria de Ignis", "Com a Chave do Dragão, derrote Ignis no seu Covil.", "Aldric",
            Missao.Tipo.CHEFE, "Dragao", 1, 1000, 1000, null, 7, "torre");
    }

    private void add(String id, String t, String d, String doador, Missao.Tipo tipo, String alvo, int qtd,
                     int ouro, int xp, String item, int nv, String req) {
        missoes.add(new Missao(id, t, d, doador, tipo, alvo, qtd, ouro, xp, item, nv, req));
    }

    Missao obter(String id) {
        for (Missao m : missoes) if (m.id.equals(id)) return m;
        return null;
    }

    boolean concluida(String id) { Missao m = obter(id); return m != null && m.estado == 2; }

    /** Missões que o doador pode oferecer agora. */
    ArrayList<Missao> disponiveis(String doador, Heroi h) {
        ArrayList<Missao> r = new ArrayList<>();
        for (Missao m : missoes) {
            if (!m.doador.equals(doador) || m.estado != 0) continue;
            if (h.getNivel() < m.nivelMin) continue;
            if (m.requer != null && !concluida(m.requer)) continue;
            r.add(m);
        }
        return r;
    }

    ArrayList<Missao> prontas(String doador, Heroi h) {
        ArrayList<Missao> r = new ArrayList<>();
        for (Missao m : missoes) if (m.doador.equals(doador) && m.pronta(h)) r.add(m);
        return r;
    }

    int ativas() { int n = 0; for (Missao m : missoes) if (m.estado == 1) n++; return n; }
    int entregues() { int n = 0; for (Missao m : missoes) if (m.estado == 2) n++; return n; }

    /** Chamado quando um monstro é derrotado. */
    void registrarAbate(String idMonstro) {
        for (Missao m : missoes) {
            if (m.estado == 1 && (m.tipo == Missao.Tipo.CACAR || m.tipo == Missao.Tipo.CHEFE)
                && m.alvo.equals(idMonstro) && m.progresso < m.qtd) {
                m.progresso++;
                Eventos.texto(Cor.c(Cor.AMARELO, "📜 Missão \"" + m.titulo + "\": " + m.progresso + "/" + m.qtd
                    + (m.progresso >= m.qtd ? " — PRONTA PARA ENTREGAR!" : "")));
            }
        }
    }

    void mostrar(Heroi h) {
        Eventos.texto();

        boolean algum = false;
        for (Missao m : missoes) {
            if (m.estado != 1) continue;
            algum = true;
            String status = m.pronta(h) ? Cor.c(Cor.VERDE, " [ENTREGAR para " + m.doador + "]") : "";
            Eventos.texto(Cor.c(Cor.NEGRITO, "• " + m.titulo) + status);
            Eventos.texto("    " + m.objetivo(h) + "  |  Recompensa: " + m.recompensa());
        }
        if (!algum) Eventos.texto("Nenhuma missão ativa. Converse com os moradores da vila!");
        Eventos.texto(Cor.c(Cor.CINZA, "Missões concluídas: " + entregues() + "/" + missoes.size()));
    }
}

// =========================================================================
// PARTE 7 - ENGINE DO JOGO: MENUS, VILA, NPCs, LOJAS, TAVERNA E TEMPLO
// =========================================================================
