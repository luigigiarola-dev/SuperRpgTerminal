# ⚔️ SuperRpgTerminal

Um RPG de aventura em **Java puro**, jogado inteiramente no terminal. Escolha uma classe, explore o reino, cumpra missões, lute com monstros e chefes em combate por turnos e enfrente o dragão Ignis.

Tudo em um único arquivo (`SuperRpgTerminal.java`), sem dependências externas.

```
      .-^-.        ^                       /\    /\
     ( o_o )       |           _   /  \__/  \   _
    /|#####|\     -+-         / \_|  O    O  |_/ \
   [ |#####| ]======|        /    \    ^^    /    \
      |_| |_|      |         \_    \  VVVV  /    _/
      /_/ \_\      v           \____\_/||\_/____/
▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔
Arthur o Viajante (Nv 5 Paladino)
  HP   [████████████████░░░░░░░░] 148/220
  MANA [██████████░░░░░░░░░░░░░░] 52/90
Ignis, o Dragão Ancestral ⚠️ CARREGANDO GOLPE!
  HP   [████████████████████░░░░] 400/480
```

## ✨ Destaques

- **5 classes** com habilidades próprias: Paladino, Necromante, Arqueiro, Mago e Ladino
- **Combate tático por turnos** com status, elementos, esquiva, críticos, fuga e ordem por agilidade
- **Chefes que avisam o golpe** devastador antes de usá-lo, com fase de fúria abaixo de 50% de HP
- **Mundo para explorar:** vila, 6 locais selvagens, ciclo de dia e noite e eventos aleatórios com escolhas
- **10 missões** encadeadas, com diário
- **Economia e artesanato:** lojas, ferreiro, alquimia, equipamentos e aliados
- **Sistema de honra** que muda preços, título e o final
- **Save/load** e **ranking** local
- **Arte ASCII** para heróis, monstros, locais e efeitos de golpe, com cores ANSI

## 🚀 Como rodar

Requisitos: **Java 11 ou superior** (JDK).

```bash
javac -encoding UTF-8 SuperRpgTerminal.java
java SuperRpgTerminal
```

Opções de execução:

| Opção | Efeito |
|---|---|
| `--sem-cor` | Desativa as cores ANSI (use se aparecerem códigos estranhos como `[31m`) |
| `--rapido` | Remove as pausas e animações de espera |

Exemplo: `java SuperRpgTerminal --sem-cor --rapido`

### Dicas de terminal

- **Windows:** use o Windows Terminal, ou rode `chcp 65001` antes de iniciar, para exibir emojis e caracteres especiais.
- Use uma **fonte monoespaçada** e deixe a janela com pelo menos **70 colunas** para os sprites ficarem alinhados.
- **OnlineGDB:** funciona, mas o arquivo precisa se chamar `SuperRpgTerminal.java`. Como o console do site pode não permitir gravar arquivos, o save e o ranking podem não funcionar.

## 🎮 Como jogar

Os menus são numerados: digite o número da opção e pressione Enter.

### Classes

| Classe | Estilo | Habilidades |
|---|---|---|
| **Paladino** | HP e defesa altos | Julgamento da Luz (cura), Escudo Divino, Martelo Sagrado (atordoa) |
| **Necromante** | Dano sombrio | Sifão da Alma (rouba vida), Praga Mortal (veneno), Explosão Necrótica |
| **Arqueiro** | Rápido e preciso | Flecha Precisa, Flecha Flamejante (queima), Chuva de Flechas (sangramento) |
| **Mago** | Muita mana | Bola de Fogo, Relâmpago, Prisão Arcana (atordoa) |
| **Ladino** | Muito veloz | Golpe Furtivo, Lâmina Envenenada, Dança das Lâminas |

### Combate

A cada turno você pode: **Atacar**, usar uma **Habilidade**, **Defender** (reduz o dano e recupera mana), usar um **Item**, **Analisar** o inimigo (mostra fraquezas e resistências, sem gastar o turno) ou **Fugir**.

- **Elementos:** Físico, Luz, Sombra, Fogo e Raio. Explorar fraquezas aumenta muito o dano (esqueletos temem Luz, golens temem Raio, slimes e lobos temem Fogo).
- **Status:** veneno, queimadura, sangramento, atordoamento, escudo, lentidão e fúria.
- **Habilidades** gastam mana e algumas têm tempo de recarga.
- **Chefes** avisam quando vão usar um golpe devastador. Defenda-se ou atordoe-os para interromper.
- Aliados contratados agem sozinhos a cada turno.

### O mundo

| Local | Nível recomendado | Recurso |
|---|---|---|
| Vila Aurora | – | Lojas, NPCs, taverna, templo |
| Floresta Sussurrante | 1 | Erva Lunar |
| Caverna das Aranhas | 2 | Presa de Aranha |
| Ruínas Esquecidas | 3 | Essência Arcana |
| Montanha de Ferro | 5 | Pedra de Fogo |
| Torre do Observador | 6 | Chefe: Observador do Caos |
| Covil de Ignis | 9 | Chefe final: Ignis, o Dragão Ancestral |

A Torre abre ao concluir a missão *A Seita do Olho*. O Covil só abre com a **Chave do Dragão**, que o Observador guarda.

Cada ação fora da vila avança o relógio (Manhã, Tarde, Noite e Madrugada). **À noite** as lojas fecham, e os monstros ficam mais fortes e dão mais ouro.

### Na vila

- **Moradores e missões:** Capitã Mira, Irmã Lira, Borin, Sábio Aldric e Gus
- **Mercador:** comprar e vender suprimentos e equipamentos
- **Ferreiro:** melhora arma e armadura até +5 (com risco de falha)
- **Alquimista:** transforma ingredientes em poções, antídotos, bombas e pergaminhos
- **Taverna:** descansar, contratar aliados (Fenrir, o Lobo Cinzento, ou Lira, a Curandeira), jogar dados e resolver a charada do dia
- **Templo:** curar tudo ou doar ouro para ganhar honra

### Honra

Ajudar viajantes e doar ao templo aumenta a **honra**, o que dá desconto nas lojas. Roubar a diminui e encarece os preços. A honra também define o título do herói e o epílogo.

### Modos

- **Normal:** ao cair em combate você acorda na vila, perde 25% do ouro e recomeça com metade do HP.
- **Hardcore:** se cair, o jogo termina.

### Salvar e ranking

O jogo salva em `rpg_save.txt` e o ranking fica em `rpg_ranking.txt`, ambos na pasta onde o jogo foi iniciado. O save é apagado ao terminar a aventura (vitória ou derrota).

## 🧩 Estrutura do código

O arquivo único é organizado em seções, que usam herança e polimorfismo:

| Seção | Conteúdo |
|---|---|
| Visual | `Cor`, `Tela`, `Arte`, `Efeitos` (cores ANSI, barras, caixas, artes ASCII) |
| Itens | `Item` e suas subclasses (`Pocao`, `Elixir`, `BombaFogo`, `Equipamento`...) e a `Fabrica` |
| Entidades | `Entidade` → `Heroi` (5 classes) e `Aliado` |
| Monstros | `Monstro` → 9 criaturas, incluindo os chefes `Beholder` e `DragaoAncestral`, e o `Bestiario` |
| Combate | `Combate`: ordem de turno, menus, status e fuga |
| Missões | `Missao` e `Diario` |
| Jogo | `SuperRpgTerminal`: vila, mundo, eventos, lojas e loop principal |
| Persistência | `Persistencia`: save, load, ranking e telas finais |

## 🗺️ Ideias futuras

- Interface gráfica
- Mais classes, locais e missões
- Efeitos de status mais variados e árvore de habilidades

## 📄 Licença

Este projeto é distribuído sob a **Licença MIT**. Você pode usar, copiar, modificar e distribuir o código livremente, inclusive em projetos próprios, desde que mantenha o aviso de copyright e a licença. O software é fornecido "como está", sem garantias. Veja o arquivo [LICENSE](LICENSE) para o texto completo.
