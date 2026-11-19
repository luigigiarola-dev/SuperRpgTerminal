package br.com.superrpg.jogo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;
import static br.com.superrpg.jogo.EstadoJogo.Fase.*;

class RankingTest {
    @TempDir Path pasta;
    private Ranking ranking() { return new Ranking(pasta.resolve("ranking.json")); }
    private MotorJogo fim(String nome,boolean hardcore,boolean venceu) {
        MotorJogo j=new MotorJogo(nome,"Paladino",hardcore);j.fase=venceu?VITORIA:DERROTA;return j;
    }
    @Test void ausenteRetornaListaVaziaSemCriarArquivo() throws Exception {
        assertTrue(ranking().listar().isEmpty());assertFalse(Files.exists(pasta.resolve("ranking.json")));
    }
    @Test void registroPersistenteOrdenadoEImutavel() throws Exception {
        var baixo=fim("Álvaro",true,false);var alto=fim("João",false,true);
        assertTrue(ranking().registrar(baixo));assertTrue(ranking().registrar(alto));
        var entradas=ranking().listar();assertEquals("João",entradas.get(0).resultado().nome());
        assertEquals(baixo.encerramento().orElseThrow(),entradas.get(1).resultado());
        assertThrows(UnsupportedOperationException.class,()->entradas.clear());
        assertEquals(1,new Ranking(pasta.resolve("ranking.anterior.json")).listar().size());
    }
    @Test void repetirRegistroPreservaPrimeiroResultado() throws Exception {
        var j=fim("Arthur",false,true);ranking().registrar(j);String antes=Files.readString(pasta.resolve("ranking.json"));
        j.heroi.ouro+=100;assertFalse(ranking().registrar(j));
        assertEquals(antes,Files.readString(pasta.resolve("ranking.json")));
    }
    @Test void jornadasComMesmoNomeSaoIndependentes() throws Exception {
        ranking().registrar(fim("Arthur",false,true));ranking().registrar(fim("Arthur",false,true));
        assertEquals(2,ranking().listar().size());
    }
    @Test void naoRegistraJornadaAbertaNemDerrotaNormal() {
        assertThrows(IllegalStateException.class,()->ranking().registrar(new MotorJogo("Arthur","Paladino",false)));
        assertThrows(IllegalArgumentException.class,()->ranking().registrar(fim("Arthur",false,false)));
        assertFalse(Files.exists(pasta.resolve("ranking.json")));
    }
    @Test void corrompidoNaoEhSobrescrito() throws Exception {
        Path arquivo=pasta.resolve("ranking.json");Files.writeString(arquivo,"{inválido");
        assertThrows(IOException.class,()->ranking().listar());
        assertThrows(IOException.class,()->ranking().registrar(fim("Arthur",false,true)));
        assertEquals("{inválido",Files.readString(arquivo));
    }
    @Test void versaoIncompativelNaoEhSobrescrita() throws Exception {
        Path arquivo=pasta.resolve("ranking.json");Files.writeString(arquivo,"{\"versao\":2,\"entradas\":[]}");
        assertThrows(IOException.class,()->ranking().registrar(fim("Arthur",false,true)));
        assertTrue(Files.readString(arquivo).contains("\"versao\":2"));
    }
    @Test void travaConcorrenteFalhaSemPerderDadosEPermiteNovaTentativa() throws Exception {
        var j=fim("Arthur",false,true);
        try(var canal=FileChannel.open(pasta.resolve("ranking.json.lock"),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            var trava=canal.lock()) {
            assertThrows(IOException.class,()->ranking().registrar(j));assertTrue(ranking().listar().isEmpty());
        }
        assertTrue(ranking().registrar(j));
    }
    @Test void identidadeSobreviveAoSaveEReloadSemDuplicarFinal() throws Exception {
        var j=new MotorJogo("Arthur","Paladino",false);var save=new Salvamento(pasta.resolve("jornada.json"));save.salvar(j);
        String conteudo=Files.readString(save.arquivo());j.fase=VITORIA;ranking().registrar(j);
        var lido=save.carregar();assertEquals(j.idJornada(),lido.idJornada());lido.fase=VITORIA;
        assertFalse(ranking().registrar(lido));assertEquals(conteudo,Files.readString(save.arquivo()));
    }
    @Test void saveAntigoRecebeIdentidadeEstavelSemSerAlterado() throws Exception {
        var save=new Salvamento(pasta.resolve("jornada.json"));save.salvar(new MotorJogo("Arthur","Paladino",false));
        var json=com.google.gson.JsonParser.parseString(Files.readString(save.arquivo())).getAsJsonObject();json.remove("idJornada");
        String antigo=json.toString();Files.writeString(save.arquivo(),antigo);
        var a=save.carregar();var b=save.carregar();assertEquals(a.idJornada(),b.idJornada());
        assertEquals(antigo,Files.readString(save.arquivo()));
        save.salvar(a);assertEquals(a.idJornada(),save.carregar().idJornada());
    }
}
