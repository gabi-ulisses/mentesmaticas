# MentesMáticas 🎲

Bem-vindo ao MentesMáticas! Este documento contém todas as instruções necessárias para configurar e jogar o jogo.

### Desenvolvido por: 
[![Ana Beatriz Rocha Duarte](https://github.com/AnaDuarte1.png?size=50)](https://github.com/AnaDuarte1) [![Gabrielle Ulisses](https://github.com/gabi-ulisses.png?size=50)](https://github.com/gabi-ulisses) 

*Projeto final da disciplina de "Desenvolvimento Orientado a Objetos",*
*do curso de **Tecnologia em Sistemas para Internet** do **IFSP - Câmpus Araraquara** *


## 📖 Sobre o Jogo

MentesMáticas é um jogo multiplayer em rede onde os jogadores desafiam suas habilidades matemáticas em rodadas de perguntas. O objetivo é acertar o maior número de respostas e vencer a disputa, tornando a experiência competitiva e educativa ao mesmo tempo.

## 🖥️ Pré-requisitos

* **Java Development Kit (JDK):** É necessário ter o Java instalado em sua máquina para executar o jogo. Versão 17 ou superior é recomendada.

## ⚙️ Configuração do Projeto

Antes de jogar, são necessários dois arquivos de configuração na **pasta raiz do projeto** (junto com a pasta `src`):

1.  `questoes.xml`: Contém as perguntas e respostas usadas no jogo.
2.  `config.xml`: Contém as configurações de rede (IP e Porta) para a conexão.

### Estrutura dos Arquivos de Configuração

**`config.xml`**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<mentesmaticas>
    <servidor ip="127.0.0.1" porta="12345" />
</mentesmaticas>
````

**`questoes.xml` (exemplo de estrutura)**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<questoes>
    <questao enunciado="Quanto é 2+2?" respostaCorreta="1">
        <opcao>3</opcao>
        <opcao>4</opcao>
        <opcao>5</opcao>
    </questao>
    <questao enunciado="Qual a raiz quadrada de 81?" respostaCorreta="2">
        <opcao>7</opcao>
        <opcao>8</opcao>
        <opcao>9</opcao>
    </questao>
    </questoes>
```

## 🌐 Modos de Jogo

### Jogando no Mesmo Computador (Local)

Para testar o jogo sozinho, você pode rodar o servidor e dois clientes na mesma máquina. Para isso, use o IP `127.0.0.1` no seu `config.xml`.

### Jogando em Computadores Diferentes (Rede Local/LAN)

Um jogador será o **Anfitrião (Servidor)** e os outros serão os **Convidados (Clientes)**.

**Passo a Passo:**

1.  **O Anfitrião Descobre seu IP:** A pessoa que irá rodar o `Servidor.java` precisa descobrir o seu endereço IP na rede local.

      * **No Windows:** Abra o `Prompt de Comando (cmd)` e digite `ipconfig`. Procure pelo **"Endereço IPv4"** (geralmente começa com `192.168...`).
      * **No macOS/Linux:** Abra o `Terminal` e digite `hostname -I` ou `ip addr`.

2.  **O Anfitrião compartilha o IP:** O anfitrião informa o seu endereço IP (ex: `192.168.1.7`) para o outro jogador.

3.  **Todos os Jogadores Configuram o `config.xml`:** Agora, **todos os jogadores** devem abrir seus arquivos `config.xml` e colocar o **mesmo IP** que o anfitrião compartilhou.

## 🚀 Como Jogar

### Passo 1: O Anfitrião Inicia o Servidor

1.  Com o `config.xml` devidamente configurado, o **Anfitrião** deve executar a classe `br.edu.ifsp.arq.network.Servidor.java`.
2.  Um console irá aparecer com a mensagem "Servidor MentesMáticas iniciado...". **Deixe este console aberto durante todo o jogo.**

### Passo 2: Todos os Jogadores Iniciam seus Clientes

1.  Com o `config.xml` de todos apontando para o IP do anfitrião, cada jogador deve executar a classe `br.edu.ifsp.arq.App.java`.
2.  A janela de login do MentesMáticas aparecerá para cada um.

### Passo 3: Conectar e Jogar

1.  Cada jogador digita seu nome e clica em "Conectar".
2.  Assim que o segundo jogador se conectar, a partida começará automaticamente para ambos\!

**Divirta-se\!**

## 💡 Dicas e Solução de Problemas (Troubleshooting)

  * **Não consigo conectar\!**
      * Verifique se o IP no `config.xml` está **exatamente** igual ao IP do anfitrião.
      * Certifique-se de que o **Servidor foi iniciado antes** dos clientes.
      * **FIREWALL:** O Firewall do Windows ou de algum antivírus pode estar bloqueando a conexão. O anfitrião pode precisar criar uma "exceção" para o Java ou desativar temporariamente o firewall para permitir que os outros jogadores se conectem. Este é o problema mais comum\!
  * **O jogo fechou inesperadamente.**
      * Isso geralmente acontece se a janela do console do Servidor for fechada ou se a conexão de rede de um dos jogadores cair.