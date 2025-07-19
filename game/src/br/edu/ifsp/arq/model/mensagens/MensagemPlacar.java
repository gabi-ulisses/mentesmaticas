package br.edu.ifsp.arq.model.mensagens;

import java.util.Map;

// Mensagem para enviar o placar atualizado.
public class MensagemPlacar implements Mensagem {
    private final Map<String, Integer> placares;

    public MensagemPlacar(Map<String, Integer> placares) {
        this.placares = placares;
    }
    
    public Map<String, Integer> getPlacares() { 
        return placares; 
    }
}