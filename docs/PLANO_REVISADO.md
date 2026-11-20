# Plano revisado e estado real da migração

## Ampliação de escopo autorizada

Em 19/11/2026, o autor autorizou mudanças radicais para priorizar diversão, além da migração
fiel do terminal. A [direção criativa](DIRECAO_CRIATIVA.md) registra fontes, critérios,
diferenças autorizadas e a nova sequência. As fases abaixo continuam como histórico e
controle técnico; não impedem redesenhar combate, exploração ou progressão.

Referência de acompanhamento: plano de fases 0–6 aprovado pelo autor.
Atualizado em 19/11/2026. Funcionalidade implementada não significa acabamento final
nem disponibilidade nos aplicativos já publicados: a última release é a alpha 6.
Prólogo, epílogos e ranking estão na `main`, integrados pelas revisões #8 e #9.
O ranking passou em 111 testes e no roteiro gráfico das seis combinações de sistema e Java.

| Fase | Implementado | Pendências para concluir |
| --- | --- | --- |
| 0 — Proteger o jogo | Git, tag v1.0.0, licença, exclusões e testes de regras/save; correção do descanso após emboscada | Registrar uma campanha completa de referência e comparar com a versão gráfica |
| 1 — Extrair a lógica | Classes extraídas, sessão `MotorJogo`, `EstadoJogo`, `AcaoJogo` e `EventoJogo`; caminho gráfico sem entrada bloqueante | Remover resíduos de apresentação das entidades, reduzir RNG global e unificar a orquestração do console com o motor gráfico |
| 2 — MVP gráfico | Menu, personagem, vila por botões, mapa, combate, inventário, diálogos e save/load | Fluxo básico entregue; validação humana e melhorias contínuas permanecem necessárias |
| 3 — Combate completo | Arena por local, ações, habilidades/custos/recargas, status textuais, números de dano/cura, fila visual com bloqueio de entrada, aviso de chefe e histórico recolhível | Movimento de herói/inimigo, barras suaves, ícones próprios e distinção visual estruturada de crítico/resistência |
| 4 — Sistemas da vila | Compra/venda, forja, alquimia, taverna, templo, diário, inventário e equipamentos | Revisar completude da ficha de atributos e usabilidade; não exigir arrastar equipamentos |
| 5 — Mundo e apresentação | Cenários em Canvas, destinos bloqueados, transições, variação dia/noite e eventos narrativos | Vila com prédios clicáveis, caminhos visuais do mapa e animações de viagem/coleta/acampamento |
| 6 — Arte, áudio e acabamento | Tema escuro/dourado, alguns atalhos, tela cheia e aplicativos portáteis com Java nos três sistemas | Arte definitiva, efeitos avançados, áudio, opções persistentes, acessibilidade, instaladores e assinatura/notarização |

## Alterações deliberadas na sequência

- Sistemas da vila e distribuição portátil foram antecipados para permitir testar a jornada
  e abrir o jogo sem terminal. Isso não encerra as fases de animação e acabamento.
- Introdução, finais por honra e ranking complementam o fluxo da campanha antes da próxima
  publicação. Ranking não é requisito para considerar a arte final pronta.
- O código mantém `AcaoJogo` e `EventoJogo`, equivalentes em português a `GameAction` e
  `GameEvent`. A UI envia comandos curtos; não há thread de jogo esperando cliques.
- O terminal preservado ainda não usa integralmente a mesma orquestração do gráfico.
  O critério completo da Fase 1, portanto, continua pendente.
- Save JSON versionado, pasta do usuário, arquivo temporário e backup foram implementados.
  A importação do save v2 é **manual**, com seleção do arquivo e sem alterar a origem;
  a migração automática na primeira abertura não foi implementada.
- Cenários e personagens são desenhos próprios em Canvas. A fonte de títulos atual não é
  pixelada. A direção retrô moderna é referência; sprites e tipografia definitivos estão pendentes.

## Próxima sequência de trabalho

1. Ranking local validado e integrado, preservando saves existentes — etapa concluída.
2. Retomar o combate visual da Fase 3: impactos estruturados e rótulos de crítico, afinidade,
   guarda, escudo e esquiva integrados pela revisão #10. Recuo e esquiva dos personagens
   estão em validação separada. Avanço de ataque, barras suaves e ícones próprios seguem pendentes.
3. Conferir ficha de atributos e interação das telas compactas; avançar na apresentação do mundo.
   A avaliação dos primeiros minutos ganhou prioridade após relato de monotonia e falta de
   objetivo claro. Orientação de missão e habilidades visíveis estão na revisão de escolhas;
   avaliar o efeito com o roteiro `PLAYTEST_INICIAL.md`, sem presumir diversão pelos testes automáticos.
4. Consolidar opções, áudio e arte com licenças registradas.
5. Publicar a próxima prévia somente com os pacotes correspondentes validados.

Campanhas completas nas cinco classes, comparação com a referência e revisão de equilíbrio
continuam sendo critérios da versão estável, não consequências automáticas de testes verdes.
Os testes controlados de progressão não substituem jogar partidas completas.

## Diversão e ritmo como critérios de qualidade

Entretenimento e decisões interessantes têm prioridade sobre a duração mínima do turno.
Um turno mais longo é válido quando sua preparação, impacto e consequência sustentam a atenção;
o objetivo é evitar repetição e espera sem significado, não acelerar tudo.
Golpes importantes e chefes podem receber maior destaque, enquanto ações comuns pedem variação
e legibilidade. As reações simples atuais são curtas e paralelas aos números flutuantes;
isso não impõe um limite de duração às futuras animações. O modo rápido permanece opcional.
Nas campanhas jogadas, avaliar tempo entre decisões, repetição de encontros, utilidade real
das habilidades, dificuldade dos chefes e sensação de progresso. Testes automáticos não
comprovam diversão; ajustar regras e balanceamento exige evidência de jogo e registro das mudanças.
