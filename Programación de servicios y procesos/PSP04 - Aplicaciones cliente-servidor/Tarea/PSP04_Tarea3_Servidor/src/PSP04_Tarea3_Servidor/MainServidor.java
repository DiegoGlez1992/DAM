/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PSP04_Tarea3_Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * <strong>Clase para el método main del servidor.</strong><br>
 * El servidor publica su puerto y espera clientes.<br>
 * El servidor contiene una lista de usuarios admitidos.
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
     * Lista de usuarios existentes en el servidor
     */
    private static ArrayList<User> listaUsuarios;

    /**
     * Método main
     *
     * @param args
     */
    public static void main(String[] args) {
        listaUsuarios = new ArrayList();    //Lista que contiene a los usuarios autorizados
        listaUsuarios.add(new User("admin", "admin"));  //Añade el usuario "admin" a la lista de usuarios autorizados
        listaUsuarios.add(new User("diego", "diego"));  //Añade el usuario "diego" a la lista de usuarios autorizados
        try {
            serverSocket = new ServerSocket(PORT);  //Publica el puerto del servidor
            System.out.println("ESCUCHO EL PUERTO: " + PORT);
            while (true) {
                clientSocket = serverSocket.accept();   //Acepta la conexión del cliente y crea su socket para la comunicación
                System.out.println("CLIENTE CONECTADO");
                new Servidor(clientSocket, listaUsuarios).start(); //Crea un nuevo hilo para el cliente
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
