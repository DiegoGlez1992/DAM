/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Clie {

    static final String Host = "localhost";
    static final int Puerto = 5000;

    public Clie() {

        try {
            // Me conecto al servidor en un detrminado puerto
            Socket sCliente = new Socket(Host, Puerto);
            System.out.println("se conecta");

            //Flujo para leer mensajes del servidor
            DataInputStream flujo_entrada = new DataInputStream(sCliente.getInputStream());
            //Flujo para escribir mensajes al servidor
            DataOutputStream flujo_salida = new DataOutputStream(sCliente.getOutputStream());

            // TAREAS QUE REALIZA EL  CLIENTE
            
            
            
            
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
