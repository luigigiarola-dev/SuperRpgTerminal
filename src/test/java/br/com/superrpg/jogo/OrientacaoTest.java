package br.com.superrpg.jogo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class OrientacaoTest {
    private MotorJogo jogo() { return new MotorJogo("Arthur","Paladino",false,41L); }
    @Test void inicioIndicaMiraSemAceitarMissao() {
        var j=jogo();var antes=j.estado();
        assertTrue(j.objetivoAtual().contains("Mira"));assertTrue(j.objetivoAtual().contains("Praga na Floresta"));
        assertEquals(antes,j.estado());assertTrue(j.taticaCombate().isEmpty());
    }
    @Test void objetivoAtivoMostraProgressoLocalEEntrega() {
        var j=jogo();var m=j.diario.obter("praga");m.estado=1;m.progresso=1;
        assertTrue(j.objetivoAtual().contains("1/3"));assertTrue(j.objetivoAtual().contains(Local.FLORESTA.nome));
        m.progresso=3;assertTrue(j.objetivoAtual().contains("entregue"));assertTrue(j.objetivoAtual().contains("Mira"));
        assertEquals(1,m.estado);
    }
    @Test void coletaUsaContagemRealEEntregaTemPrioridade() {
        var j=jogo();j.diario.obter("praga").estado=1;var ervas=j.diario.obter("ervas");ervas.estado=1;
        for(int i=0;i<3;i++)j.heroi.inv.add(Fabrica.criar("Erva Lunar"));
        assertTrue(j.objetivoAtual().contains("Lira"));assertTrue(j.objetivoAtual().contains("entregue"));
    }
    @ParameterizedTest @ValueSource(strings={"Paladino","Necromante","Arqueiro","Mago","Ladino"})
    void habilidadesCorrespondemAoMotorESemConsumirSorteios(String classe) {
        var j=new MotorJogo("Arthur",classe,false);Eventos.capturar(e->{},()->j.iniciarCombate(new Slime()));
        var antes=j.estado();Entidade.RNG.setSeed(123);var esperado=new Random(123);
        var t=j.taticaCombate().orElseThrow();assertEquals(3,t.habilidades().size());
        for(int i=0;i<3;i++) {
            var h=j.heroi.habilidades().get(i);var exibida=t.habilidades().get(i);
            assertEquals(h.nome,exibida.nome());assertEquals(h.custo,exibida.custo());
            assertEquals(j.heroi.podeUsar(h),exibida.disponivel());
        }
        assertEquals(antes,j.estado());assertEquals(esperado.nextInt(),Entidade.RNG.nextInt());
        assertThrows(UnsupportedOperationException.class,()->t.habilidades().clear());
    }
    @Test void explicaManaERecargaSemConsumirTurno() {
        var j=jogo();Eventos.capturar(e->{},()->j.iniciarCombate(new Slime()));
        var h=j.heroi.habilidades().get(1);j.heroi.mana=0;j.heroi.recargas.put(h.nome,2);
        var antes=j.estado();var exibida=j.taticaCombate().orElseThrow().habilidades().get(1);
        assertFalse(exibida.disponivel());assertTrue(exibida.indisponibilidade().contains("Recarga: 2"));
        assertTrue(exibida.indisponibilidade().contains("Faltam 15 MP"));assertEquals(antes,j.estado());
    }
    @Test void ordemRealEAvisoDoChefe() {
        var j=jogo();j.heroi.agi=100;var chefe=new Beholder();
        Eventos.capturar(e->{},()->j.iniciarCombate(chefe));
        assertTrue(j.taticaCombate().orElseThrow().ordem().contains("você → inimigo"));
        assertEquals("",j.taticaCombate().orElseThrow().ameaca());chefe.carregando=true;
        assertTrue(j.taticaCombate().orElseThrow().ameaca().contains(chefe.aviso()));
        assertTrue(j.taticaCombate().orElseThrow().defesa().contains("8 MP"));
    }
}
