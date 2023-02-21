/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Clie {
   public static final String HOST = "localhost";
static final int Puerto=2000;

public Clie( ) {
    
  
       try {
           Socket skCliente = new Socket( HOST , Puerto );
           // Creo los flujos de entrada y salida
           DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
           DataOutputStream flujo_salida= new DataOutputStream(skCliente.getOutputStream());
           
           // TAREAS QUE REALIZA EL CLIENTE
           
           
           flujo_entrada.close();
           flujo_salida.close();
           skCliente.close();
       } catch (IOException ex) {
           System.out.println("Excepción de entrada/salida");
       }

    }
}
