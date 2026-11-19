package br.com.superrpg.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.*;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;
import java.util.Random;

/** Arte original desenhada em código, sem dependência de imagens externas. */
final class Cenario extends Region {
    private final Canvas canvas = new Canvas();
    private final String local, classe, monstro;
    private final boolean noite;
    private final DoubleProperty deslocamentoHeroi=new SimpleDoubleProperty();
    private final DoubleProperty deslocamentoInimigo=new SimpleDoubleProperty();
    Cenario(String local, String classe, String monstro, boolean noite) {
        this.local = local; this.classe = classe; this.monstro = monstro; this.noite = noite;
        getChildren().add(canvas); setMinHeight(180); setPrefHeight(370);
        deslocamentoHeroi.addListener((o,a,b)->desenhar());
        deslocamentoInimigo.addListener((o,a,b)->desenhar());
    }

    /** Reação apenas visual: o cenário fica imóvel e nenhuma regra é recalculada. */
    Animation reagir(boolean alvoHeroi, boolean esquiva) {
        DoubleProperty deslocamento=alvoHeroi?deslocamentoHeroi:deslocamentoInimigo;
        double distancia=(alvoHeroi?-1:1)*(esquiva?32:12);
        Timeline movimento=new Timeline(
            new KeyFrame(Duration.ZERO,new KeyValue(deslocamento,0)),
            new KeyFrame(Duration.millis(90),new KeyValue(deslocamento,distancia,Interpolator.EASE_OUT)),
            new KeyFrame(Duration.millis(esquiva?300:220),new KeyValue(deslocamento,0,Interpolator.EASE_BOTH)));
        return movimento;
    }

    boolean posicaoNeutra() { return deslocamentoHeroi.get()==0 && deslocamentoInimigo.get()==0; }
    @Override protected void layoutChildren() {
        canvas.setWidth(getWidth()); canvas.setHeight(getHeight()); desenhar();
    }
    private void desenhar() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double w = getWidth(), h = getHeight(); if (w <= 0 || h <= 0) return;
        g.save(); g.scale(w / 960, h / 420);
        boolean fogo = local.equals("COVIL"), caverna = local.equals("CAVERNA");
        g.setFill(new LinearGradient(0,0,0,420,false,CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(fogo ? "#27151d" : noite ? "#0d1d31" : "#344e61")), new Stop(1,Color.web(noite ? "#1c3740" : "#91a393"))));
        g.fillRect(0,0,960,420);
        Random r = new Random(417);
        for (int i=0;i<(noite?85:0);i++) {
            g.setFill(Color.web("#bdd6cf", .18 + r.nextDouble()*.5));
            g.fillRect(r.nextInt(960), r.nextInt(240),2,2);
        }
        g.setFill(Color.web("#d5d6ad", .035)); g.fillOval(664,18,180,180);
        g.setFill(Color.web("#d5d6ad", .07)); g.fillOval(694,48,120,120);
        g.setFill(Color.web("#cbd5b0")); g.fillOval(724,77,62,62);
        if(noite){g.setFill(Color.web("#182e40")); g.fillOval(739,68,61,61);}
        mountain(g, Color.web("#263f4c"), 245, 0);
        mountain(g, Color.web("#1c333e"), 291, 49);
        mountain(g, Color.web("#132a30"), 326, 137);
        if (local.equals("VILA") || local.equals("TITULO")) {
            for(int i=0;i<6;i++) casa(g, 405+i*76, 285+(i%2)*19, .6+(i%3)*.13);
            g.setFill(Color.web("#102128")); g.fillRect(580,174,38,135); g.fillRect(574,168,50,13);
            for(int i=0;i<4;i++) g.fillRect(575+i*13,157,7,15);
            g.setFill(Color.web("#d8a65e")); g.fillRect(594,210,7,13);
        } else if (local.equals("RUINAS") || local.equals("TORRE")) {
            for(int i=0;i<5;i++) {
                int x=80+i*170, y=205+(i%3)*22;
                g.setFill(Color.web("#344447")); g.fillRect(x,y,30,132); g.fillRect(x-5,y-8,40,12);
                g.setFill(Color.web("#172c32")); g.fillRect(x+9,y+6,7,120);
            }
        }
        if (caverna || fogo) {
            g.setFill(Color.web(fogo ? "#261c26" : "#162730"));
            g.fillPolygon(new double[]{0,0,960,960,880,800,710,610,520,410,280,175,80},new double[]{240,0,0,240,80,140,60,135,45,110,70,160,100},13);
        }
        if (!caverna && !fogo) {
            for(int i=0;i<13;i++) {
                if((local.equals("VILA") || local.equals("TITULO"))&&i>=5&&i<=10)continue;
                arvore(g, i*87-30, 330+r.nextInt(24), .65+r.nextDouble()*.8, Color.web("#0e2529"));
            }
        }
        g.setFill(Color.web(fogo ? "#382329" : "#182e2b")); g.fillRect(0,338,960,82);
        g.setFill(Color.web(fogo ? "#c47645" : "#5e6c52", .4)); g.fillRect(0,337,960,3);
        g.setFill(Color.web(fogo ? "#8e4b3b" : "#63715a", .22));
        g.fillPolygon(new double[]{410,510,770,185},new double[]{339,339,420,420},4);
        for(int i=0;i<80;i++) {
            g.setFill(Color.web(fogo ? "#e59251" : "#849777", .12+r.nextDouble()*.12));
            g.fillRect(r.nextInt(960),345+r.nextInt(75),r.nextInt(12)+2,2);
        }
        if (classe != null) heroi(g, (monstro == null ? 470 : 250)+deslocamentoHeroi.get(), 339, classe);
        if (monstro != null) inimigo(g, 700+deslocamentoInimigo.get(),339,monstro);
        for(int i=0;i<17;i++) {
            int x=r.nextInt(960),y=210+r.nextInt(170);
            g.setFill(Color.web(fogo ? "#efa45c" : "#c8c27e", .08)); g.fillOval(x-5,y-5,14,14);
            g.setFill(Color.web(fogo ? "#efa45c" : "#c8c27e", .65)); g.fillRect(x,y,2,2);
        }
        g.setFill(new LinearGradient(0,0,0,420,false,CycleMethod.NO_CYCLE,new Stop(0,Color.TRANSPARENT),new Stop(.75,Color.TRANSPARENT),new Stop(1,Color.web("#0b141a",.65))));
        g.fillRect(0,0,960,420); g.restore();
    }
    private void mountain(GraphicsContext g, Color c, int base, int offset) {
        g.setFill(c); double[] xs=new double[14], ys=new double[14]; xs[0]=0;ys[0]=420;
        for(int i=1;i<13;i++) {xs[i]=(i-1)*90;ys[i]=base-((i*73+offset)%101);}
        xs[13]=960;ys[13]=420; g.fillPolygon(xs,ys,14);
    }
    private void arvore(GraphicsContext g, double x, double y,double s,Color c) {
        g.setFill(c); g.fillRect(x-4*s,y-125*s,8*s,125*s);
        for(int j=0;j<4;j++) {double yy=y-150*s+j*26*s, half=(20+j*9)*s;
            g.fillPolygon(new double[]{x,x-half,x+half},new double[]{yy-32*s,yy+27*s,yy+27*s},3);}
    }
    private void casa(GraphicsContext g,double x,double y,double s) {
        g.save();g.translate(x,y);g.scale(s,s);
        g.setFill(Color.web("#2a3938"));g.fillRect(0,-70,70,70);
        g.setFill(Color.web("#15272d"));g.fillPolygon(new double[]{-12,35,82},new double[]{-67,-112,-67},3);
        g.setFill(Color.web("#947852"));g.fillRect(29,-32,15,32);
        g.setFill(Color.web("#e9bb6a"));g.fillRect(10,-51,12,15);g.fillRect(48,-51,12,15);
        g.setFill(Color.web("#e9bb6a",.06));g.fillOval(-13,-72,60,60);g.restore();
    }
    private void heroi(GraphicsContext g,double x,double y,String classe) {
        g.save();g.translate(x,y);g.scale(3,3);
        g.setFill(Color.web("#050d11",.6));g.fillOval(-18,-3,38,7);
        String cape=switch(classe){case "Paladino"->"#386d83";case "Mago"->"#5263a1";case "Necromante"->"#705580";case "Arqueiro"->"#5b7957";default->"#a18a51";};
        g.setFill(Color.web(cape));g.fillPolygon(new double[]{-7,7,14,-15},new double[]{-29,-29,-3,-3},4);
        g.setFill(Color.web("#bac6be"));g.fillRect(-7,-30,14,15);
        g.setFill(Color.web("#647f85"));g.fillRect(-7,-27,4,15);g.fillRect(3,-27,4,15);
        g.setFill(Color.web("#263941"));g.fillRect(-6,-13,5,11);g.fillRect(3,-13,5,11);
        g.setFill(Color.web("#17282e"));g.fillRect(-8,-3,8,3);g.fillRect(3,-3,9,3);
        g.setFill(Color.web("#c6b08b"));g.fillRect(-5,-40,11,10);
        g.setFill(Color.web("#91a9ab"));g.fillRect(-7,-44,14,6);g.fillRect(-8,-40,4,8);
        g.setFill(Color.web("#14252c"));g.fillRect(-1,-37,7,2);
        if (classe.equals("Mago") || classe.equals("Necromante")) {
            g.setFill(Color.web(cape));g.fillPolygon(new double[]{-10,1,11},new double[]{-41,-58,-41},3);
            g.setFill(Color.web("#a58c66"));g.fillRect(15,-42,2,40);
            g.setFill(Color.web("#9ad8d0"));g.fillRect(12,-48,8,8);
        } else if (classe.equals("Arqueiro")) {
            g.setStroke(Color.web("#bd9c68"));g.setLineWidth(2);g.strokeArc(7,-36,18,30,-85,170,javafx.scene.shape.ArcType.OPEN);
            g.setLineWidth(.5);g.strokeLine(17,-35,17,-6);
        } else {
            g.setFill(Color.web("#c5d5d1"));g.fillRect(16,-43,3,24);g.fillRect(17,-46,1,3);
            g.setFill(Color.web("#cfb16d"));g.fillRect(12,-20,11,3);g.fillRect(16,-17,3,8);
            if(classe.equals("Paladino")){g.setFill(Color.web("#537988"));g.fillRect(-16,-27,10,16);g.setFill(Color.web("#ceb679"));g.fillRect(-12,-25,2,12);}
        }
        g.restore();
    }
    private void inimigo(GraphicsContext g,double x,double y,String tipo) {
        g.save();g.translate(x,y);g.scale(3,3);
        g.setFill(Color.web("#050d11",.6));g.fillOval(-25,-3,50,7);
        if(tipo.equals("Slime")) {
            g.setFill(Color.web("#386f66"));g.fillRoundRect(-22,-22,44,23,19,18);
            g.setFill(Color.web("#78ac81"));g.fillRoundRect(-18,-27,35,24,17,16);
            g.setFill(Color.web("#acd3a2"));g.fillRect(-12,-22,7,3);
            g.setFill(Color.web("#152e34"));g.fillRect(-9,-14,4,4);g.fillRect(5,-14,4,4);g.fillRect(-3,-5,6,2);
        } else if(tipo.equals("Lobo")) {
            g.setFill(Color.web("#84959b"));g.fillRect(-14,-24,34,17);g.fillRect(-22,-29,16,17);
            g.fillPolygon(new double[]{-23,-18,-15},new double[]{-27,-39,-27},3);g.fillRect(19,-20,12,5);
            g.fillRect(-13,-8,5,9);g.fillRect(12,-8,5,9);g.setFill(Color.web("#ebc17b"));g.fillRect(-19,-23,3,2);
        } else if(tipo.equals("Aranha")) {
            g.setStroke(Color.web("#907398"));g.setLineWidth(3);
            for(int i=0;i<4;i++){g.strokeLine(-8,-15,-31+i*3,-24+i*6);g.strokeLine(8,-15,31-i*3,-24+i*6);}
            g.setFill(Color.web("#67506e"));g.fillOval(-15,-25,30,23);g.setFill(Color.web("#ecb385"));g.fillRect(-8,-17,4,4);g.fillRect(4,-17,4,4);
        } else if(tipo.equals("Beholder")) {
            g.setFill(Color.web("#865c83"));g.fillOval(-27,-62,54,49);g.setFill(Color.web("#d0c5af"));g.fillOval(-17,-52,34,24);
            g.setFill(Color.web("#ab545c"));g.fillOval(-7,-50,14,20);g.setFill(Color.web("#111d29"));g.fillRect(-2,-48,4,16);
        } else if(tipo.equals("Dragao")) {
            g.setFill(Color.web("#8f4d48"));g.fillPolygon(new double[]{-12,-65,-42,-15,0,24,55,65,20},new double[]{-30,-85,-26,-15,-4,-18,-30,-83,-40},9);
            g.setFill(Color.web("#b46e51"));g.fillRect(-15,-52,32,46);g.fillRect(-29,-65,35,22);
            g.setFill(Color.web("#efcc7f"));g.fillRect(-23,-58,5,3);
        } else {
            String c=tipo.equals("Golem")?"#8d9290":tipo.equals("Esqueleto")?"#bbbea5":tipo.equals("Cultista")?"#78587d":"#8b7157";
            g.setFill(Color.web(c));g.fillRect(-14,-38,28,25);g.fillRect(-9,-53,19,17);g.fillRect(-12,-12,8,13);g.fillRect(4,-12,8,13);
            g.fillRect(-23,-35,8,22);g.fillRect(15,-35,8,22);
            g.setFill(Color.web("#182931"));g.fillRect(-5,-47,4,4);g.fillRect(4,-47,4,4);
            g.setFill(Color.web("#bda777"));g.fillRect(-25,-43,3,35);
        }
        g.restore();
    }
}
