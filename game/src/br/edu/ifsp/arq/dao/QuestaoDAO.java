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
 * Lê as perguntas do arquivo questoes.xml, isolando a lógica de leitura de arquivos do resto do jogo.
 * Funcionalidade principal:
 * - Ler o arquivo "questoes.xml";
 * - Interpretar o enunciado, as opções e a resposta correta de cada pergunta;
 * - Retornar uma lista de objetos Questao para uso no jogo.
 */
public class QuestaoDAO {
    // Lê o arquivo XML e retorna uma lista de objetos Questao.
    public List<Questao> carregarQuestoesDoXML(String caminhoArquivo) {
        List<Questao> questoes = new ArrayList<>();
        try {
            // Prepara as ferramentas do Java para ler e entender o arquivo XML.
            File inputFile = new File(caminhoArquivo);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            // Lê o arquivo e o transforma em uma "árvore" de objetos na memória.
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // Pega uma lista de todos os elementos que têm a tag <questao>.
            NodeList listaDeNosQuestao = doc.getElementsByTagName("questao");

            // Passa por cada elemento <questao> que foi encontrado.
            for (int i = 0; i < listaDeNosQuestao.getLength(); i++) {
                Element elementoQuestao = (Element) listaDeNosQuestao.item(i);

                // Extrai o texto de dentro da tag <enunciado>.
                String enunciado = elementoQuestao.getElementsByTagName("enunciado").item(0).getTextContent();
                int correta = Integer.parseInt(elementoQuestao.getAttribute("correta"));

                // Pega a lista de todas as tags <opcao> dentro da questão atual.
                NodeList listaDeNosOpcao = elementoQuestao.getElementsByTagName("opcao");
                // Cria um array de Strings para guardar os textos das opções.
                String[] opcoes = new String[listaDeNosOpcao.getLength()];
                for (int j = 0; j < listaDeNosOpcao.getLength(); j++) {
                    opcoes[j] = listaDeNosOpcao.item(j).getTextContent();
                }

                //Cria o objeto Questao e adiciona na lista
                Questao questao = new Questao(enunciado, opcoes, correta);
                questoes.add(questao);
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        return questoes;
    }
}