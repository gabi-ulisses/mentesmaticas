package br.edu.ifsp.arq.model.mensagens;

// Mensagem para enviar textos simples (avisos, status, etc)
public class MensagemStatus implements Mensagem {
    private final String texto;

    public MensagemStatus(String texto) {
        this.texto = texto;
    }
    
    public String getTexto() { 
        return texto; 
    }
}