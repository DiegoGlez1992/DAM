/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servi extends Thread {

    Socket skCliente;

    public Servi(Socket sCliente) {
        skCliente = sCliente;
    }

    public void run() {
      
            
        try {
            // Creo los flujos de entrada y salida
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());
            
            // ATENDER PETICIÓN DEL CLIENTE
            


            // Se cierran los flujos y la conexión
            flujo_entrada.close();
            flujo_salida.close();
            skCliente.close();
            System.out.println("Cliente desconectado");
        } catch (IOException ex) {
            Logger.getLogger(Servi.class.getName()).log(Level.SEVERE, null, ex);
        } 
      
    }
}
