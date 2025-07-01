package br.edu.ifsp.arq.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Fila  para gerenciar as respostas dos jogadores, utilizando LinkedList, synchronized, wait() e notifyAll().
 */
public class FilaDeRespostas {

    private List<RespostaJogador> fila = new LinkedList<>();

   
    public synchronized void adicionar(RespostaJogador resposta) {
        this.fila.add(resposta);
        // Acorda a thread da Partida, caso ela esteja dormindo esperando por uma resposta.
        this.notifyAll(); 
    }

   
    public synchronized RespostaJogador remover() {
        // O loop 'while' é crucial para se proteger contra "despertares espúrios"
        while (this.fila.isEmpty()) {
            try {
                // Se a fila está vazia, libera a trava e põe a thread para "dormir".
                this.wait(); 
            } catch (InterruptedException e) {
                // Se a thread for interrompida, restaura o estado de interrupção.
                Thread.currentThread().interrupt(); 
                return null;
            }
        }
        return ((LinkedList<RespostaJogador>) this.fila).removeFirst();
    }
    
    public synchronized void limpar() {
        this.fila.clear();
    }
}