package br.com.superrpg.jogo;

import java.io.*;
import java.util.*;

enum TipoEfeito {
    VENENO("☠️ Veneno"), QUEIMADURA("🔥 Queimadura"), SANGRAMENTO("🩸 Sangramento"),
    ATORDOADO("💫 Atordoado"), ESCUDO("🛡️ Escudo"), LENTIDAO("🐌 Lentidão"), FURIA("💪 Fúria");
    final String rotulo;
    TipoEfeito(String r) { rotulo = r; }
}
