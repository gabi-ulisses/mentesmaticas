# MentesMáticas

MentesMáticas é um jogo multiplayer em rede onde os jogadores desafiam suas habilidades matemáticas em rodadas de perguntas. O objetivo é acertar o maior número de respostas dentro de um tempo limite e vencer a disputa, tornando a experiência competitiva e educativa ao mesmo tempo.

Este projeto foi desenvolvido como atividade acadêmica para os cursos de Tecnologia em Sistemas para Internet e Análise e Desenvolvimento de Sistemas.

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