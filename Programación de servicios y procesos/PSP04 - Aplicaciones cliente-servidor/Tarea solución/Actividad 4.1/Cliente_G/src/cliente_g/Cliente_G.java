
package cliente_g;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


class Cliente_G {
    
    //Host es un string que guarda el nombre o dirección IP  
    //Ponemos localhost porque estoy ejecutándolo todo en el mismo ordenador.
    //se puede cambiar por una IP
    static final String Host = "localhost";
    
    // Puerto por el que se va a conectar al servidor
    static final int PUERTO = 5000;
    
    public Cliente_G(){
    
        try{
            //Conexión del cliente al servidor en un determinado puerto (donde
            //está escuchando el servidor).
            Socket s_Cliente = new Socket (Host, PUERTO);
            System.out.println("El cliente se conecta ....");
            
            //TAREAS QUE REALIZA EL CLIENTE
            
            DataInputStream flujo_entrada = new DataInputStream(s_Cliente.getInputStream());
            DataOutputStream flujo_salida = new DataOutputStream(s_Cliente.getOutputStream());
            
            //Cierro el socket
            s_Cliente.close();
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }finally{
            flujo_salida.close();
        }
    }
}
