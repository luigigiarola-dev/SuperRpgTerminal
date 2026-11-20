# Direção criativa além da migração do terminal

## Decisão de escopo

Em 19/11/2026, o autor autorizou explicitamente mudanças radicais nas regras, conteúdo e
estrutura para tornar o SuperRPG mais divertido. A finalidade deixa de ser apenas reproduzir
o terminal numa janela. A referência original permanece preservada na tag `v1.0.0` e no
arquivo monolítico; fidelidade absoluta às regras antigas deixa de ser critério de aprovação
para novos experimentos de design. Correções e mudanças intencionais devem ser distinguidas.

O objetivo passa a ser um RPG 2D de fantasia por turnos com decisões compreensíveis, ações
expressivas e consequências visíveis. Java/JavaFX e Windows, macOS e Linux continuam como base.
Não há decisão de mudar para 3D, ação em tempo real, cartas ou outra engine.

## Problemas que orientam a próxima entrega

O autor relatou pouca sensação de ataque, eventos apresentados como blocos de texto,
fonte pequena, navegação lateral genérica, falta de som, NPCs sem identidade e necessidade
de rolar para sair das conversas. Também relatou pouca distinção dos cenários e do dia/noite.
Um amigo, pouco familiarizado com RPGs de turno, sentiu monotonia e falta de objetivo no início.

O motor já distingue locais e períodos, mas o desenho compartilha muito da composição e
representa manhã/tarde e noite/madrugada em dois grupos visuais. Isso não atende ainda à
expectativa de lugares vivos e reconhecíveis. Parte das animações recentes também não está
nos downloads da alpha 6: é necessário testar um pacote atualizado, não apenas a branch.

## Referências e aplicações propostas

As fontes descrevem decisões dos próprios desenvolvedores. As aplicações abaixo são
hipóteses para o SuperRPG, não garantias de diversão nem instruções para copiar os jogos.

| Referência | Evidência de design | Aplicação proposta no SuperRPG |
| --- | --- | --- |
| Into the Breach | Matthew Davis descreve ataques antecipados, legibilidade e restrições que orientaram tanto combate quanto estrutura do mundo. | Experimentar intenções inimigas comprometidas antes da decisão do jogador; reduzir regras opacas e dar alternativas a simplesmente causar mais dano. |
| Slay the Spire | Na entrevista com seus criadores, escolhas e resultados são analisados junto de feedback subjetivo; números sozinhos não descrevem a sensação de jogar. | Registrar escolhas apenas em testes locais e observar partidas; investigar habilidades ignoradas e estratégias dominantes antes de ajustar valores. |
| Sea of Stars | Thierry Boulanger descreve encontros montados manualmente, mana que incentiva usar habilidades e formas de enfraquecer ou interromper golpes preparados. | Criar uma abertura com encontros deliberados e respostas diferentes. Avaliar um sistema próprio de ruptura de preparação, sem exigir reflexos do jogador. |
| Darkest Dungeon | A apresentação oficial liga identidade temática a consequências mecânicas como estresse e características dos heróis. | Fazer a identidade de classes, NPCs e expedições aparecer nas escolhas e consequências, não apenas em retratos. Não importar punição severa ou estresse automaticamente. |

Fontes consultadas em 19/11/2026:

- Matthew Davis, [Into the Breach Design Postmortem, GDC 2019](https://media.gdcvault.com/gdc2019/presentations/Into%20the%20Breach%20Postmortem%20Final.pdf), especialmente slides 7–13, 20–21 e 38–47.
- Anthony Giovannetti e Casey Yano, entrevista [How Slay the Spire's devs use data to balance their roguelike deck-builder](https://www.gamedeveloper.com/design/how-i-slay-the-spire-i-s-devs-use-data-to-balance-their-roguelike-deck-builder), 27/02/2018.
- Thierry Boulanger, [A closer look at the turn-based combat in Sea of Stars](https://blog.playstation.com/2022/07/07/a-closer-look-at-the-turn-based-combat-in-sea-of-stars/), publicado em 07/07/2022. Referência às intenções descritas naquele momento, não a todas as regras das versões posteriores.
- Red Hook Studios, [apresentação oficial de Darkest Dungeon](https://www.darkestdungeon.com/darkest-dungeon/).

## Identidade desejada

1. **Ler a ameaça, decidir, sentir a consequência.** Preparação, movimento, contato, reação,
   som e resultado devem formar uma ação compreensível. Um golpe importante pode durar mais;
   uma repetição sem informação ou impacto não se justifica pela duração.
2. **Aurora responde ao jogador.** Moradores reconhecíveis, locais distintos e pequenas
   mudanças após missões devem sustentar o interesse entre combates.
3. **Poucas escolhas com função real.** Ataque, defesa e habilidades precisam disputar a
   preferência conforme a situação. Mais botões ou números maiores não bastam.

Estética inicial: fantasia retrô moderna, silhuetas próprias, contraste legível, painéis
com função diegética de diário/cartografia e tipografia com licença registrada. Emblemas
de moradores são provisórios; não são retratos finais. Não reutilizar arte, personagens,
trilha, textos ou identidade visual protegida dos jogos estudados.

## Ordem de implementação adotada

### Prévia perceptível e acessível

- Corrigir saída fixa de diálogos, reduzir texto simultâneo e aumentar legibilidade.
- Completar um ciclo visual de ataque: preparação/avanço ou projétil, contato e reação;
  atualizar barras no momento correspondente, sem anunciar dano antes do contato.
- Adicionar efeitos sonoros com licença/proveniência, volume e silêncio acessíveis desde
  o menu. Falha ou ausência de áudio não pode impedir jogar. Música vem após os efeitos básicos.
- Dar composições e paletas distintas à floresta, caverna, ruínas e vila; tornar os períodos
  perceptíveis. Separar camadas do cenário antes de aumentar partículas.
- Substituir a aparência genérica da lateral por navegação coerente com o diário de aventura,
  conservando nomes legíveis e foco por teclado; criar linguagem visual para eventos e NPCs.
- Gerar novos aplicativos testados para as três plataformas. A próxima prévia é da linha
  `2.0.0-alpha.7`; não republicar nem renomear a antiga alpha 2. Uma alpha não é a versão final.

### Protótipo de abertura redesenhada

Proposta ainda não implementada: uma missão inicial curta de Mira sobre uma caravana que
não chegou. A vila apresenta um problema concreto, e a saída oferece duas rotas com riscos
e recompensas descritos. Dois encontros apresentam situações diferentes, seguidos de um
minichefe e uma consequência visível na vila. Meta inicial de teste: 10–15 minutos, não
um limite obrigatório de duração nem uma promessa de tempo para todos os jogadores.

Substituir a repetição aleatória necessária para concluir a primeira missão por progresso
deliberado nesse protótipo. Permitir explorar sem travar a aprendizagem num tutorial longo.
Primeiro provar a experiência com uma classe; depois adaptar as cinco, sem remover classes
dos saves existentes. Não multiplicar conteúdo antes de validar a abertura.

### Combate com intenções e preparação

Prototipar inimigos que anunciam uma intenção e de fato a executam se não forem interrompidos.
A ordem por agilidade exige revisão: uma intenção não pode ser mostrada só depois de o inimigo
agir. Decisões são sorteadas uma vez no planejamento, nunca ao consultar a interface. Dano
previsto, caso exibido, precisa declarar faixa/condições e respeitar defesa, escudo e estado.

Testar ataques que abrem oportunidades de resposta e uma ruptura própria de preparação,
com alternativas viáveis por classe. Não exigir um elemento indisponível para vencer.
Comparar a estratégia de repetir ataque com defesa, interrupção e gasto de recursos. Evitar
apenas aumentar HP, reduzir recompensas ou punir repetição artificialmente.

Comandos temporizados, grupos de vários inimigos, combos e modificações maiores de mana
são possibilidades posteriores. Não entrarão juntos no primeiro experimento; cada sistema
precisa justificar sua contribuição e manter alternativa acessível sem exigência de reflexos.

## Compatibilidade e validação

Experimentos de regras ficam em branches próprias e não sobrescrevem saves de jogadores.
Se a estrutura de campanha mudar, definir versão nova do formato, backup e migração explícita
antes de publicar; não presumir que o formato 4 comporta intenções ou rotas persistentes.
O ranking deve distinguir conjuntos de regras quando suas pontuações deixarem de ser comparáveis.

Cada mudança deve registrar: problema observado, hipótese, regras alteradas, teste técnico,
resultado de partidas e decisão de manter/revisar/reverter. Comparações devem identificar
versão, classe e modo. Testes locais não devem enviar telemetria pessoal automaticamente.

Critérios para aprovar a abertura: objetivo entendido sem orientação externa; decisão
explicável em combate; retorno perceptível de ataque/defesa; interesse em continuar; ausência
de cortes, saídas escondidas ou dependência de som. Seguir `PLAYTEST_INICIAL.md` com pessoas
familiarizadas e não familiarizadas com o gênero. Diversão exige avaliação humana.
