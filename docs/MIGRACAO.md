# Migração para JavaFX

## Versão desta entrega

A versão original do terminal é `v1.0.0`. Esta primeira entrega gráfica é
`v2.0.0-alpha.1`: o salto de versão representa a nova interface e a nova estrutura do projeto;
o sufixo `alpha.1` deixa explícito que a migração ainda não está completa.
A entrega atual, `v2.0.0-alpha.2`, completa os serviços da vila e acrescenta validação
automatizada em Windows, macOS e Linux com Java 21 e 26.

As tags publicadas são imutáveis. Correções recebem incremento de patch, recursos compatíveis
incrementam minor e novas prévias avançam o sufixo (`alpha.2`, `beta.1`) até a versão `v2.0.0`
estar pronta. Alterações relevantes são registradas em `CHANGELOG.md`.

## Objetivo

Entregar um RPG desktop com identidade visual de fantasia, preservando as regras da versão terminal.
A primeira etapa cria uma fatia jogável e uma base testada. A conclusão desta etapa não significa
paridade de todos os sistemas nem acabamento artístico final.

## Decisões

- Java 21 como versão de linguagem; Gradle 9.4.0 permite usar também o JDK 26 presente na máquina.
- JavaFX 21.0.9, CSS e Canvas. Sem bibliotecas externas de imagens ou sons nesta etapa.
- `MotorJogo` por partida, comandos síncronos curtos e snapshots imutáveis para a apresentação.
- Animações com transições JavaFX; entrada desabilitada enquanto os eventos visuais são apresentados.
- Nada de `Thread.sleep` ou `Scanner` no caminho de execução do motor gráfico.
- `Eventos` é uma ponte de migração com destino limitado ao processamento de uma ação. No console,
  preserva as mensagens; na UI, produz eventos. Ainda há referências à formatação antiga e sprites
  como dados nas entidades extraídas. Sua remoção faz parte da próxima limpeza arquitetural.
- RNGs de algumas entidades ainda são compartilhados pelo código legado. Para testes reproduzíveis
  de partidas inteiras, a próxima etapa deve injetar uma fonte de aleatoriedade em todo o domínio.
- O arquivo monolítico original permanece congelado para referência. Mudanças futuras devem entrar em `src/`.
- Saves gráficos versionados e escrita por substituição atômica quando o sistema oferece suporte,
  com uma cópia anterior. Importação do formato interno 2 do save legado sem alterar o arquivo
  de origem; essa numeração do arquivo é independente da versão pública do jogo.

## Entrega inicial

- [x] Gradle Wrapper, dependências e extração das classes.
- [x] Console extraído funcional e escolha de talento fora da entidade.
- [x] Motor orientado a ações e eventos, separado do JavaFX.
- [x] Testes de combate, missões, economia, inventário, progressão e persistência.
- [x] Menu → personagem → vila → mapa → combate → recompensa → retorno.
- [x] Interface de inventário, equipamentos, diário e compra de itens.
- [x] Save/load JSON e importação v2.
- [x] Cenários/avatares originais em código e teste visual em duas dimensões de janela.
- [x] Venda, forja, alquimia, templo e contratação/dispensa de aliados na interface.
- [x] Testes e roteiro gráfico automatizados nos três sistemas de destino.
- [ ] Paridade completa com o terminal.
- [ ] Arte final, áudio, efeitos avançados e instaladores independentes de Java.

## Próximas entregas, em ordem

1. Migrar todos os eventos de exploração, dados, charadas, rumores e diálogos de NPCs.
2. Completar as pausas entre andares da torre e permitir gestão de itens/retirada como no terminal.
3. Validar uma campanha completa por classe e comparar recompensas e progressão com a referência.
4. Extrair regras ainda repetidas entre a orquestração do console e a gráfica, substituir a ponte
   de mensagens por eventos estruturados adicionais, injetar RNG e dividir os serviços por domínio.
5. Refinar personagens, cenários e animações: movimento de ataque, projéteis, crítico, impacto,
   barras animadas, morte, level up e sinalização de turnos. Adicionar áudio com licenças registradas.
6. Ranking, opções persistentes, acessibilidade e conferência manual Windows/Linux.
7. Empacotar com runtime Java usando `jpackage`; documentar assinatura e distribuição por plataforma.

## Regras preservadas e diferenças explícitas

Preservados os atributos iniciais, catálogo, habilidades, fórmula de dano, afinidades, efeitos,
recargas, crescimento por nível, recompensas, missões e pré-requisitos. A morte Normal mantém
a perda de 25% do ouro e o retorno à vila; Hardcore encerra a sessão.

A exploração gráfica usa encontros garantidos na ação “Procurar um confronto”; baús, fontes,
armadilhas e eventos com escolhas ainda não foram convertidos. Na torre, “Continuar” avança
automaticamente ao próximo combate; a parada intermediária completa ainda está pendente.

A recompensa da missão final e a liberdade de enfrentar Ignis apenas com a chave seguem o
comportamento da referência: o encerramento não exige aceitar a missão. Isso será objeto de
uma decisão de design posterior, não de uma alteração silenciosa na migração.

O gráfico permite salvar apenas na exploração, após recompensas e talentos. A versão original
continua com o próprio save/ranking. O marcador de charada diária não migra enquanto a charada
não fizer parte da UI. Nenhuma alegação de balanceamento humano foi feita nesta etapa.

## Verificação

Windows x64 é uma plataforma de destino obrigatória. A versão estável `2.0.0` exige
validar nesse sistema a inicialização, criação de personagem, combate, redimensionamento,
salvamento/carregamento e a distribuição gerada por `gradlew.bat installDist`.
O build atual seleciona os componentes nativos JavaFX `win` (x64); Windows ARM nativo
precisa de uma configuração específica e não faz parte desta prévia.

`./gradlew test` verifica regras e persistência. `./gradlew run --args=--smoke` abre a interface,
aciona criação, entrada na vila, mapa e ataque, e registra capturas para inspeção visual.
O modo smoke não deve ser usado como substituto para jogar uma campanha completa.

Recursos oficiais consultados: [JavaFX](https://openjfx.io/openjfx-docs/) e
[compatibilidade do Gradle](https://docs.gradle.org/current/userguide/compatibility.html).
