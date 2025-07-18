package br.edu.ifsp.arq.controller;

import br.edu.ifsp.arq.network.Cliente;
import br.edu.ifsp.arq.view.TelaInicial;
import br.edu.ifsp.arq.view.TelaJogo;
import javax.swing.JOptionPane;

/**
 * Controla a lógica da aplicação do lado do jogador.
 * Serve como uma ponte entre as telas (View) e a conexão de rede (Cliente).
 */
public class JogoController {
    private TelaInicial telaInicial;
    private TelaJogo telaJogo;
    private Cliente cliente;

    // O construtor cria as telas e passa a si mesmo (this) como referência para elas.
    // Isso permite que as telas chamem os métodos do controller (ex: quando um botão é clicado).
    public JogoController(){
        this.telaInicial = new TelaInicial(this);
        this.telaJogo = new TelaJogo(this);
    }

    // Método que inicia a aplicação, chamado pelo App.java.
    // A única função dele é mostrar a primeira tela.
    public void iniciar() {
        telaInicial.mostrar();
    }

    // Este método é chamado pela TelaInicial quando o jogador clica em "Conectar".
    public void conectar(String nomeJogador) {
        // Verifica se o jogador digitou um nome.
        if (nomeJogador == null || nomeJogador.trim().isEmpty()) {
            telaInicial.mostrarAviso("O nome do jogador não pode estar vazio.");
            return;
        }
        
        this.cliente = new Cliente(this);
        boolean conectado = cliente.conectar();
        
        // Se a conexão foi bem-sucedida, esconde a tela de login e mostra a tela do jogo.
        if (conectado) {
            telaInicial.esconder();
            telaJogo.mostrar();
            telaJogo.setTitulo("MentesMáticas - " + nomeJogador);
            enviarResposta(nomeJogador); // Envia o nome para o servidor
        } else {
            // Se não conseguiu conectar, exibe uma mensagem de erro.
            telaInicial.mostrarAviso("Não foi possível conectar ao servidor.");
        }
    }

    // Este método é chamado pela TelaJogo quando o jogador clica em "Enviar".
    public void enviarResposta(String texto) {
        // Verifica se o cliente existe e se ainda está conectado.
        if (cliente != null && cliente.isConectado()) {
            cliente.enviarMensagem(texto);
            telaJogo.limparCampoResposta();
        }
    }
    /**
     * Processa as mensagens que chegam do servidor.
     * Este método é chamado pela thread "Ouvinte" da classe Cliente.
     */    
    public void processarMensagemDoServidor(String mensagem) {
        // Lógica para decidir o que fazer com base no início da mensagem (protocolo).
        if (mensagem.startsWith("CONTROL|")) {
            String comando = mensagem.substring(8);
            if (comando.equals("PLAY_AGAIN")) {
                int escolha = telaJogo.mostrarDialogoReiniciar();
                if (escolha == JOptionPane.YES_OPTION) {
                    cliente.enviarMensagem("CMD|RESTART_YES");
                    telaJogo.limparLog();
                    telaJogo.adicionarLog("Aguardando o outro jogador decidir...", true);
                } else {
                    cliente.enviarMensagem("CMD|RESTART_NO");
                }
            } else if (comando.equals("SESSION_END")) {
                notificarDesconexao();
            }
        } else if (mensagem.startsWith("PROMPT|")) {
            // Se for um prompt, remove o prefixo e manda a tela imprimir sem pular linha.
            String prompt = mensagem.substring(7);
            telaJogo.adicionarLog(prompt, false);
        } else if (mensagem.startsWith("TIMER|")) {
            // Se for do timer, remove o prefixo e manda a tela atualizar o contador.
            String tempo = mensagem.substring(6);
            telaJogo.atualizarContador(tempo);
        } else {
            // Se for uma mensagem normal, manda a tela imprimir com quebra de linha.
            telaJogo.adicionarLog(mensagem, true);
        }
    }
    
    public void notificarDesconexao() {
        telaJogo.mostrarAviso("A conexão com o servidor foi perdida. O jogo será fechado.");
            // Se for uma mensagem normal, manda a tela imprimir com quebra de linha.
        telaJogo.desabilitarInteracao();
        
        // Usa um timer do Swing para fechar a aplicação após 2 segundos.
        // Isso dá tempo para o jogador ler a mensagem de aviso.
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> System.exit(0));
        timer.setRepeats(false); // Garante que execute apenas uma vez
        timer.start();
    }
}
