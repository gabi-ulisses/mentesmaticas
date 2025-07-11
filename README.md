# MentesMáticas 🎲

Bem-vindo ao MentesMáticas! Este documento contém todas as instruções necessárias para configurar e jogar o jogo.

## Sobre o Jogo

MentesMáticas é um jogo multiplayer em rede onde os jogadores desafiam suas habilidades matemáticas em rodadas de perguntas. O objetivo é acertar o maior número de respostas e vencer a disputa, tornando a experiência competitiva e educativa ao mesmo tempo.

## Pré-requisitos

* **Java Development Kit (JDK):** É necessário ter o Java instalado em sua máquina para executar o jogo. Versão 17 ou superior é recomendada.

## Configuração do Projeto

Para que o jogo funcione, existe apenas um passo de configuração essencial:

1.  **Arquivo de Perguntas:** Certifique-se de que o arquivo `questoes.xml` está localizado na **pasta raiz do projeto**. Ele deve estar no mesmo nível que a pasta `src` e este arquivo `README.md`. O servidor precisa deste arquivo para carregar as perguntas da partida.

## Como Jogar (Passo a Passo)

O jogo utiliza uma arquitetura Cliente-Servidor. Isso significa que precisamos executar dois programas diferentes: o `Servidor` (que gerencia o jogo) e o `Cliente` (a interface para cada jogador).

### Passo 1: Iniciar o Servidor

O servidor é o cérebro do jogo e deve ser iniciado primeiro. Ele não possui interface gráfica.

1.  No seu ambiente de desenvolvimento (VS Code, Eclipse, etc.), encontre e execute a classe `br.edu.ifsp.arq.network.Servidor.java`.
2.  Um console irá aparecer com a mensagem `Servidor MentesMáticas iniciado...` e em seguida `Aguardando 2 jogadores para uma nova partida...`.
3.  **Deixe este console aberto.** Ele precisa ficar rodando durante toda a partida.

### Passo 2: Iniciar os Clientes (Jogadores)

Agora, vamos iniciar a aplicação com a interface gráfica para cada jogador.

1.  Encontre e execute a classe `br.edu.ifsp.arq.App.java`.
2.  Uma janela de login do jogo aparecerá.
3.  **Repita este passo para o segundo jogador.** Execute `App.java` uma segunda vez. Outra janela de login, independente da primeira, irá aparecer.

Neste ponto, você terá 3 programas rodando: 1 console do Servidor e 2 janelas de Login do jogo.

### Passo 3: Conectar e Jogar

1.  Em cada uma das janelas de login, digite um nome de jogador **diferente**.
2.  Clique no botão **"Conectar ao Jogo"** em ambas as janelas.
3.  Assim que os dois jogadores se conectarem, as janelas de login desaparecerão e as janelas principais do jogo aparecerão para cada jogador.
4.  A partida começará! A ordem dos turnos é definida por ordem alfabética de acordo com os nomes inseridos.
5.  **Jogando:**
    * As perguntas e o log da partida aparecerão na área de texto principal.
    * Quando for a sua vez, você receberá o prompt **"Sua vez, [Seu Nome]! Resposta:"**. Digite a letra da alternativa correta no campo de texto na parte inferior e clique em "Enviar" ou pressione Enter.
    * Quando for a vez do seu oponente, você verá uma mensagem para aguardar.
6.  A partida continua rodada por rodada até a última questão. No final, o grande vencedor (ou o empate) será anunciado.

Divirta-se!