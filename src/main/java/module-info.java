module br.com.superrpg {
    requires javafx.controls;
    requires com.google.gson;
    requires java.desktop;
    exports br.com.superrpg.ui;
    opens br.com.superrpg.jogo to com.google.gson;
}
