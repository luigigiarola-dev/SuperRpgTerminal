package br.com.superrpg.ui;

/** Bootstrap separado evita a detecção automática de módulos JavaFX pelo launcher do Java. */
public final class Iniciar {
    public static void main(String[] args) { javafx.application.Application.launch(Aplicativo.class, args); }
}
