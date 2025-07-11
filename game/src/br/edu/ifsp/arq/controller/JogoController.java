package br.edu.ifsp.arq.controller;

import br.edu.ifsp.arq.network.Cliente;
import br.edu.ifsp.arq.view.TelaInicial;
import br.edu.ifsp.arq.view.TelaJogo;

/**
 * Controller (no padrão MVC).
 * Orquestra a interação entre a View (as telas) e o Model (a lógica de rede do Cliente).
 */
public class JogoController {
    private TelaInicial telaInicial;
    private TelaJogo telaJogo;
    private Cliente cliente;

    public JogoController() {
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
        // Verifica se a mensagem tem pelo menos o tamanho do prefixo
        if (mensagem.length() >= 7 && mensagem.substring(0, 7).equals("PROMPT|")) {
            String prompt = mensagem.substring(7);
            telaJogo.adicionarLog(prompt, false);
        } else {
            telaJogo.adicionarLog(mensagem, true);
        }
    }
    
    public void notificarDesconexao() {
        telaJogo.mostrarAviso("A conexão com o servidor foi perdida.");
        telaJogo.desabilitarInteracao();
    }
}