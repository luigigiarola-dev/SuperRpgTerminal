package br.com.superrpg.jogo;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ImpactosTest {
    private List<EventoJogo> capturar(Runnable acao) {
        List<EventoJogo> eventos=new ArrayList<>();Eventos.capturar(eventos::add,acao);return eventos;
    }
    private EventoJogo unico(List<EventoJogo> eventos,EventoJogo.Tipo tipo) {
        var filtrados=eventos.stream().filter(e->e.tipo()==tipo).toList();
        assertEquals(1,filtrados.size());return filtrados.get(0);
    }
    @ParameterizedTest @ValueSource(longs={0,1,31,81,999})
    void calculoPreservaFormulaEQuantidadeDeSorteios(long semente) {
        var h=new Paladino("Arthur");var referencia=new Random(semente);
        int esperado=(int)((h.getAtaque()+referencia.nextInt(7)-3)*1.3);
        boolean critico=referencia.nextInt(100)<8+h.getAgilidade()/2;
        if(critico)esperado=(int)(esperado*1.5);
        Entidade.RNG.setSeed(semente);final Entidade.Ataque[] ataque=new Entidade.Ataque[1];
        capturar(()->ataque[0]=h.calcularAtaque(1.3));
        assertEquals(Math.max(1,esperado),ataque[0].valor());assertEquals(critico,ataque[0].critico());
        assertEquals(referencia.nextInt(),Entidade.RNG.nextInt());
        assertEquals(ataque[0].valor()+55,ataque[0].somar(55).valor());
        assertEquals(critico,ataque[0].somar(55).critico());
    }
    @Test void criticoEFraquezaNoMesmoImpactoSemDanoDuplicado() {
        var alvo=new Slime();
        var e=unico(capturar(()->assertEquals(27,alvo.receberDano(new Entidade.Ataque(20,true),Elemento.FOGO,false))),EventoJogo.Tipo.DANO);
        assertEquals(27,e.valor());assertEquals(33,alvo.hp);assertTrue(e.impacto().critico());
        assertEquals("Fogo",e.impacto().elemento());assertEquals(1.5,e.impacto().afinidade());
    }
    @Test void resistenciaGuardaEEscudoParcial() {
        var alvo=new Slime();alvo.afinidade.put(Elemento.LUZ,0.5);alvo.defendendo=true;
        alvo.efeitos.add(new Efeito(TipoEfeito.ESCUDO,3,2));
        var e=unico(capturar(()->alvo.receberDano(40,Elemento.LUZ,false)),EventoJogo.Tipo.DANO);
        assertEquals(6,e.valor());assertEquals(54,alvo.hp);assertEquals(0.5,e.impacto().afinidade());
        assertTrue(e.impacto().guarda());assertEquals(2,e.impacto().absorvido());assertFalse(e.impacto().critico());
    }
    @Test void bloqueioTotalNaoEmiteDano() {
        var alvo=new Slime();alvo.efeitos.add(new Efeito(TipoEfeito.ESCUDO,3,100));
        var eventos=capturar(()->assertEquals(0,alvo.receberDano(new Entidade.Ataque(20,true),Elemento.FOGO,false)));
        var e=unico(eventos,EventoJogo.Tipo.BLOQUEIO);
        assertEquals(27,e.impacto().absorvido());assertEquals(0,e.valor());assertEquals(60,alvo.hp);
        assertTrue(eventos.stream().noneMatch(v->v.tipo()==EventoJogo.Tipo.DANO));
    }
    @Test void esquivaNaoCarregaCriticoParaAtaqueSeguinte() {
        var alvo=new Slime();alvo.agi=100;
        long semente=0;while(new Random(semente).nextInt(100)>=30)semente++;
        Entidade.RNG.setSeed(semente);
        var eventos=capturar(()->assertEquals(0,alvo.receberDano(new Entidade.Ataque(20,true),Elemento.FOGO,true)));
        assertNull(unico(eventos,EventoJogo.Tipo.ESQUIVA).impacto());assertEquals(60,alvo.hp);
        var e=unico(capturar(()->alvo.receberDano(10,Elemento.FISICO,false)),EventoJogo.Tipo.DANO);
        assertFalse(e.impacto().critico());
    }
    @Test void danoLetalMostraSomenteVidaRealmentePerdida() {
        var alvo=new Slime();alvo.hp=5;
        var e=unico(capturar(()->assertEquals(27,alvo.receberDano(20,Elemento.FOGO,false))),EventoJogo.Tipo.DANO);
        assertEquals(5,e.valor());assertEquals(0,alvo.hp);
    }
    @Test void danoDiretoECuraNaoHerdamMetadadosDeAtaque() {
        var alvo=new Slime();capturar(()->alvo.receberDano(new Entidade.Ataque(10,true),Elemento.FISICO,false));
        var e=unico(capturar(()->alvo.perderHp(2)),EventoJogo.Tipo.DANO);assertNull(e.impacto());
        var cura=unico(capturar(()->alvo.curar(2)),EventoJogo.Tipo.CURA);assertNull(cura.impacto());
    }
}
