package br.edu.ifsp.arq.model.mensagens;

// Mensagem para controlar o fluxo do jogo.
public class MensagemControle implements Mensagem {
    
    // Usamos um Enum para definir os comandos de forma segura e clara.
    public enum Tipo {
        INICIAR_JOGO,
        JOGAR_NOVAMENTE,
        FIM_DE_SESSAO,
        ATUALIZAR_TIMER,
        SOLICITAR_RESPOSTA, 
        AGUARDAR_OPONENTE
    }

    private final Tipo tipo;
    private final Object dados; // Carga de dados (tempo, resposta, etc)

    public MensagemControle(Tipo tipo, Object dados) {
        this.tipo = tipo;
        this.dados = dados;
    }
    
    public Tipo getTipo() { 
        return tipo; 
    }
    
    public Object getDados() { 
        return dados; 
    }
}
