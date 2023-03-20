/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package psp03_servidor;

import java.io.*;
import java.net.*;

/**
 * <strong>Clase para el objeto servidor.</strong><br>
 * El servidor publica su puerto y espera un cliente.<br>
 * Recibe del cliente el nombre de un archivo y lo busca en el sistema.<br>
 * En caso de que el fichero no exista, envía un mensaje de error al cliente. En
 * caso de que si exista, lo lee y envía en contenido al cliente línea a línea.
 * Indica la finalización del fichero enviando "EOF".
 *
 * @author Diego González García
 */
public class Servidor {

    /**
     * Puerto de conexión
     */
    private static final int PORT = 2223;

    /**
     * Socket del servidor
     */
    ServerSocket serverSocket;
    /**
     * Socket para la comunicación con el cliente
     */
    Socket clientSocket;
    /**
     * Nombre del archivo
     */
    private String fileName = "";
    /**
     * Variable auxiliar para la lectura del fichero
     */
    private String aux = "";
    /**
     * Objeto para el archivo
     */
    private File file = null;
    /**
     * Lector de archivo
     */
    private FileReader fileReader;
    /**
     * Buffer de lectura
     */
    private BufferedReader bufferedReader;
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
    public Servidor() {
        try {
            boolean fileExists = false;
            serverSocket = new ServerSocket(PORT);  //Publica el puerto del servidor
            System.out.println("ESCUCHO EL PUERTO: " + PORT);
            clientSocket = serverSocket.accept();   //Acepta la conexión del cliente y crea su socket para la comunicación
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());    //Crea el stream de salida
            dataInputStream = new DataInputStream(clientSocket.getInputStream());   //Crea el stream de entrada

            /**
             * Recibe el nombre del archivo a consultar, instancia el objeto del
             * archivo, lo busca en el sistema y lo lee en el buffer.
             *
             * En caso de que el archivo no exista, informa al cliente y espera
             * otro nombre.
             */
            do {
                try {
                    fileName = dataInputStream.readUTF();  //Recibe el nombre del archivo a consultar
                    file = new File(fileName);  //Instancia el archivo
                    fileReader = new FileReader(file);  //Crea el lector del archivo
                    bufferedReader = new BufferedReader(fileReader);    //Crea el buffer de lectura
                    fileExists = true;  //Indica que el archivo existe
                } catch (FileNotFoundException fne) {   //En caso de error al buscar el archivo
                    System.err.println(fne.getMessage());   //Mostramos error en consola
                    dataOutputStream.writeUTF("FNF");    //Envía la clave de error al cliente
                    fileExists = false;  //Indica que el archivo no existe
                }
            } while (!fileExists);

            System.out.println("COMIENZA LA TRANSMISIÓN DEL ARCHIVO");
            while ((aux = bufferedReader.readLine()) != null) { //Lee el archivo línea a línea
                dataOutputStream.writeUTF(aux);  //Envía el contenído de la línea
            }
            dataOutputStream.writeUTF("EOF");    //Envía la clave de final de archivo
            System.out.println("FINALIZA LA TRANSMISIÓN DEL ARCHIVO");
            bufferedReader.close(); //Cierra el buffer de lectura
            fileReader.close(); //Cierra el lector del archivo
            dataInputStream.close();    //Cierra el stream de entrada
            dataOutputStream.close();   //Cierra el stream de salida
            clientSocket.close();   //Cierra el socket con el cliente
            serverSocket.close();   //Cierra el socket del servidor
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
