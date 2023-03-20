
package cliente_g;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


class Cliente {
    
    static final String Host = "localhost";
    
    // Puerto por el que se va a conectar al servidor
    static final int Puerto = 2223;
    
    public Cliente(){
    
        DataInputStream flujo_entrada = null;
        DataOutputStream flujo_salida = null;
        String datos;
        
        try{
            String fichero = new String(); //String para almacenar el nombre del fichero.
            
            Socket sCliente = new Socket (Host, Puerto);
            System.out.println("El cliente se conecta ....");
            
            //Creamos los flujos de E/S de comunicación con el servidor
            flujo_entrada = new DataInputStream(sCliente.getInputStream());
            flujo_salida = new DataOutputStream(sCliente.getOutputStream());
            
            //CUERPO DEL ALGORITMO
            
            //Pido al cliente el nombre del fichero
            System.out.print("Nombre del fichero a leer: ");
            
            //Lectura del nombre del fichero desde el teclado
            BufferedReader br = new BufferedReader (new InputStreamReader(System.in));
            fichero = br.readLine();
            
            //Se manda el nombre del fichero al Servidor
            flujo_salida.writeUTF(fichero);       
            
            //Creo un fichero de salida para recoger los datos enviados por el 
            //servidor.
            FileWriter fw = new FileWriter(fichero);
            BufferedWriter bw = new BufferedWriter (fw);
            
            System.out.println();
            //Se leen los datos que envía el servidor.
            do{
               datos = flujo_entrada.readUTF();
               bw.write(datos); //Escribe el contenido de datos
               bw.newLine();    //Escribe un cambio de linea 
               
               /* La otra forma de hacerlo es:
                    fw.write(datos);
                    fw.write("\n");
                  No es la más adecuado porque la representación del salto de
                  linea es distinto en las diferentes plataformas. Además el uso
                  de Buffered hace que el programa sea más eficiente.
               */
               
               if(!datos.equals("EOF"))
                   System.out.println(datos);  
            }while(!datos.endsWith("EOF"));
            
            //MUY IMPORTANTE: cuando se utilizan flujos es imprescindible
            //cerrarlos. Sobre todo en los de escritura.
            //No hace falta cerra el fw, ya que cerramos bw que es más externo.           
            bw.close();
            br.close();
            flujo_entrada.close();
            flujo_salida.close();
                      
            
            //Cierro el socket
            sCliente.close();
            
        }catch(IOException e){
            System.out.println("Error de E/S en el cliente");
        }
    }
}
