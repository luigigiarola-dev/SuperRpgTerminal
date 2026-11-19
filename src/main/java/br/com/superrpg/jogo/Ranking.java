package br.com.superrpg.jogo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Ranking local. Uma jornada registra somente seu primeiro encerramento. */
public final class Ranking {
    private static final Gson JSON=new GsonBuilder().setPrettyPrinting().create();
    public record Entrada(String idJornada, HistoriaCampanha.Encerramento resultado) {}
    private record Dados(int versao, List<Entrada> entradas) {}
    private final Path arquivo;

    public Ranking(Path arquivo) { this.arquivo=arquivo.toAbsolutePath(); }
    public static Ranking padrao() { return new Ranking(Salvamento.padrao().arquivo().resolveSibling("ranking.json")); }

    public List<Entrada> listar() throws IOException {
        if(!Files.exists(arquivo))return List.of();
        try {
            if(Files.size(arquivo)>10_000_000)throw new IOException("Ranking grande demais para leitura segura.");
            Dados d=JSON.fromJson(Files.readString(arquivo,StandardCharsets.UTF_8),Dados.class);
            if(d==null||d.versao()!=1||d.entradas()==null)throw new IllegalArgumentException();
            Set<String> ids=new HashSet<>();
            for(Entrada e:d.entradas()) {
                validar(e);
                if(!ids.add(e.idJornada()))throw new IllegalArgumentException();
            }
            return d.entradas().stream().sorted(Comparator
                .comparingInt((Entrada e)->e.resultado().pontuacao()).reversed()
                .thenComparing(e->e.resultado().nome()).thenComparing(Entrada::idJornada)).toList();
        } catch(RuntimeException e) { throw new IOException("Ranking inválido ou incompatível. O arquivo foi preservado.",e); }
    }

    public boolean registrar(MotorJogo jogo) throws IOException {
        var fim=jogo.encerramento().orElseThrow(()->new IllegalStateException("A jornada ainda não terminou."));
        return registrar(new Entrada(jogo.idJornada(),fim));
    }

    // Também utilizado pelos testes; a interface registra somente sessões encerradas pelo motor.
    boolean registrar(Entrada entrada) throws IOException {
        validar(entrada);Files.createDirectories(arquivo.getParent());
        try(var canal=FileChannel.open(arquivo.resolveSibling(arquivo.getFileName()+".lock"),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            var trava=canal.tryLock()) {
            if(trava==null)throw new IOException("Ranking em uso por outra janela. Tente novamente.");
            List<Entrada> entradas=new ArrayList<>(listar());
            if(entradas.stream().anyMatch(e->e.idJornada().equals(entrada.idJornada())))return false;
            entradas.add(entrada);
            Path temporario=Files.createTempFile(arquivo.getParent(),"ranking-",".tmp");
            try {
                Files.writeString(temporario,JSON.toJson(new Dados(1,entradas)),StandardCharsets.UTF_8);
                if(Files.exists(arquivo))Files.copy(arquivo,arquivo.resolveSibling("ranking.anterior.json"),StandardCopyOption.REPLACE_EXISTING);
                try { Files.move(temporario,arquivo,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
                catch(AtomicMoveNotSupportedException e) { Files.move(temporario,arquivo,StandardCopyOption.REPLACE_EXISTING); }
            } finally { Files.deleteIfExists(temporario); }
            return true;
        } catch(OverlappingFileLockException e) { throw new IOException("Ranking em uso por outra janela. Tente novamente.",e); }
    }

    private static void validar(Entrada e) {
        if(e==null||e.resultado()==null)throw new IllegalArgumentException("Entrada inválida");
        UUID.fromString(e.idJornada());var r=e.resultado();
        if(r.nome()==null||r.nome().isBlank()||r.nome().length()>30||r.classe()==null
            ||!List.of("Paladino","Necromante","Arqueiro","Mago","Ladino").contains(r.classe())
            ||r.nivel()<1||r.nivel()>15||r.dias()<1||r.abates()<0||r.missoes()<0||r.totalMissoes()!=10
            ||r.missoes()>r.totalMissoes()||r.honra() < -5||r.honra()>5||r.pontuacao()<0||(!r.venceu()&&!r.hardcore()))
            throw new IllegalArgumentException("Resultado inválido");
    }
}
