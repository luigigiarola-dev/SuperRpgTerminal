package br.com.superrpg.jogo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import static br.com.superrpg.jogo.AcaoJogo.Tipo.*;
import static br.com.superrpg.jogo.EstadoJogo.Fase.*;

/** Integração controlada da progressão; não é uma simulação de balanceamento. */
class CampanhaTest {
    private void agir(MotorJogo j,AcaoJogo.Tipo tipo,int id){j.executar(new AcaoJogo(tipo,id));}
    private void talentos(MotorJogo j){while(j.heroi.talentosPendentes>0)agir(j,TALENTO,1);}
    private void vencer(MotorJogo j) {
        // O resultado do combate é controlado; recompensas e progresso usam o motor real.
        j.inimigo.hp=0;agir(j,DEFENDER,0);talentos(j);
        if(j.fase==RECOMPENSA)agir(j,CONTINUAR,0);
    }
    private void nivel(MotorJogo j,int desejado) {
        for(int i=0;j.heroi.nivel<desejado&&i<150;i++) {
            Eventos.capturar(e->{},()->j.iniciarCombate(new Slime()));vencer(j);
        }
        assertTrue(j.heroi.nivel>=desejado);
    }
    private MotorJogo checkpoint(MotorJogo j,Path arquivo) throws Exception {
        Salvamento save=new Salvamento(arquivo);save.salvar(j);MotorJogo lido=save.carregar();
        // A rodada do combate anterior é transitória e não pertence ao save.
        for(var campo:EstadoJogo.class.getRecordComponents())if(!campo.getName().equals("rodada"))
            assertEquals(campo.getAccessor().invoke(j.estado()),campo.getAccessor().invoke(lido.estado()),"Checkpoint: "+campo.getName());
        return lido;
    }

    @ParameterizedTest
    @ValueSource(strings={"Paladino","Necromante","Arqueiro","Mago","Ladino"})
    void encadeamentoDeMissoesAteIgnisComCheckpoints(String classe,@TempDir Path pasta) throws Exception {
        Entidade.RNG.setSeed(81);
        MotorJogo j=new MotorJogo("Jornada",classe,false,81L);
        assertFalse(j.estado().locais().get(5).disponivel());assertFalse(j.estado().locais().get(6).disponivel());
        String[] ordem={"praga","lobos","ervas","ladrao","teias","fogo","ossos","seita","torre"};
        for(String id:ordem) {
            Missao m=j.diario.obter(id);nivel(j,m.nivelMin);
            int indice=j.diario.missoes.indexOf(m);
            agir(j,MISSAO,indice);assertEquals(1,m.estado,"Aceitação: "+id);
            if(m.tipo==Missao.Tipo.COLETAR) {
                // Recursos controlados para isolar entrega/consumo, sem alterar atributos.
                for(int k=0;k<m.qtd;k++)j.heroi.inv.add(Fabrica.criar(m.alvo));
            } else if(id.equals("torre")) {
                agir(j,VIAJAR,5);assertEquals(Local.TORRE,j.local);agir(j,EXPLORAR,0);
                vencer(j);assertEquals(PAUSA_TORRE,j.fase);agir(j,CONTINUAR,0);
                vencer(j);assertEquals(PAUSA_TORRE,j.fase);agir(j,CONTINUAR,0);
                assertInstanceOf(Beholder.class,j.inimigo);vencer(j);
                assertEquals(1,j.heroi.contar("Chave do Dragão"));agir(j,VIAJAR,0);
            } else {
                for(int k=0;k<m.qtd;k++) {
                    MotorJogo atual=j;Eventos.capturar(e->{},()->atual.iniciarCombate(Bestiario.criar(m.alvo)));vencer(j);
                }
            }
            assertTrue(m.pronta(j.heroi),"Objetivo: "+id);
            int ouro=j.heroi.ouro;agir(j,MISSAO,indice);talentos(j);
            assertEquals(ouro+m.ouro,j.heroi.ouro);assertEquals(2,m.estado);
            if(m.tipo==Missao.Tipo.COLETAR)assertEquals(0,j.heroi.contar(m.alvo));
            if(m.itemRec!=null)assertTrue(j.heroi.contar(m.itemRec)>0);
            var estado=j.estado();agir(j,MISSAO,indice);assertEquals(estado,j.estado());
            j=checkpoint(j,pasta.resolve("jornada.json"));
        }
        nivel(j,7);agir(j,MISSAO,9);assertEquals(1,j.diario.obter("dragao").estado);
        agir(j,VIAJAR,6);assertEquals(Local.COVIL,j.local);j=checkpoint(j,pasta.resolve("jornada.json"));
        agir(j,EXPLORAR,0);assertInstanceOf(DragaoAncestral.class,j.inimigo);vencer(j);
        assertEquals(VITORIA,j.fase);assertEquals(10,j.diario.entregues());
        var fim=j.encerramento().orElseThrow();assertTrue(fim.venceu());
        assertEquals(classe,fim.classe());assertEquals(10,fim.missoes());
        var finalizado=j.estado();agir(j,CONTINUAR,0);agir(j,MISSAO,9);assertEquals(finalizado,j.estado());
    }

    private MotorJogo emboscada() {
        MotorJogo j=new MotorJogo("Arthur","Paladino",false,new Random(){
            @Override public int nextInt(int limite){return 0;}
        });
        j.local=Local.FLORESTA;j.heroi.hp=50;j.heroi.mana=0;j.heroi.agi=1000;
        agir(j,ACAMPAR,0);assertEquals(COMBATE,j.fase);j.inimigo.xpRec=0;return j;
    }
    @Test void vitoriaNaEmboscadaConcedeDescansoUmaVez() {
        MotorJogo j=emboscada();j.heroi.hp=40;j.heroi.mana=0;
        vencer(j);assertEquals(88,j.heroi.hp);assertEquals(27,j.heroi.mana);
        var antes=j.estado();agir(j,CONTINUAR,0);assertEquals(antes,j.estado());
        Eventos.capturar(e->{},()->j.iniciarCombate(new Slime()));j.inimigo.xpRec=0;j.heroi.hp=40;
        vencer(j);assertEquals(40,j.heroi.hp,"Não repetir descanso em combate seguinte");
    }
    @Test void fugirDaEmboscadaNaoConcedeDescansoNemOAdia() {
        MotorJogo j=emboscada();j.heroi.hp=40;agir(j,FUGIR,0);assertEquals(EXPLORACAO,j.fase);assertEquals(40,j.heroi.hp);
        Eventos.capturar(e->{},()->j.iniciarCombate(new Slime()));j.inimigo.xpRec=0;vencer(j);assertEquals(40,j.heroi.hp);
    }
    @Test void derrotaNaEmboscadaCancelaDescanso() {
        MotorJogo j=emboscada();j.heroi.hp=0;agir(j,DEFENDER,0);agir(j,CONTINUAR,0);
        assertEquals(Local.VILA,j.local);assertEquals(85,j.heroi.hp);
        Eventos.capturar(e->{},()->j.iniciarCombate(new Slime()));j.inimigo.xpRec=0;j.heroi.hp=40;
        vencer(j);assertEquals(40,j.heroi.hp);
    }
}
