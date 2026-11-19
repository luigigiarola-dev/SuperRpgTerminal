package br.com.superrpg.jogo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Formato explícito, versionado e independente das classes de apresentação. */
public final class Salvamento {
    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path arquivo;
    record ItemSalvo(String nome, int melhoria) {}
    record MissaoSalva(String id, int estado, int progresso) {}
    record Dados(int versao, String nome, String classe, boolean hardcore, int nivel, int xp,
            int hp, int hpMax, int mana, int manaMax, int ataque, int defesa, int agilidade,
            int ouro, int honra, int abates, int dia, int periodo, String local,
            List<ItemSalvo> equipamentos, List<ItemSalvo> inventario, String aliado, List<MissaoSalva> missoes, boolean charadaHoje, String idJornada) {}

    public Salvamento(Path arquivo) { this.arquivo = arquivo.toAbsolutePath(); }
    public static Salvamento padrao() {
        String os=System.getProperty("os.name").toLowerCase(Locale.ROOT);
        Path base=Path.of(System.getProperty("user.home"));
        if(os.contains("mac")) base=base.resolve("Library/Application Support");
        else if(os.contains("win")) {
            String appdata=System.getenv("APPDATA");base=appdata==null?base.resolve("AppData/Roaming"):Path.of(appdata);
        } else {
            String xdg=System.getenv("XDG_DATA_HOME");base=xdg==null||xdg.isBlank()?base.resolve(".local/share"):Path.of(xdg);
        }
        return new Salvamento(base.resolve("SuperRpgTerminal/jornada.json"));
    }
    public Path arquivo() { return arquivo; }
    public boolean existe() { return Files.isRegularFile(arquivo); }

    public void salvar(MotorJogo jogo) throws IOException {
        if(jogo.fase!=EstadoJogo.Fase.EXPLORACAO || jogo.heroi.talentosPendentes>0) throw new IOException("Conclua o combate ou evento, desça da torre se estiver em uma subida e escolha seus talentos antes de salvar.");
        Heroi h=jogo.heroi;
        Dados dados=new Dados(4,h.getNome(),h.getClasse(),jogo.hardcore,h.nivel,h.xp,h.hp,h.hpMax,h.mana,h.manaMax,h.atk,h.def,h.agi,h.ouro,h.honra,h.abates,
                jogo.dia,jogo.periodo,jogo.local.name(),Arrays.stream(h.equip).map(Salvamento::item).toList(),h.inv.stream().map(Salvamento::item).toList(),
                h.aliado==null?null:h.aliado instanceof LoboCinzento?"lobo":"lira",jogo.diario.missoes.stream().map(m->new MissaoSalva(m.id,m.estado,m.progresso)).toList(),jogo.charadaHoje,jogo.idJornada);
        restaurar(dados); // valida antes de substituir o arquivo existente
        Files.createDirectories(arquivo.getParent());
        Path temporario=Files.createTempFile(arquivo.getParent(),"jornada-",".tmp");
        try {
            Files.writeString(temporario,JSON.toJson(dados),StandardCharsets.UTF_8);
            if(existe()) Files.copy(arquivo,arquivo.resolveSibling("jornada.anterior.json"),StandardCopyOption.REPLACE_EXISTING);
            try { Files.move(temporario,arquivo,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
            catch(AtomicMoveNotSupportedException e){Files.move(temporario,arquivo,StandardCopyOption.REPLACE_EXISTING);}
        } finally { Files.deleteIfExists(temporario); }
    }
    public MotorJogo carregar() throws IOException {
        try { return restaurar(JSON.fromJson(Files.readString(arquivo,StandardCharsets.UTF_8),Dados.class)); }
        catch(RuntimeException e){throw new IOException("O arquivo salvo é inválido ou pertence a uma versão incompatível.",e);}
    }
    private static ItemSalvo item(Item it) {
        if(it==null)return null;
        return it instanceof Equipamento e?new ItemSalvo(e.getNomeBase(),e.melhoria):new ItemSalvo(it.getNome(),0);
    }
    private static Item restaurarItem(ItemSalvo i) {
        if(i==null)return null;
        Item item=Fabrica.criar(i.nome());exigir(item!=null,"Item desconhecido");
        exigir(i.melhoria()>=0&&i.melhoria()<=5,"Melhoria inválida");
        if(item instanceof Equipamento e)e.melhoria=i.melhoria();else exigir(i.melhoria()==0,"Consumível com melhoria");
        return item;
    }
    private static void exigir(boolean condicao,String mensagem) { if(!condicao)throw new IllegalArgumentException(mensagem); }
    private static MotorJogo restaurar(Dados d) {
        exigir(d!=null&&(d.versao()==3||d.versao()==4),"Versão inválida");
        exigir(d.nome()!=null&&!d.nome().isBlank()&&d.nome().length()<=30,"Nome inválido");
        exigir(d.nivel()>=1&&d.nivel()<=15&&d.xp()>=0&&d.xp()<d.nivel()*100,"Nível ou XP inválido");
        exigir(d.hpMax()>0&&d.hpMax()<=10000&&d.hp()>0&&d.hp()<=d.hpMax(),"Vida inválida");
        exigir(d.manaMax()>0&&d.manaMax()<=10000&&d.mana()>=0&&d.mana()<=d.manaMax(),"Mana inválida");
        exigir(d.ataque()>0&&d.ataque()<=10000&&d.defesa()>=0&&d.defesa()<=10000&&d.agilidade()>0&&d.agilidade()<=10000,"Atributos inválidos");
        exigir(d.ouro()>=0&&d.honra()>=-5&&d.honra()<=5&&d.abates()>=0,"Progresso inválido");
        exigir(d.dia()>0&&d.periodo()>=0&&d.periodo()<4,"Tempo inválido");
        exigir(d.equipamentos()!=null&&d.equipamentos().size()==3&&d.inventario()!=null&&d.inventario().size()<=10000,"Inventário inválido");
        MotorJogo jogo=new MotorJogo(d.nome(),d.classe(),d.hardcore());Heroi h=jogo.heroi;
        // Saves antigos idênticos recebem a mesma identidade sem modificar o arquivo original.
        jogo.idJornada=d.idJornada()==null?UUID.nameUUIDFromBytes(JSON.toJson(d).getBytes(StandardCharsets.UTF_8)).toString():UUID.fromString(d.idJornada()).toString();
        h.nivel=d.nivel();h.xp=d.xp();h.hp=d.hp();h.hpMax=d.hpMax();h.mana=d.mana();h.manaMax=d.manaMax();h.atk=d.ataque();h.def=d.defesa();h.agi=d.agilidade();
        h.ouro=d.ouro();h.honra=d.honra();h.abates=d.abates();h.inv.clear();
        for(int i=0;i<3;i++) {
            Item item=restaurarItem(d.equipamentos().get(i));
            exigir(item==null||item instanceof Equipamento e&&e.slot.ordinal()==i,"Slot inválido");h.equip[i]=(Equipamento)item;
        }
        for(ItemSalvo i:d.inventario()){exigir(i!=null,"Item vazio");h.inv.add(restaurarItem(i));}
        exigir(d.aliado()==null||List.of("lobo","lira").contains(d.aliado()),"Aliado inválido");
        if(d.aliado()!=null)h.aliado=d.aliado().equals("lobo")?new LoboCinzento():new IrmaLira();
        jogo.dia=d.dia();jogo.periodo=d.periodo();jogo.local=Local.valueOf(d.local());
        jogo.charadaHoje=d.versao()==4&&d.charadaHoje();
        exigir(d.missoes()!=null&&d.missoes().size()==10,"Diário inválido");Set<String> vistos=new HashSet<>();
        for(MissaoSalva s:d.missoes()) {
            exigir(s!=null&&vistos.add(s.id()),"Missão duplicada");Missao m=jogo.diario.obter(s.id());
            exigir(m!=null&&s.estado()>=0&&s.estado()<=2&&s.progresso()>=0&&s.progresso()<=m.qtd,"Missão inválida");m.estado=s.estado();m.progresso=s.progresso();
        }
        return jogo;
    }

    /** Importa sem alterar o arquivo v2 e sem usar o estado estático do console. */
    public MotorJogo importarV2(Path origem) throws IOException {
        try {
            List<String> l=Files.readAllLines(origem,StandardCharsets.UTF_8);
            exigir(l.size()>=9&&l.get(0).equals("v2"),"Formato v2 inválido");
            int[] a=Arrays.stream(l.get(3).split(",")).mapToInt(Integer::parseInt).toArray();exigir(a.length==12,"Atributos incompletos");
            List<ItemSalvo> eq=new ArrayList<>();for(String i:l.get(4).split(";"))eq.add(itemV2(i));
            List<ItemSalvo> inv=new ArrayList<>();if(!l.get(5).equals("-"))for(String i:l.get(5).split(";"))inv.add(itemV2(i));
            String[] t=l.get(7).split(",");exigir(t.length==5,"Tempo incompleto");
            List<MissaoSalva> ms=new ArrayList<>();for(String m:l.get(8).split(";")){String[] p=m.split(":");ms.add(new MissaoSalva(p[0],Integer.parseInt(p[1]),Integer.parseInt(p[2])));}
            exigir(t[4].equals("true")||t[4].equals("false"),"Marcador da charada inválido");
            return restaurar(new Dados(4,l.get(2),l.get(1),Boolean.parseBoolean(t[3]),a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],
                    Integer.parseInt(t[0]),Integer.parseInt(t[1]),t[2],eq,inv,l.get(6).equals("-")?null:l.get(6),ms,Boolean.parseBoolean(t[4]),null));
        } catch(RuntimeException e){throw new IOException("Não foi possível importar este save v2.",e);}
    }
    private static ItemSalvo itemV2(String s) {
        if(s.equals("-"))return null;String[] p=s.split("\\|");return new ItemSalvo(p[0],p.length>1?Integer.parseInt(p[1]):0);
    }
}
