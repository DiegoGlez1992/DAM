/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PSP04_Tarea2_Servidor;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <strong>Clase para el método main del servidor.</strong><br>
 * El servidor publica su puerto y espera clientes.
 *
 * @author Diego González García
 */
public class MainServidor {
/**
     * Puerto de conexión
     */
    private static final int PORT = 2223;
    /**
     * Socket del servidor
     */
    private static ServerSocket serverSocket;
    /**
     * Socket para la comunicación con el cliente
     */
    private static Socket clientSocket;

    /**
     * Método main
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);  //Publica el puerto del servidor
            System.out.println("ESCUCHO EL PUERTO: " + PORT);
            while (true) {
                clientSocket = serverSocket.accept();   //Acepta la conexión del cliente y crea su socket para la comunicación
                System.out.println("CLIENTE CONECTADO");
                new Servidor(clientSocket).start(); //Crea un nuevo hilo para el cliente
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
