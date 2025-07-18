package br.edu.ifsp.arq.network;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Guarda o IP e a Porta do servidor lidos do arquivo config.xml.
 * É como um "bloco de notas" com o endereço do servidor.
 */
public class Config {
    
    // Variáveis para guardar o IP e a Porta depois de lidos.
    // Começam vazias.
    private static String ip;
    private static int porta = -1;

    // Construtor privado. Impede que alguém crie um objeto "new Config()".
    // A classe só deve ser usada assim: Config.getIp().    
    private Config() { } 
    
    // Método que faz o trabalho pesado de ler o arquivo XML.
    private static void lerConfiguracoes() {
        try {
            // Encontra o arquivo config.xml na pasta do projeto.
            File arquivo = new File("config.xml");
            // Prepara as ferramentas do Java para ler XML.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(arquivo);
            doc.getDocumentElement().normalize();
            
            // Pega o elemento <servidor> de dentro do XML.
            Element root = doc.getDocumentElement();                
            Node nodeServidor = root.getElementsByTagName("servidor").item(0);
            
            // Lê os atributos "ip" e "porta" do elemento <servidor>.
            ip = nodeServidor.getAttributes().getNamedItem("ip").getNodeValue();
            porta = Integer.parseInt(nodeServidor.getAttributes().getNamedItem("porta").getNodeValue());
            
        } catch (Exception ex) {
            // Lê os atributos "ip" e "porta" do elemento <servidor>.
            ex.printStackTrace();
            // E usa valores padrão para o jogo não quebrar.
            ip = "127.0.0.1";
            porta = 12345;
        }
    }

    // Método que as outras classes usam para pegar o IP.
    public static String getIp() {
        // Se o IP ainda não foi lido, chama o método para ler.
        // Isso garante que a leitura do arquivo aconteça só uma vez.
        if (ip == null) {
            lerConfiguracoes();
        }
        return ip;
    }
    
    // Método que as outras classes usam para pegar a Porta.
    public static int getPorta() {
        if (porta == -1) {
            lerConfiguracoes();
        }
        return porta;
    }
}