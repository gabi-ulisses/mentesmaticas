package br.edu.ifsp.arq.controller;

import br.edu.ifsp.arq.network.Cliente;
import br.edu.ifsp.arq.view.TelaInicial;
import br.edu.ifsp.arq.view.TelaJogo;
import javax.swing.JOptionPane;

/**
 * Controller (no padrão MVC).
 * Orquestra a interação entre a View (as telas) e o Model (a lógica de rede do Cliente).
 */
public class JogoController {
    private TelaInicial telaInicial;
    private TelaJogo telaJogo;
    private Cliente cliente;

    public JogoController(){
        this.telaInicial = new TelaInicial(this);
        this.telaJogo = new TelaJogo(this);
    }

    public void iniciar() {
        telaInicial.mostrar();
    }

    public void conectar(String nomeJogador) {
        if (nomeJogador == null || nomeJogador.trim().isEmpty()) {
            telaInicial.mostrarAviso("O nome do jogador não pode estar vazio.");
            return;
        }
        
        this.cliente = new Cliente(this);
        boolean conectado = cliente.conectar();
        
        if (conectado) {
            telaInicial.esconder();
            telaJogo.mostrar();
            telaJogo.setTitulo("MentesMáticas - " + nomeJogador);
            enviarResposta(nomeJogador); // Envia o nome para o servidor
        } else {
            telaInicial.mostrarAviso("Não foi possível conectar ao servidor.");
        }
    }

    public void enviarResposta(String texto) {
        if (cliente != null && cliente.isConectado()) {
            cliente.enviarMensagem(texto);
            if (telaJogo != null) {
                telaJogo.limparCampoResposta();
            }
        }
    }
    
    public void processarMensagemDoServidor(String mensagem) {
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
            String prompt = mensagem.substring(7);
            telaJogo.adicionarLog(prompt, false);
        } else if (mensagem.startsWith("TIMER|")) {
            String tempo = mensagem.substring(6);
            telaJogo.atualizarContador(tempo);
        } else {
            telaJogo.adicionarLog(mensagem, true);
        }
    }
    
    public void notificarDesconexao() {
        telaJogo.mostrarAviso("A conexão com o servidor foi perdida. O jogo será fechado.");
        telaJogo.desabilitarInteracao();
        
        // CORREÇÃO: Usando javax.swing.Timer explicitamente para evitar ambiguidade.
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> System.exit(0));
        timer.setRepeats(false); // Garante que execute apenas uma vez
        timer.start();
    }
}
