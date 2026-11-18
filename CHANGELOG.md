# Histórico de versões

Este projeto segue o [Versionamento Semântico](https://semver.org/lang/pt-BR/):

- **MAJOR** (`2.0.0`): mudança ampla ou incompatível, como a nova experiência gráfica.
- **MINOR** (`1.1.0`): novos recursos compatíveis dentro da mesma geração.
- **PATCH** (`1.0.1`): correções sem novos recursos relevantes.
- Sufixos como `alpha.1` indicam versões de desenvolvimento que ainda não estão completas.

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
