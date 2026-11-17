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
O roteiro `--smoke` cria um personagem, abre as seis abas dos serviços, realiza uma cura,
confere o débito de ouro, abre vila e mapa, entra em combate, ataca e redimensiona a janela.
Ele também captura a taverna na dimensão mínima. O Linux utiliza uma tela virtual Xvfb.

A última captura é obrigatória para a execução ser considerada concluída. Relatórios
JUnit e capturas são guardados como artefatos por 14 dias, inclusive quando há falhas.
Uma execução pode durar até 20 minutos por ambiente; falhas não cancelam os demais.

## Limites e conferência manual

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
