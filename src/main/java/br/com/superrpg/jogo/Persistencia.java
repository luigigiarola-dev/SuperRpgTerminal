package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

final class Persistencia {
    private static String ser(Item i) {
        if (i instanceof Equipamento) return ((Equipamento) i).getNomeBase() + "|" + ((Equipamento) i).melhoria;
        return i.getNome() + "|0";
    }

    private static Item deser(String s) {
        String[] p = s.split("\\|");
        Item it = Fabrica.criar(p[0]);
        if (it instanceof Equipamento && p.length > 1) ((Equipamento) it).melhoria = Integer.parseInt(p[1]);
        return it;
    }

    static void salvar() {
        Heroi h = SuperRpgTerminal.heroi;
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(SuperRpgTerminal.SAVE), "UTF-8"))) {
            w.println("v2");
            w.println(h.getClasse());
            w.println(h.getNome());
            w.println(h.nivel + "," + h.xp + "," + h.hp + "," + h.hpMax + "," + h.mana + "," + h.manaMax + "," + h.atk + ","
                + h.def + "," + h.agi + "," + h.ouro + "," + h.honra + "," + h.abates);
            StringBuilder eq = new StringBuilder();
            for (Equipamento e : h.equip) eq.append(e == null ? "-" : ser(e)).append(";");
            w.println(eq);
            StringBuilder inv = new StringBuilder();
            for (Item i : h.inv) inv.append(ser(i)).append(";");
            w.println(inv.length() == 0 ? "-" : inv);
            w.println(h.aliado == null ? "-" : (h.aliado instanceof LoboCinzento ? "lobo" : "lira"));
            w.println(SuperRpgTerminal.dia + "," + SuperRpgTerminal.periodo + "," + SuperRpgTerminal.local.name() + ","
                + SuperRpgTerminal.hardcore + "," + SuperRpgTerminal.charadaHoje);
            StringBuilder ms = new StringBuilder();
            for (Missao m : SuperRpgTerminal.diario.missoes) ms.append(m.id).append(":").append(m.estado).append(":").append(m.progresso).append(";");
            w.println(ms);
            System.out.println(Cor.c(Cor.VERDE, "💾 Jogo salvo em " + SuperRpgTerminal.SAVE + "."));
        } catch (Exception e) {
            System.out.println(Cor.c(Cor.VERMELHO, "❌ Não foi possível salvar (este ambiente pode não permitir gravar arquivos)."));
        }
    }

    static boolean carregar() {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(SuperRpgTerminal.SAVE), "UTF-8"))) {
            if (!"v2".equals(r.readLine())) return false;
            String classe = r.readLine();
            String nome = r.readLine();
            Heroi h;
            switch (classe) {
                case "Paladino": h = new Paladino(nome); break;
                case "Necromante": h = new Necromante(nome); break;
                case "Arqueiro": h = new Arqueiro(nome); break;
                case "Mago": h = new Mago(nome); break;
                default: h = new Ladino(nome);
            }
            String[] s = r.readLine().split(",");
            h.nivel = Integer.parseInt(s[0]); h.xp = Integer.parseInt(s[1]); h.hp = Integer.parseInt(s[2]);
            h.hpMax = Integer.parseInt(s[3]); h.mana = Integer.parseInt(s[4]); h.manaMax = Integer.parseInt(s[5]);
            h.atk = Integer.parseInt(s[6]); h.def = Integer.parseInt(s[7]); h.agi = Integer.parseInt(s[8]);
            h.ouro = Integer.parseInt(s[9]); h.honra = Integer.parseInt(s[10]); h.abates = Integer.parseInt(s[11]);
            String[] eq = r.readLine().split(";");
            for (int i = 0; i < 3 && i < eq.length; i++) if (!eq[i].equals("-")) h.equip[i] = (Equipamento) deser(eq[i]);
            String inv = r.readLine();
            if (!inv.equals("-")) for (String p : inv.split(";")) if (!p.isEmpty()) { Item it = deser(p); if (it != null) h.inv.add(it); }
            String al = r.readLine();
            if (al.equals("lobo")) h.aliado = new LoboCinzento();
            else if (al.equals("lira")) h.aliado = new IrmaLira();
            String[] t = r.readLine().split(",");
            Diario d = new Diario();
            String linhaMissoes = r.readLine();
            if (linhaMissoes != null) for (String p : linhaMissoes.split(";")) {
                if (p.isEmpty()) continue;
                String[] q = p.split(":");
                Missao m = d.obter(q[0]);
                if (m != null) { m.estado = Integer.parseInt(q[1]); m.progresso = Integer.parseInt(q[2]); }
            }
            SuperRpgTerminal.heroi = h;
            SuperRpgTerminal.diario = d;
            SuperRpgTerminal.dia = Integer.parseInt(t[0]);
            SuperRpgTerminal.periodo = Integer.parseInt(t[1]);
            SuperRpgTerminal.local = Local.valueOf(t[2]);
            SuperRpgTerminal.hardcore = Boolean.parseBoolean(t[3]);
            SuperRpgTerminal.charadaHoje = Boolean.parseBoolean(t[4]);
            System.out.println(Cor.c(Cor.VERDE, "📂 Jogo carregado: " + h.ficha()));
            return true;
        } catch (Exception e) {
            System.out.println(Cor.c(Cor.VERMELHO, "❌ Save corrompido ou inacessível."));
            return false;
        }
    }

    static int pontuacao() {
        Heroi h = SuperRpgTerminal.heroi;
        int p = h.getNivel() * 100 + h.getOuro() + h.abates * 10 + SuperRpgTerminal.diario.entregues() * 150
            + (SuperRpgTerminal.venceu ? 3000 : 0) + h.getHonra() * 20 - SuperRpgTerminal.dia * 5;
        return Math.max(0, p);
    }

    static void registrarRanking() {
        Heroi h = SuperRpgTerminal.heroi;
        try (PrintWriter w = new PrintWriter(new FileWriter(SuperRpgTerminal.RANKING, true))) {
            w.println(pontuacao() + ";" + h.getNome() + ";" + h.getClasse() + ";" + h.getNivel() + ";" + SuperRpgTerminal.dia
                + ";" + (SuperRpgTerminal.venceu ? "VENCEU" : "caiu"));
        } catch (Exception ignorada) { }
    }

    static void mostrarRanking() {
        ArrayList<String[]> linhas = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader(SuperRpgTerminal.RANKING))) {
            String l;
            while ((l = r.readLine()) != null) { String[] p = l.split(";"); if (p.length >= 6) linhas.add(p); }
        } catch (Exception ignorada) { }
        linhas.sort((a, b) -> Integer.parseInt(b[0]) - Integer.parseInt(a[0]));
        System.out.println();
        Tela.titulo("SALÃO DOS HERÓIS", Cor.AMARELO);
        if (linhas.isEmpty()) System.out.println("Nenhum herói no ranking ainda.");
        for (int i = 0; i < Math.min(5, linhas.size()); i++) {
            String[] p = linhas.get(i);
            System.out.println((i + 1) + ". " + Tela.pad(p[1], 14) + Tela.pad(p[2], 11) + "Nv " + Tela.pad(p[3], 3) + "Dia " + Tela.pad(p[4], 4)
                + Tela.pad(p[5], 8) + Cor.c(Cor.AMARELO, p[0] + " pts"));
        }
        Tela.enter();
    }

    static void telaVitoria() {
        Heroi h = SuperRpgTerminal.heroi;
        System.out.println();
        Tela.mostrarArte(Arte.TROFEU, Cor.AMARELO);
        Tela.titulo("👑 O DRAGÃO CAIU! VITÓRIA ABSOLUTA! 👑", Cor.AMARELO);
        String final1 = h.getHonra() >= 2 ? "O povo canta seu nome: " + h.getNome() + " " + h.titulo() + ", salvador do reino!"
            : (h.getHonra() <= -2 ? "Venceu o Dragão, mas as histórias contam que seu coração é mais frio que o gelo..."
            : "O reino respira aliviado. " + h.getNome() + " desaparece pela estrada, sem alarde.");
        Tela.fala("Narrador", final1);
        System.out.println("\n  Nível final: " + h.getNivel() + " | Dias: " + SuperRpgTerminal.dia + " | Abates: " + h.abates
            + " | Missões: " + SuperRpgTerminal.diario.entregues() + "/" + SuperRpgTerminal.diario.missoes.size());
        System.out.println(Cor.c(Cor.AMARELO, "  Pontuação: " + pontuacao()));
        Tela.enter();
    }

    static void telaDerrota() {
        System.out.println();
        Tela.mostrarArte(Arte.CAVEIRA, Cor.VERMELHO);
        Tela.titulo("GAME OVER - Sua lenda termina aqui...", Cor.VERMELHO);
        System.out.println("  Pontuação: " + pontuacao());
        Tela.enter();
    }
}
