package br.com.superrpg.jogo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class SalvamentoTest {
    @TempDir Path pasta;
    @Test void roundTripPreservaEstadoEEquipamentoMelhorado() throws Exception {
        MotorJogo jogo=new MotorJogo("João da Luz","Paladino",true);
        jogo.heroi.equip[0].melhoria=4;jogo.heroi.ouro=399;jogo.local=Local.RUINAS;jogo.periodo=3;jogo.dia=7;
        jogo.diario.obter("praga").estado=1;jogo.diario.obter("praga").progresso=2;jogo.heroi.aliado=new IrmaLira();
        Salvamento save=new Salvamento(pasta.resolve("jornada.json"));save.salvar(jogo);MotorJogo lido=save.carregar();
        assertEquals(jogo.estado(),lido.estado());assertInstanceOf(IrmaLira.class,lido.heroi.aliado);
    }
    @Test void salvamentoPreservaCopiaAnterior() throws Exception {
        Salvamento save=new Salvamento(pasta.resolve("jornada.json"));MotorJogo jogo=new MotorJogo("Um","Mago",false);
        save.salvar(jogo);jogo.heroi.ouro=7;save.salvar(jogo);
        assertEquals(7,save.carregar().heroi.ouro);assertEquals(60,new Salvamento(pasta.resolve("jornada.anterior.json")).carregar().heroi.ouro);
    }
    @Test void rejeitaArquivoCorrompidoSemApagaLo() throws Exception {
        Path arquivo=pasta.resolve("jornada.json");Files.writeString(arquivo,"{\"versao\":999}");
        assertThrows(IOException.class,()->new Salvamento(arquivo).carregar());assertTrue(Files.exists(arquivo));
    }
    @Test void rejeitaSalvarCombateOuTalentoPendente() {
        MotorJogo jogo=new MotorJogo("Um","Mago",false);Salvamento save=new Salvamento(pasta.resolve("jornada.json"));
        jogo.heroi.talentosPendentes=1;assertThrows(IOException.class,()->save.salvar(jogo));jogo.heroi.talentosPendentes=0;
        jogo.fase=EstadoJogo.Fase.COMBATE;assertThrows(IOException.class,()->save.salvar(jogo));assertFalse(save.existe());
    }
    @Test void importaV2SemModificarOrigem() throws Exception {
        String conteudo="v2\nPaladino\nArthur\n1,0,160,160,50,50,30,15,8,60,0,0\nEspada Enferrujada|2;-;-;\nPoção de Cura|0;\nlobo\n2,1,VILA,false,false\npraga:1:2;lobos:0:0;ervas:0:0;ladrao:0:0;teias:0:0;fogo:0:0;ossos:0:0;seita:0:0;torre:0:0;dragao:0:0;\n";
        conteudo=conteudo.replace("VILA,false,false","VILA,false,true");
        Path antigo=pasta.resolve("rpg_save.txt");Files.writeString(antigo,conteudo);
        MotorJogo jogo=new Salvamento(pasta.resolve("jornada.json")).importarV2(antigo);
        assertEquals(2,jogo.heroi.equip[0].melhoria);assertEquals(2,jogo.diario.obter("praga").progresso);
        assertEquals(2,jogo.dia);assertInstanceOf(LoboCinzento.class,jogo.heroi.aliado);assertEquals(conteudo,Files.readString(antigo));
        assertTrue(jogo.charadaHoje);
    }
}
