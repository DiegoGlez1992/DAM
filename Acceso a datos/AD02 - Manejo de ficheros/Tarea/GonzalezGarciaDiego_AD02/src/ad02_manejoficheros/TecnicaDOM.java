/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ad02_manejoficheros;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Diego González García
 */
public class TecnicaDOM {

    /**
     * Método para leer todas las etiquetas del fichero "baloncesto.xml" utilizando la técnica DOM
     */
    public void visualizarEtiquetas() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("baloncesto.xml"));  //Obtenemos el documento
            document.getDocumentElement().normalize();

            System.out.println("Elemento raiz: " + document.getDocumentElement().getNodeName());    //Mostramos el elemento raiz
            NodeList nodeList = document.getElementsByTagName("competicion");   //Creamos la lista de nodos
            for (int a = 0; a < nodeList.getLength(); a++) {    //La vamos recorriendo
                Node node = nodeList.item(a);   //Tomamos un nodo
                System.out.println("\t" + node.getNodeName() + getAtributes(node)); //Mostrmos la información por pantalla
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;   //Tomamos como elemento el nodo
                    NodeList nodeList2 = element.getChildNodes();   //Creamos otra lista con los nodos hijos
                    for (int b = 0; b < nodeList2.getLength(); b++) {
                        Node node2 = nodeList2.item(b);
                        if (node2.getNodeType() == Node.ELEMENT_NODE) {
                            System.out.println("\t\t" + node2.getNodeName() + getAtributes(node2));
                            Element element2 = (Element) node2;
                            NodeList nodeList3 = element2.getChildNodes();
                            for (int c = 0; c < nodeList3.getLength(); c++) {
                                Node node3 = nodeList3.item(c);
                                if (node3.getNodeType() == Node.ELEMENT_NODE) {
                                    System.out.println("\t\t\t" + node3.getNodeName());
                                    Element element3 = (Element) node3;
                                    NodeList nodeList4 = element3.getChildNodes();
                                    for (int d = 0; d < nodeList4.getLength(); d++) {
                                        Node node4 = nodeList4.item(d);
                                        if (node4.getNodeType() == Node.ELEMENT_NODE) {
                                            System.out.println("\t\t\t\t" + node4.getNodeName());
                                            Element element4 = (Element) node4;
                                            NodeList nodeList5 = element4.getChildNodes();
                                            for (int e = 0; e < nodeList5.getLength(); e++) {
                                                Node node5 = nodeList5.item(e);
                                                if (node5.getNodeType() == Node.ELEMENT_NODE) {
                                                    System.out.println("\t\t\t\t\t" + node5.getNodeName() + " " + node5.getTextContent());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Método para sacar los atributos de un nodo
     *
     * @param node
     * @return
     */
    private String getAtributes(Node node) {
        String info = null;
        NamedNodeMap attributes = node.getAttributes(); //Tomamos los atributos del nodo
        if (attributes.getLength() > 0) {
            Node item = attributes.item(0);
            info = (": " + item.getNodeName() + " " + item.getTextContent());   //Mostramos la información
        }
        return info;
    }
}
