package br.edu.ifsp.arq.model;

import java.util.Objects;

/**
 * Guarda o nome e a pontuação de um jogador, além de comparar dois jogadores pelo nome.
  * Também fornece métodos para:
 * - Adicionar pontos;
 * - Resetar a pontuação (por exemplo, ao reiniciar uma partida)
 * - Gerar um hash consistente para uso em coleções como HashMap ou HashSet.
 */
public class Jogador {

    private String nome;
    private int pontuacao;

    public Jogador(String nome) {
        this.nome = nome;
        this.pontuacao = 0; // Todo jogador começa com 0 pontos.
    }

    public String getNome() {
        return nome;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void adicionarPonto() {
        this.pontuacao++;
    }

    public void resetarPontuacao() {
        this.pontuacao = 0;
    }

    @Override
    // No nosso caso, dois jogadores são iguais se tiverem o mesmo nome.
    public boolean equals(Object o) {
        if (this == o){
            return true; // Se for exatamente o mesmo objeto na memória.
        } 
        if (o == null || getClass() != o.getClass()){
            return false; // Se não for um objeto do tipo Jogador.
        } 
        Jogador jogador = (Jogador) o;
        return Objects.equals(nome, jogador.nome);
    }

    @Override
    // "código" numérico único baseado no nome.
    public int hashCode() {
        return Objects.hash(nome);
    }
}
