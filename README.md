# MentesMáticas 🎲

Bem-vindo ao MentesMáticas\! Este documento contém todas as instruções necessárias para configurar e jogar o jogo.

## Sobre o Jogo

MentesMáticas é um jogo multiplayer em rede onde os jogadores desafiam suas habilidades matemáticas em rodadas de perguntas. O objetivo é acertar o maior número de respostas e vencer a disputa, tornando a experiência competitiva e educativa ao mesmo tempo.

## Pré-requisitos

  * **Java Development Kit (JDK):** É necessário ter o Java instalado em sua máquina para executar o jogo. Versão 17 ou superior é recomendada.

## Configuração do Projeto

Antes de jogar, são necessários dois arquivos de configuração na **pasta raiz do projeto** (junto com a pasta `src`):

1.  **`questoes.xml`:** Contém as perguntas e respostas usadas no jogo.
2.  **`config.xml`:** Contém as configurações de rede (IP e Porta) para a conexão.

### Jogando no Mesmo Computador (Local)

Para testar o jogo sozinho ou com duas instâncias no mesmo PC, use esta configuração no seu `config.xml`. Com este IP, a comunicação nunca sai da sua própria máquina.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<mentesmaticas>
    <servidor ip="127.0.0.1" porta="12345" />
</mentesmaticas>
```

### Jogando em Computadores Diferentes (Rede Local/LAN)

Para jogar com um amigo em outra máquina na mesma rede (ex: mesmo Wi-Fi).

**O Conceito Principal:** Um jogador será o **Anfitrião (Servidor)**. Os outros jogadores serão os **Convidados (Clientes)**. Todos os convidados precisam saber o "endereço" do anfitrião para se conectar.

**Passo a Passo:**

1.  **O Anfitrião Descobre seu IP:** A pessoa que irá rodar o `Servidor.java` precisa descobrir o seu endereço IP na rede local.

      * **No Windows:** Abra o `cmd` e digite `ipconfig`. Procure pelo **"Endereço IPv4"** (geralmente começa com `192.168...`).
      * **No macOS/Linux:** Abra o terminal e digite `hostname -I` ou `ip addr`.

2.  **O Anfitrião compartilha o IP:** O anfitrião informa o seu endereço IP (ex: `192.168.1.7`) para todos os outros jogadores.

3.  **Todos os Jogadores Configuram o `config.xml`:** Agora, **todos os jogadores** (incluindo o anfitrião que também vai jogar) devem abrir seus arquivos `config.xml` e colocar o **mesmo IP** que o anfitrião compartilhou.

    **Exemplo (para TODOS os jogadores, se o IP do anfitrião for `192.168.1.5`):**

    ```xml
    <servidor ip="192.168.1.5" porta="12345" />
    ```

    *(**Nota para o anfitrião:** Você pode manter `ip="0.0.0.0"` no seu `config.xml` do Servidor, mas para o seu próprio Cliente, é mais simples usar o mesmo IP que passou para os outros).*

## Como Jogar

### Passo 1: O Anfitrião Inicia o Servidor

1.  Com o `config.xml` devidamente configurado, a pessoa que é o anfitrião deve executar a classe `br.edu.ifsp.arq.network.Servidor.java`.
2.  Um console irá aparecer. **Deixe este console aberto.**

### Passo 2: Todos os Jogadores Iniciam seus Clientes

1.  Com o `config.xml` apontando para o IP do anfitrião, cada jogador deve executar a classe `br.edu.ifsp.arq.App.java`.
2.  Uma janela de login aparecerá para cada um.

### Passo 3: Conectar e Jogar

1.  Cada jogador digita seu nome e clica em "Conectar".
2.  Assim que todos se conectarem, a partida começará\!

Divirta-se\!