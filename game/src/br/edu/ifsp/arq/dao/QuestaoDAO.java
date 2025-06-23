package br.edu.ifsp.arq.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import br.edu.ifsp.arq.model.Questao;

/**
 * Implementação do padrão Data Access Object (DAO) para as questões do jogo.
 * Esta classe é responsável por ler e analisar (fazer o parse) do arquivo `questoes.xml`.
 * Utiliza a API DOM para carregar a estrutura hierárquica do arquivo  e transformar os
 * dados em uma lista de objetos {@link Questao}, que será usada pela classe Partida.
 * O objetivo é isolar a lógica de acesso e manipulação de arquivos  da lógica de negócio do jogo.
 */

public class QuestaoDAO {

    public List<Questao> carregarQuestoesDoXML(String caminhoArquivo) {
        List<Questao> questoes = new ArrayList<>();
        try {
            //Cria um objeto File para o caminho do arquivo XML
            File inputFile = new File(caminhoArquivo);
            
            //Configura a fábrica de parsers DOM
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            //Faz o parse do arquivo XML para um objeto Document (a árvore na memória)
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            //Pega a lista de todos os nós (elementos) com a tag "questao"
            NodeList listaDeNosQuestao = doc.getElementsByTagName("questao");

            //Itera sobre cada nó de questão encontrado
            for (int i = 0; i < listaDeNosQuestao.getLength(); i++) {
                Element elementoQuestao = (Element) listaDeNosQuestao.item(i);

                //Extrai os dados de cada elemento
                String enunciado = elementoQuestao.getElementsByTagName("enunciado").item(0).getTextContent();
                int correta = Integer.parseInt(elementoQuestao.getAttribute("correta"));
                
                NodeList listaDeNosOpcao = elementoQuestao.getElementsByTagName("opcao");
                String[] opcoes = new String[listaDeNosOpcao.getLength()];
                for (int j = 0; j < listaDeNosOpcao.getLength(); j++) {
                    opcoes[j] = listaDeNosOpcao.item(j).getTextContent();
                }

                //Cria o objeto Questao e adiciona na lista
                Questao questao = new Questao(enunciado, opcoes, correta);
                questoes.add(questao);
            }
        } catch (Exception e) {
            e.printStackTrace(); //Em um projeto real, trataríamos o erro de forma mais elegante
        }
        return questoes;
    }
}