package br.com.superrpg.jogo;

import java.util.function.Consumer;

/** Ponte de migração: contexto limitado à execução de uma ação, sem estado gráfico global. */
final class Eventos {
    static void texto() { texto(""); }
    private static final ThreadLocal<Consumer<EventoJogo>> DESTINO = new ThreadLocal<>();

    static void capturar(Consumer<EventoJogo> destino, Runnable acao) {
        Consumer<EventoJogo> anterior = DESTINO.get();
        DESTINO.set(destino);
        try { acao.run(); }
        finally {
            if (anterior == null) DESTINO.remove(); else DESTINO.set(anterior);
        }
    }

    static void texto(Object texto) {
        String s = String.valueOf(texto);
        if (DESTINO.get() == null) System.out.println(s);
        else emitir(new EventoJogo(EventoJogo.Tipo.TEXTO, "", 0, s.replaceAll("\u001B\\[[;\\d]*m", "").strip()));
    }

    static void emitir(EventoJogo evento) {
        if (DESTINO.get() != null) DESTINO.get().accept(evento);
    }
}
