/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejemplo3server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jfranco
 */
public class Ejemplo3Server {
   
    public static void main(String[] args) {
        int puerto = 6600;
       System.setProperty("java.security.policy", 
                "./server3.policy");
        System.setSecurityManager(new SecurityManager());
          try {
            ServerSocket servidor = 
                    new ServerSocket(puerto);
            System.out.println ("Servidor preparado.");
            Socket cliente = servidor.accept();
            System.out.println ("Servidor preparado para aceptar conexión del cliente");
            cliente.close();
            System.out.println ("Cliente desconectado");
            servidor.close();
        } catch (IOException ex) {
            System.err.println ("Error de servidor" + ex.toString());
        }
    }
    
}
