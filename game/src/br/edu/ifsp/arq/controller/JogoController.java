package br.edu.ifsp.arq.controller;

import br.edu.ifsp.arq.model.mensagens.Mensagem;
import br.edu.ifsp.arq.model.mensagens.MensagemControle;
import br.edu.ifsp.arq.model.mensagens.MensagemPergunta;
import br.edu.ifsp.arq.model.mensagens.MensagemPlacar;
import br.edu.ifsp.arq.model.mensagens.MensagemStatus;
import br.edu.ifsp.arq.network.Cliente;
import br.edu.ifsp.arq.view.TelaInicial;
import br.edu.ifsp.arq.view.TelaJogo;

import javax.swing.JOptionPane;

/**
 * Controla a lógica da aplicação do lado do jogador.
 * VERSÃO SIMPLIFICADA SEM SwingUtilities.invokeLater
 */
public class JogoController {
    private TelaInicial telaInicial;
    private TelaJogo telaJogo;
    private Cliente cliente;
    private String nomeEsteJogador;

    public JogoController(){
        this.telaInicial = new TelaInicial(this);
        this.telaJogo = new TelaJogo(this);
    }

    public void iniciar() {
        telaInicial.mostrar();
    }

    public void conectar(String nomeJogador) {
        if (nomeJogador == null || nomeJogador.trim().isEmpty()) {
            telaInicial.mostrarAviso("O nome do jogador não pode estar vazio.");
            return;
        }
        this.nomeEsteJogador = nomeJogador;
        this.cliente = new Cliente(this);
        
        if (cliente.conectar()) {
            telaInicial.esconder();
            telaJogo.mostrar();
            telaJogo.setTitulo("MentesMáticas - " + nomeEsteJogador);
            telaJogo.setNomeJogador(nomeEsteJogador);
            cliente.enviarMensagem(nomeEsteJogador);
        } else {
            telaInicial.mostrarAviso("Não foi possível conectar ao servidor.");
        }
    }

    public void enviarResposta(String texto) {
        if (cliente != null && cliente.isConectado()) {
            cliente.enviarMensagem(texto);
            telaJogo.limparCampoResposta();
            telaJogo.habilitarInteracao(false); // Bloqueia o campo após o envio.
        }
    }
        
    public void processarMensagemDoServidor(Mensagem mensagem) {
        // As chamadas para a tela agora são feitas diretamente.
        if (mensagem instanceof MensagemPergunta) {
            MensagemPergunta msg = (MensagemPergunta) mensagem;
            telaJogo.setTextoPergunta(msg.getEnunciado(), msg.getOpcoes().toArray(new String[0]));

        } else if (mensagem instanceof MensagemPlacar) {
            MensagemPlacar msg = (MensagemPlacar) mensagem;
            telaJogo.atualizarPlacares(msg.getPlacares());

        } else if (mensagem instanceof MensagemStatus) {
            MensagemStatus msg = (MensagemStatus) mensagem;
            telaJogo.setStatusTurno(msg.getTexto());
            
        } else if (mensagem instanceof MensagemControle) {
            MensagemControle msg = (MensagemControle) mensagem;
            processarComandoDeControle(msg);
        }
    }

    private void processarComandoDeControle(MensagemControle mensagem) {
        switch (mensagem.getTipo()) {
            case INICIAR_JOGO:
                telaJogo.mostrarPainelJogo();
                break;
            
            case JOGAR_NOVAMENTE:
                int escolha = telaJogo.mostrarDialogoReiniciar();
                if (escolha == JOptionPane.YES_OPTION) {
                    cliente.enviarMensagem("JOGAR_NOVAMENTE_SIM");
                    telaJogo.mostrarPainelEspera();
                    telaJogo.setNomeJogador(nomeEsteJogador);
                }
                break;
            
            case ATUALIZAR_TIMER:
                int tempo = (Integer) mensagem.getDados();
                telaJogo.atualizarContador(String.valueOf(tempo));
                break;
                
            case SOLICITAR_RESPOSTA:
                telaJogo.habilitarInteracao(true); 
                String textoStatusSolicitar = (String) mensagem.getDados();
                telaJogo.setStatusTurno(textoStatusSolicitar);
                break;
            
            case AGUARDAR_OPONENTE:
                telaJogo.habilitarInteracao(false); 
                String textoStatusAguardar = (String) mensagem.getDados();
                telaJogo.setStatusTurno(textoStatusAguardar);
                break;
                
            case FIM_DE_SESSAO:
                notificarDesconexao("A sessão foi encerrada pelo servidor.");
                break;
        }
    }
    
    public void notificarDesconexao(String aviso) {
        // Chamada direta para a tela.
        if (telaJogo.estaVisivel()) {
            telaJogo.mostrarAviso(aviso);
            telaJogo.desabilitarInteracao();
            
        java.awt.event.ActionListener acaoDoTimer = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0); // Fecha a aplicação
            }
        };

        // Um timer simples para fechar a aplicação após 3 segundos
        javax.swing.Timer timer = new javax.swing.Timer(3000, acaoDoTimer);
        timer.setRepeats(false);
        timer.start();
        }
    }
}