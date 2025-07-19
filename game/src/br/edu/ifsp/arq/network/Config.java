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
    
    private static String ip;
    private static int porta = -1;

    private Config() { } 
    
    // Método que lê o arquivo XML.
    private static void lerConfiguracoes() {
        try {
            File arquivo = new File("config.xml");
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

    public static String getIp() {
        if (ip == null) {
            lerConfiguracoes();
        }
        return ip;
    }
    
    public static int getPorta() {
        if (porta == -1) {
            lerConfiguracoes();
        }
        return porta;
    }
}