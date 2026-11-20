# Histórico de versões

Este projeto segue o [Versionamento Semântico](https://semver.org/lang/pt-BR/):

- **MAJOR** (`2.0.0`): mudança ampla ou incompatível, como a nova experiência gráfica.
- **MINOR** (`1.1.0`): novos recursos compatíveis dentro da mesma geração.
- **PATCH** (`1.0.1`): correções sem novos recursos relevantes.
- Sufixos como `alpha.1` indicam versões de desenvolvimento que ainda não estão completas.

## Em desenvolvimento — próxima prévia

- Saída das conversas em rodapé fixo, fora da rolagem; Escape encerra apenas eventos
  que oferecem uma saída válida. Detalhes de missão recolhíveis e emblemas provisórios de NPCs.
- Textos de apoio maiores e com mais contraste; falas destacadas em 18 px.
- Nova direção autorizada pelo autor: pesquisa e redesenho de jogabilidade além do terminal,
  preservando a referência original e documentando mudanças de regras e compatibilidade.

- Objetivo sugerido visível na exploração: primeiro contato, progresso, local de busca
  e retorno para entrega. Não aceita nem entrega missões automaticamente.
- Três habilidades acessíveis diretamente no combate, com custo/elemento e motivos
  de indisponibilidade. Ordem de ação e benefício de defender mais claros.
- Aviso específico de cada chefe durante a carga, sem prever decisões aleatórias futuras.
- Roteiro de avaliação dos primeiros minutos, motivado por relato de monotonia e falta de direção.

- Recuo curto ao receber dano/bloqueio e deslocamento maior na esquiva, para herói e inimigo.
  As reações acompanham os números flutuantes sem aumentar a duração da fila existente;
  o modo rápido pula os efeitos. Cenário e regras permanecem inalterados.

- Impactos estruturados de combate: crítico, elemento, afinidade, guarda e absorção por escudo.
- Indicação visual explícita de crítico, fraqueza, resistência, esquiva e bloqueio total,
  sem interpretar mensagens do histórico nem alterar a fórmula de dano.
- Testes da sequência de sorteios e dos impactos; roteiro gráfico testa bloqueio e
  liberação de comandos durante uma animação real, além de capturar os novos rótulos.

- Salão dos heróis com cinco melhores resultados por modo Normal/Hardcore.
- Ranking local em JSON com escrita atômica quando disponível, cópia anterior e trava
  contra gravação simultânea. Arquivos inválidos são preservados, com erro visível e nova tentativa.
- Identidade persistente opcional no save formato 4: o primeiro encerramento de uma jornada
  é registrado uma única vez, inclusive após carregar o mesmo save.
- Ranking do terminal permanece separado; não há importação automática nem placar online.

- Prólogo com falas originais, avanço por clique e opção de pular, sem repetir ao carregar save.
- Epílogo conforme a honra, resumo da campanha e pontuação com a fórmula do terminal.
- Tela de derrota Hardcore; derrota Normal mantém o retorno à vila.
- Save anterior preservado e limitação do ranking explicitada no encerramento.
- Testes dos limites de honra, pontuação e integração; roteiro gráfico cobre prólogo,
  encerramentos com dados de apresentação e retorno ao menu em janela compacta.

## [2.0.0-alpha.6] — 2026-11-18

- Empacotamento com `jpackage`: aplicativo nativo e runtime incluído por plataforma.
- Roteiro gráfico executado pelo lançador nativo em Windows, macOS e Linux com Java 21.
- Arquivos portáteis, instruções de uso e avisos de licença das dependências.
- Sem mudanças nas regras ou no formato de save. Instaladores e assinatura ainda pendentes.

## [2.0.0-alpha.5] — 2026-11-18

- Corrige a recuperação de 30% de vida e mana após vencer uma emboscada no acampamento,
  preservando o comportamento original. Fuga e derrota cancelam o descanso pendente.
- Testa a sequência de missões até Ignis nas cinco classes, com resultados de combate
  controlados, consumo/entrega de recursos, desbloqueio da torre, chave e checkpoints.
- 8 novos casos, totalizando 89 testes. Não substituem a avaliação de equilíbrio da campanha.
- Documenta a entrega futura de aplicativos com Java incluído para Windows e macOS.

## [2.0.0-alpha.4] — 2026-11-18

- Conversas com Mira, Lira, Borin, Aldric e Gus, preservando as falas do terminal.
- Missões filtradas por morador, aceitação/entrega explícita, pré-requisitos e recompensa única.
- Pausa entre os dois primeiros andares da torre: usar itens, equipar, continuar ou descer.
- Retirada/fuga/derrota reiniciam a subida; recompensas já obtidas são mantidas na retirada.
- Save continua no formato 4; é necessário terminar ou abandonar a subida antes de salvar.
- 12 novos testes, totalizando 81, e roteiro gráfico ampliado com conversa e aceitação de missão.

## [2.0.0-alpha.3] — 2026-11-18

- Exploração com probabilidades do terminal: combate, baú, armadilha, fogueira e encontros narrativos.
- Escolhas para viajante ferido, santuário, cofre e mercador errante; fonte de mana.
- Dados com apostas de 1–100 ouro, rumores ligados ao progresso e charada diária com resposta livre.
- Eventos exibidos em cartões com escolhas e resultados; outras ações ficam bloqueadas até a conclusão.
- Save JSON formato 4 para preservar o limite diário da charada, com leitura do formato 3 e
  importação do marcador do save de terminal. Versões anteriores não leem o novo formato.
- 28 novos testes, totalizando 69; roteiro gráfico reproduzível com eventos e minijogos.

## [2.0.0-alpha.2] — 2026-11-17

- Serviços da vila na interface: venda, forja, alquimia, cura, doação, contratação e dispensa de aliados.
- Custos, horários, chances e ingredientes faltantes exibidos antes das ações; confirmação para
  venda, forja, doação, contratação e dispensa.
- 15 novos testes para regras da vila e persistência, totalizando 41 testes.
- Roteiro gráfico ampliado para visitar as seis abas, realizar uma cura e testar a taverna compacta.

- Automação no GitHub para testes de regras, persistência e execução da distribuição
  gráfica em Windows, macOS e Linux, com Java 21 e 26.
- Relatórios e capturas de tela por ambiente, com roteiro de conferência manual documentado.

## [2.0.0-alpha.1] — 2026-11-17

Primeira prévia pública da migração para uma interface gráfica em JavaFX.

### Adicionado

- Projeto Gradle modular com Java 21, JavaFX 21 e Gradle Wrapper.
- Interface gráfica para título, criação de personagem, vila, mapa, combate e recompensas.
- Cinco classes jogáveis e modos Normal e Hardcore.
- Inventário, equipamentos, missões, compra de suprimentos e descanso.
- Combate com habilidades, mana, recargas, status, defesa, análise, fuga e chefes.
- Cenários e personagens desenhados em código, animações iniciais e histórico de eventos.
- Motor de jogo orientado a ações e eventos, separado da apresentação JavaFX.
- Salvamento JSON com cópia de segurança e importação do save legado.
- 26 testes automatizados e um roteiro de validação visual.

### Limitações conhecidas

- Ainda faltam venda, forja, alquimia, templo, contratação de aliados pela interface,
  eventos narrativos, minijogos, ranking gráfico, áudio e instaladores com Java embutido.
- Windows e Linux ainda precisam de validação visual.
- Esta versão é uma prévia e não substitui a experiência completa do terminal.

## [1.0.0] — 2026-11-17

Versão original completa do RPG executada no terminal.

### Incluído

- Cinco classes, combate tático por turnos, chefes e afinidades elementais.
- Vila, seis locais, dez missões, ciclo de tempo e eventos aleatórios.
- Economia, ferreiro, alquimia, aliados, honra, save e ranking locais.
- Arte ASCII e cores ANSI em um único arquivo Java, sem dependências externas.

[2.0.0-alpha.1]: https://github.com/luigigiarola-dev/SuperRpgTerminal/releases/tag/v2.0.0-alpha.1
[2.0.0-alpha.2]: https://github.com/luigigiarola-dev/SuperRpgTerminal/releases/tag/v2.0.0-alpha.2
[2.0.0-alpha.3]: https://github.com/luigigiarola-dev/SuperRpgTerminal/releases/tag/v2.0.0-alpha.3
[1.0.0]: https://github.com/luigigiarola-dev/SuperRpgTerminal/releases/tag/v1.0.0
