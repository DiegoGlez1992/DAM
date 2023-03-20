/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ad02_manejoficheros;

import java.io.*;
import java.util.ArrayList;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 * <strong>Tarea AD02</strong><br>
 * <div>
 * <strong>Apartado 1) (5 puntos) Realiza las siguientes acciones utilizando
 * NetBeans:</strong><br>
 * <ul>
 * <li>(2,5 puntos) Crear un fichero DEPARTAMENTOS.DAT de acceso aleatorio, que
 * contenga al menos cinco empleados. Dicho fichero contendrá los campos
 * siguientes: NUMERO_DPTO (int), NOMBRE (string), LOCALIDAD (sTRING). </li>
 * <li>(2,5 puntos) A partir de los datos del fichero DEPARTAMENTOS.DAT crear un
 * fichero llamado DEPARTAMENTOS.XML usando DOM.</li>
 * </ul>
 * </div>
 * <div>
 * <strong>Apartado 2) (5 puntos) Visualizar todas las etiquetas del fichero
 * BALONCESTO.XML utilizando las técnicas DOM y SAX</strong><br>
 * </div>
 *
 * @author Diego González García
 */
public class AD02_ManejoFicheros {

    /**
     * Método para crear elementos
     *
     * @param dato - Nombre del dato
     * @param valor - Valor del dato
     * @param raiz - Elemento raiz
     * @param document - Documento
     */
    static void CrearElemento(String dato, String valor, Element raiz, Document document) {
        Element elem = document.createElement(dato);    //Creamos un elemento del tipo especificado
        Text text = document.createTextNode(valor); //Damos valor al elemento
        raiz.appendChild(elem); //Indicamos elemento hijo
        elem.appendChild(text); //Indicamos el valor
    }

    /**
     * Método main
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // EJERCICIO 1 APARTADO 1 //////////////////////////////////////////////
        File file = null;
        RandomAccessFile raf = null;

        //Creamos un array list con los datos de los empleados
        ArrayList<Empleado> empleados = new ArrayList<>();
        empleados.add(new Empleado(1, "Diego Gonzalez Garcia", "Barcenilla"));
        empleados.add(new Empleado(1, "Pepe Gonzalez Sanchez", "Santander"));
        empleados.add(new Empleado(2, "Manuel Garcia Fernandez", "Torrelavega"));
        empleados.add(new Empleado(3, "Federico Perez Hevia", "Astillero"));
        empleados.add(new Empleado(4, "Susana Perez Gonzalez", "Santander"));

        try {
            file = new File("Departamentos.dat");  //Instanciamos el fichero
            raf = new RandomAccessFile(file, "rw"); //Creamos el objeto de acceso aleatorio al archivo
            for (Empleado empleado : empleados) {
                raf.writeByte(empleado.getNumero_dpto());    //Escribimos el númnero del departamento
                raf.writeUTF(empleado.getNombre());   //Escribimos el nombre
                raf.writeUTF(empleado.getLocalidad());    //Escribimos la localidad
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        System.out.println("Ejercicio 1, apartado 1 completado.\n");

        // EJERCICIO 1 APARTADO 2 //////////////////////////////////////////////
        File file2;
        RandomAccessFile raf2;
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Source source;
        Result result;
        Transformer transformer;
        int numero_dpto;
        String nombre, localidad;

        try {
            file2 = new File("Departamentos.dat");
            raf2 = new RandomAccessFile(file2, "r");
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            document = implementation.createDocument(null, "empleados", null);
            document.setXmlVersion("1.0");  //Indicamos versión de XML
            while (raf2.getFilePointer() != raf2.length()) {  //Recorremos el fichero mientras que el puntero no sea igual al tamaño de este
                numero_dpto = raf2.readByte();  //Leemos el numero de departamento
                nombre = raf2.readUTF();    //Leemos el nombre
                localidad = raf2.readUTF(); //Leemos la localidad
                Element raiz = document.createElement("empleado");  //Creamos el elemento raíz
                document.getDocumentElement().appendChild(raiz);    //Añadimos un elemento hijo
                CrearElemento("numero_dpto", Integer.toString(numero_dpto), raiz, document);    //Creamos el elemento "numero_dpto"
                CrearElemento("nombre", nombre, raiz, document);    //Creamos el elemento "nombre"
                CrearElemento("localidad", localidad, raiz, document);  //Creamos el elemento "localidad"
            }
            source = new DOMSource(document);   //Instanciamos el adaptador source
            result = new StreamResult(new java.io.File("Departamentos.xml"));    //Instanciamos el adaptador result
            transformer = TransformerFactory.newInstance().newTransformer();    //Instanciamos el transformer
            transformer.transform(source, result);
        } catch (ParserConfigurationException | DOMException | IOException | TransformerException ex) {
            System.err.println(ex);
        }
        System.out.println("Ejercicio 1, apartado 2 completado.\n");
        
        // EJERCICIO 2 APARTADO 1 //////////////////////////////////////////////
        System.out.println("******************************************\n"
                + "| Documento baloncesto.xml leido con DOM |\n"
                + "******************************************\n");
        TecnicaDOM dom = new TecnicaDOM();
        dom.visualizarEtiquetas();
        System.out.println("Ejercicio 2, apartado DOM completado.\n");
        
        // EJERCICIO 2 APARTADO 2 //////////////////////////////////////////////
        System.out.println("******************************************\n"
                + "| Documento baloncesto.xml leido con SAX |\n"
                + "******************************************\n");
        TecnicaSAX sax = new TecnicaSAX();
        sax.visualizarEtiquetas();
        System.out.println("Ejercicio 2, apartado SAX completado.\n");
    }
}