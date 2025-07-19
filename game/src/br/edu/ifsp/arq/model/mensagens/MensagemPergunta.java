package br.edu.ifsp.arq.model.mensagens;

import java.util.List;

// Mensagem para enviar uma nova pergunta e suas opções.
public class MensagemPergunta implements Mensagem {
    private final String enunciado;
    private final List<String> opcoes;

    public MensagemPergunta(String enunciado, List<String> opcoes) {
        this.enunciado = enunciado;
        this.opcoes = opcoes;
    }

    public String getEnunciado() { 
        return enunciado; 
    }
    public List<String> getOpcoes() { 
        return opcoes; 
    }
}