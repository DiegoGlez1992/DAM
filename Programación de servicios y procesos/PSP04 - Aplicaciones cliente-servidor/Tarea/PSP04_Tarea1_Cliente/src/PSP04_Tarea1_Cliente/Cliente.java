package PSP04_Tarea1_Cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
class Cliente {
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
     * Buffer de lectura
     */
    private BufferedReader bufferedReader;
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
            System.out.println("EL CLIENTE SE CONECTA...");
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());    //Crea el stream de salida
            dataInputStream = new DataInputStream(clientSocket.getInputStream());   //Crea el stream de entrada
            //file = new File(fileName);  //Instancia el archivo
            //fileWriter = new FileWriter(file);  //Crea el escritor del archivo
            //printWriter = new PrintWriter(file);    //Crea el escritor formateado del archivo
            bufferedReader = new BufferedReader(new InputStreamReader(System.in)); //Crea un buffer de lectura para el teclado

            System.out.print("\tNombre del fichero a leer: ");
            fileName = bufferedReader.readLine();   //Lee el nombre del archivo a consultar
            System.out.println("");
            dataOutputStream.writeUTF(fileName);    //Envía el nombre del archivo a consultar
            file = new File(fileName);  //Instancia el archivo
            printWriter = new PrintWriter(file);    //Crea el escritor formateado del archivo
            while (!(aux = dataInputStream.readUTF()).endsWith("EOF")) {    //Lee la respuesta del servidor y va filtrando
                switch (aux) {
                    case "FNF": //Si el archivo no existe
                        System.out.println("EL ARCHIVO INDICADO NO EXISTE. PRUEBA DE NUEVO.");
                        printWriter.close();    //Cierra la escritura formateada del archivo
                        file.delete();  //Borra el archivo creado ya que no existe en el servidor
                        System.out.print("\tNombre del fichero a leer: ");
                        fileName = bufferedReader.readLine();   //Lee el nombre del archivo a consultar
                        System.out.println("");
                        dataOutputStream.writeUTF(fileName);    //Envía el nombre del archivo a consultar
                        file = new File(fileName);  //Instancia el archivo
                        printWriter = new PrintWriter(file);    //Crea el escritor formateado del archivo
                        break;
                    default:
                        //fileWriter.write(aux + "\n");
                        printWriter.println(aux);   //Guarda la información recibida en el archivo creado
                        System.out.println("\t" + aux);    //Muestra por consola la información recibida
                }
            }
            System.out.println("FÍN DE LECTURA DEL ARCHIVO");
            printWriter.close();    //Cierra la escritura formateada del archivo
            //fileWriter.close(); //Cierra la escritura del archivo
            //dataInputStream.close();    //Cierra el stream de entrada
            //dataOutputStream.close();   //Cierra el stream de salida
            //clientSocket.close();   //Cierra el socket 
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
