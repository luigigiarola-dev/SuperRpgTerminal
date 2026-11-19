package br.com.superrpg.jogo;

import java.util.List;

/** Informações para decidir uma ação, sem prever sorteios nem consumir um turno. */
public record TaticaCombate(String ordem, String defesa, String ameaca, List<Habilidade> habilidades) {
    public TaticaCombate { habilidades=List.copyOf(habilidades); }
    public record Habilidade(int id,String nome,String descricao,String elemento,int custo,
            int recarga,String indisponibilidade) {
        public boolean disponivel() { return indisponibilidade.isEmpty(); }
    }
}
