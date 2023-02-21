/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1.servidor;

import java.io.*;

import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Servi {

    static final int Puerto = 5000;

    public Servi() {

        try {
            // Inicio la escucha del servidor en un determinado puerto

            ServerSocket skServidor = new ServerSocket(Puerto);

            System.out.println("Escucho el puerto " + Puerto);

            // Espero a que se conecte un cliente y creo un nuevo socket para el cliente
            Socket sCliente = skServidor.accept();
            
            //Flujo para leer mensajes del cliente
            DataInputStream flujo_entrada = new DataInputStream(sCliente.getInputStream());
            //Flujo para escribir mensajes al cliente
            DataOutputStream flujo_salida = new DataOutputStream(sCliente.getOutputStream());            

            // ATENDER PETICIÓN DEL CLIENTE
            
            
            
            
            //Cierro los flujos
            flujo_entrada.close();
            flujo_salida.close();
            // Cierro el socket
            sCliente.close();
        } catch (IOException ex) {
            System.out.println("Excepción de entrada/salida");
        }

    }

}
