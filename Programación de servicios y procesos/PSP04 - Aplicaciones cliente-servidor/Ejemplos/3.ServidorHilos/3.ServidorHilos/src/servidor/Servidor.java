/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Servidor {

    /**
     * @param args the command line arguments
     */
    static final int Puerto=2000;
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            
            // Inicio el servidor en el puerto
            ServerSocket skServidor = new ServerSocket(Puerto);
            System.out.println("Escucho el puerto " + Puerto );
            while(true){
                // Se conecta un cliente
                Socket skCliente = skServidor.accept();
                System.out.println("Cliente conectado");
                // Atiendo al cliente mediante un thread
                /*Servi servi=new Servi(skCliente);
                servi.start();*/
                new Servi(skCliente).start();
                
            }
        } catch (IOException ex) {
            System.out.println("Error en la creación del socket del servidor");
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
