package br.com.superrpg.jogo;

import org.junit.jupiter.api.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;
import static br.com.superrpg.jogo.AcaoJogo.Tipo.*;

class MotorJogoTest {
    MotorJogo jogo;
    @BeforeEach void preparar() {
        Entidade.RNG.setSeed(31);
        jogo = new MotorJogo("Arthur", "Paladino", false, new Random(19));
    }
    private void silencioso(Runnable r) { Eventos.capturar(e -> {}, r); }

    @Test void preservaAtributosEInventarioInicial() {
        var s = jogo.estado();
        assertEquals(160, s.heroi().hp());assertEquals(50, s.heroi().mana());
        assertEquals(33,s.heroi().ataque());assertEquals(60,s.ouro());assertEquals(3,s.inventario().size());
        assertEquals(10,s.missoes().size());assertEquals(7,s.locais().size());
    }
    @Test void todasAsClassesTemTresHabilidades() {
        for(String c:List.of("Paladino","Necromante","Arqueiro","Mago","Ladino")) {
            var s=new MotorJogo("Teste",c,false).estado();
            assertEquals(c,s.classe());assertEquals(3,s.habilidades().size());assertTrue(s.heroi().hp()>0);
        }
    }
    @Test void afinidadeEDefesaMantemFormulaOriginal() {
        Slime alvo=new Slime();
        silencioso(()->assertEquals(27,alvo.receberDano(20,Elemento.FOGO,false)));
        assertEquals(33,alvo.hp);
    }
    @Test void guardaEEscudoAbsorvemDano() {
        silencioso(()->{
            jogo.heroi.setDefendendo(true);jogo.heroi.adicionarEfeito(new Efeito(TipoEfeito.ESCUDO,3,10));
            assertEquals(7,jogo.heroi.receberDano(50,Elemento.FISICO,false));
        });assertEquals(153,jogo.heroi.hp);
    }
    @Test void venenoMataNoInicioDoTurno() {
        Slime alvo=new Slime();alvo.hp=5;
        silencioso(()->{alvo.adicionarEfeito(new Efeito(TipoEfeito.VENENO,2,8));assertFalse(alvo.iniciarTurno());});
        assertEquals(0,alvo.hp);
    }
    @Test void subirNivelNaoLeTecladoEExigeTalento() {
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(1),()->silencioso(()->jogo.heroi.ganharXp(100)));
        assertEquals(2,jogo.heroi.nivel);assertEquals(1,jogo.estado().talentos());
        jogo.executar(new AcaoJogo(VIAJAR,1));assertEquals("VILA",jogo.estado().localId());
        jogo.executar(new AcaoJogo(TALENTO,1));assertEquals(215,jogo.heroi.hpMax);assertEquals(0,jogo.estado().talentos());
    }
    @Test void habilidadeSemManaNaoGastaTurno() {
        silencioso(()->jogo.iniciarCombate(new Slime()));jogo.heroi.mana=0;
        int hp=jogo.heroi.hp,rodada=jogo.rodada;
        jogo.executar(new AcaoJogo(HABILIDADE,0));assertEquals(hp,jogo.heroi.hp);assertEquals(rodada,jogo.rodada);
    }
    @Test void itemNaoUtilizavelNaoGastaTurno() {
        silencioso(()->jogo.iniciarCombate(new Slime()));
        jogo.heroi.hp=jogo.heroi.hpMax;int rodada=jogo.rodada;
        jogo.executar(new AcaoJogo(ITEM,0));assertEquals(rodada,jogo.rodada);assertEquals(3,jogo.heroi.inv.size());
    }
    @Test void recargaImpedeUsoRepetido() {
        silencioso(()->jogo.iniciarCombate(new GolemFerro()));
        jogo.executar(new AcaoJogo(HABILIDADE,1));assertFalse(jogo.estado().habilidades().get(1).disponivel());
        int rodada=jogo.rodada;jogo.executar(new AcaoJogo(HABILIDADE,1));assertEquals(rodada,jogo.rodada);
    }
    @Test void atordoarInterrompeGolpeDoChefe() {
        Beholder b=new Beholder();jogo.heroi.agi=50;
        silencioso(()->jogo.iniciarCombate(b));b.carregando=true;
        var eventos=jogo.executar(new AcaoJogo(HABILIDADE,2));
        assertFalse(b.carregando);assertTrue(eventos.stream().anyMatch(e->e.texto().contains("interrompido")));
    }
    @Test void chefeNaoPermiteFuga() {
        silencioso(()->jogo.iniciarCombate(new Beholder()));int rodada=jogo.rodada;
        jogo.executar(AcaoJogo.de(FUGIR));assertEquals(EstadoJogo.Fase.COMBATE,jogo.fase);assertEquals(rodada,jogo.rodada);
    }
    @Test void vitoriaEntregaRecompensaUmaUnicaVez() {
        jogo.heroi.agi=1000;jogo.heroi.atk=999;
        silencioso(()->jogo.iniciarCombate(new Slime()));
        var eventos=jogo.executar(AcaoJogo.de(ATACAR));assertEquals(EstadoJogo.Fase.RECOMPENSA,jogo.fase);
        int ouro=jogo.heroi.ouro;jogo.executar(AcaoJogo.de(ATACAR));assertEquals(ouro,jogo.heroi.ouro);
        assertEquals(1,jogo.heroi.abates);assertTrue(eventos.stream().anyMatch(e->e.tipo()==EventoJogo.Tipo.VITORIA));
        jogo.executar(AcaoJogo.de(CONTINUAR));assertEquals(EstadoJogo.Fase.EXPLORACAO,jogo.fase);
    }
    @Test void derrotaNormalRetornaAVilaComPerdaDeOuro() {
        silencioso(()->jogo.iniciarCombate(new Slime()));jogo.heroi.hp=1;
        silencioso(()->jogo.heroi.adicionarEfeito(new Efeito(TipoEfeito.VENENO,3,999)));
        jogo.executar(AcaoJogo.de(DEFENDER));
        assertEquals(EstadoJogo.Fase.RECOMPENSA,jogo.fase);assertEquals("VILA",jogo.estado().localId());assertEquals(45,jogo.heroi.ouro);
    }
    @Test void derrotaHardcoreEncerraSessao() {
        jogo=new MotorJogo("Teste","Paladino",true,new Random(1));
        silencioso(()->jogo.iniciarCombate(new Slime()));jogo.heroi.hp=1;
        silencioso(()->jogo.heroi.adicionarEfeito(new Efeito(TipoEfeito.VENENO,3,999)));
        jogo.executar(AcaoJogo.de(DEFENDER));assertEquals(EstadoJogo.Fase.DERROTA,jogo.fase);
    }
    @Test void locaisBloqueadosNaoPodemSerVisitados() {
        jogo.executar(new AcaoJogo(VIAJAR,5));assertEquals("VILA",jogo.estado().localId());
        jogo.executar(new AcaoJogo(VIAJAR,6));assertEquals("VILA",jogo.estado().localId());
    }
    @Test void missaoDeColetaConsomeIngredientesEPagaUmaVez() {
        jogo.executar(new AcaoJogo(MISSAO,2));
        for(int i=0;i<3;i++)jogo.heroi.inv.add(Fabrica.criar("Erva Lunar"));
        jogo.executar(new AcaoJogo(MISSAO,2));assertEquals(100,jogo.heroi.ouro);assertEquals(0,jogo.heroi.contar("Erva Lunar"));
        jogo.executar(new AcaoJogo(MISSAO,2));assertEquals(100,jogo.heroi.ouro);assertTrue(jogo.diario.concluida("ervas"));
    }
    @Test void equiparDevolveArmaAnteriorAMochila() {
        Equipamento arma=(Equipamento)Fabrica.criar("Lâmina de Aço");jogo.heroi.inv.add(arma);
        jogo.executar(new AcaoJogo(EQUIPAR,3));assertEquals(37,jogo.heroi.getAtaque());assertEquals(1,jogo.heroi.contar("Espada Enferrujada"));
    }
    @Test void comprarNaoPermiteSaldoNegativo() {
        jogo.executar(new AcaoJogo(COMPRAR,0));assertEquals(30,jogo.heroi.ouro);
        jogo.executar(new AcaoJogo(COMPRAR,1));assertEquals(30,jogo.heroi.ouro);
    }
    @Test void estadoNaoExpoeColecoesMutaveis() {
        var s=jogo.estado();assertThrows(UnsupportedOperationException.class,()->s.inventario().clear());
        jogo.executar(new AcaoJogo(COMPRAR,0));assertEquals(3,s.inventario().size());assertEquals(4,jogo.estado().inventario().size());
    }
    @Test void sessoesTemEstadosIndependentes() {
        MotorJogo outro=new MotorJogo("Lira","Mago",false);jogo.executar(new AcaoJogo(COMPRAR,0));assertEquals(60,outro.estado().ouro());
    }
    @Test void capturaDeEventosNaoVazaEntreAcoes() {
        AtomicReference<EventoJogo> capturado=new AtomicReference<>();
        assertThrows(IllegalStateException.class,()->Eventos.capturar(capturado::set,()->{throw new IllegalStateException();}));
        List<EventoJogo> eventos=jogo.executar(new AcaoJogo(COMPRAR,0));assertNull(capturado.get());assertFalse(eventos.isEmpty());
    }
}
