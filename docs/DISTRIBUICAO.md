# SuperRPG — abrir sem terminal

Os pacotes da alpha 6 incluem Java e JavaFX. Não é necessário instalar Java ou Gradle.
São aplicativos portáteis de prévia, ainda sem instalador nem assinatura de distribuição.

## Escolher o download

- Windows x64: extraia **toda** a pasta do ZIP e abra `SuperRPG.exe` dentro dela.
  Não mova apenas o executável: as pastas `app` e `runtime` precisam acompanhá-lo.
- macOS: extraia o ZIP e mova `SuperRPG.app` para Aplicativos. Abra com dois cliques.
  Use o pacote da arquitetura indicada no nome: ARM64 para Apple Silicon, X64 para Intel.
  Só serão publicados pacotes das arquiteturas efetivamente geradas e testadas.
- Linux x64: extraia o `.tar.gz` e execute `SuperRPG/bin/SuperRPG` em sessão gráfica.
  Bibliotecas gráficas do sistema (GTK/X11) continuam necessárias.

Baixe os arquivos `SuperRPG-...` na release, não os links automáticos de código-fonte.
O nome do arquivo identifica o sistema e a arquitetura. Não copie pacotes entre sistemas.

## Avisos de segurança

Esta prévia não está assinada/notarizada. Windows ou macOS podem mostrar um alerta ou
impedir a abertura. Confira a origem e o checksum antes de decidir autorizar o aplicativo
nas opções de segurança do sistema. Não desative a proteção global do computador.
Assinatura e notarização são pendências de distribuição, não garantias já oferecidas.

## Saves e atualização

Feche o jogo antes de trocar a pasta/aplicativo por uma versão nova. Os saves ficam fora
do aplicativo: `%APPDATA%/SuperRpgTerminal` no Windows, `~/Library/Application Support/SuperRpgTerminal`
no Mac e `$XDG_DATA_HOME/SuperRpgTerminal` (ou `~/.local/share/SuperRpgTerminal`) no Linux.
Faça uma cópia dessa pasta antes de atualizar. A alpha 6 mantém o formato 4 das alphas 3–5.
Não há atualização automática nem sincronização entre computadores.

## Gerar a partir do código (desenvolvedores)

Use um JDK completo 21–26 no sistema/arquitetura de destino. Execute `./gradlew test packageBundle`
ou `gradlew.bat test packageBundle` no Windows. O resultado fica em `build/native`.
A tarefa substitui apenas o aplicativo gerado nessa pasta; não apaga saves.
`jpackage` inclui um runtime mínimo do próprio JDK usado no build.
O número interno do pacote é 2.0.0; a identificação alpha permanece na descrição e na release.

As licenças adicionais estão em `documentacao/terceiros`, ao lado da pasta/aplicativo.
Os avisos do Java permanecem em `runtime/.../legal`. Preserve esses avisos ao redistribuir.

## Limitações desta prévia

O teste automático abre o aplicativo nativo e percorre o roteiro gráfico. Não substitui
uma instalação manual em Windows 10/11 nem uma campanha completa. Ranking gráfico,
introdução/epílogo completos, áudio, refinamento visual e balanceamento continuam pendentes.
Instaladores `.exe`/`.dmg`, ícones finais e assinatura serão uma etapa posterior.
