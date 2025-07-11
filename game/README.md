# MentesMáticas

MentesMáticas é um jogo multiplayer em rede onde os jogadores desafiam suas habilidades matemáticas em rodadas de perguntas. O objetivo é acertar o maior número de respostas dentro de um tempo limite e vencer a disputa, tornando a experiência competitiva e educativa ao mesmo tempo.

Este projeto foi desenvolvido como atividade acadêmica para os cursos de Tecnologia em Sistemas para Internet.

## Tecnologias e Requisitos

* **Linguagem:** Java
* **Interface Gráfica:** Java Swing (Opcional)
* **Persistência de Dados:** XML
* **Comunicação em Rede:** Sockets Java (TCP/IP)
* **Concorrência:** Threads
* **Controle de Versão:** Git / GitHub

## Estrutura do Projeto

O projeto segue padrões de arquitetura de software modernos e robustos, como o **MVC (Model-View-Controller)** e o **DAO (Data Access Object)**, para garantir uma boa organização, manutenibilidade e separação de responsabilidades.

A estrutura de pacotes é a seguinte:

* `src/br/edu/ifsp/arq/model`
    * **O quê?** O coração do jogo. Contém as classes que representam os dados e as regras de negócio.
    * **Classes:** `Jogador.java`, `Questao.java`, `Partida.java`.

* `src/br/edu/ifsp/arq/view`
    * **O quê?** A camada de apresentação. Tudo que o usuário vê e interage diretamente.
    * **Classes:** `TelaInicial.java`, `TelaJogo.java` (janelas, painéis e botões em Swing).

* `src/br/edu/ifsp/arq/controller`
    * **O quê?** O cérebro da aplicação. Atua como intermediário, recebendo as ações do usuário da `view` e acionando as regras de negócio na `model`.
    * **Classes:** `JogoController.java`.

* `src/br/edu/ifsp/arq/dao`
    * **O quê?** Camada de Acesso a Dados. Responsável por isolar a lógica de persistência (leitura e escrita de arquivos).
    * **Classes:** `QuestaoDAO.java` (para ler as perguntas do arquivo XML).

* `src/br/edu/ifsp/arq/network`
    * **O quê?** A camada de rede. Gerencia toda a comunicação entre o cliente e o servidor.
    * **Classes:** `Servidor.java`, `Cliente.java`.

## Checklist de Desenvolvimento

Este checklist serve como um roteiro para o desenvolvimento do projeto, dividido nos módulos que planejamos. Marquem as tarefas conforme forem concluídas!

### Módulo 1: Lógica e Estrutura Base
- [X] Criar a classe `model.Questao` com seus atributos (enunciado, opções, resposta).
- [X] Criar a classe `model.Jogador` com seus atributos (nome, pontuação).
- [X] Criar a classe `model.Partida` para gerenciar a lógica do jogo (lista de jogadores, lista de questões, etc.).
- [X] **Marco:** Fazer uma versão simples do jogo funcionar no console, sem rede, para testar a lógica principal.

### Módulo 2: Persistência de Dados com XML
- [X] Definir a estrutura do arquivo `questoes.xml`.
- [X] Criar a classe `dao.QuestaoDAO` com um método para ler o XML e retornar uma `List<Questao>`.
- [X] Integrar o DAO na classe `Partida` para carregar as perguntas do arquivo em vez de criá-las no código.
- [ ] (Opcional) Criar um DAO para salvar as pontuações mais altas em um arquivo `scores.xml`.

### Módulo 3: Comunicação em Rede (Cliente-Servidor)
- [X] Desenvolver a classe `network.Servidor` para conseguir aceitar uma conexão de cliente via Socket.
- [X] Desenvolver a classe `network.Cliente` para conseguir se conectar ao servidor.
- [X] Definir um protocolo de comunicação simples (ex: "CONECTAR;NOME_JOGADOR", "PERGUNTA;5*8", "RESPOSTA;40").
- [X] Fazer o servidor enviar uma pergunta de teste para o cliente conectado.
- [X] Fazer o cliente enviar uma resposta de teste para o servidor.
- [X] Implementar **Threads** no `Servidor` para que ele possa lidar com múltiplos clientes simultaneamente.

### Módulo 4: Funcionalidades do Jogo e Concorrência
- [X] Implementar a lógica de rodadas e turnos no servidor.
    - [X] Refatorar a classe Partida para ser um Runnable que gerencia uma sessão de jogo em rede.
    - [X] Criar uma fila segura (FilaDeRespostas) para gerenciar respostas concorrentes dos jogadores.
    - [X] Implementar o padrão Produtor-Consumidor com threads "Ouvintes" para receber as respostas.
- [ ] Implementar o temporizador de resposta (usando Threads).
- [X] Fazer o servidor transmitir o placar atualizado para todos os clientes.
- [ ] Implementar as funções de Início e Reinício de partida.

### Módulo 5: Interface Gráfica com Java Swing (Opcional)
- [X] Desenvolver a `view.TelaInicial` para o jogador inserir o IP do servidor e seu nome.
- [X] Desenvolver a `view.TelaJogo` para exibir a pergunta, as opções, o tempo restante e o placar.
- [X] Implementar o `controller.JogoController` para gerenciar os eventos das telas e se comunicar com a camada `network.Cliente`.
- [X] **Marco:** Substituir toda a interação de console pela interface gráfica.
