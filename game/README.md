# MentesMáticas

MentesMáticas é um jogo multiplayer em rede onde os jogadores desafiam suas habilidades matemáticas em rodadas de perguntas. O objetivo é acertar o maior número de respostas dentro de um tempo limite e vencer a disputa, tornando a experiência competitiva e educativa ao mesmo tempo.

Este projeto foi desenvolvido como projeto final da disciplina de "Desenvolvimento Orientado a Objetos", durante o curso superior de Tecnologia em Sistemas para Internet.

## Tecnologias e Requisitos

* **Linguagem:** Java
* **Interface Gráfica:** Java Swing
* **Persistência de Dados:** XML
* **Comunicação em Rede:** Sockets Java (TCP/IP) e Serialização de Objetos
* **Concorrência:** Threads e Sincronização (`wait`/`notify`)
* **Controle de Versão:** Git / GitHub

## Estrutura do Projeto

O projeto segue o padrão de arquitetura **MVC (Model-View-Controller)** para o lado do cliente e uma arquitetura de servidor autoritário.

A estrutura de pacotes é a seguinte:

* `br.edu.ifsp.arq.model`: Contém as classes que representam os dados e as regras de negócio do jogo (`Partida`, `Jogador`, `Questao`, `RespostaJogador`).
    * `br.edu.ifsp.arq.model.mensagens`: Contém as classes do protocolo de comunicação orientado a objetos, que são serializadas e trocadas entre cliente e servidor (`Mensagem`, `MensagemControle`, `MensagemPergunta`, `MensagemPlacar`, `MensagemStatus`).
* `br.edu.ifsp.arq.dao`: Isola a lógica de acesso a dados, lendo as perguntas do arquivo `questoes.xml` (`QuestaoDAO`).
* `br.edu.ifsp.arq.network`: Gerencia a comunicação em rede (`Servidor`, `Cliente`) e a configuração de conexão (`Config`).
* `br.edu.ifsp.arq.view`: Contém as classes da interface gráfica (`Tela`, `TelaInicial`, `TelaJogo`).
    * `br.edu.ifsp.arq.view.componentes`: Contém componentes Swing customizados para a estilização da interface (`JPanelArredondado`, `JLabelArredondado`, etc.).
* `br.edu.ifsp.arq.controller`: Contém o "cérebro" da aplicação cliente, que conecta a View e a camada de Rede (`JogoController`).
* `br.edu.ifsp.arq`: Contém o ponto de entrada da aplicação cliente (`App.java`).

## Checklist de Desenvolvimento

### Módulo 1: Lógica e Estrutura Base
- [X] Criar a classe `model.Questao`.
- [X] Criar a classe `model.Jogador`.
- [X] Criar a classe `model.Partida`.
- [X] **Marco:** Jogo funcional no console.

### Módulo 2: Persistência de Dados com XML
- [X] Definir a estrutura do `questoes.xml`.
- [X] Criar a classe `dao.QuestaoDAO`.
- [X] Integrar o DAO na `Partida`.

### Módulo 3: Comunicação em Rede (Cliente-Servidor)
- [X] Desenvolver a classe `network.Servidor` para aceitar conexões.
- [X] Desenvolver a classe `network.Cliente`.
- [X] Interligar o Servidor com a classe Partida
- [X] Implementar `Threads` no Servidor.

### Módulo 4: Funcionalidades do Jogo e Concorrência
- [X] Implementar a lógica de rodadas e turnos no servidor.
- [X] Implementar o temporizador de resposta.
- [X] Fazer o servidor transmitir o placar atualizado.
- [X] Implementar as funções de Início e Reinício de partida.

### Módulo 5: Interface Gráfica com Java Swing
- [X] Desenvolver a `view.TelaInicial`.
- [X] Desenvolver a `view.TelaJogo`.
- [X] Implementar o `controller.JogoController`.
- [X] **Marco:** Substituir a interação de console pela interface gráfica.