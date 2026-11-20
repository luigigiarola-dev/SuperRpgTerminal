package br.com.superrpg.jogo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import static br.com.superrpg.jogo.AcaoJogo.Tipo.*;

class NarrativaSaidaTest {
    @ParameterizedTest @ValueSource(ints={0,1,2,3,4})
    void conversaIdentificaMoradorESaidaValida(int indice) {
        var j=new MotorJogo("Arthur","Paladino",false,41L);j.heroi.nivel=4;
        j.executar(new AcaoJogo(CONVERSAR,indice));var n=j.estado().narrativa();
        assertEquals(Moradores.TODOS.get(indice).id(),n.moradorId());assertEquals(0,n.saida());
        assertTrue(n.escolhas().stream().anyMatch(o->o.id()==n.saida()&&o.disponivel()));
        j.executar(new AcaoJogo(ESCOLHER_EVENTO,n.saida()));assertEquals(EstadoJogo.Fase.EXPLORACAO,j.estado().fase());
    }
    @Test void resultadoOfereceSaidaMasCharadaNaoPodeSerIgnorada() {
        var j=new MotorJogo("Arthur","Paladino",false,41L);
        j.executar(AcaoJogo.de(CHARADA));assertNull(j.estado().narrativa().saida());
        j.executar(new AcaoJogo(RESPONDER,0,"teste"));assertEquals(0,j.estado().narrativa().saida());
        assertNull(j.estado().narrativa().moradorId());
    }
}
