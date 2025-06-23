package br.edu.ifsp.arq;

import br.edu.ifsp.arq.dao.QuestaoDAO;
import br.edu.ifsp.arq.model.Questao;
import java.util.List;

/**
 * Classe de teste simples para validar o funcionamento do QuestaoDAO.
 * O objetivo é carregar as questões do XML e exibi-las no console.
 */
public class Versao2 {

    public static void main(String[] args) {
        System.out.println("--- Iniciando teste do QuestaoDAO ---");

        QuestaoDAO dao = new QuestaoDAO();

        List<Questao> questoesCarregadas = dao.carregarQuestoesDoXML("questoes.xml");

        if (questoesCarregadas != null && !questoesCarregadas.isEmpty()) {
            
            System.out.println("\nSucesso! " + questoesCarregadas.size() + " questões foram carregadas.");
            System.out.println("----------------------------------------");

            int contador = 1;
            for (Questao questao : questoesCarregadas) {
                System.out.println("Questão " + contador + ":");
                System.out.println("  Enunciado: " + questao.getEnunciado());
                System.out.print("  Opções: ");
                for (String opcao : questao.getOpcoes()) {
                    System.out.print("[" + opcao + "] ");
                }
                System.out.println("\n----------------------------------------");
                contador++;
            }

        } else {
            System.out.println("\nFalha ao carregar as questões. A lista está vazia ou nula.");
        }

        System.out.println("--- Teste do QuestaoDAO finalizado ---");
    }
}