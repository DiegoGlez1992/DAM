
package cliente_g;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.IOException;


class Cliente_G2 {
    
    //Host es un string que guarda el nombre o dirección IP  
    //Ponemos localhost porque estoy ejecutándolo todo en el mismo ordenador.
    //se puede cambiar por una IP
    static final String HOST = "localhost";
    
    // Puerto por el que se va a conectar al servidor
    static final int PUERTO = 5000;
    
    public Cliente_G2(){
        
        String mensaje=""; // String para recoger los mensajes del servidor
        String teclado=""; //String que recibe el comando a ejecutar,
                                   // el usuario, la contraseña... 
       
        try{       
            //Conexión del cliente al servidor en un determinado puerto (donde
            //está escuchando el servidor).
            Socket skCliente = new Socket (HOST, PUERTO);
            System.out.println("Conexión correcta por el puerto " + PUERTO+ "\n");
            
            // Se crean los flujos de entrada y salida
            DataInputStream flujo_entrada = new DataInputStream(skCliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(skCliente.getOutputStream());
            
            //Recibe el mensaje de bienvenida del servidor 
            do{
                mensaje = flujo_entrada.readUTF();
                if(!mensaje.equals("EOF"))
                    System.out.println(mensaje);
            }while(!mensaje.equals("EOF"));
            
            //Para leer del teclado
            BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
            teclado = lector.readLine();
            
            do{
                flujo_salida.writeUTF(teclado);
                
                do{
                    mensaje = flujo_entrada.readUTF();
                    if(!mensaje.equals("EOF"))
                        System.out.println(mensaje);
                }while(!mensaje.equals("EOF"));
                
                teclado = lector.readLine();                
            }while(!teclado.toLowerCase().equals("exit"));

            
            System.out.println("\n\t*********** FIN DE PROGRAMA ***********\n");
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

            