/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package psp05_tarea1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <strong>Clase principal</strong><br>
 *
 * @author Diego González García
 */
public class MainServidor {

    /**
     * Puerto de conexión
     */
    private static final int PORT = 8066;
    /**
     * Socket del servidor
     */
    private static ServerSocket serverSocket;
    /**
     * Socket para la comunicación con el cliente
     */
    private static Socket clientSocket;

    /**
     * <strong>Método main</strong><br>
     * Procedimiento principal que asigna a cada petición entrante un socket
     * cliente, por donde se enviará la respuesta una vez procesada.
     * <br><br>
     * Como resolución de la tarea, se ha modificado la forma en la que se
     * aceptan los clientes. Ahora lo que se hace es aceptarlos desde el hilo
     * principal, a continuación se crea un nuevo hilo y este último es quien
     * procesa su petición. De esta forma, el servidor, a través de su hilo
     * principal, podrá seguir atendiendo nuevas peticiones de clientes sin
     * mantenerse ocupado en su procesamiento.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);  //Publica el puerto del servidor
            ServidorHTTP.imprimeDisponible();   //Muestra por consola las indicaciones pertinentes

            while (true) {  //Bucle infinito que espera clientes
                clientSocket = serverSocket.accept();   //Acepta la conexión del cliente y crea su socket para la comunicación
                System.out.println("Atendiendo al cliente: " + clientSocket.toString());
                new ServidorHTTP(clientSocket).start(); //Crea un nuevo hilo para el cliente
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
