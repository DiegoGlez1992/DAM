/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejemplo3client;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jfranco
 */
public class Ejemplo3Client {
       
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int puerto = 6600;
       String host="localhost";
          System.setProperty("java.security.policy", 
                "./client3.policy");
        System.setSecurityManager(new SecurityManager());
         try{
            Socket cliente = new Socket (host, puerto);
            System.out.println ("Cliente preparado");
            cliente.close();
            System.out.println ("Cliente se va");
        } catch (IOException ex) {
            System.err.println ("Error socket cliente" + ex.toString());
        }
    }
    
}
