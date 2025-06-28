package br.edu.ifsp.arq.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        String ipServidor = Servidor.IP;
        int portaServidor = Servidor.PORTA;

        // O bloco try-with-resources fecha automaticamente 'conexao', 'saida', 'entrada' e 'scanner'
        try (
            Socket conexao = new Socket(InetAddress.getByName(ipServidor), portaServidor);
            ObjectOutputStream saida = new ObjectOutputStream(conexao.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(conexao.getInputStream());
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Conexão com o servidor " + ipServidor + " realizada com sucesso.");

            Thread threadLeitura = new Thread(() -> {
                try {
                    while (true) {
                        String mensagem = (String) entrada.readObject();
                        System.out.println("Servidor: " + mensagem);
                    }
                // Captura exceções específicas de I/O e fim de conexão
                } catch (ClassNotFoundException | IOException e) {
                    System.out.println("\nConexão com o servidor foi encerrada.");
                }
            });
            threadLeitura.start();
            
            // Loop para enviar dados do cliente
            while (threadLeitura.isAlive()) {
                if (scanner.hasNextLine()) {
                    String linha = scanner.nextLine();
                    saida.writeObject(linha);
                    saida.flush();
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Endereço de IP do servidor não encontrado: " + ipServidor);
        } catch (IOException e) {
            System.err.println("Não foi possível conectar ao servidor: " + e.getMessage());
        }
    }
}
