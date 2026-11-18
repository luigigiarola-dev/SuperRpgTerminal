package br.com.superrpg.jogo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static br.com.superrpg.jogo.AcaoJogo.Tipo.*;
import static br.com.superrpg.jogo.EstadoJogo.Fase.*;

class AventurasTest {
    private MotorJogo jogo(int... valores) {
        Queue<Integer> fila=new ArrayDeque<>();for(int valor:valores)fila.add(valor);
        return new MotorJogo("Arthur","Paladino",false,new Random(1) {
            @Override public int nextInt(int limite) {
                assertFalse(fila.isEmpty(),"Sorteio inesperado");int valor=fila.remove();
                assertTrue(valor>=0&&valor<limite,"Valor fora do limite do sorteio");return valor;
            }
            @Override public boolean nextBoolean(){return true;}
        });
    }
    private void explorar(MotorJogo j){j.local=Local.FLORESTA;j.executar(AcaoJogo.de(EXPLORAR));}
    private void escolher(MotorJogo j,int id){j.executar(new AcaoJogo(ESCOLHER_EVENTO,id));}
    private void resposta(MotorJogo j,String texto){j.executar(new AcaoJogo(RESPONDER,0,texto));}

    @Test void quatroResultadosIniciamCombateEAvancamUmPeriodo() {
        for(int r=0;r<4;r++) {
            MotorJogo j=jogo(r);explorar(j);
            assertEquals(COMBATE,j.fase);assertEquals(1,j.periodo);assertNull(j.estado().narrativa());
        }
    }
    @Test void bauDeOuroUsaNivelDoLocalEPagaApenasUmaVez() {
        MotorJogo j=jogo(4,49,19);j.local=Local.MONTANHA;j.executar(AcaoJogo.de(EXPLORAR));
        assertEquals(60+15+19*(Local.MONTANHA.nivelRec/2+1),j.heroi.ouro);
        int ouro=j.heroi.ouro;escolher(j,0);escolher(j,0);assertEquals(ouro,j.heroi.ouro);assertEquals(EXPLORACAO,j.fase);
    }
    @Test void bauSelecionaConsumivelOuEquipamentoNosLimites() {
        for(int r:new int[]{50,84,85,99}) {
            MotorJogo j=jogo(4,r,0);explorar(j);
            assertEquals(r<85?"Poção de Cura":"Gibão de Couro",j.heroi.inv.getLast().getNome());
            assertEquals(4,j.heroi.inv.size());
        }
    }
    @Test void armadilhaRespeitaAgilidadeEOuEntregaXpOuDano() {
        MotorJogo sucesso=jogo(5,15);explorar(sucesso);assertEquals(15,sucesso.heroi.xp);
        MotorJogo falha=jogo(5,16);falha.heroi.hp=1;explorar(falha);
        assertEquals(1,falha.heroi.hp);assertEquals(0,falha.heroi.xp);assertEquals(EVENTO,falha.fase);
    }
    @Test void agilidadeDaArmadilhaTemTetoDeSessentaPorCento() {
        MotorJogo j=jogo(5,60);j.heroi.agi=100;j.heroi.hp=50;explorar(j);
        assertEquals(45,j.heroi.hp);assertEquals(0,j.heroi.xp);
    }
    @Test void fogueiraEFonteRestauramRecursosSemExcederMaximo() {
        MotorJogo fogo=jogo(6);fogo.heroi.hp=50;explorar(fogo);assertEquals(90,fogo.heroi.hp);
        MotorJogo fonte=jogo(9,4);fonte.heroi.mana=0;explorar(fonte);assertEquals(fonte.heroi.manaMax,fonte.heroi.mana);
    }
    @Test void viajanteConsomePocaoERecompensaUmaVez() {
        MotorJogo j=jogo(7,0,29);explorar(j);escolher(j,1);
        assertEquals(119,j.heroi.ouro);assertEquals(1,j.heroi.honra);assertEquals(30,j.heroi.xp);
        assertEquals(1,j.heroi.contar("Poção de Cura"));
        var antes=j.estado();escolher(j,1);assertEquals(antes,j.estado());escolher(j,0);assertEquals(EXPLORACAO,j.fase);
    }
    @Test void viajanteSemPocaoPermiteSeguirSemRecompensa() {
        MotorJogo j=jogo(7,0);j.heroi.inv.clear();explorar(j);
        assertFalse(j.estado().narrativa().escolhas().getFirst().disponivel());
        var antes=j.estado();escolher(j,1);assertEquals(antes,j.estado());
        escolher(j,2);escolher(j,0);assertEquals(60,j.heroi.ouro);assertEquals(0,j.heroi.honra);
    }
    @Test void roubarViajanteReduzHonra() {
        MotorJogo j=jogo(7,0,39);explorar(j);escolher(j,3);
        assertEquals(139,j.heroi.ouro);assertEquals(-1,j.heroi.honra);assertEquals(2,j.heroi.contar("Poção de Cura"));
    }
    @Test void santuarioRespeitaLimiteDaOracaoEDanoNaoLetal() {
        MotorJogo luz=jogo(7,1,59);luz.heroi.hp=20;luz.heroi.mana=0;explorar(luz);escolher(luz,1);
        assertEquals(105,luz.heroi.hp);assertEquals(33,luz.heroi.mana);
        MotorJogo sombra=jogo(7,1,60);sombra.heroi.hp=1;explorar(sombra);escolher(sombra,1);
        assertEquals(1,sombra.heroi.hp);
    }
    @Test void oferendaCobraVinteSemDescontoERestauraTudo() {
        MotorJogo j=jogo(7,1);j.heroi.honra=2;j.heroi.hp=1;explorar(j);escolher(j,2);
        assertEquals(40,j.heroi.ouro);assertEquals(3,j.heroi.honra);assertEquals(j.heroi.hpMax,j.heroi.hp);
    }
    @Test void oferendaSemOuroNaoAlteraEvento() {
        MotorJogo j=jogo(7,1);j.heroi.ouro=19;explorar(j);var antes=j.estado();
        escolher(j,2);assertEquals(antes,j.estado());escolher(j,3);escolher(j,0);assertEquals(EXPLORACAO,j.fase);
    }
    @Test void cofreForcaRecompensaETemFalhaNaoLetal() {
        MotorJogo sucesso=jogo(7,2,49,59);explorar(sucesso);escolher(sucesso,1);
        assertEquals(159,sucesso.heroi.ouro);assertEquals("Elixir de Mana",sucesso.heroi.inv.getLast().getNome());
        MotorJogo falha=jogo(7,2,50);falha.heroi.hp=1;explorar(falha);escolher(falha,1);assertEquals(1,falha.heroi.hp);
    }
    @Test void cofreDelicadezaUsaAgilidadeComTeto() {
        MotorJogo sucesso=jogo(7,2,84,0);sucesso.heroi.agi=100;explorar(sucesso);escolher(sucesso,2);assertEquals(100,sucesso.heroi.ouro);
        MotorJogo falha=jogo(7,2,85);falha.heroi.agi=100;explorar(falha);escolher(falha,2);assertEquals(150,falha.heroi.hp);
    }
    @Test void abandonarCofreNaoSorteiaNemPaga() {
        MotorJogo j=jogo(7,2);explorar(j);escolher(j,3);assertEquals(60,j.heroi.ouro);
    }
    @Test void mercadorMantemOfertaEPrecoIndependentesDeHonra() {
        MotorJogo j=jogo(7,3,0);j.heroi.honra=-5;explorar(j);
        assertEquals(j.estado(),j.estado());escolher(j,1);assertEquals(39,j.heroi.ouro);assertEquals(3,j.heroi.contar("Poção de Cura"));
        escolher(j,1);assertEquals(39,j.heroi.ouro);
    }
    @Test void mercadorSemOuroPermiteRecusar() {
        MotorJogo j=jogo(7,3,0);j.heroi.ouro=20;explorar(j);escolher(j,1);assertEquals(20,j.heroi.ouro);
        escolher(j,0);escolher(j,0);assertEquals(EXPLORACAO,j.fase);assertEquals(3,j.heroi.inv.size());
    }
    @Test void eventoBloqueiaOutrasAcoesESalvamento(@TempDir Path pasta) {
        MotorJogo j=jogo(7,0);explorar(j);var antes=j.estado();
        for(var tipo:new AcaoJogo.Tipo[]{VIAJAR,COMPRAR,ITEM,EXPLORAR,DADOS,CHARADA,DOAR,CONTINUAR})j.executar(new AcaoJogo(tipo,0));
        escolher(j,99);assertEquals(antes,j.estado());
        assertThrows(IOException.class,()->new Salvamento(pasta.resolve("save.json")).salvar(j));
    }
    @Test void dadosGanhamPerdemOuEmpatamSemAvancarTempo() {
        int[][] sequencias={{5,5,0,0},{0,0,5,5},{2,3,3,2}};
        int[] saldos={110,10,60};
        for(int i=0;i<3;i++) {
            MotorJogo j=jogo(sequencias[i]);j.executar(new AcaoJogo(DADOS,50));assertEquals(saldos[i],j.heroi.ouro);
            assertEquals(0,j.periodo);assertEquals(EVENTO,j.fase);var antes=j.estado();j.executar(new AcaoJogo(DADOS,50));assertEquals(antes,j.estado());
        }
    }
    @Test void apostaRejeitaValoresInvalidosSemSorteio() {
        MotorJogo j=jogo();var antes=j.estado();
        for(int aposta:new int[]{-1,0,61,101,Integer.MAX_VALUE})j.executar(new AcaoJogo(DADOS,aposta));
        assertEquals(antes,j.estado());
    }
    @Test void minijogosNaoFuncionamForaDaVilaOuEmCombate() {
        MotorJogo j=jogo();j.local=Local.FLORESTA;var antes=j.estado();
        for(var tipo:new AcaoJogo.Tipo[]{DADOS,CHARADA,RUMOR})j.executar(new AcaoJogo(tipo,10));assertEquals(antes,j.estado());
        j.iniciarCombate(new Slime());antes=j.estado();
        for(var tipo:new AcaoJogo.Tipo[]{DADOS,CHARADA,RUMOR})j.executar(new AcaoJogo(tipo,10));assertEquals(antes,j.estado());
    }
    @Test void todasAsCharadasAceitamRespostaComoNoTerminal() {
        String[] respostas={"a CHUVA","abacaxi","um buraco","mapa","pente"};
        for(int i=0;i<5;i++) {
            MotorJogo j=jogo(i,39);j.executar(AcaoJogo.de(CHARADA));assertTrue(j.charadaHoje);
            resposta(j,respostas[i]);assertEquals(129,j.heroi.ouro);assertEquals(40,j.heroi.xp);
            var antes=j.estado();resposta(j,respostas[i]);assertEquals(antes,j.estado());
            escolher(j,0);j.executar(AcaoJogo.de(CHARADA));assertEquals(EXPLORACAO,j.fase);
        }
    }
    @Test void charadaVaziaMantemPerguntaEErroOuDesistenciaConsomeDia() {
        for(boolean desistir:new boolean[]{false,true}) {
            MotorJogo j=jogo(0);j.executar(AcaoJogo.de(CHARADA));var antes=j.estado();
            resposta(j,"  ");resposta(j,"a".repeat(201));assertEquals(antes,j.estado());
            if(desistir)escolher(j,0);else resposta(j,"errada");
            assertTrue(j.charadaHoje);assertEquals(60,j.heroi.ouro);assertEquals(0,j.heroi.xp);
        }
    }
    @Test void charadaPersisteELiberaSomenteNoProximoDia(@TempDir Path pasta) throws Exception {
        MotorJogo j=jogo(0);j.periodo=3;j.executar(AcaoJogo.de(CHARADA));escolher(j,0);escolher(j,0);
        Salvamento save=new Salvamento(pasta.resolve("save.json"));save.salvar(j);MotorJogo lido=save.carregar();
        assertTrue(lido.charadaHoje);lido.executar(AcaoJogo.de(CHARADA));assertEquals(EXPLORACAO,lido.fase);
        lido.executar(AcaoJogo.de(DESCANSAR));assertEquals(2,lido.dia);assertFalse(lido.charadaHoje);
    }
    @Test void carregarFormatoAnteriorAssumeCharadaNaoTentada(@TempDir Path pasta) throws Exception {
        MotorJogo j=jogo();j.charadaHoje=true;Path arquivo=pasta.resolve("save.json");Salvamento save=new Salvamento(arquivo);save.salvar(j);
        String antigo=Files.readString(arquivo).replace("\"versao\": 4","\"versao\": 3").replace(",\n  \"charadaHoje\": true","");
        Files.writeString(arquivo,antigo);assertFalse(save.carregar().charadaHoje);
    }
    @Test void rumoresAcompanhamProgresso() {
        for(int etapa=0;etapa<3;etapa++) {
            MotorJogo j=jogo(4);if(etapa>0)j.diario.obter("seita").estado=2;if(etapa>1)j.diario.obter("torre").estado=2;
            j.executar(AcaoJogo.de(RUMOR));assertTrue(j.estado().narrativa().texto().contains(new String[]{"Cultistas","Torre","Chave"}[etapa]));
            assertEquals(60,j.heroi.ouro);
        }
    }
    @Test void recompensaDeEventoPermiteTalentoAntesDeContinuar() {
        MotorJogo j=jogo(0,0);j.heroi.xp=90;j.executar(AcaoJogo.de(CHARADA));resposta(j,"chuva");
        assertEquals(1,j.heroi.talentosPendentes);escolher(j,0);assertEquals(EVENTO,j.fase);
        j.executar(new AcaoJogo(TALENTO,1));escolher(j,0);assertEquals(EXPLORACAO,j.fase);assertEquals(2,j.heroi.nivel);
    }
    @Test void roteiroGraficoReproduzViajanteSeguidoDeCombate() {
        MotorJogo j=new MotorJogo("Arthur","Paladino",false,41L);
        j.executar(AcaoJogo.de(CURAR));j.executar(AcaoJogo.de(CHARADA));resposta(j,"teste");escolher(j,0);
        j.executar(new AcaoJogo(DADOS,10));escolher(j,0);j.executar(AcaoJogo.de(RUMOR));escolher(j,0);
        j.executar(new AcaoJogo(VIAJAR,1));assertEquals(EXPLORACAO,j.fase);j.executar(AcaoJogo.de(EXPLORAR));
        assertEquals("Viajante ferido",j.estado().narrativa().titulo());escolher(j,2);escolher(j,0);
        j.executar(AcaoJogo.de(EXPLORAR));assertEquals(COMBATE,j.fase);
    }
}
