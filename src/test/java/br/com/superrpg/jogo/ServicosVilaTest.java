package br.com.superrpg.jogo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import static br.com.superrpg.jogo.AcaoJogo.Tipo.*;

class ServicosVilaTest {
    private MotorJogo jogo(int sorteio) {
        MotorJogo j = new MotorJogo("Aurora", "Paladino", false, new Random() {
            @Override public int nextInt(int limite) { return sorteio; }
        });
        j.heroi.ouro = 5000;
        return j;
    }
    private void executar(MotorJogo j, AcaoJogo.Tipo tipo, int id) { j.executar(new AcaoJogo(tipo, id)); }

    @Test void vendaUsaIndiceOriginalEPreservaItemDeMissao() {
        MotorJogo j = jogo(0);j.heroi.inv.clear();
        j.heroi.inv.add(Fabrica.criar("Chave do Dragão"));j.heroi.inv.add(new Pocao());
        var venda = j.estado().vila().vendas().getFirst();assertEquals(1, venda.id());
        executar(j,VENDER,0);assertEquals(5000,j.heroi.ouro);assertEquals(2,j.heroi.inv.size());
        int valor = j.heroi.inv.get(1).getPrecoVenda();executar(j,VENDER,1);
        assertEquals(5000+valor,j.heroi.ouro);assertEquals(1,j.heroi.contar("Chave do Dragão"));
        assertNotNull(j.heroi.equip[0]);
    }

    @Test void vendaPreservaPrecoLegadoMesmoComMelhoriaEHonra() {
        MotorJogo j=jogo(0);Equipamento e=(Equipamento)Fabrica.criar("Lâmina de Aço");
        e.melhoria=5;j.heroi.honra=5;j.heroi.inv.clear();j.heroi.inv.add(e);
        executar(j,VENDER,0);assertEquals(5060,j.heroi.ouro);
    }

    @Test void forjaPreservaCustosChancesELimite() {
        MotorJogo j=jogo(0);
        int custo=0;
        for(int i=0;i<5;i++){custo+=60*(i+1);executar(j,MELHORAR,0);assertEquals(i+1,j.heroi.equip[0].melhoria);}
        assertEquals(5000-custo,j.heroi.ouro);assertFalse(j.estado().vila().forja().getFirst().disponivel());
        executar(j,MELHORAR,0);assertEquals(5000-custo,j.heroi.ouro);
        assertEquals(13,j.heroi.equip[0].getAtk());
    }

    @Test void limitesDeProbabilidadeDaForja() {
        int[] chances={100,100,80,60,45};
        for(int nivel=0;nivel<5;nivel++) {
            MotorJogo sucesso=jogo(chances[nivel]-1);sucesso.heroi.equip[0].melhoria=nivel;
            executar(sucesso,MELHORAR,0);assertEquals(nivel+1,sucesso.heroi.equip[0].melhoria);
            if(chances[nivel]<100) {
                MotorJogo falha=jogo(chances[nivel]);falha.heroi.equip[0].melhoria=nivel;
                executar(falha,MELHORAR,0);assertEquals(nivel,falha.heroi.equip[0].melhoria);
                assertEquals(5000-60*(nivel+1),falha.heroi.ouro);
            }
        }
    }

    @Test void forjaSemPecaSemOuroEAmuletoNaoConsomeRecursos() {
        MotorJogo j=jogo(0);executar(j,MELHORAR,1);executar(j,MELHORAR,2);
        assertEquals(5000,j.heroi.ouro);j.heroi.ouro=59;executar(j,MELHORAR,0);
        assertEquals(59,j.heroi.ouro);assertEquals(0,j.heroi.equip[0].melhoria);
    }

    @Test void todasAsReceitasConsomemQuantidadesExatasSemOuro() {
        for(int i=0;i<Fabrica.RECEITAS.length;i++) {
            MotorJogo j=jogo(0);j.heroi.inv.clear();String[] r=Fabrica.RECEITAS[i];
            for(int k=1;k<r.length;k++)j.heroi.inv.add(Fabrica.criar(r[k]));
            assertTrue(j.estado().vila().receitas().get(i).disponivel());executar(j,CRIAR,i);
            assertEquals(1,j.heroi.inv.size());assertEquals(r[0],j.heroi.inv.getFirst().getNome());
            assertEquals(5000,j.heroi.ouro);
        }
    }

    @Test void receitaIncompletaNaoConsomeIngredientes() {
        MotorJogo j=jogo(0);j.heroi.inv.clear();j.heroi.inv.add(Fabrica.criar("Erva Lunar"));
        assertFalse(j.estado().vila().receitas().getFirst().disponivel());
        assertTrue(j.estado().vila().receitas().getFirst().descricao().contains("1/2"));
        executar(j,CRIAR,0);assertEquals(1,j.heroi.contar("Erva Lunar"));assertEquals(1,j.heroi.inv.size());
    }

    @Test void temploRestauraRecursosRemoveMalesENaoAvancaTempo() {
        MotorJogo j=jogo(0);j.periodo=3;j.heroi.hp=10;j.heroi.mana=0;
        j.heroi.adicionarEfeito(new Efeito(TipoEfeito.VENENO,3,5));
        executar(j,CURAR,0);assertEquals(j.heroi.hpMax,j.heroi.hp);assertEquals(j.heroi.manaMax,j.heroi.mana);
        assertEquals(4990,j.heroi.ouro);assertEquals(3,j.periodo);assertEquals(1,j.dia);
        assertTrue(j.heroi.efeitos.isEmpty());
    }

    @Test void honraAlteraPrecosMasDoacaoEVendaSaoFixas() {
        for(int honra:new int[]{-2,0,2}) {
            MotorJogo j=jogo(0);j.heroi.honra=honra;int custo=j.preco(60);
            executar(j,MELHORAR,0);assertEquals(5000-custo,j.heroi.ouro);
            executar(j,DOAR,0);assertEquals(4950-custo,j.heroi.ouro);assertEquals(honra+1,j.heroi.honra);
        }
        MotorJogo j=jogo(0);j.heroi.honra=5;executar(j,DOAR,0);
        assertEquals(5,j.heroi.honra);assertEquals(4950,j.heroi.ouro);
    }

    @Test void contratarSubstituirEDispensarPreservaRegras() {
        MotorJogo j=jogo(0);j.periodo=3;j.heroi.honra=2;
        executar(j,CONTRATAR,0);assertInstanceOf(LoboCinzento.class,j.heroi.aliado);
        int ouro=5000-j.preco(new LoboCinzento().preco);assertEquals(ouro,j.heroi.ouro);
        executar(j,CONTRATAR,1);assertInstanceOf(IrmaLira.class,j.heroi.aliado);
        ouro-=j.preco(new IrmaLira().preco);assertEquals(ouro,j.heroi.ouro);
        executar(j,DISPENSAR,0);assertNull(j.heroi.aliado);assertEquals(ouro,j.heroi.ouro);
        assertFalse(j.estado().vila().dispensa().getFirst().disponivel());
    }

    @Test void ouroInsuficienteNaoTrocaAliadoNemCuraNemDoa() {
        MotorJogo j=jogo(0);j.heroi.aliado=new LoboCinzento();j.heroi.ouro=0;j.heroi.hp=10;
        for(var tipo:new AcaoJogo.Tipo[]{CONTRATAR,CURAR,DOAR})executar(j,tipo,tipo==CONTRATAR?1:0);
        assertInstanceOf(LoboCinzento.class,j.heroi.aliado);assertEquals(10,j.heroi.hp);
        assertEquals(0,j.heroi.honra);assertEquals(0,j.heroi.ouro);
    }

    @Test void comercioFechaNosDoisPeriodosNoturnos() {
        for(int periodo:new int[]{2,3}) {
            MotorJogo j=jogo(0);j.periodo=periodo;
            j.heroi.inv.add(Fabrica.criar("Erva Lunar"));j.heroi.inv.add(Fabrica.criar("Erva Lunar"));
            var antes=j.estado();
            for(var tipo:new AcaoJogo.Tipo[]{COMPRAR,VENDER,MELHORAR,CRIAR})executar(j,tipo,0);
            assertEquals(antes,j.estado());
            assertFalse(j.estado().vila().receitas().getFirst().disponivel());
            assertTrue(j.estado().vila().cura().getFirst().disponivel());
        }
    }

    @Test void comandosNaoAutorizadosNaoMudamEstado() {
        for(int cenario=0;cenario<5;cenario++) {
            MotorJogo j=jogo(0);
            switch(cenario) {
                case 0 -> j.local=Local.FLORESTA;
                case 1 -> j.iniciarCombate(new Lobo());
                case 2 -> j.fase=EstadoJogo.Fase.RECOMPENSA;
                case 3 -> j.fase=EstadoJogo.Fase.DERROTA;
                case 4 -> j.heroi.talentosPendentes=1;
            }
            var antes=j.estado();
            for(var tipo:new AcaoJogo.Tipo[]{VENDER,MELHORAR,CRIAR,CURAR,DOAR,CONTRATAR,DISPENSAR})executar(j,tipo,0);
            assertEquals(antes,j.estado());
        }
    }

    @Test void indicesInvalidosNaoMudamEstado() {
        MotorJogo j=jogo(0);var antes=j.estado();
        for(var tipo:new AcaoJogo.Tipo[]{VENDER,MELHORAR,CRIAR,CURAR,DOAR,CONTRATAR,DISPENSAR})
            for(int indice:new int[]{-1,999})executar(j,tipo,indice);
        assertEquals(antes,j.estado());
    }

    @Test void servicosPersistemNoSave(@TempDir Path pasta) throws Exception {
        MotorJogo j=jogo(0);executar(j,MELHORAR,0);executar(j,CONTRATAR,1);executar(j,DOAR,0);
        Salvamento save=new Salvamento(pasta.resolve("jornada.json"));save.salvar(j);
        assertEquals(j.estado(),save.carregar().estado());
    }
}
