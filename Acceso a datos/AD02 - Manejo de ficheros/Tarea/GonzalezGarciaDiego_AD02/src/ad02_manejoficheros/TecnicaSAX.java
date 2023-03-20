/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ad02_manejoficheros;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Diego González García
 */
public class TecnicaSAX extends DefaultHandler {

    private final StringBuilder sb = new StringBuilder();   //Para poder leer los elementos simples

    @Override
    public void characters(char[] ch, int start, int length) {
        sb.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "eurobasket":
                break;
            case "competicion":
                break;
            case "jornada":
                break;
            case "partido":
                break;
            case "equipo":
                break;
            case "nombre":
                System.out.println(sb.toString());
                break;
            case "puntuacion":
                System.out.println(sb.toString());
                break;
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "eurobasket":
                System.out.println("Elemento raíz eurobarket ");
                break;
            case "competicion":
                System.out.println("\tCompeticion año: " + attributes.getValue("año"));
                break;
            case "jornada":
                System.out.println("\t\tJornada fecha: " + attributes.getValue("fecha"));
                break;
            case "partido":
                System.out.println("\t\t\tPartido: ");
                break;
            case "equipo":
                System.out.println("\t\t\t\tEquipo: ");
                break;
            case "nombre":
                System.out.print("\t\t\t\t\tNombre: ");
                sb.delete(0, sb.length());  //Borramos el buffer
                break;
            case "puntuacion":
                System.out.print("\t\t\t\t\tPuntuacion: ");
                sb.delete(0, sb.length());  //Borramos el buffer
                break;
        }
    }
    
    /**
     * Método para leer todas las etiquetas del fichero "baloncesto.xml" utilizando la técnica SAX
     */
    public void visualizarEtiquetas() {
        try {
            TecnicaSAX sax = new TecnicaSAX();
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(sax);
            InputSource inputSource = new InputSource("baloncesto.xml");
            xmlReader.parse(inputSource);
        } catch (IOException | SAXException ex){
            ex.printStackTrace();
        }
    }
}
