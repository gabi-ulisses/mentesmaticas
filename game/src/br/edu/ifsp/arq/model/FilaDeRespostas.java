package br.edu.ifsp.arq.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Uma "caixa de entrada" segura para as respostas dos jogadores.
 * Várias threads podem colocar respostas aqui ao mesmo tempo
 * sem quebrar o programa.
 */
public class FilaDeRespostas {

    private List<RespostaJogador> fila = new LinkedList<>();

    // só uma thread pode executar este método por vez.
    public synchronized void adicionar(RespostaJogador resposta) {
        // Adiciona a resposta no final da lista.
        this.fila.add(resposta);
        // "Acorda" qualquer thread que esteja esperando por um item na fila.
        this.notifyAll(); 
    }

   
    public synchronized RespostaJogador remover() {
        // O loop 'while' é importante aqui. A thread só sai dele quando a fila não estiver vazia.
        while (this.fila.isEmpty()) {
            try {
                // Se a fila está vazia, a thread "dorme" e espera ser acordada pelo notifyAll().
                this.wait(); 
            } catch (InterruptedException e) {
                // Se a thread for interrompida, marcamos isso e retornamos null.
                Thread.currentThread().interrupt(); 
                return null;
            }
        }
        // Quando a thread acorda e a fila tem itens, remove o primeiro e o retorna.
        return ((LinkedList<RespostaJogador>) this.fila).removeFirst();
    }
    
    public synchronized void limpar() {
        this.fila.clear();
    }
}