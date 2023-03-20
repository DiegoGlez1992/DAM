
package servidor_g;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    
    //Puerto por el que el Servidor escucha
    static final int Puerto = 2223;
    
    
    public Servidor(){
        
        //Debo crear los flujos fuera del try para poder cerrarlos con finally 
        //ya que éste está a la misma altura que el try y daría error (dice que
        //no existe). Por lo tanto siempre deben de estar en un bloque anterior
        //al try.
        DataInputStream flujo_entrada = null;
        DataOutputStream flujo_salida = null;
        BufferedReader br = null;
        
        try{
            //Inicio el servidor en el puerto indicado.
            ServerSocket skServidor = new ServerSocket(Puerto);
            
            System.out.println("Escuchando en el puerto " + Puerto);
            
            //Se conecta un cliente
            Socket sCliente =skServidor.accept();
           
            //Creamos los flujos de E/S de comunicación con el cliente.
            flujo_entrada = new DataInputStream(sCliente.getInputStream());
            flujo_salida = new DataOutputStream(sCliente.getOutputStream());
            
            //CUERPO DEL ALGORITMO
            
            // 1º Se recibe el nombre del fichero:
            
            String fichero; //Guarda el nombre del fichero solicitado
            fichero = flujo_entrada.readUTF();
            
            System.out.println("Fichero solicitado por el cliente: " + fichero);
            
            //El fichero, obviamente, tiene que estar en el servidor. Suponemos 
            //que está en la raiz del proyecto del servidor.
            //Encapsulo el FileReader del nombre del fichero que se ha pasado
            //en un BufferedReader (sobre todo para tener el método readLine() 
            //que permite leer línea a línea un fichero, devolviendo un String.
            br = new BufferedReader (new FileReader(fichero));
            
            String linea ="";
            
            while((linea = br.readLine())!= null)
                flujo_salida.writeUTF(linea);
            
            //Indicamos al cliente que el fichero ha sido enviado correctamente
            System.out.println("El fichero " + fichero + "ha sido enviado "+
                    "correctamente.");
            
            flujo_salida.writeUTF("EOF");
            
            /* Cierro los flujos - Podría ser de la siguiente manera, pero lo
               voy a hacer de la más correcta que es usando finally.
               
               br.close();
               flujo_salida.close();
               flujo_entrada.close();
            
            */
             //Cierro socket del cliente
            sCliente.close();
            
            //Cierro socket del servidor
            skServidor.close();
            
        }catch(IOException e){
            System.out.println("Excepción de E/S");
        }finally{
            if(br != null){
                try{
                    br.close();
                }catch(IOException e){
                    System.out.println("Excepción de E/S al cerrar br");
                }             
            }
            
            if(flujo_salida != null){
                try{
                    flujo_salida.close();
                }catch(IOException e){
                    System.out.println("Excepción de E/S al cerrar flujo_salida");
                }  
            }
            if(flujo_entrada != null){
                try{
                    flujo_salida.close();
                }catch(IOException e){
                    System.out.println("Excepción de E/S al cerrar flujo_entrada");
                }  
            }
        }
    }     
}
