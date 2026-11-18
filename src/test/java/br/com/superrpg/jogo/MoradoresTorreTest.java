package br.com.superrpg.jogo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import static br.com.superrpg.jogo.AcaoJogo.Tipo.*;
import static br.com.superrpg.jogo.EstadoJogo.Fase.*;

class MoradoresTorreTest {
    private MotorJogo jogo() { return new MotorJogo("Arthur","Paladino",false,new Random(41)); }
    private void agir(MotorJogo j,AcaoJogo.Tipo tipo,int id){j.executar(new AcaoJogo(tipo,id));}
    private void vencer(MotorJogo j) {
        j.inimigo.hp=1;j.inimigo.agi=0;j.inimigo.xpRec=0;j.heroi.atk=10000;
        agir(j,ATACAR,0);assertEquals(RECOMPENSA,j.fase);agir(j,CONTINUAR,0);
    }
    private MotorJogo torre() {
        MotorJogo j=jogo();j.local=Local.TORRE;j.heroi.agi=1000;
        agir(j,EXPLORAR,0);return j;
    }

    @Test void todosMoradoresTemFalasOriginaisESomenteSuasMissoes() {
        for(int i=0;i<Moradores.TODOS.size();i++) {
            MotorJogo j=jogo();j.heroi.nivel=4;agir(j,CONVERSAR,i);
            var m=Moradores.TODOS.get(i);var n=j.estado().narrativa();
            assertEquals(m.nome(),n.titulo());assertTrue(m.falas().contains(n.texto()));
            for(var o:n.escolhas())if(o.id()>0)assertEquals(m.id(),j.diario.missoes.get(o.id()-1).doador);
            assertEquals(1,j.dia);assertEquals(0,j.periodo);
        }
    }
    @Test void aceitarMissaoNaoDuplicaNemAceitaMissaoDeOutroMorador() {
        MotorJogo j=jogo();agir(j,CONVERSAR,0);agir(j,ESCOLHER_EVENTO,3);
        assertEquals(0,j.diario.obter("ervas").estado);
        agir(j,ESCOLHER_EVENTO,1);assertEquals(1,j.diario.obter("praga").estado);
        var antes=j.estado();agir(j,ESCOLHER_EVENTO,1);assertEquals(antes,j.estado());
    }
    @Test void entregarColetaConsomeItensEPagaUmaVez() {
        MotorJogo j=jogo();j.diario.obter("ervas").estado=1;
        for(int i=0;i<3;i++)j.heroi.inv.add(Fabrica.criar("Erva Lunar"));
        agir(j,CONVERSAR,1);agir(j,ESCOLHER_EVENTO,3);
        assertEquals(0,j.heroi.contar("Erva Lunar"));assertEquals(100,j.heroi.ouro);
        assertEquals(70,j.heroi.xp);assertEquals(1,j.heroi.honra);assertEquals(3,j.heroi.contar("Poção de Cura"));
        var antes=j.estado();agir(j,ESCOLHER_EVENTO,3);assertEquals(antes,j.estado());
    }
    @Test void entregaDesbloqueiaSeguinteEPermiteTalentoSemPerderConversa() {
        MotorJogo j=jogo();j.heroi.xp=90;Missao m=j.diario.obter("praga");m.estado=1;m.progresso=3;
        agir(j,CONVERSAR,0);agir(j,ESCOLHER_EVENTO,1);assertEquals(1,j.heroi.talentosPendentes);
        agir(j,ESCOLHER_EVENTO,0);assertEquals(EVENTO,j.fase);
        agir(j,TALENTO,1);
        assertTrue(j.estado().narrativa().escolhas().stream().anyMatch(o->o.id()==5&&o.disponivel()));
        agir(j,ESCOLHER_EVENTO,5);assertEquals(1,j.diario.obter("teias").estado);
        agir(j,ESCOLHER_EVENTO,0);assertEquals(EXPLORACAO,j.fase);
    }
    @Test void aldricExigeNivelQuatro() {
        MotorJogo j=jogo();agir(j,CONVERSAR,3);
        assertTrue(j.estado().narrativa().texto().contains("Nível 4"));assertEquals(1,j.estado().narrativa().escolhas().size());
    }
    @Test void conversaValidaLocalIndicesEBloqueiaComandosESave(@TempDir Path pasta) {
        MotorJogo j=jogo();var antes=j.estado();agir(j,CONVERSAR,-1);agir(j,CONVERSAR,5);assertEquals(antes,j.estado());
        j.local=Local.FLORESTA;antes=j.estado();agir(j,CONVERSAR,0);assertEquals(antes,j.estado());
        j.local=Local.VILA;agir(j,CONVERSAR,4);antes=j.estado();
        for(var t:new AcaoJogo.Tipo[]{VIAJAR,MISSAO,ITEM,DADOS,CONVERSAR})agir(j,t,0);
        assertEquals(antes,j.estado());assertThrows(IOException.class,()->new Salvamento(pasta.resolve("save.json")).salvar(j));
    }
    @Test void torrePausaDepoisDeCadaAndarEApenasTerceiroEhChefe() {
        MotorJogo j=torre();assertEquals(1,j.estado().andarTorre());assertFalse(j.inimigo.chefe);
        vencer(j);assertEquals(PAUSA_TORRE,j.fase);assertNull(j.inimigo);assertEquals(1,j.periodo);
        agir(j,CONTINUAR,0);assertEquals(2,j.estado().andarTorre());assertFalse(j.inimigo.chefe);
        vencer(j);assertEquals(PAUSA_TORRE,j.fase);agir(j,CONTINUAR,0);
        assertEquals(3,j.estado().andarTorre());assertInstanceOf(Beholder.class,j.inimigo);
        vencer(j);assertEquals(EXPLORACAO,j.fase);assertEquals(0,j.estado().andarTorre());
        assertEquals(1,j.heroi.contar("Chave do Dragão"));assertEquals(1,j.periodo);
    }
    @Test void pausaPermiteConsumirEquiparMasNaoViajarOuSalvar(@TempDir Path pasta) {
        MotorJogo j=torre();vencer(j);j.heroi.hp=50;
        agir(j,ITEM,0);assertTrue(j.heroi.hp>50);assertEquals(1,j.heroi.contar("Poção de Cura"));
        j.heroi.inv.add(Fabrica.criar("Lâmina de Aço"));agir(j,EQUIPAR,j.heroi.inv.size()-1);
        assertEquals("Lâmina de Aço",j.heroi.equip[0].getNome());var antes=j.estado();
        for(var t:new AcaoJogo.Tipo[]{VIAJAR,EXPLORAR,ACAMPAR,COLETAR,CONVERSAR,DADOS})agir(j,t,0);
        assertEquals(antes,j.estado());assertThrows(IOException.class,()->new Salvamento(pasta.resolve("save.json")).salvar(j));
    }
    @Test void descerReiniciaSubidaSemPerderRecompensas(@TempDir Path pasta) throws Exception {
        MotorJogo j=torre();vencer(j);int ouro=j.heroi.ouro;agir(j,DESCER_TORRE,0);
        assertEquals(EXPLORACAO,j.fase);assertEquals(Local.TORRE,j.local);assertEquals(0,j.estado().andarTorre());assertEquals(ouro,j.heroi.ouro);
        Salvamento save=new Salvamento(pasta.resolve("save.json"));save.salvar(j);assertEquals(0,save.carregar().estado().andarTorre());
        agir(j,EXPLORAR,0);assertEquals(1,j.estado().andarTorre());assertEquals(2,j.periodo);
    }
    @Test void derrotaLimpaSubida() {
        MotorJogo j=torre();j.heroi.hp=0;agir(j,DEFENDER,0);
        assertEquals(RECOMPENSA,j.fase);assertEquals(0,j.estado().andarTorre());agir(j,CONTINUAR,0);
        assertEquals(EXPLORACAO,j.fase);assertEquals(Local.VILA,j.local);
    }
    @Test void fugaDaTorreReiniciaSubida() {
        MotorJogo j=new MotorJogo("Arthur","Paladino",false,new Random(){
            @Override public int nextInt(int limite){return 0;}
        });
        j.local=Local.TORRE;j.heroi.agi=1000;agir(j,EXPLORAR,0);agir(j,FUGIR,0);
        assertEquals(EXPLORACAO,j.fase);assertEquals(0,j.estado().andarTorre());
        agir(j,EXPLORAR,0);assertEquals(1,j.estado().andarTorre());
    }
    @Test void talentoDaTorreDeveSerEscolhidoAntesDaPausa() {
        MotorJogo j=torre();j.heroi.xp=99;j.inimigo.xpRec=1;j.inimigo.hp=1;j.inimigo.agi=0;j.heroi.atk=10000;
        agir(j,ATACAR,0);assertEquals(1,j.heroi.talentosPendentes);agir(j,CONTINUAR,0);assertEquals(RECOMPENSA,j.fase);
        agir(j,TALENTO,1);agir(j,CONTINUAR,0);assertEquals(PAUSA_TORRE,j.fase);
    }
}
