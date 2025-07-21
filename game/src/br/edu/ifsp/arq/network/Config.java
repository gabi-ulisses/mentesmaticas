package br.edu.ifsp.arq.network;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Guarda o IP e a Porta do servidor lidos do arquivo config.xml.
 * Lê o arquivo config.xml
 */
public class Config {
    
    private static String ip;
    private static int porta = -1;

    private Config() { } 
    
    private static void lerConfiguracoes() {
        try {
            File arquivo = new File("config.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(arquivo);
            doc.getDocumentElement().normalize();
            
            // elemento <servidor> de dentro do XML.
            Element root = doc.getDocumentElement();                
            Node nodeServidor = root.getElementsByTagName("servidor").item(0);
            
            // atributos "ip" e "porta" do elemento <servidor>.
            ip = nodeServidor.getAttributes().getNamedItem("ip").getNodeValue();
            porta = Integer.parseInt(nodeServidor.getAttributes().getNamedItem("porta").getNodeValue());
            
        } catch (Exception ex) {
            ex.printStackTrace();
            // usa valores padrão para o jogo não quebrar.
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