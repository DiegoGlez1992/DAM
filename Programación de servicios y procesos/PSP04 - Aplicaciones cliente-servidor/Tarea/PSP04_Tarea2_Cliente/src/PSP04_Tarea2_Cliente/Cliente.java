/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PSP04_Tarea2_Cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * <strong>Clase para el objeto cliente.</strong><br>
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
    private String fileName = "";
    /**
     * Variable auxiliar para la lectura del fichero
     */
    private String aux = "";
    /**
     * Variable auxiliar para la lectura de los comandos
     */
    private String comando = "";
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
    public Cliente() {
        try {
            clientSocket = new Socket(HOST, PORT);  //Se conecta al servidor por el puerto indicado
            System.out.println("EL CLIENTE SE CONECTA...");
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());    //Crea el stream de salida
            dataInputStream = new DataInputStream(clientSocket.getInputStream());   //Crea el stream de entrada
            bufferedReader = new BufferedReader(new InputStreamReader(System.in)); //Crea un buffer de lectura para el teclado

            System.out.println(dataInputStream.readUTF());  //Recibe confirmación de conexión
            do {

                System.out.println(dataInputStream.readUTF());  //Recibe opciones disponibles
                comando = bufferedReader.readLine();    //Lee el comando a ejecutar del teclado
                dataOutputStream.writeUTF(comando); //Envía el comando a ejecutar
                switch (comando) {
                    case "ls":
                        while (!(aux = dataInputStream.readUTF()).endsWith("EOT")) {    //Lee la respuesta del servidor hasta el final de transmisión
                            System.out.println("\t" + aux);    //Muestra por consola la información recibida
                        }
                        break;
                    case "get":
                        System.out.println(dataInputStream.readUTF());  //Recibe la solicitud de archivo
                        fileName = bufferedReader.readLine();   //Lee el nombre del archivo a solicitar
                        dataOutputStream.writeUTF(fileName);    //Envía el nombre del archivo
                        while (!(aux = dataInputStream.readUTF()).endsWith("EOF")) {    //Lee la respuesta del servidor hasta el final del archivo
                            System.out.println("\t" + aux);    //Muestra por consola la información recibida
                        }
                }
            } while (!comando.equalsIgnoreCase("exit"));
            clientSocket.close();   //Cierra el socket 

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
                    System.out.println("CLIENTE DESCONECTADO");
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar clientSocket");
                }
            }
        }
    }
}
