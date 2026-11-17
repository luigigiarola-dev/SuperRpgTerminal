package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

final class Fabrica {
    static final String[] CONSUMIVEIS = { "Poção de Cura", "Elixir de Mana", "Antídoto", "Ensopado Quente",
        "Bomba de Fogo", "Pergaminho do Trovão" };
    static final String[] EQUIPAMENTOS_LOJA = { "Lâmina de Aço", "Adaga Sombria", "Lança do Trovão", "Machado Flamejante",
        "Gibão de Couro", "Cota de Malha", "Placa Rúnica", "Anel Veloz", "Amuleto Vital" };

    static Item criar(String nome) {
        switch (nome) {
            case "Poção de Cura": return new Pocao();
            case "Elixir de Mana": return new Elixir();
            case "Antídoto": return new Antidoto();
            case "Ensopado Quente": return new Ensopado();
            case "Bomba de Fogo": return new BombaFogo();
            case "Pergaminho do Trovão": return new PergaminhoRaio();
            case "Erva Lunar": return new Material("Erva Lunar", 8, "Erva medicinal que brilha ao luar");
            case "Presa de Aranha": return new Material("Presa de Aranha", 12, "Presa repleta de veneno");
            case "Pedra de Fogo": return new Material("Pedra de Fogo", 15, "Pedra que arde sem se consumir");
            case "Essência Arcana": return new Material("Essência Arcana", 20, "Magia condensada em cristal");
            case "Gosma": return new Material("Gosma", 5, "Gosma viscosa de slime");
            case "Chave do Dragão": return new ItemMissao("Chave do Dragão", "Abre o Covil de Ignis");
            // Armas
            case "Espada Enferrujada": return new Equipamento(nome, "Comum", 15, Slot.ARMA, 3, 0, 0, Elemento.FISICO);
            case "Lâmina de Aço": return new Equipamento(nome, "Incomum", 120, Slot.ARMA, 7, 0, 0, Elemento.FISICO);
            case "Adaga Sombria": return new Equipamento(nome, "Raro", 220, Slot.ARMA, 8, 0, 4, Elemento.SOMBRA);
            case "Lança do Trovão": return new Equipamento(nome, "Raro", 260, Slot.ARMA, 10, 0, 0, Elemento.RAIO);
            case "Machado Flamejante": return new Equipamento(nome, "Épico", 380, Slot.ARMA, 14, 0, -2, Elemento.FOGO);
            case "Cajado Radiante": return new Equipamento(nome, "Épico", 400, Slot.ARMA, 12, 2, 0, Elemento.LUZ);
            // Armaduras
            case "Gibão de Couro": return new Equipamento(nome, "Comum", 60, Slot.ARMADURA, 0, 4, 1, Elemento.FISICO);
            case "Cota de Malha": return new Equipamento(nome, "Incomum", 180, Slot.ARMADURA, 0, 8, 0, Elemento.FISICO);
            case "Placa Rúnica": return new Equipamento(nome, "Épico", 420, Slot.ARMADURA, 0, 14, -2, Elemento.FISICO);
            // Amuletos
            case "Anel Veloz": return new Equipamento(nome, "Incomum", 140, Slot.AMULETO, 0, 0, 7, Elemento.FISICO);
            case "Amuleto Vital": return new Equipamento(nome, "Raro", 200, Slot.AMULETO, 4, 4, 0, Elemento.FISICO);
            default: return null;
        }
    }

    /** Receitas de alquimia: nome do resultado -> ingredientes. */
    static final String[][] RECEITAS = {
        { "Poção de Cura", "Erva Lunar", "Erva Lunar" },
        { "Antídoto", "Presa de Aranha", "Erva Lunar" },
        { "Elixir de Mana", "Essência Arcana", "Essência Arcana" },
        { "Bomba de Fogo", "Pedra de Fogo", "Pedra de Fogo" },
        { "Pergaminho do Trovão", "Essência Arcana", "Pedra de Fogo" },
        { "Ensopado Quente", "Erva Lunar", "Gosma" } };
}

// =========================================================================
// PARTE 3 - HABILIDADES, ENTIDADE BASE, HERÓIS E ALIADOS
// =========================================================================
