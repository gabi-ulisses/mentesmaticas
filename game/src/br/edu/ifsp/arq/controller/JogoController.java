package br.edu.ifsp.arq.controller;

import br.edu.ifsp.arq.network.Cliente;
import br.edu.ifsp.arq.view.TelaInicial;
import br.edu.ifsp.arq.view.TelaJogo;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Controla a lógica da aplicação do lado do jogador.
 * Serve como uma ponte entre as telas (View) e a conexão de rede (Cliente).
 */
public class JogoController {
    private TelaInicial telaInicial;
    private TelaJogo telaJogo;
    private Cliente cliente;
    private String nomeEsteJogador; // Guarda o nome do jogador desta instância

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
            // Atualiza a tela de espera com o nome do jogador
            telaJogo.setNomeJogador(nomeEsteJogador);
            // Envia o nome para o servidor
            cliente.enviarMensagem(nomeEsteJogador);
        } else {
            telaInicial.mostrarAviso("Não foi possível conectar ao servidor.");
        }
    }

    public void enviarResposta(String texto) {
        if (cliente != null && cliente.isConectado()) {
            cliente.enviarMensagem(texto);
            telaJogo.limparCampoResposta();
        }
    }
    
    /**
     * Processa as mensagens que chegam do servidor.
     * Funciona como um "roteador", decidindo o que fazer com cada tipo de mensagem.
     */    
    public void processarMensagemDoServidor(String mensagem) {
        // Garante que qualquer atualização na tela aconteça na thread correta
        SwingUtilities.invokeLater(() -> {
            try {
                String[] partes = mensagem.split("\\|", 2); // Divide a mensagem no primeiro "|"
                String prefixo = partes[0];
                String conteudo = partes.length > 1 ? partes[1] : "";

                switch (prefixo) {
                    case "CONTROL":
                        processarComandoDeControle(conteudo);
                        break;
                    case "PROMPT":
                        telaJogo.setStatusTurno(conteudo);
                        break;
                    case "TIMER":
                        telaJogo.atualizarContador(conteudo);
                        break;
                    case "BEMVINDO":
                        // Atualiza o nome na tela de espera (se ainda estiver nela)
                        telaJogo.setNomeJogador(conteudo);
                        break;
                    case "PLACAR":
                        // Ex: "PLACAR|Ana;1;Gabrielle;0"
                        String[] dadosPlacar = conteudo.split(";");
                        String nome1 = dadosPlacar[0];
                        int pontos1 = Integer.parseInt(dadosPlacar[1]);
                        String nome2 = dadosPlacar[2];
                        int pontos2 = Integer.parseInt(dadosPlacar[3]);
                        telaJogo.atualizarPlacares(nome1, pontos1, nome2, pontos2);
                        break;
                    case "PERGUNTA":
                        // Ex: "PERGUNTA|Quanto é 2+2?|a)3;b)4;c)5"
                        String[] dadosPergunta = conteudo.split("\\|");
                        String enunciado = dadosPergunta[0];
                        String[] opcoes = dadosPergunta[1].split(";");
                        telaJogo.setTextoPergunta(enunciado, opcoes);
                        break;
                    default:
                        // Para mensagens de status gerais (ex: "-> Ana acertou!")
                        telaJogo.setStatusTurno(mensagem);
                        break;
                }
            } catch (Exception e) {
                // Em caso de mensagem mal formatada, apenas imprime no console de debug
                System.err.println("Erro ao processar mensagem do servidor: " + mensagem);
            }
        });
    }

    private void processarComandoDeControle(String comando) {
        if (comando.equals("START_GAME")) {
            // Servidor avisou que a partida vai começar, então mudamos para a tela de jogo
            telaJogo.mostrarPainelJogo();
        } else if (comando.equals("PLAY_AGAIN")) {
            int escolha = telaJogo.mostrarDialogoReiniciar();
            if (escolha == JOptionPane.YES_OPTION) {
                cliente.enviarMensagem("CMD|RESTART_YES");
                // Prepara a tela para a próxima partida
                telaJogo.mostrarPainelEspera();
                telaJogo.setNomeJogador(nomeEsteJogador);
            } else {
                cliente.enviarMensagem("CMD|RESTART_NO");
            }
        } else if (comando.equals("SESSION_END")) {
            notificarDesconexao();
        }
    }
    
    public void notificarDesconexao() {
        SwingUtilities.invokeLater(() -> {
            telaJogo.mostrarAviso("A conexão com o servidor foi perdida. O jogo será fechado.");
            telaJogo.desabilitarInteracao();
            
            // Usa um timer para fechar a aplicação após 2 segundos
            javax.swing.Timer timer = new javax.swing.Timer(2000, e -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        });
    }
}