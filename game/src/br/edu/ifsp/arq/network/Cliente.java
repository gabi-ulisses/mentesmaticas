package br.edu.ifsp.arq.network;

import br.edu.ifsp.arq.controller.JogoController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Cuida da conexão de rede e da comunicação com o servidor.
 * Esta classe é a ponte entre a aplicação do jogador e o servidor do jogo.
 * Reporta todos os eventos (mensagens, desconexões) para o JogoController.
 */
public class Cliente {
    // Uma referência para o "cérebro" do cliente (o controller), para que possamos notificá-lo.
    private JogoController controller;
    // O Socket representa a conexão direta com o servidor.
    private Socket conexao;
    // Fluxo de dados para ENVIAR objetos/mensagens para o servidor.
    private ObjectOutputStream saida;
    // Fluxo de dados para RECEBER objetos/mensagens do servidor.
    private ObjectInputStream entrada;

    // O construtor recebe o controller para poder se comunicar com ele.
    public Cliente(JogoController controller) {
        this.controller = controller;
    }

    // Tenta estabelecer a conexão com o servidor.
    public boolean conectar() {
        try {
            // Cria a conexão (Socket) com o endereço lido do arquivo de configuração.
            conexao = new Socket(InetAddress.getByName(Config.getIp()), Config.getPorta());
            // Prepara o canal para enviar dados.
            saida = new ObjectOutputStream(conexao.getOutputStream());
            // Prepara o canal para receber dados.
            entrada = new ObjectInputStream(conexao.getInputStream());
            
            // Inicia uma thread separada que ficará apenas ouvindo as mensagens do servidor.
            iniciarOuvinte();
            
            return true;
        } catch (IOException e) {
            // Se deu algum erro (ex: servidor não iniciado), imprime o erro e retorna false.
            e.printStackTrace();
            return false;
        }
    }

    // Cria e inicia uma thread para não travar a interface gráfica do usuário.
    // A função desta thread é ficar em um loop, esperando por mensagens do servidor.
    private void iniciarOuvinte() {
        // Cria uma nova thread. 
        new Thread(() -> {
            try {
                // Fica em um loop infinito enquanto a conexão estiver ativa.
                while (isConectado()) {
                    // O código fica "bloqueado" nesta linha até uma mensagem do servidor chegar.
                    String mensagem = (String) entrada.readObject();
                    // Quando a mensagem chega, avisa o controller para que ele possa processá-la.
                    controller.processarMensagemDoServidor(mensagem);
                }
            } catch (IOException | ClassNotFoundException e) {
                // Se a conexão cair (ex: o servidor fechou), o ouvinte avisa o controller.
                controller.notificarDesconexao();
            }
        }).start();
    }

    // Método que o controller usa para enviar uma mensagem para o servidor.
    public void enviarMensagem(String mensagem) {
        try {
            // Escreve o objeto da mensagem no fluxo de saída.
            saida.writeObject(mensagem);
            // Garante que a mensagem seja enviada imediatamente pela rede.
            saida.flush();
        } catch (IOException e) {
            // Se ocorrer um erro ao enviar, provavelmente a conexão foi perdida.
            e.printStackTrace();
            controller.notificarDesconexao();
        }
    }
    // Um método simples para verificar se a conexão ainda está ativa e aberta.
    public boolean isConectado() {
        return conexao != null && !conexao.isClosed();
    }
}