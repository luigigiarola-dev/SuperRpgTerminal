package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

final class Combate {
    enum Resultado { VITORIA, DERROTA, FUGA }

    static Resultado lutar(Heroi h, Monstro m, boolean podeFugir) {
        h.limparEfeitos();
        h.recargas.clear();
        while (true) {
            boolean heroiPrimeiro = h.getAgilidade() >= m.getAgilidade();
            for (int i = 0; i < 2; i++) {
                boolean vezHeroi = (i == 0) == heroiPrimeiro;
                if (vezHeroi) {
                    if (turnoHeroi(h, m, podeFugir)) { h.limparEfeitos(); return Resultado.FUGA; }
                } else {
                    turnoMonstro(h, m);
                }
                if (!h.estaVivo()) return Resultado.DERROTA;
                if (!m.estaVivo()) { h.limparEfeitos(); return Resultado.VITORIA; }
            }
            Tela.esperar(500);
        }
    }

    private static boolean turnoHeroi(Heroi h, Monstro m, boolean podeFugir) {
        h.setDefendendo(false);
        h.tickRecargas();
        boolean agir = h.iniciarTurno();
        if (!h.estaVivo()) return false;
        if (!agir) {
            System.out.println(Cor.c(Cor.AMARELO, "💫 " + h.getNome() + " está atordoado e perde o turno!"));
            return false;
        }
        desenharCampo(h, m);
        boolean gasto = false;
        while (!gasto) {
            System.out.println("┌──────────────────────────────────────────────────────────────┐");
            System.out.println("│ 1.Atacar 2.Habilidade 3.Defender 4.Item 5.Analisar 6.Fugir   │");
            System.out.println("└──────────────────────────────────────────────────────────────┘");
            System.out.print("Seu comando: ");
            switch (Tela.ler(1, 6)) {
                case 1: h.atacar(m); gasto = true; break;
                case 2: gasto = menuHabilidades(h, m); break;
                case 3:
                    h.setDefendendo(true);
                    Efeitos.tocar(Efeitos.ESCUDO, Cor.CIANO);
                    System.out.println("🛡️  " + h.getNome() + " ergue a guarda e recupera o fôlego! (+8 Mana)");
                    h.modificarMana(8);
                    gasto = true;
                    break;
                case 4: gasto = menuItens(h, m); break;
                case 5: analisar(m); break;
                default:
                    if (!podeFugir) { System.out.println("❌ Não há como fugir desta batalha!"); break; }
                    int chance = Math.max(15, Math.min(85, 45 + (h.getAgilidade() - m.getAgilidade()) * 2));
                    if (RandomUtil.chance(chance)) {
                        System.out.println("🏃 " + h.getNome() + " escapou da batalha!");
                        return true;
                    }
                    System.out.println("❌ A fuga falhou! O inimigo bloqueou o caminho.");
                    gasto = true;
            }
        }
        if (m.estaVivo() && h.estaVivo() && h.aliado != null) h.aliado.agir(h, m);
        h.modificarMana(4);
        return false;
    }

    private static void turnoMonstro(Heroi h, Monstro m) {
        Tela.esperar(600);
        boolean agir = m.iniciarTurno();
        if (!m.estaVivo()) return;
        if (!agir) {
            System.out.println(Cor.c(Cor.AMARELO, "💫 " + m.getNome() + " está atordoado e perde o turno!"));
            if (m.carregando) {
                m.carregando = false;
                System.out.println(Cor.c(Cor.VERDE, "✅ O golpe carregado foi INTERROMPIDO!"));
            }
            return;
        }
        m.turno(h);
    }

    private static boolean menuHabilidades(Heroi h, Monstro m) {
        List<Habilidade> hs = h.habilidades();
        System.out.println("\n✨ HABILIDADES (Mana " + h.getMana() + "/" + h.getManaMax() + ")");
        for (int i = 0; i < hs.size(); i++) {
            Habilidade x = hs.get(i);
            int rc = h.recargas.getOrDefault(x.nome, 0);
            String estado = rc > 0 ? Cor.c(Cor.VERMELHO, " [recarga: " + rc + "]")
                : (h.getMana() < x.custo ? Cor.c(Cor.VERMELHO, " [sem mana]") : "");
            System.out.println((i + 1) + ". " + Tela.pad(x.nome, 19) + " " + Tela.pad(x.custo + " mana", 8) + " "
                + x.elem.icone + " " + x.desc + estado);
        }
        System.out.println("0. Voltar");
        System.out.print("Escolha: ");
        int op = Tela.ler(0, hs.size());
        if (op == 0) return false;
        Habilidade x = hs.get(op - 1);
        if (!h.podeUsar(x)) {
            System.out.println("❌ Não é possível usar " + x.nome + " agora (mana insuficiente ou em recarga).");
            return false;
        }
        h.usarHabilidade(x, m);
        return true;
    }

    private static boolean menuItens(Heroi h, Monstro m) {
        ArrayList<Item> usaveis = new ArrayList<>();
        for (Item i : h.inv) if (i.usavelEmCombate()) usaveis.add(i);
        if (usaveis.isEmpty()) { System.out.println("🎒 Você não tem itens utilizáveis em combate."); return false; }
        System.out.println("\n🎒 ITENS");
        for (int i = 0; i < usaveis.size(); i++) System.out.println((i + 1) + ". " + usaveis.get(i).linha());
        System.out.println("0. Voltar");
        System.out.print("Escolha: ");
        int op = Tela.ler(0, usaveis.size());
        if (op == 0) return false;
        Item it = usaveis.get(op - 1);
        if (it.usar(h, m)) { h.inv.remove(it); return true; }
        return false;
    }

    private static void analisar(Monstro m) {
        StringBuilder fraco = new StringBuilder(), resiste = new StringBuilder();
        for (Elemento e : Elemento.values()) {
            double a = m.afinidade.getOrDefault(e, 1.0);
            if (a > 1.0) fraco.append(e.icone).append(e.nome).append(" ");
            if (a < 1.0) resiste.append(e.icone).append(e.nome).append(" ");
        }
        Tela.caixa("ANÁLISE: " + m.getNome(),
            "HP " + m.getHp() + "/" + m.getHpMax() + "   ATK " + m.getAtaque() + "   DEF " + m.getDefesa() + "   AGI " + m.getAgilidade(),
            "Fraco a:   " + (fraco.length() == 0 ? "-" : fraco),
            "Resiste a: " + (resiste.length() == 0 ? "-" : resiste),
            m.chefe ? "CHEFE: prepara golpes devastadores. Defenda-se ou atordoe-o!" : "Criatura comum.");
    }

    static void desenharCampo(Heroi h, Monstro m) {
        System.out.println();
        Tela.titulo("CAMPO DE BATALHA", Cor.AMARELO);
        String[] sh = h.sprite();
        String[] se = m.sprite();
        int altura = Math.max(sh.length, se.length);
        int larguraH = 0;
        for (String s : sh) larguraH = Math.max(larguraH, s.length());
        larguraH += 6;
        for (int i = 0; i < altura; i++) {
            int ih = i - (altura - sh.length);
            int ie = i - (altura - se.length);
            String lh = ih >= 0 ? sh[ih] : "";
            String le = ie >= 0 ? se[ie] : "";
            System.out.println(Cor.c(h.cor(), Tela.pad(lh, larguraH)) + Cor.c(m.cor(), le));
        }
        System.out.println(Cor.c(Cor.CINZA, Tela.linha('▔')));
        System.out.println(Cor.c(h.cor() + Cor.NEGRITO, h.getNome()) + " (Nv " + h.getNivel() + " " + h.getClasse() + ")"
            + (h.isDefendendo() ? " 🛡️" : "") + h.resumoEfeitos());
        System.out.println("  HP   " + Tela.barra(h.getHp(), h.getHpMax(), 24, Tela.corHp(h.getHp(), h.getHpMax()))
            + " " + h.getHp() + "/" + h.getHpMax());
        System.out.println("  MANA " + Tela.barra(h.getMana(), h.getManaMax(), 24, Cor.AZUL)
            + " " + h.getMana() + "/" + h.getManaMax());
        if (h.aliado != null) System.out.println("  Aliado: " + h.aliado.nome);
        System.out.println(Cor.c(m.cor() + Cor.NEGRITO, m.getNome()) + m.resumoEfeitos() + m.estadoCarga());
        System.out.println("  HP   " + Tela.barra(m.getHp(), m.getHpMax(), 24, Tela.corHp(m.getHp(), m.getHpMax()))
            + " " + m.getHp() + "/" + m.getHpMax());
        System.out.println(Cor.c(Cor.AMARELO, Tela.linha('=')));
    }
}
