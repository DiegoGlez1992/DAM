package PSP04_Tarea1_Servidor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

/**
 * <strong>Clase para el objeto servidor.</strong><br>
 * Recibe del cliente el nombre de un archivo y lo busca en el sistema.<br>
 * En caso de que el fichero no exista, envía un mensaje de error al cliente. En
 * caso de que si exista, lo lee y envía en contenido al cliente línea a línea.
 * Indica la finalización del fichero enviando "EOF".
 *
 * @author Diego González García
 */
public class Servidor extends Thread {

    /**
     * Socket para la comunicación con el cliente
     */
    private static Socket clientSocket;
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
     *
     * @param sCliente Socket del cliente
     */
    public Servidor(Socket sCliente) {

        clientSocket = sCliente;
    }

    /**
     * Método run
     */
    @Override
    public void run() {
        try {
            boolean fileExists = false;
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
                    fileName = dataInputStream.readUTF();   //Recibe el nombre del archivo a consultar
                    System.out.println("FICHERO SOLICITADO POR EL CLIENTE: " + fileName);
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

            System.out.println("COMIENZA LA TRANSMISIÓN DEL ARCHIVO ");
            while ((aux = bufferedReader.readLine()) != null) { //Lee el archivo línea a línea
                dataOutputStream.writeUTF(aux);  //Envía el contenído de la línea
            }
            System.out.println("FINALIZA LA TRANSMISIÓN DEL ARCHIVO ");
            dataOutputStream.writeUTF("EOF");   //Envía la clave de final de archivo
            //bufferedReader.close(); //Cierra el buffer de lectura
            //fileReader.close(); //Cierra el lector del archivo
            //dataInputStream.close();    //Cierra el stream de entrada
            //dataOutputStream.close();   //Cierra el stream de salida
            //clientSocket.close();   //Cierra el socket con el cliente
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close(); //Cierra el buffer de lectura
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar bufferedReader");
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close(); //Cierra el lector del archivo
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar fileReader");
                }
            }
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();    //Cierra el stream de entrada
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar dataInputStream");
                }
            }
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();   //Cierra el stream de salida
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar dataOutputStream");
                }
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();   //Cierra el socket con el cliente
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar clientSocket");
                }
            }
        }
    }
}
