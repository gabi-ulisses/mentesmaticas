package br.edu.ifsp.arq.network;

import br.edu.ifsp.arq.model.Partida;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static final int PORTA = 12345;
    public static final String IP = "127.0.0.1"; 

    public static void main(String[] args) {
        try {
            InetAddress endereco = InetAddress.getByName(IP);
            
            try (ServerSocket servidor = new ServerSocket(Config.getPorta(), 2, InetAddress.getByName(Config.getIp()))) {

                System.out.println("Servidor MentesMáticas iniciado em: " + servidor.getInetAddress().getHostAddress() + ":" + PORTA);

                while (true) {
                    System.out.println("\nAguardando 2 jogadores para uma nova partida...");
                    
                    Socket j1 = servidor.accept();
                    System.out.println("Jogador 1 conectado: " + j1.getInetAddress().getHostAddress());
                    ObjectOutputStream saida1 = new ObjectOutputStream(j1.getOutputStream());
                    ObjectInputStream entrada1 = new ObjectInputStream(j1.getInputStream());

                    Socket j2 = servidor.accept();
                    System.out.println("Jogador 2 conectado: " + j2.getInetAddress().getHostAddress());
                    ObjectOutputStream saida2 = new ObjectOutputStream(j2.getOutputStream());
                    ObjectInputStream entrada2 = new ObjectInputStream(j2.getInputStream());
                    
                    Partida partida = new Partida(entrada1, saida1, entrada2, saida2);
                    
                    new Thread(partida).start();
                    System.out.println("Partida iniciada!");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar ou executar o servidor: " + e.getMessage());
        }
    }
}
