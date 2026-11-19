package br.com.superrpg.jogo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
import static br.com.superrpg.jogo.EstadoJogo.Fase.*;

class HistoriaCampanhaTest {
    @Test void introducaoUsaNomeNormalizadoSemAlterarPartida() {
        MotorJogo j=new MotorJogo("  Aurora  ","Mago",false);
        var antes=j.estado();var falas=j.introducao();
        assertEquals(3,falas.size());assertTrue(falas.get(1).texto().contains("Aurora, Mago"));
        assertEquals("Capitã Mira",falas.get(2).personagem());
        assertThrows(UnsupportedOperationException.class,()->falas.clear());
        assertEquals(antes,j.estado());assertTrue(j.encerramento().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"-4,coração", "-2,coração", "-1,estrada", "0,estrada", "1,estrada", "2,Honrado", "4,Lendário Justo"})
    void finaisRespeitamLimitesDeHonra(int honra,String trecho) {
        MotorJogo j=new MotorJogo("Arthur","Paladino",false);
        j.heroi.honra=honra;j.fase=VITORIA;
        var antes=j.estado();var fim=j.encerramento().orElseThrow();
        assertTrue(fim.venceu());assertTrue(fim.texto().contains(trecho));
        assertEquals(antes,j.estado());assertEquals(fim,j.encerramento().orElseThrow());
    }

    @Test void pontuacaoPreservaFormulaOriginalEResumo() {
        MotorJogo j=new MotorJogo("Arthur","Paladino",false);
        j.fase=VITORIA;j.dia=8;j.heroi.abates=12;j.heroi.honra=2;
        j.diario.missoes.get(0).estado=2;
        var fim=j.encerramento().orElseThrow();
        assertEquals(j.heroi.nivel*100+j.heroi.ouro+120+150+3000+40-40,fim.pontuacao());
        assertEquals(1,fim.missoes());assertEquals(j.diario.missoes.size(),fim.totalMissoes());
        assertEquals(8,fim.dias());assertEquals(12,fim.abates());
    }

    @Test void derrotaHardcoreNaoRecebeBonusENuncaTemPontuacaoNegativa() {
        MotorJogo j=new MotorJogo("Arthur","Paladino",true);
        j.fase=DERROTA;j.dia=999;
        var fim=j.encerramento().orElseThrow();
        assertFalse(fim.venceu());assertTrue(fim.hardcore());assertEquals(0,fim.pontuacao());
        assertFalse(fim.texto().contains("Dragão"));
    }

    @Test void derrotaNormalNaoEncerraCampanha() {
        MotorJogo j=new MotorJogo("Arthur","Paladino",false);
        Eventos.capturar(e->{},()->j.iniciarCombate(new Slime()));
        j.heroi.hp=0;j.executar(AcaoJogo.de(AcaoJogo.Tipo.DEFENDER));
        assertEquals(RECOMPENSA,j.fase);assertTrue(j.encerramento().isEmpty());
    }

    @Test void morteHardcoreProduzEncerramentoSemPermitirContinuar() {
        MotorJogo j=new MotorJogo("Arthur","Paladino",true);
        Eventos.capturar(e->{},()->j.iniciarCombate(new Slime()));
        j.heroi.hp=0;j.executar(AcaoJogo.de(AcaoJogo.Tipo.DEFENDER));
        assertEquals(DERROTA,j.fase);var fim=j.encerramento().orElseThrow();
        assertFalse(fim.venceu());var antes=j.estado();
        j.executar(AcaoJogo.de(AcaoJogo.Tipo.CONTINUAR));assertEquals(antes,j.estado());
        assertEquals(fim,j.encerramento().orElseThrow());
    }
}
