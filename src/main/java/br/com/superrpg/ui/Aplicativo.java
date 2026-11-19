package br.com.superrpg.ui;

import br.com.superrpg.jogo.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.util.*;
import java.nio.file.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import static br.com.superrpg.jogo.AcaoJogo.Tipo.*;

public final class Aplicativo extends Application {
    private Stage janela;
    private StackPane raiz;
    private MotorJogo jogo;
    private String pagina = "Jornada", classeEscolhida = "Paladino";
    private final Deque<String> historico = new ArrayDeque<>();
    private boolean animando, rapido;
    private String ultimoResultado = "";
    private StackPane arena;
    private String nomeHeroi = "";
    private int abaVila;
    private int abaMissoes;
    private boolean modoHardcore;
    private final Salvamento salvamento = Salvamento.padrao();
    private Ranking ranking;
    private String jornadaRegistrada;

    @Override public void start(Stage stage) {
        if(getParameters().getRaw().contains("--smoke")) {
            try { ranking=new Ranking(Files.createTempDirectory("superrpg-ranking-smoke-").resolve("ranking.json")); }
            catch(java.io.IOException e) { throw new IllegalStateException("Não foi possível isolar o ranking do teste",e); }
        } else ranking=Ranking.padrao();
        janela = stage; raiz = new StackPane();
        Scene scene = new Scene(raiz, 1280, 820);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/tema.css")).toExternalForm());
        stage.setScene(scene); stage.setTitle("SuperRPG · Vila Aurora");
        stage.setMinWidth(1024); stage.setMinHeight(720);
        stage.setOnCloseRequest(e -> {
            if (jogo != null && !getParameters().getRaw().contains("--smoke")) {
                e.consume(); confirmarSaida(stage::close);
            }
        });
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) stage.setFullScreen(!stage.isFullScreen());
            if (jogo == null || animando || !pagina.equals("Jornada") || jogo.estado().fase() != EstadoJogo.Fase.COMBATE) return;
            if (e.getCode() == KeyCode.DIGIT1) agir(AcaoJogo.de(ATACAR));
            if (e.getCode() == KeyCode.DIGIT2) habilidades();
            if (e.getCode() == KeyCode.DIGIT3) agir(AcaoJogo.de(DEFENDER));
        });
        titulo(); stage.show();
        if (getParameters().getRaw().contains("--smoke")) smoke();
    }

    private Label texto(String s, String estilo) { Label l=new Label(s); l.getStyleClass().add(estilo); l.setWrapText(true); return l; }
    private Button botao(String s, String estilo, Runnable acao) {
        Button b=new Button(s); b.getStyleClass().add(estilo); b.setOnAction(e -> acao.run()); b.setMaxWidth(Double.MAX_VALUE); return b;
    }
    private Region espaco() { Region r=new Region(); HBox.setHgrow(r,Priority.ALWAYS); VBox.setVgrow(r,Priority.ALWAYS); return r; }
    private VBox coluna(Node... n) { VBox v=new VBox(14,n); return v; }
    private HBox linha(Node... n) { HBox h=new HBox(14,n); h.setAlignment(Pos.CENTER_LEFT); return h; }
    private void trocar(Node node) {
        raiz.getChildren().setAll(node);
        if (!rapido) { FadeTransition f=new FadeTransition(Duration.millis(180),node);f.setFromValue(.45);f.setToValue(1);f.play(); }
    }

    private void titulo() {
        Cenario fundo=new Cenario("TITULO",null,null,true); fundo.setMinHeight(0);
        VBox painel=coluna(texto("UMA AVENTURA EM JAVA", "eyebrow"), texto("SUPER\nRPG", "logo"),
                texto("As sombras despertam.\nSua história começa em Aurora.", "intro"));
        painel.setMaxWidth(510);painel.setAlignment(Pos.CENTER_LEFT);painel.setPadding(new Insets(44,60,44,60));
        Button continuar=botao("Continuar jornada", "secondary",()->carregar(false));continuar.setDisable(!salvamento.existe());
        painel.getChildren().addAll(espaco(),botao("NOVA JORNADA   →", "primary",this::criacao),continuar,
                botao("Salão dos heróis", "secondary",()->ranking(false)),
                botao("Importar save do terminal", "ghost",()->carregar(true)),
                botao("Sair", "ghost",janela::close));
        StackPane.setAlignment(painel,Pos.CENTER_LEFT);
        Label rodape=texto("VILA AURORA    /    EDIÇÃO GRÁFICA · EM DESENVOLVIMENTO", "footer");
        StackPane.setAlignment(rodape,Pos.BOTTOM_RIGHT);StackPane.setMargin(rodape,new Insets(28));
        StackPane p=new StackPane(fundo,painel,rodape);p.getStyleClass().add("title-screen");trocar(p);
    }

    private void criacao() {
        VBox conteudo=coluna(texto("01  /  UM NOME PARA A LENDA", "eyebrow"), texto("Quem cruza os portões?", "heading"));
        conteudo.setPadding(new Insets(36));
        TextField nome=new TextField(nomeHeroi);nome.setPromptText("Nome do herói");nome.setMaxWidth(420);
        nome.setTextFormatter(new TextFormatter<String>(c -> c.getControlNewText().length() <= 30 ? c : null));
        nome.textProperty().addListener((o,a,b)->nomeHeroi=b);
        HBox classes=new HBox(10);
        for(String c: List.of("Paladino","Necromante","Arqueiro","Mago","Ladino")) {
            Button b=botao(c,c.equals(classeEscolhida)?"selected":"secondary",()->{classeEscolhida=c;criacao();});
            b.setPrefWidth(170);classes.getChildren().add(b);
        }
        EstadoJogo preview=new MotorJogo("Viajante",classeEscolhida,false).estado();
        VBox descricao=coluna(texto(classeEscolhida,"heading"),texto(descricaoClasse(),"intro"),
                texto("VIDA  " + preview.heroi().hpMax() + "     MANA  " + preview.heroi().manaMax(),"eyebrow"));
        for(var h:preview.habilidades()) descricao.getChildren().add(coluna(texto(h.nome(),"card-title"),texto(h.descricao(),"muted")));
        descricao.setPrefWidth(400);descricao.getStyleClass().add("panel");
        Cenario retrato=new Cenario("FLORESTA",classeEscolhida,null,true); HBox.setHgrow(retrato,Priority.ALWAYS);
        HBox centro=linha(retrato,descricao);VBox.setVgrow(centro,Priority.ALWAYS);
        CheckBox hardcore=new CheckBox("Hardcore — uma única vida");hardcore.setSelected(modoHardcore);hardcore.setOnAction(e->modoHardcore=hardcore.isSelected());
        conteudo.getChildren().addAll(nome,classes,centro,linha(hardcore,espaco(),botao("Voltar","ghost",this::titulo),botao("ENTRAR EM AURORA   →","primary",()->{
            jogo=getParameters().getRaw().contains("--smoke")?new MotorJogo(nomeHeroi,classeEscolhida,modoHardcore,41L):new MotorJogo(nomeHeroi,classeEscolhida,modoHardcore);historico.clear();
            ultimoResultado="";pagina="Jornada";
            for(var fala:jogo.introducao())historico.add(fala.personagem()+": "+fala.texto());
            introducao(0);
        })));
        trocar(conteudo);
    }
    private void introducao(int indice) {
        var falas=jogo.introducao();var fala=falas.get(indice);
        VBox painel=coluna(texto("PRÓLOGO  /  "+(indice+1)+" DE "+falas.size(),"eyebrow"),
            texto("Os portões de Aurora","heading"),texto(fala.personagem(),"gold"),texto(fala.texto(),"intro"));
        painel.getChildren().add(botao(indice==falas.size()-1?"COMEÇAR JORNADA   →":"PRÓXIMO   →","primary",
            ()->{if(indice+1<falas.size())introducao(indice+1);else renderizar();}));
        if(indice<falas.size()-1)painel.getChildren().add(botao("Pular introdução","ghost",this::renderizar));
        mostrarHistoria(painel,"VILA",jogo.estado().classe());
    }

    private void mostrarHistoria(VBox painel,String local,String classe) {
        painel.getStyleClass().add("story-panel");painel.setPadding(new Insets(32));painel.setMaxWidth(650);
        ScrollPane rolagem=new ScrollPane(painel);rolagem.setFitToWidth(true);rolagem.setMaxWidth(680);rolagem.setMaxHeight(570);
        rolagem.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        StackPane tela=new StackPane(new Cenario(local,classe,null,false),rolagem);
        tela.setPadding(new Insets(30));trocar(tela);
    }

    private void encerramento(HistoriaCampanha.Encerramento fim) {
        String registro="Prévia visual: nenhum resultado registrado.";
        boolean falhou=false;
        if(jogo!=null && jogo.encerramento().isPresent()) {
            try {
                if(!jogo.idJornada().equals(jornadaRegistrada)) { ranking.registrar(jogo);jornadaRegistrada=jogo.idJornada(); }
                registro="Primeiro encerramento desta jornada registrado no Salão dos heróis.";
            } catch(java.io.IOException e) { registro="Não foi possível registrar o ranking. Seu resultado continua nesta tela. "+e.getMessage();falhou=true; }
        }
        VBox painel=coluna(texto(fim.venceu()?"EPÍLOGO  /  O DRAGÃO CAIU":"FIM DA JORNADA  /  HARDCORE","eyebrow"),
            texto(fim.titulo(),"heading"),texto(fim.texto(),"intro"),new Separator(),
            texto(fim.nome()+" · "+fim.classe()+" · Nível "+fim.nivel(),"card-title"),
            texto("Dias: "+fim.dias()+"  ·  Abates: "+fim.abates()+"  ·  Missões: "+fim.missoes()+"/"+fim.totalMissoes(),"muted"),
            texto("Honra: "+fim.honra()+"  ·  Modo "+(fim.hardcore()?"Hardcore":"Normal"),"muted"),
            texto(fim.pontuacao()+" PONTOS","gold"),
            texto(registro,"muted"),
            texto("Seu último salvamento foi preservado. Repetir o final dessa jornada não adiciona outro resultado ao ranking.","tiny"),
            botao("VOLTAR AO MENU","primary",()->{jogo=null;titulo();}));
        if(falhou)painel.getChildren().add(botao("Tentar registrar novamente","secondary",this::renderizar));
        mostrarHistoria(painel,fim.venceu()?"VILA":"TITULO",fim.classe());
    }

    private void ranking(boolean hardcore) {
        ranking(hardcore,null);
    }

    private void ranking(boolean hardcore,List<Ranking.Entrada> previa) {
        VBox painel=coluna(texto("SALÃO DOS HERÓIS","eyebrow"),texto("Lendas de Aurora","heading"),
            texto("Cinco maiores pontuações por modo. Uma entrada por jornada; o primeiro encerramento é preservado.","muted"),
            linha(botao("Normal",hardcore?"secondary":"selected",()->ranking(false)),
                botao("Hardcore",hardcore?"selected":"secondary",()->ranking(true))));
        try {
            var entradas=(previa==null?ranking.listar():previa).stream().filter(e->e.resultado().hardcore()==hardcore).limit(5).toList();
            if(entradas.isEmpty())painel.getChildren().add(texto("Nenhuma jornada concluída neste modo ainda.","intro"));
            for(int i=0;i<entradas.size();i++) {
                var r=entradas.get(i).resultado();
                painel.getChildren().add(coluna(texto((i+1)+". "+r.nome()+" · "+r.pontuacao()+" pontos","card-title"),
                    texto(r.classe()+" · Nível "+r.nivel()+" · Dia "+r.dias()+" · "+(r.venceu()?"Vitória":"Derrota"),"muted")));
            }
        } catch(java.io.IOException e) {
            painel.getChildren().add(texto("Não foi possível ler o ranking. "+e.getMessage(),"intro"));
            painel.getChildren().add(botao("Tentar novamente","secondary",()->ranking(hardcore)));
        }
        painel.getChildren().add(botao("VOLTAR AO MENU","primary",this::titulo));
        mostrarHistoria(painel,"TITULO",null);
    }

    private String descricaoClasse() {
        return switch(classeEscolhida) {
            case "Paladino" -> "A luz é seu juramento. Aço, proteção e cura para resistir na linha de frente.";
            case "Necromante" -> "A vida tem um preço. Drene a essência dos inimigos e domine a sombra.";
            case "Arqueiro" -> "Cada flecha encontra um destino. Velocidade, fogo e precisão à distância.";
            case "Mago" -> "O mundo responde à sua vontade. Conjure fogo, raios e prisões arcanas.";
            default -> "Um passo nas sombras. Veneno e lâminas encontram brechas na defesa.";
        };
    }

    private void renderizar() {
        EstadoJogo s=jogo.estado();
        if(s.talentos()==0 && jogo.encerramento().isPresent()) { encerramento(jogo.encerramento().orElseThrow());return; }
        if(s.fase()!=EstadoJogo.Fase.EXPLORACAO && !pagina.equals("Mochila")) pagina="Jornada";
        if(s.talentos()>0) pagina="Jornada";
        if(s.fase()==EstadoJogo.Fase.EVENTO) pagina="Jornada";
        BorderPane shell=new BorderPane();shell.setLeft(lateral(s));
        VBox centro=new VBox(18);centro.setPadding(new Insets(26,30,22,30));
        HBox topo=linha(texto("AURORA  /  " + pagina.toUpperCase(Locale.ROOT),"eyebrow"),espaco(),
                texto("DIA " + s.dia() + "  ·  " + s.periodo().toUpperCase(Locale.ROOT),"muted"),texto(s.ouro()+"  OURO","gold"));
        centro.getChildren().add(topo);
        Node corpo=s.talentos()>0?talentos(s):switch(pagina) {
            case "Mapa" -> mapa(s);
            case "Mochila" -> inventario(s);
            case "Missões" -> moradoresEMissoes(s);
            case "Serviços" -> servicos(s);
            default -> jornada(s);
        };
        VBox.setVgrow(corpo,Priority.ALWAYS);centro.getChildren().add(corpo);
        TitledPane log=new TitledPane();log.setText("RELATOS DA JORNADA  ·  " + (historico.isEmpty()?"Sua história começa aqui":resumo(historico.peekLast())));log.setExpanded(false);
        TextArea area=new TextArea(String.join("\n",historico));area.setEditable(false);area.setWrapText(true);area.setPrefRowCount(5);area.positionCaret(area.getLength());log.setContent(area);
        centro.getChildren().add(log);shell.setCenter(centro);trocar(shell);
    }
    private String resumo(String s) { return s.length()>80?s.substring(0,77)+"…":s; }

    private VBox lateral(EstadoJogo s) {
        VBox v=new VBox(8);v.getStyleClass().add("sidebar");v.setPrefWidth(208);v.setPadding(new Insets(24,18,18,18));
        v.getChildren().addAll(texto("SUPER RPG","brand"),texto("CRÔNICAS DE AURORA","tiny"),new Separator());
        boolean combate=s.fase()==EstadoJogo.Fase.COMBATE;
        for(String p:List.of("Jornada","Mapa","Mochila","Missões","Serviços")) {
            Button b=botao(p,p.equals(pagina)?"nav-active":"nav",()->{pagina=p;renderizar();});
            b.setDisable(s.talentos()>0 || (s.fase()!=EstadoJogo.Fase.EXPLORACAO && !((combate || s.fase()==EstadoJogo.Fase.PAUSA_TORRE) && p.equals("Mochila")) && !p.equals("Jornada")) || (p.equals("Mercador")||p.equals("Serviços"))&&!s.localId().equals("VILA"));
            v.getChildren().add(b);
        }
        v.getChildren().add(espaco());
        v.getChildren().addAll(texto(s.heroi().nome(),"card-title"),texto(s.classe()+" · Nível "+s.heroi().nivel(),"muted"),
                barra("VIDA",s.heroi().hp(),s.heroi().hpMax(),"hp"),barra("MANA",s.heroi().mana(),s.heroi().manaMax(),"mana"),
                barra("EXPERIÊNCIA",s.xp(),s.xpProximo(),"xp"),texto("Honra "+s.honra()+(s.hardcore()?" · Hardcore":" · Normal"),"tiny"),new Separator());
        CheckBox rapidez=new CheckBox("Animações rápidas");rapidez.setSelected(rapido);rapidez.setOnAction(e->rapido=rapidez.isSelected());
        Button salvar=botao("Salvar jornada","secondary",this::salvar);
        salvar.setDisable(s.fase()!=EstadoJogo.Fase.EXPLORACAO || s.talentos()>0);
        v.getChildren().addAll(rapidez,salvar,botao("Menu principal","ghost",()->confirmarSaida(()->{jogo=null;titulo();})));
        return v;
    }
    private VBox barra(String nome,int atual,int max,String tipo) {
        Label a=texto(nome,"tiny"), b=texto(atual+" / "+max,"tiny");
        ProgressBar p=new ProgressBar(max==0?0:(double)atual/max);p.setMaxWidth(Double.MAX_VALUE);p.getStyleClass().add(tipo);
        p.setMinHeight(7);
        VBox box=new VBox(6,linha(a,espaco(),b),p);box.setMinHeight(Region.USE_PREF_SIZE);return box;
    }
    private Node jornada(EstadoJogo s) {
        if(s.fase()==EstadoJogo.Fase.EVENTO)return narrativa(s.narrativa());
        VBox v=new VBox(14);boolean combate=s.fase()==EstadoJogo.Fase.COMBATE;
        String titulo=combate?"O confronto":s.fase()==EstadoJogo.Fase.VITORIA?"Aurora verá outro amanhecer":s.fase()==EstadoJogo.Fase.DERROTA?"Uma lenda chega ao fim":s.fase()==EstadoJogo.Fase.RECOMPENSA?(s.heroi().hp()>0&&s.inimigo().hp()==0?"O caminho está livre":"De volta ao abrigo"):s.local();
        v.getChildren().add(linha(texto(titulo,"heading"),espaco(),texto((s.andarTorre()>0?"ANDAR "+s.andarTorre()+" / 3  ·  ":"")+(combate?"RODADA "+s.rodada():"REINO DE AURORA"),"eyebrow")));
        Cenario cena=new Cenario(s.localId(),s.classe(),s.inimigo()==null?null:s.inimigo().tipo(),!s.periodo().equals("Manhã")&&!s.periodo().equals("Tarde"));
        arena=new StackPane(cena);arena.getStyleClass().add("arena");VBox.setVgrow(arena,Priority.ALWAYS);
        if(combate && s.inimigo().carregando()) {
            Label aviso=texto("GOLPE CARREGADO  ·  Defenda-se ou atordoe o inimigo", "boss-warning");StackPane.setAlignment(aviso,Pos.TOP_CENTER);StackPane.setMargin(aviso,new Insets(14));arena.getChildren().add(aviso);
        }
        v.getChildren().add(arena);
        if(combate) {
            HBox fichas=linha(fichaCombate(s.heroi()),espaco(),fichaCombate(s.inimigo()));v.getChildren().add(fichas);
            FlowPane acoes=new FlowPane(12,10,botao("1  Atacar","primary",()->agir(AcaoJogo.de(ATACAR))),botao("2  Habilidade","secondary",this::habilidades),botao("3  Defender","secondary",()->agir(AcaoJogo.de(DEFENDER))),
                    botao("Mochila","secondary",()->{pagina="Mochila";renderizar();}),botao("Analisar","ghost",()->mensagem(s.inimigo().nome(),s.inimigo().afinidades()+"\nAtaque "+s.inimigo().ataque()+" · Defesa "+s.inimigo().defesa()+" · Agilidade "+s.inimigo().agilidade())),
                    botao("Fugir","ghost",()->agir(AcaoJogo.de(FUGIR))));
            v.getChildren().add(acoes);
        } else if(s.fase()==EstadoJogo.Fase.PAUSA_TORRE) {
            v.getChildren().add(texto("Andar "+s.andarTorre()+" concluído. Prepare-se antes de "+(s.andarTorre()==2?"enfrentar o Observador.":"continuar subindo.")+" Descer reinicia a subida; o tempo não avança nesta pausa.","intro"));
            v.getChildren().add(new FlowPane(12,10,
                botao(s.andarTorre()==2?"ENFRENTAR O OBSERVADOR":"CONTINUAR SUBINDO","primary",()->agir(AcaoJogo.de(CONTINUAR))),
                botao("Preparar equipamentos e itens","secondary",()->{pagina="Mochila";renderizar();}),
                botao("Descer da torre","ghost",()->agir(AcaoJogo.de(DESCER_TORRE)))));
        } else if(s.fase()!=EstadoJogo.Fase.EXPLORACAO) {
            v.getChildren().add(texto(ultimoResultado,"intro"));
            v.getChildren().add(s.fase()==EstadoJogo.Fase.RECOMPENSA?botao("CONTINUAR   →","primary",()->agir(AcaoJogo.de(CONTINUAR))):botao("VOLTAR AO MENU","primary",()->{jogo=null;titulo();}));
        } else {
            String frase=s.localId().equals("VILA")?"Há luz nas janelas e rumores na taverna. Prepare-se antes de partir.":"O caminho guarda perigos. Observe seus recursos e escolha seu próximo passo.";
            v.getChildren().add(texto(frase,"muted"));
            HBox acoes=new HBox(12);
            if(s.localId().equals("VILA")) {
                acoes.getChildren().addAll(botao("EXPLORAR O MAPA   →","primary",()->{pagina="Mapa";renderizar();}),botao("Moradores e missões","secondary",()->{pagina="Missões";renderizar();}),botao("Descansar na taverna","secondary",()->agir(AcaoJogo.de(DESCANSAR))));
            } else {
                acoes.getChildren().add(botao(s.localId().equals("COVIL")?"ENFRENTAR IGNIS":s.localId().equals("TORRE")?"SUBIR A TORRE":"EXPLORAR OS ARREDORES","primary",()->agir(AcaoJogo.de(EXPLORAR))));
                if(!List.of("TORRE","COVIL").contains(s.localId())) acoes.getChildren().addAll(botao("Coletar recursos","secondary",()->agir(AcaoJogo.de(COLETAR))),botao("Acampar","secondary",()->agir(AcaoJogo.de(ACAMPAR))));
                acoes.getChildren().add(botao("Voltar à vila","ghost",()->agir(new AcaoJogo(VIAJAR,0))));
            }
            acoes.getChildren().forEach(n->HBox.setHgrow(n,Priority.ALWAYS));v.getChildren().add(acoes);
        }
        return v;
    }
    private VBox fichaCombate(EstadoJogo.Personagem p) {
        VBox v=coluna(texto(p.nome(),"card-title"),barra("VIDA",p.hp(),p.hpMax(),"hp"));v.setPrefWidth(310);
        if(!p.efeitos().isBlank())v.getChildren().add(texto(p.efeitos(),"tiny"));return v;
    }
    private Node mapa(EstadoJogo s) {
        VBox v=coluna(texto("Além dos portões", "heading"),texto("Escolha seu destino. Viajar avança o tempo e pode trazer encontros.","muted"));
        TilePane tiles=new TilePane(14,14);tiles.setPrefColumns(2);tiles.setPrefTileWidth(360);
        for(var l:s.locais()) {
            VBox card=coluna(texto(String.format("%02d  /  %s",l.id()+1,l.nome()),"card-title"),texto(l.descricao(),"muted"),botao(l.disponivel()?"Viajar   →":l.id()==Arrays.asList("VILA","FLORESTA","CAVERNA","RUINAS","MONTANHA","TORRE","COVIL").indexOf(s.localId())?"Você está aqui":"Caminho selado","secondary",()->agir(new AcaoJogo(VIAJAR,l.id()))));
            card.getStyleClass().add("panel");card.setPrefWidth(360);card.getChildren().getLast().setDisable(!l.disponivel());tiles.getChildren().add(card);
        }
        ScrollPane scroll=new ScrollPane(tiles);scroll.setFitToWidth(true);VBox.setVgrow(scroll,Priority.ALWAYS);v.getChildren().add(scroll);return v;
    }
    private Node inventario(EstadoJogo s) {
        VBox v=coluna(texto("Tudo o que você carrega", "heading"),texto(String.join("\n",s.equipamentos()),"muted"));
        v.getChildren().add(opcoes("Mochila", "Selecione um consumível para usar ou um equipamento para vestir.",s.inventario(),ITEM));
        VBox.setVgrow(v.getChildren().getLast(),Priority.ALWAYS);return v;
    }
    private Node moradoresEMissoes(EstadoJogo s) {
        List<EstadoJogo.Opcao> moradores=new ArrayList<>();
        for(int i=0;i<Moradores.TODOS.size();i++) {
            var m=Moradores.TODOS.get(i);
            moradores.add(new EstadoJogo.Opcao(i,m.nome(),"Conversar e consultar as missões de "+m.id()+".",s.localId().equals("VILA")));
        }
        TabPane abas=new TabPane(
            new Tab("Moradores",opcoes("As vozes de Aurora","Visite a vila para conversar com seus moradores.",moradores,CONVERSAR)),
            new Tab("Diário",opcoes("O diário da sua jornada","Aceite e entregue missões ao visitar a vila.",s.missoes(),MISSAO)));
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        abas.getSelectionModel().select(abaMissoes);
        abas.getSelectionModel().selectedIndexProperty().addListener((o,a,b)->abaMissoes=b.intValue());
        return abas;
    }
    private Node opcoes(String titulo,String subtitulo,List<EstadoJogo.Opcao> lista,AcaoJogo.Tipo tipo) {
        return opcoes(titulo,subtitulo,lista,tipo,true);
    }
    private Node opcoes(String titulo,String subtitulo,List<EstadoJogo.Opcao> lista,AcaoJogo.Tipo tipo,boolean rolagem) {
        VBox v=coluna(texto(titulo,"card-title"),texto(subtitulo,"muted"));
        VBox itens=new VBox(10);
        if(lista.isEmpty())itens.getChildren().add(texto("Nada por aqui ainda.","intro"));
        for(var o:lista) {
            VBox descricao=new VBox(5,texto(o.nome(),"card-title"),texto(o.descricao(),"muted"));HBox.setHgrow(descricao,Priority.ALWAYS);
            boolean eq=tipo==ITEM&&o.descricao().startsWith("Equipamento");
            String rotulo=switch(tipo) {
                case CONVERSAR -> "Conversar";
                case COMPRAR -> "Comprar"; case VENDER -> "Vender"; case MELHORAR -> "Melhorar";
                case CRIAR -> "Criar"; case CURAR -> "Receber cura"; case DOAR -> "Doar";
                case CONTRATAR -> "Contratar"; case DISPENSAR -> "Dispensar"; case DESCANSAR -> "Descansar";
                case MISSAO -> o.descricao().startsWith("Entregar")?"Entregar":"Aceitar";
                default -> eq?"Equipar":"Usar";
            };
            Button b=botao(rotulo,"secondary",()->{
                AcaoJogo acao=new AcaoJogo(eq?EQUIPAR:tipo,o.id());
                if(List.of(VENDER,MELHORAR,DOAR,CONTRATAR,DISPENSAR).contains(tipo)) confirmarServico(rotulo,o,acao);
                else agir(acao);
            });
            b.setDisable(!o.disponivel());b.setMinWidth(104);
            HBox card=linha(descricao,b);card.getStyleClass().add("item-card");itens.getChildren().add(card);
        }
        if(!rolagem){v.getChildren().add(itens);v.setMinHeight(Region.USE_PREF_SIZE);return v;}
        ScrollPane scroll=new ScrollPane(itens);scroll.setFitToWidth(true);VBox.setVgrow(scroll,Priority.ALWAYS);v.getChildren().add(scroll);return v;
    }
    private Node secoes(Node... secoes) {
        VBox conteudo=new VBox(22,secoes);conteudo.setPadding(new Insets(0,8,12,0));
        ScrollPane scroll=new ScrollPane(conteudo);scroll.setFitToWidth(true);return scroll;
    }
    private Node servicos(EstadoJogo s) {
        var vila=s.vila();
        TabPane abas=new TabPane();abas.setMinHeight(0);abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Node templo=secoes(opcoes("Bênção", "O templo acolhe a todos, a qualquer hora.",vila.cura(),CURAR,false),
                opcoes("Generosidade", "A doação não recebe desconto por honra.",vila.doacao(),DOAR,false));
        Node taverna=secoes(opcoes("Repouso", "Um teto para recuperar as forças.",vila.descanso(),DESCANSAR,false),
                opcoes("Companhia", "Aliado atual: "+vila.aliadoAtual(),vila.aliados(),CONTRATAR,false),
                opcoes("Seguir sozinho", "A dispensa não devolve o ouro da contratação.",vila.dispensa(),DISPENSAR,false));
        abas.getTabs().addAll(new Tab("Compra",opcoes("O estoque de Tobias",s.periodo().equals("Noite")||s.periodo().equals("Madrugada")?"O mercado está fechado. Descanse até a manhã.":"Suprimentos e equipamentos. Sua honra influencia os preços.",s.loja(),COMPRAR)),
                new Tab("Venda",opcoes("Negociar com Tobias","Apenas itens da mochila. Itens de missão não podem ser vendidos.",vila.vendas(),VENDER)),
                new Tab("Forja",opcoes("A forja de Borin","Melhore a arma ou armadura equipada até +5.",vila.forja(),MELHORAR)),
                new Tab("Alquimia",opcoes("O laboratório de Mara","Os ingredientes são consumidos somente quando a receita está completa.",vila.receitas(),CRIAR)),
                new Tab("Templo",templo),new Tab("Taverna",taverna),new Tab("Jogos e rumores",jogos(s)));
        abas.getSelectionModel().select(abaVila);
        abas.getSelectionModel().selectedIndexProperty().addListener((o,a,b)->abaVila=b.intValue());
        VBox v=coluna(texto("Os serviços de Aurora","heading"),abas);VBox.setVgrow(abas,Priority.ALWAYS);return v;
    }
    private Node jogos(EstadoJogo s) {
        Spinner<Integer> aposta=new Spinner<>(1,Math.max(1,Math.min(100,s.ouro())),Math.min(10,Math.max(1,s.ouro())));
        aposta.setAccessibleText("Valor da aposta em ouro");aposta.setMaxWidth(140);
        Button dados=botao("Jogar dados","primary",()->agir(new AcaoJogo(DADOS,aposta.getValue())));dados.setDisable(s.ouro()<1);
        Button charada=botao("Aceitar charada","secondary",()->agir(AcaoJogo.de(CHARADA)));charada.setDisable(s.charadaHoje());
        VBox dadosBox=coluna(texto("Dados com Gus","card-title"),texto("Dois dados para cada jogador. Maior soma ganha o valor apostado; empate não altera seu ouro. Limite: 100 ouro.","muted"),linha(aposta,dados));
        VBox charadaBox=coluna(texto("Desafio diário","card-title"),texto(s.charadaHoje()?"Você já tentou hoje. Volte no próximo dia do jogo.":"Uma tentativa por dia, consumida ao aceitar. Acerto: 30–69 ouro e 40 XP.","muted"),charada);
        VBox rumorBox=coluna(texto("Conversas ao pé do fogo","card-title"),texto("Gus tem notícias e conselhos sobre o reino. Ouvir é gratuito.","muted"),botao("Ouvir rumores","secondary",()->agir(AcaoJogo.de(RUMOR))));
        for(VBox box:List.of(dadosBox,charadaBox,rumorBox))box.getStyleClass().add("panel");
        return secoes(dadosBox,charadaBox,rumorBox);
    }
    private Node narrativa(EstadoJogo.Narrativa n) {
        VBox conteudo=coluna(texto("ENCONTROS E ESCOLHAS","eyebrow"),texto(n.titulo(),"heading"),texto(n.texto(),"intro"));
        conteudo.setPadding(new Insets(18));
        if(n.respostaLivre()) {
            TextField resposta=new TextField();resposta.setId("resposta-charada");resposta.setPromptText("Sua resposta");resposta.setAccessibleText("Resposta à charada");
            resposta.setTextFormatter(new TextFormatter<String>(c->c.getControlNewText().length()<=200?c:null));
            Button enviar=botao("Responder","primary",()->agir(new AcaoJogo(RESPONDER,0,resposta.getText())));
            enviar.disableProperty().bind(resposta.textProperty().isEmpty());
            resposta.setOnAction(e->{if(!enviar.isDisabled())enviar.fire();});
            conteudo.getChildren().addAll(resposta,enviar);
        }
        for(var escolha:n.escolhas()) {
            Button b=botao(escolha.nome(),"secondary",()->agir(new AcaoJogo(ESCOLHER_EVENTO,escolha.id())));b.setDisable(!escolha.disponivel());
            VBox card=coluna(texto(escolha.descricao(),"muted"),b);card.getStyleClass().add("panel");conteudo.getChildren().add(card);
        }
        return secoes(conteudo);
    }
    private void confirmarServico(String titulo,EstadoJogo.Opcao opcao,AcaoJogo acao) {
        Alert alerta=new Alert(Alert.AlertType.CONFIRMATION,opcao.descricao(),ButtonType.CANCEL,ButtonType.OK);
        alerta.initOwner(janela);alerta.setHeaderText(titulo+": "+opcao.nome());
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/tema.css").toExternalForm());
        if(alerta.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK)agir(acao);
    }
    private Node talentos(EstadoJogo s) {
        VBox v=coluna(texto("SEU PODER DESPERTA", "eyebrow"),texto("Uma nova força. Uma escolha.","heading"),texto("Nível "+s.heroi().nivel()+" · "+s.talentos()+" talento(s) para escolher","muted"));
        String[] nomes={"Vigor","Poder","Arcano","Reflexos"},desc={"+30 de vida máxima","+6 de ataque","+25 de mana máxima","+4 de agilidade e +2 de defesa"};
        for(int i=0;i<4;i++){int n=i+1;v.getChildren().add(botao(nomes[i]+"   ·   "+desc[i],"secondary",()->agir(new AcaoJogo(TALENTO,n))));}
        return v;
    }
    private void habilidades() {
        if(animando)return;
        Dialog<Void> d=dialogo("Escolha sua habilidade");VBox v=new VBox(10);
        for(var h:jogo.estado().habilidades()) {
            Button b=botao(h.nome()+"\n"+h.descricao(),"secondary",()->{d.close();agir(new AcaoJogo(HABILIDADE,h.id()));});b.setWrapText(true);b.setDisable(!h.disponivel());v.getChildren().add(b);
        }
        d.getDialogPane().setContent(v);d.showAndWait();
    }
    private Dialog<Void> dialogo(String titulo) {
        Dialog<Void> d=new Dialog<>();d.initOwner(janela);d.setTitle(titulo);d.setHeaderText(titulo);
        d.getDialogPane().getStylesheets().add(getClass().getResource("/tema.css").toExternalForm());d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);return d;
    }
    private void mensagem(String titulo,String mensagem) { Dialog<Void>d=dialogo(titulo);d.setContentText(mensagem);d.showAndWait(); }
    private void confirmarSaida(Runnable sair) {
        if(jogo!=null && jogo.encerramento().isPresent()) { sair.run();return; }
        Alert a=new Alert(Alert.AlertType.CONFIRMATION,"O progresso desde o último salvamento será perdido. Use Salvar jornada antes de sair se quiser continuar depois.",ButtonType.CANCEL,ButtonType.OK);
        a.initOwner(janela);a.setHeaderText("Encerrar esta jornada?");a.getDialogPane().getStylesheets().add(getClass().getResource("/tema.css").toExternalForm());
        if(a.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK)sair.run();
    }
    private void salvar() {
        try { salvamento.salvar(jogo);historico.addLast("Jornada salva com sucesso.");renderizar(); }
        catch(Exception e){mensagem("Não foi possível salvar",e.getMessage());}
    }
    private void carregar(boolean importar) {
        try {
            MotorJogo carregado;
            if(importar) {
                FileChooser chooser=new FileChooser();chooser.setTitle("Importar rpg_save.txt do terminal");
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save do terminal", "*.txt"));
                var arquivo=chooser.showOpenDialog(janela);if(arquivo==null)return;
                carregado=salvamento.importarV2(arquivo.toPath());
            } else carregado=salvamento.carregar();
            jogo=carregado;historico.clear();historico.add("Bem-vindo de volta, "+jogo.estado().heroi().nome()+".");pagina="Jornada";renderizar();
        }catch(Exception e){mensagem("Não foi possível carregar",e.getMessage());}
    }
    private void agir(AcaoJogo a) {
        if(animando)return;
        EstadoJogo antes=jogo.estado();
        List<EventoJogo> eventos=jogo.executar(a);
        for(var e:eventos) {
            if(!e.texto().isBlank()){historico.addLast(e.texto());while(historico.size()>160)historico.removeFirst();}
            if(e.tipo()==EventoJogo.Tipo.VITORIA || e.tipo()==EventoJogo.Tipo.DERROTA)ultimoResultado=e.texto();
        }
        if(a.tipo()==VIAJAR || jogo.estado().fase()!=EstadoJogo.Fase.EXPLORACAO)pagina="Jornada";
        List<EventoJogo> visuais=eventos.stream().filter(e->e.tipo()==EventoJogo.Tipo.DANO||e.tipo()==EventoJogo.Tipo.CURA
            ||e.tipo()==EventoJogo.Tipo.ESQUIVA||e.tipo()==EventoJogo.Tipo.BLOQUEIO).toList();
        if(rapido || antes.fase()!=EstadoJogo.Fase.COMBATE || arena==null || visuais.isEmpty()) {renderizar();return;}
        animando=true;raiz.setDisable(true);
        SequentialTransition fila=new SequentialTransition();
        for(var e:visuais) {
            Label dano=rotuloImpacto(e);
            boolean heroi=e.alvo().equals(antes.heroi().nome());dano.setTranslateX((heroi?-.23:.23)*arena.getWidth());dano.setTranslateY(20);dano.setOpacity(0);arena.getChildren().add(dano);
            FadeTransition aparecer=new FadeTransition(Duration.millis(50),dano);aparecer.setToValue(1);
            TranslateTransition subir=new TranslateTransition(Duration.millis(260),dano);subir.setByY(-50);
            FadeTransition sumir=new FadeTransition(Duration.millis(150),dano);sumir.setToValue(0);
            SequentialTransition numero=new SequentialTransition(aparecer,subir,sumir);
            if(e.tipo()!=EventoJogo.Tipo.CURA && arena.getChildren().getFirst() instanceof Cenario cena)
                fila.getChildren().add(new ParallelTransition(numero,cena.reagir(heroi,e.tipo()==EventoJogo.Tipo.ESQUIVA)));
            else fila.getChildren().add(numero);
        }
        fila.setOnFinished(e->{
            if(getParameters().getRaw().contains("--smoke") && arena.getChildren().getFirst() instanceof Cenario cena && !cena.posicaoNeutra()) {
                new IllegalStateException("Personagens não retornaram à posição inicial.").printStackTrace();System.exit(1);
            }
            animando=false;raiz.setDisable(false);renderizar();
        });fila.play();
    }

    private Label rotuloImpacto(EventoJogo e) {
        String rotulo=switch(e.tipo()) {
            case ESQUIVA -> "ESQUIVA";
            case BLOQUEIO -> "BLOQUEADO";
            case CURA -> "+"+e.valor();
            default -> "−"+e.valor();
        };
        String estilo=e.tipo()==EventoJogo.Tipo.CURA?"heal-number":
            e.tipo()==EventoJogo.Tipo.ESQUIVA||e.tipo()==EventoJogo.Tipo.BLOQUEIO?"defense-number":"damage-number";
        var impacto=e.impacto();
        if(impacto!=null) {
            if(e.tipo()==EventoJogo.Tipo.DANO)estilo="impact-number";
            List<String> detalhes=new ArrayList<>();
            if(impacto.critico()) { detalhes.add("CRÍTICO");estilo="critical-number"; }
            if(impacto.afinidade()>1)detalhes.add("FRAQUEZA · "+impacto.elemento());
            else if(impacto.afinidade()<1)detalhes.add("RESISTÊNCIA · "+impacto.elemento());
            if(impacto.guarda())detalhes.add("EM GUARDA");
            if(impacto.absorvido()>0)detalhes.add("ESCUDO · "+impacto.absorvido()+" absorvido");
            if(!detalhes.isEmpty())rotulo+="\n"+String.join("\n",detalhes);
        }
        Label label=texto(rotulo,estilo);label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);label.setMaxWidth(360);label.setMinHeight(Region.USE_PREF_SIZE);
        label.setMouseTransparent(true);return label;
    }

    /** Teste visual reproduzível sem alterar saves nem configurações do usuário. */
    private void smoke() {
        rapido=true;
        List<Runnable> passos=List.of(
            ()->{snapshot("01-titulo");clicar("NOVA JORNADA   →");},
            ()->{snapshot("02-criacao");((TextField)raiz.lookup(".text-field")).setText("Arthur");clicar("ENTRAR EM AURORA   →");},
            ()->{snapshot("25-prologo");clicar("PRÓXIMO   →");},
            ()->{clicar("PRÓXIMO   →");},
            ()->{snapshot("26-boas-vindas");clicar("COMEÇAR JORNADA   →");},
            ()->{snapshot("03-vila");clicar("Serviços");},
            ()->{snapshot("14-compra");((TabPane)raiz.lookup(".tab-pane")).getSelectionModel().select(1);},
            ()->{snapshot("08-venda");((TabPane)raiz.lookup(".tab-pane")).getSelectionModel().select(2);},
            ()->{snapshot("09-forja");((TabPane)raiz.lookup(".tab-pane")).getSelectionModel().select(3);},
            ()->{snapshot("10-alquimia");((TabPane)raiz.lookup(".tab-pane")).getSelectionModel().select(4);},
            ()->{snapshot("11-templo");clicar("Receber cura");
                if(jogo.estado().ouro()!=50)throw new IllegalStateException("Cura não foi debitada corretamente.");
                ((TabPane)raiz.lookup(".tab-pane")).getSelectionModel().select(5);},
            ()->{snapshot("12-taverna");janela.setWidth(1024);janela.setHeight(720);},
            ()->{snapshot("13-taverna-compacta");((TabPane)raiz.lookup(".tab-pane")).getSelectionModel().select(6);},
            ()->{snapshot("15-jogos");clicar("Aceitar charada");},
            ()->{snapshot("16-charada");((TextField)raiz.lookup("#resposta-charada")).setText("teste");clicar("Responder");},
            ()->{snapshot("17-resposta");clicar("Continuar jornada");clicar("Serviços");clicar("Jogar dados");},
            ()->{snapshot("18-dados");clicar("Continuar jornada");clicar("Serviços");clicar("Ouvir rumores");},
            ()->{snapshot("19-rumor");clicar("Continuar jornada");clicar("EXPLORAR O MAPA   →");},
            ()->{snapshot("04-mapa");agir(new AcaoJogo(VIAJAR,1));clicar("EXPLORAR OS ARREDORES");},
            ()->{snapshot("20-viajante");clicar("Seguir caminho");},
            ()->{snapshot("21-escolha");clicar("Continuar jornada");clicar("EXPLORAR OS ARREDORES");},
            ()->{snapshot("05-combate");rapido=false;clicar("1  Atacar");
                if(!animando||!raiz.isDisabled())throw new IllegalStateException("Fila visual não bloqueou a entrada.");
                var antes=jogo.estado();agir(AcaoJogo.de(ATACAR));
                if(!antes.equals(jogo.estado()))throw new IllegalStateException("Comando extra durante animação.");},
            ()->{if(raiz.isDisabled())throw new IllegalStateException("Entrada não foi liberada.");rapido=true;snapshot("06-apos-ataque");janela.setWidth(1024);janela.setHeight(720);},
            ()->{snapshot("07-compacto");jogo=new MotorJogo("Arthur","Paladino",false,41L);historico.clear();pagina="Missões";renderizar();},
            ()->{snapshot("22-moradores");agir(new AcaoJogo(CONVERSAR,0));},
            ()->{snapshot("23-dialogo");clicar("Aceitar: Praga na Floresta");},
            ()->{if(!jogo.estado().narrativa().texto().contains("Conto com você"))throw new IllegalStateException("Missão não foi aceita na conversa.");clicar("Encerrar conversa");snapshot("24-conversa-concluida");
                // Dados de apresentação: regras e transição real de vitória são testadas em JUnit.
                encerramento(new HistoriaCampanha.Encerramento(true,"Aurora verá outro amanhecer",
                    "O povo canta seu nome: Arthur o Lendário Justo, salvador do reino!","Arthur","Paladino",10,30,80,10,10,4,false,6200));},
            ()->{snapshot("27-epilogo-compacto");clicar("VOLTAR AO MENU");clicar("NOVA JORNADA   →");clicar("ENTRAR EM AURORA   →");},
            ()->{snapshot("28-prologo-compacto");clicar("Pular introdução");
                if(raiz.lookupAll(".button").stream().noneMatch(n->n instanceof Button b&&b.getText().equals("EXPLORAR O MAPA   →")))throw new IllegalStateException("Pular prólogo não abriu a vila.");
                encerramento(new HistoriaCampanha.Encerramento(false,"Uma lenda chega ao fim",
                    "Sua lenda termina aqui. Aurora guardará a memória de sua jornada.","Arthur","Paladino",3,8,12,1,10,0,true,650));},
            ()->{snapshot("29-derrota-compacta");clicar("VOLTAR AO MENU");snapshot("30-retorno-menu");clicar("Salão dos heróis");},
            ()->{snapshot("31-ranking-vazio");
                List<Ranking.Entrada> previa=new ArrayList<>();
                for(int i=0;i<5;i++)previa.add(new Ranking.Entrada(UUID.randomUUID().toString(),
                    new HistoriaCampanha.Encerramento(true,"Vitória","Prévia","Alexandre das Terras de Aurora","Necromante",15,100,1000,10,10,5,false,10000-i*100)));
                ranking(false,previa);},
            ()->{snapshot("34-ranking-preenchido");clicar("Hardcore");},
            ()->{snapshot("32-ranking-hardcore");
                TilePane exemplos=new TilePane(20,30);exemplos.setPrefColumns(2);exemplos.setPrefTileWidth(430);
                exemplos.getChildren().addAll(
                    rotuloImpacto(new EventoJogo(EventoJogo.Tipo.DANO,"Alvo",42,"",new EventoJogo.Impacto(true,"Fogo",1.5,false,0))),
                    rotuloImpacto(new EventoJogo(EventoJogo.Tipo.DANO,"Alvo",8,"",new EventoJogo.Impacto(false,"Sombra",0.5,true,0))),
                    rotuloImpacto(new EventoJogo(EventoJogo.Tipo.BLOQUEIO,"Alvo",0,"",new EventoJogo.Impacto(false,"Físico",1,false,20))),
                    rotuloImpacto(new EventoJogo(EventoJogo.Tipo.ESQUIVA,"Alvo",0,"")));
                VBox previa=coluna(texto("IMPACTOS · PRÉVIA VISUAL","heading"),exemplos);previa.setPadding(new Insets(36));trocar(previa);},
            ()->{snapshot("35-impactos");
                Cenario cena=new Cenario("FLORESTA","Paladino","Slime",false);trocar(cena);raiz.applyCss();raiz.layout();
                for(boolean heroi:new boolean[]{true,false})for(boolean esquiva:new boolean[]{false,true}) {
                    Animation movimento=cena.reagir(heroi,esquiva);movimento.play();movimento.pause();movimento.jumpTo(Duration.millis(90));
                    if(cena.posicaoNeutra())throw new IllegalStateException("Reação não deslocou o personagem.");
                    snapshot("36-reacao-"+(heroi?"heroi":"inimigo")+(esquiva?"-esquiva":"-dano"));
                    movimento.jumpTo(movimento.getTotalDuration());
                    if(!cena.posicaoNeutra())throw new IllegalStateException("Reação não retornou à posição inicial.");
                    movimento.stop();
                }
                titulo();snapshot("33-ranking-concluido");janela.close();Platform.exit();}
        );
        passoSmoke(passos,0);
    }
    private void passoSmoke(List<Runnable> passos,int i) {
        PauseTransition p=new PauseTransition(Duration.millis(350));p.setOnFinished(e->{
            if(animando) { passoSmoke(passos,i);return; }
            try { passos.get(i).run();if(i+1<passos.size())passoSmoke(passos,i+1); }
            catch(Throwable falha){falha.printStackTrace();System.exit(1);}
        });p.play();
    }
    private void clicar(String rotulo) {
        raiz.applyCss();raiz.layout();
        Button b=raiz.lookupAll(".button").stream().filter(n->n instanceof Button botao&&botao.getText().equals(rotulo)&&!botao.isDisabled()).map(n->(Button)n).findFirst().orElseThrow(()->new IllegalStateException("Botão indisponível: "+rotulo));b.fire();
    }
    private void snapshot(String nome) {
        try {
            raiz.applyCss();raiz.layout();WritableImage image=raiz.snapshot(null,null);
            BufferedImage png=new BufferedImage((int)image.getWidth(),(int)image.getHeight(),BufferedImage.TYPE_INT_ARGB);
            for(int y=0;y<png.getHeight();y++)for(int x=0;x<png.getWidth();x++)png.setRGB(x,y,image.getPixelReader().getArgb(x,y));
            Path dir=Path.of("build","screenshots");Files.createDirectories(dir);ImageIO.write(png,"png",dir.resolve(nome+".png").toFile());
        }catch(Exception e){throw new IllegalStateException("Falha na captura visual",e);}
    }
}
