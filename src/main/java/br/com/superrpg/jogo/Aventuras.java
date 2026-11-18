package br.com.superrpg.jogo;

import java.util.*;
import static br.com.superrpg.jogo.EstadoJogo.*;
import static br.com.superrpg.jogo.AcaoJogo.Tipo.*;

/** Eventos com escolhas são estados explícitos; não há leitura de teclado nem espera. */
final class Aventuras {
    private enum Tipo { RESULTADO, VIAJANTE, SANTUARIO, COFRE, MERCADOR, CHARADA }
    private static final String[] CONSUMIVEIS = {"Poção de Cura", "Elixir de Mana", "Antídoto", "Bomba de Fogo", "Pergaminho do Trovão"};
    private static final String[][] CHARADAS = {
        {"O que é, o que é: cai em pé e corre deitada?", "chuva"},
        {"O que tem coroa mas não é rei?", "abacaxi"},
        {"O que quanto mais se tira, maior fica?", "buraco"},
        {"Tenho cidades sem casas e rios sem água. O que sou?", "mapa"},
        {"Tem dentes mas não morde. O que é?", "pente"}
    };
    private final MotorJogo jogo;
    private final Heroi h;
    private final Random sorte;
    private Narrativa narrativa;
    private Tipo tipo;
    private Item oferta;
    private int precoOferta, charada;

    Aventuras(MotorJogo jogo, Random sorte) { this.jogo=jogo;h=jogo.heroi;this.sorte=sorte; }
    Narrativa estado() { return narrativa; }
    private Opcao escolha(int id,String nome,String desc,boolean pode) { return new Opcao(id,nome,desc,pode); }
    private void abrir(Tipo tipo,String titulo,String texto,Opcao... escolhas) {
        this.tipo=tipo;jogo.fase=Fase.EVENTO;
        narrativa=new Narrativa(titulo,texto,List.of(escolhas),tipo==Tipo.CHARADA);
    }
    private void resultado(String titulo,String texto) {
        Eventos.texto(texto);
        abrir(Tipo.RESULTADO,titulo,texto,escolha(0,"Continuar jornada","",true));
    }
    private int danoSeguro(int valor) {
        int dano=Math.min(valor,Math.max(0,h.hp-1));h.perderHp(dano);return dano;
    }

    void explorar(int r) {
        switch(r) {
            case 4 -> bau();
            case 5 -> {
                if(sorte.nextInt(100)<Math.min(60,h.getAgilidade()*2)) {
                    h.ganharXp(15);resultado("Armadilha desarmada","Sua agilidade revelou a armadilha. +15 XP.");
                } else resultado("Armadilha!","Você perdeu "+danoSeguro(8+jogo.local.nivelRec*2)+" de vida.");
            }
            case 6 -> { h.curar(35);resultado("Fogueira abandonada","As brasas ainda aquecem o caminho. Você recupera até 35 de vida."); }
            default -> evento(sorte.nextInt(5));
        }
    }
    private void bau() {
        int r=sorte.nextInt(100);
        if(r<50) {
            int ouro=15+sorte.nextInt(20)*Math.max(1,jogo.local.nivelRec/2+1);h.ganharOuro(ouro);
            resultado("Baú esquecido","Dentro havia "+ouro+" de ouro!");
        } else {
            String[] nomes=r<85?CONSUMIVEIS:new String[]{"Gibão de Couro","Cota de Malha","Lâmina de Aço","Anel Veloz","Amuleto Vital","Adaga Sombria"};
            Item item=Fabrica.criar(nomes[sorte.nextInt(nomes.length)]);h.inv.add(item);
            resultado("Baú esquecido","Você encontrou "+item.getNome()+". "+item.getDescricao());
        }
    }
    private void evento(int id) {
        switch(id) {
            case 0 -> abrir(Tipo.VIAJANTE,"Viajante ferido","Por favor... estou sangrando. Você tem algo para me ajudar?",
                    escolha(1,"Oferecer Poção de Cura","Consome 1 poção · +1 honra, 30 XP e 30–59 ouro. Você tem "+h.contar("Poção de Cura")+".",h.contar("Poção de Cura")>0),
                    escolha(2,"Seguir caminho","Deixe o viajante e siga adiante.",true),
                    escolha(3,"Roubar o viajante","−1 honra · recebe 40–79 ouro.",true));
            case 1 -> abrir(Tipo.SANTUARIO,"Santuário antigo","Uma aura misteriosa envolve o altar.",
                    escolha(1,"Rezar em silêncio","60% de chance de recuperar metade da vida e mana; senão, perde até 15 de vida.",true),
                    escolha(2,"Oferecer 20 ouro","Restaura vida e mana, remove males e concede +1 honra."+(h.ouro<20?" Ouro insuficiente.":""),h.ouro>=20),
                    escolha(3,"Ignorar o santuário","Siga adiante.",true));
            case 2 -> abrir(Tipo.COFRE,"Cofre arcano","Um cofre de ferro trancado exibe símbolos arcanos.",
                    escolha(1,"Forçar a tranca","50% de sucesso; falha causa até 20 de dano.",true),
                    escolha(2,"Abrir com delicadeza",Math.min(85,30+h.getAgilidade()*2)+"% de sucesso; falha causa até 10 de dano.",true),
                    escolha(3,"Deixar o cofre","Siga adiante.",true));
            case 3 -> {
                oferta=Fabrica.criar(CONSUMIVEIS[sorte.nextInt(CONSUMIVEIS.length)]);
                precoOferta=Math.max(1,(int)(oferta.getPreco()*.7));
                abrir(Tipo.MERCADOR,"Mercador errante","Tenho "+oferta.getNome()+" em promoção por "+precoOferta+" ouro!",
                        escolha(1,"Comprar oferta",oferta.getDescricao()+(h.ouro<precoOferta?" · Ouro insuficiente.":""),h.ouro>=precoOferta),
                        escolha(0,"Recusar oferta","Siga adiante.",true));
            }
            default -> { h.modificarMana(h.manaMax);resultado("Fonte mística","A energia da fonte restaurou toda a sua mana."); }
        }
    }

    void responder(AcaoJogo acao) {
        if(tipo==Tipo.CHARADA && acao.tipo()==RESPONDER) {
            String resposta=acao.texto()==null?"":acao.texto().strip().toLowerCase(Locale.ROOT);
            if(resposta.isBlank()||resposta.length()>200){Eventos.texto("Digite uma resposta entre 1 e 200 caracteres.");return;}
            if(resposta.contains(CHARADAS[charada][1])) {
                int ouro=30+sorte.nextInt(40);h.ganharOuro(ouro);h.ganharXp(40);
                resultado("Resposta correta!","Gus entrega "+ouro+" de ouro. +40 XP.");
            } else resultado("Não foi desta vez","A resposta era: "+CHARADAS[charada][1]+". Volte amanhã para tentar novamente.");
            return;
        }
        if(acao.tipo()!=ESCOLHER_EVENTO)return;
        boolean valido=narrativa.escolhas().stream().anyMatch(o->o.id()==acao.indice()&&o.disponivel());
        if(!valido){Eventos.texto("Escolha indisponível.");return;}
        int op=acao.indice();
        switch(tipo) {
            case RESULTADO -> { narrativa=null;tipo=null;oferta=null;jogo.fase=Fase.EXPLORACAO; }
            case VIAJANTE -> {
                if(op==1) {
                    if(!h.consumir("Poção de Cura",1))return;
                    int ouro=30+sorte.nextInt(30);h.ganharOuro(ouro);h.mudarHonra(1);h.ganharXp(30);
                    resultado("Um gesto de bondade","O viajante agradece. +"+ouro+" ouro, +1 honra e +30 XP.");
                } else if(op==3) {
                    int ouro=40+sorte.nextInt(40);h.ganharOuro(ouro);h.mudarHonra(-1);
                    resultado("O preço da escolha","Você roubou "+ouro+" de ouro. Honra −1.");
                } else resultado("Seguindo adiante","Você deixa o viajante para trás.");
            }
            case SANTUARIO -> {
                if(op==2) {
                    if(!h.gastarOuro(20))return;
                    h.restaurarTotal();h.mudarHonra(1);resultado("Oferenda aceita","Vida e mana restauradas, males removidos. Honra +1.");
                } else if(op==1) {
                    if(sorte.nextInt(100)<60) {
                        h.curar(h.hpMax/2);h.modificarMana(h.manaMax/2);
                        resultado("Luz do santuário","Você recuperou até metade da vida e da mana máximas.");
                    } else resultado("Presença sombria","O altar drenou "+danoSeguro(15)+" de vida.");
                } else resultado("Seguindo adiante","Você deixa o santuário em silêncio.");
            }
            case COFRE -> {
                if(op==3){resultado("Seguindo adiante","Você deixa o cofre fechado.");return;}
                if(sorte.nextInt(100)<(op==1?50:Math.min(85,30+h.getAgilidade()*2))) {
                    int ouro=40+sorte.nextInt(60);h.ganharOuro(ouro);
                    Item item=Fabrica.criar(sorte.nextBoolean()?"Elixir de Mana":"Bomba de Fogo");h.inv.add(item);
                    resultado("O cofre se abre","Você recebeu "+ouro+" ouro e "+item.getNome()+".");
                } else resultado("Armadilha arcana","O cofre permanece fechado. Você perdeu "+danoSeguro(op==1?20:10)+" de vida.");
            }
            case MERCADOR -> {
                if(op==1) {
                    if(!h.gastarOuro(precoOferta))return;
                    h.inv.add(oferta);resultado("Negócio fechado","Comprado: "+oferta.getNome()+" por "+precoOferta+" ouro.");
                } else resultado("Seguindo adiante","Você recusou a oferta do mercador.");
            }
            case CHARADA -> resultado("Até amanhã","Você desistiu. A resposta era: "+CHARADAS[charada][1]+". A tentativa de hoje foi consumida.");
        }
    }

    void taverna(AcaoJogo acao) {
        switch(acao.tipo()) {
            case DADOS -> {
                int aposta=acao.indice();
                if(aposta<1||aposta>100||aposta>h.ouro){Eventos.texto("Aposte entre 1 e 100 ouro, sem ultrapassar seu saldo.");return;}
                int jogador=sorte.nextInt(6)+sorte.nextInt(6)+2,gus=sorte.nextInt(6)+sorte.nextInt(6)+2;
                if(jogador>gus)h.ganharOuro(aposta);else if(jogador<gus)h.gastarOuro(aposta);
                resultado("Dados com Gus","Você: "+jogador+" · Gus: "+gus+". "+(jogador>gus?"Ganhou "+aposta+" ouro!":jogador<gus?"Perdeu "+aposta+" ouro.":"Empate: ninguém perde ouro."));
            }
            case CHARADA -> {
                if(jogo.charadaHoje){Eventos.texto("Você já tentou hoje. Volte amanhã.");return;}
                charada=sorte.nextInt(CHARADAS.length);jogo.charadaHoje=true;
                abrir(Tipo.CHARADA,"A charada de Gus",CHARADAS[charada][0]+"\nUma tentativa por dia. Acerto: 30–69 ouro e 40 XP.",
                        escolha(0,"Desistir da charada","A tentativa de hoje já foi consumida.",true));
            }
            case RUMOR -> {
                List<String> rumores=List.of("Slimes e lobos são vulneráveis ao fogo.","Bandidos roubam ouro, mas quem os derrota recupera tudo e mais um pouco.",
                        "Chefes avisam antes de um golpe devastador. Defender ou atordoar salva vidas!", "Honra alta dá desconto nas lojas. Roubar viajantes... nem tanto.",
                        !jogo.diario.concluida("seita")?"Cultistas vagam pelas Ruínas. Aldric quer saber o que eles adoram.":!jogo.diario.concluida("torre")?"A Torre do Observador está aberta. Leve luz e uma poção extra.":"A Chave do Dragão abre o Covil de Ignis. Ele resiste a fogo, mas teme raios!");
                resultado("Rumores da taverna",rumores.get(sorte.nextInt(rumores.size())));
            }
            default -> { }
        }
    }
}
