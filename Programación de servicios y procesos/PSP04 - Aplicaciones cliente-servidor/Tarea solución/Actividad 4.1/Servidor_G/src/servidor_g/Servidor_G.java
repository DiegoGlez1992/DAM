
package servidor_g;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


class Servidor_G extends Thread{
    
    //Por cada hilo se crea un Socket para el cliente
    Socket skCliente;
    
    public Servidor_G(Socket sCliente){
        skCliente = sCliente;
    }
    
    @Override
    public void run(){
        
        try{
            // Creo los flujos de entrada y salida
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());
            
            // Instrucciones para atender al cliente
            

            // Se cierran los flujos y la conexión
            flujo_entrada.close();
            flujo_salida.close();
            
            skCliente.close();
            
            System.out.println("Cliente desconectado");
            
        } catch (IOException ex) {
            Logger.getLogger(Servidor_G.class.getName()).log(Level.SEVERE, null, ex);
        } 
      
    }
}
           
