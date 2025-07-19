package br.edu.ifsp.arq.network;

import br.edu.ifsp.arq.controller.JogoController;
import br.edu.ifsp.arq.model.mensagens.Mensagem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Cuida da conexão de rede e da comunicação com o servidor.
 * Reporta todos os eventos (mensagens, desconexões) para o JogoController.
 */
public class Cliente {
    private JogoController controller;
    private Socket conexao;
    private ObjectOutputStream saida;
    private ObjectInputStream entrada;

    // O construtor recebe o controller para poder se comunicar com ele.
    public Cliente(JogoController controller) {
        this.controller = controller;
    }

    public boolean conectar() {
        try {
            conexao = new Socket(InetAddress.getByName(Config.getIp()), Config.getPorta());
            saida = new ObjectOutputStream(conexao.getOutputStream());
            entrada = new ObjectInputStream(conexao.getInputStream());
            
            // Inicia uma thread separada que ficará apenas ouvindo as mensagens do servidor.
            iniciarOuvinte();
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
    * Cria e inicia uma thread para não travar a interface gráfica do usuário.
    * Recebe qualquer objeto do tipo Mensagem.
    */
    private void iniciarOuvinte() {
        new Thread(() -> {
            try {
                while (isConectado()) {
                    // Lê um Objeto genérico que chega do servidor.
                    Object objetoDoServidor = entrada.readObject();
                    
                    // Verifica se o objeto é do tipo Mensagem.
                    if (objetoDoServidor instanceof Mensagem) {
                        //  Se for, entrega o objeto diretamente para o controller, que saberá o que fazer.
                        controller.processarMensagemDoServidor((Mensagem) objetoDoServidor);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                // Se a conexão cair, o ouvinte avisa o controller.
                // Usamos a versão do controller que recebe uma mensagem de aviso.
                controller.notificarDesconexao("A conexão com o servidor foi perdida.");
            }
        }).start();
    }

    // Método que o controller usa para enviar uma mensagem para o servidor.
    public void enviarMensagem(String mensagem) {
        try {
            saida.writeObject(mensagem);
            saida.flush();
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            controller.notificarDesconexao("Não foi possível enviar dados para o servidor.");
        }
    }
    
    // Método para verificar se a conexão ainda está ativa e aberta.
    public boolean isConectado() {
        return conexao != null && !conexao.isClosed();
    }
}