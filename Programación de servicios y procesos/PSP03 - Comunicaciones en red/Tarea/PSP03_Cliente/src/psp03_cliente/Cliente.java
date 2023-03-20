/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package psp03_cliente;

import java.io.*;
import java.net.*;

/**
 * <strong>Clase para el objeto cliente.</strong><br>
 * El cliente solicita conexión al servidor.<br>
 * Envía el nombre de un archivo para consultar.<br>
 * Si el archivo no existe en el servidor, recibe un mensaje de error por parte
 * de este. En caso de que el archivo si exista, recibe su contenido línea a
 * línea, lo guarda en un archivo en su sistema y muestra su información por
 * consola.<br>
 * La transimión finaliza al recibir "EOF".
 *
 * @author Diego González García
 */
public class Cliente {

    /**
     * IP del servidor
     */
    private static final String HOST = "localhost";
    /**
     * Puerto de conexión
     */
    private static final int PORT = 2223;
    /**
     * Socket para la comunicación con el cliente
     */
    Socket clientSocket;
    /**
     * Nombre del archivo
     */
    private String fileName = "Fichero1.txt";
    /**
     * Nombre del archivo erróneo
     */
    private String wrongFileName = "FicheroError.txt";
    /**
     * Variable auxiliar para la lectura del fichero
     */
    private String aux = "";
    /**
     * Objeto para el archivo
     */
    private File file = null;
    /**
     * Escritor de archivo
     */
    private FileWriter fileWriter = null;
    /**
     * Escritor para archivo formateado
     */
    private PrintWriter printWriter = null;
    /**
     * Stream para la salida de datos
     */
    private DataOutputStream dataOutputStream = null;
    /**
     * Stream para la entrada de datos
     */
    private DataInputStream dataInputStream = null;

    /**
     * Método constructor
     */
    public Cliente() {
        try {
            clientSocket = new Socket(HOST, PORT);  //Se conecta al servidor por el puerto indicado
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());    //Crea el stream de salida
            dataInputStream = new DataInputStream(clientSocket.getInputStream());   //Crea el stream de entrada
            file = new File(fileName);  //Instancia el archivo
            fileWriter = new FileWriter(file);  //Crea el escritor del archivo
            printWriter = new PrintWriter(file);    //Crea el escritor formateado del archivo

            dataOutputStream.writeUTF(wrongFileName);    //Envía el nombre erróneo del archivo a consultar
            while (!(aux = dataInputStream.readUTF()).endsWith("EOF")) {    //Lee la respuesta del servidor y va filtrando
                switch (aux) {
                    case "FNF": //Si el archivo no existe
                        System.out.println("EL ARCHIVO INDICADO NO EXISTE. PRUEBA DE NUEVO.");
                        dataOutputStream.writeUTF(fileName);    //Envía el nombre correcto del archivo a consultar
                        break;
                    default:
                        //fileWriter.write(aux + "\n");
                        printWriter.println(aux);   //Guarda la información recibida en el archivo creado
                        System.out.println(aux);    //Muestra por consola la información recibida
                }
            }
            System.out.println("FÍN DE LECTURA DEL ARCHIVO");
            printWriter.close();    //Cierra la escritura formateada del archivo
            //fileWriter.close(); //Cierra la escritura del archivo
            dataInputStream.close();    //Cierra el stream de entrada
            dataOutputStream.close();   //Cierra el stream de salida
            clientSocket.close();   //Cierra el socket
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
