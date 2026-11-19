# Validação multiplataforma

O workflow `.github/workflows/validacao.yml` valida cada atualização da `main`, branches
`codex/**` e pull requests destinados à `main`. Também pode ser iniciado manualmente
pela aba Actions do GitHub.

## Matriz

| Ambiente do GitHub | Java |
| --- | --- |
| Windows Server 2022 x64 | 21 e 26 |
| Ubuntu 24.04 | 21 e 26 |
| macOS 15 | 21 e 26 |

Cada combinação compila o projeto, executa os testes de regras e persistência, gera a
distribuição e inicia o jogo pelo script distribuído (`super-rpg.bat` ou `super-rpg`).
O roteiro `--smoke` cria um personagem com semente reproduzível, abre as sete abas dos
serviços, realiza uma cura, confere o débito de ouro, responde uma charada, joga dados,
ouve um rumor, viaja, resolve o encontro com um viajante e entra em combate.
Ele ataca e captura a janela reduzida, inclusive a taverna. Em uma nova sessão, abre
moradores, conversa com Mira, aceita uma missão e encerra a conversa. A captura final é
`33-ranking-concluido.png`. Também percorre o prólogo completo e sua opção de pular,
e captura vitória e derrota em janela compacta com dados fixos de apresentação.
Esses dados não simulam vencer a campanha: a transição real e o resumo são cobertos
nos testes de regras. O Linux utiliza uma tela virtual Xvfb.
O ranking é isolado em diretório temporário durante o roteiro: nenhum ranking pessoal
é lido ou escrito. Há capturas da lista vazia, das duas modalidades e de cinco resultados
com nomes longos usando dados de apresentação. Persistência, duplicidade, trava de escrita,
arquivos corrompidos e identidade de saves antigos são verificados por `RankingTest`.

Os testes de regras cobrem os dois andares e o chefe da torre, preparação, descida,
reinício da subida, derrota, restrição de save e recompensa única das conversas.
Também verificam os limites de honra dos três epílogos, a pontuação original,
a ausência de encerramento após derrota Normal e o encerramento Hardcore.

A validação da alpha 6 na `main` passou nas seis combinações:
[execução 44411202298](https://github.com/luigigiarola-dev/SuperRpgTerminal/actions/runs/44411202298).
A falha anterior ao resolver dependências no Linux/Java 26 não se repetiu nessa execução.

Nas três combinações com Java 21, a automação também gera o aplicativo nativo com
`packageBundle` e executa seu lançador com `--smoke`. Usa uma pasta de trabalho nova
e exige uma nova captura final, sem reaproveitar o sucesso do roteiro anterior.
`JAVA_HOME` aponta para uma pasta inexistente nessa execução. Isso confirma o uso do
runtime incluído pelo lançador, mas não substitui testar num computador sem JDK.
Somente após essa etapa o ZIP/tar.gz é disponibilizado como artefato `aplicativo-*`.

A última captura é obrigatória para a execução ser considerada concluída. Relatórios
JUnit e capturas são guardados como artefatos por 14 dias, inclusive quando há falhas.
Uma execução pode durar até 20 minutos por ambiente; falhas não cancelam os demais.

## Limites e conferência manual

### Progressão controlada nas cinco classes

`CampanhaTest` percorre a cadeia de missões até Ignis para Paladino, Necromante,
Arqueiro, Mago e Ladino. Verifica aceitação, pré-requisitos, recompensa de entrega
única, consumo de coleta, torre com dois andares e chefe, Chave do Dragão, acesso ao
Covil e encerramento. A cada entrega, salva e recarrega a jornada, comparando o estado
persistente; a rodada do combate anterior é transitória e não pertence ao save.

Os adversários são colocados em condição de derrota e os materiais de coleta são
fornecidos pelo teste. XP e recompensas seguem o motor real, sem aumentar os atributos
do herói artificialmente. Portanto, esses casos **não** comprovam dificuldade, uso
eficiente de habilidades, equilíbrio entre classes ou uma partida real completa.
Há também regressões específicas para vitória, fuga e derrota em emboscadas no acampamento.

### Conferência manual pendente

Um resultado verde comprova o fluxo automatizado naquele ambiente. Não comprova uma
campanha inteira nem a qualidade visual de todas as telas. Em particular, o runner
Windows Server não substitui a conferência em um computador com Windows 10/11.

Antes da versão estável, conferir em cada sistema de destino:

- Texto legível, fontes, acentos, barras e botões sem cortes, inclusive em escala de tela elevada.
- Teclado, tela cheia, redimensionamento e fechamento da janela.
- Salvar, fechar, reabrir e continuar uma partida na pasta de dados padrão do usuário.
- Importar o save legado usando o seletor de arquivos.
- Uma campanha completa por classe, incluindo derrotas, chefes e conclusão.
- Inicialização da distribuição em outra máquina da mesma plataforma.

Windows ARM nativo ainda está fora da configuração suportada. Instaladores com Java
embutido e assinatura de aplicativos são etapas posteriores.

Na primeira conferência das capturas Windows, alguns símbolos de status do legado
apareceram como quadradinhos por falta de glifos nas fontes instaladas. O nome do
status continua legível. Substituir esses símbolos por ícones próprios faz parte do
acabamento visual multiplataforma.
