package br.edu.ifsp.arq.network;

import br.edu.ifsp.arq.controller.JogoController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Gerencia a conexão de rede e a comunicação com o servidor.
 * Não possui mais interface com o usuário; agora se reporta ao JogoController.
 */
public class Cliente {
    private JogoController controller;
    private Socket conexao;
    private ObjectOutputStream saida;
    private ObjectInputStream entrada;

    public Cliente(JogoController controller) {
        this.controller = controller;
    }

    public boolean conectar() {
        try {
            conexao = new Socket(InetAddress.getByName(Servidor.IP), Servidor.PORTA);
            saida = new ObjectOutputStream(conexao.getOutputStream());
            entrada = new ObjectInputStream(conexao.getInputStream());
            
            // Inicia a thread que fica ouvindo o servidor
            iniciarOuvinte();
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void iniciarOuvinte() {
        new Thread(() -> {
            try {
                while (isConectado()) {
                    String mensagem = (String) entrada.readObject();
                    // Em vez de imprimir, agora notifica o controller
                    controller.processarMensagemDoServidor(mensagem);
                }
            } catch (IOException | ClassNotFoundException e) {
                // Notifica o controller que a conexão foi perdida
                controller.notificarDesconexao();
            }
        }).start();
    }
    
    public void enviarMensagem(String mensagem) {
        try {
            saida.writeObject(mensagem);
            saida.flush();
        } catch (IOException e) {
            e.printStackTrace();
            controller.notificarDesconexao();
        }
    }
    
    public boolean isConectado() {
        return conexao != null && !conexao.isClosed();
    }
}