
package Tarea1_servidor_http_g;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * *****************************************************************************
 * Servidor HTTP que atiende peticiones de tipo 'GET' recibidas por el puerto 
 * 8066, capaz de gestionar la concurrencia mediante hilos.
 *
 * NOTA: para probar este código, comprueba primero de que no tienes ningún otro
 * servicio por el puerto 8066 (por ejemplo, con el comando 'netstat' si estás
 * utilizando Windows)
 */

class Servidor_HTTP_Multihilo_G {

  /**
   * **************************************************************************
   * Procedimiento principal que asigna a cada petición entrante un socket 
   * cliente, por donde se enviará la respuesta una vez procesada 
   *
   */
    public static void main(String[] args) {
        
        //Variables locales
        ServerSocket skServidor;
        Socket sCliente;
        HiloPeticiones hilo;
        
        try{
            //Asociamos al servidor el puerto 8066
             skServidor= new ServerSocket(8066);
            
            //Mensaje de ejecución del servidor e instruciones para el cliente.
            imprimeDisponible();
            
                        
            //Ante una petición entrante, procesa la petición por el socket cliente
            //por donde la recibe.
            //El servidor permanece abierto indefinidamente
            while(true){
                //Ante una petición entrante, asigna un socket cliente para 
                //enviar la respuesta a la petición.
                sCliente = skServidor.accept();
                System.out.println("\n\n --> Cliente conectado. Atendiendo ...");
                
                //Se crea un nuevo hilo para atender la petición por el socket del cliente
                hilo = new HiloPeticiones(sCliente);
                
                //Se arranca el hilo
                hilo.start();
                
                System.out.println("Cliente desconectado. Fin de la atención.");
            }   
        }catch(IOException e){
            System.out.println("ERROR: "+ e.getMessage());
        }catch(Exception ex){
             System.out.println("ERROR: "+ ex.getMessage());
        }
    }
    
   
   /***************************************************************************
   * Muestra un mensaje en la Salida que confirma el arranque, y da algunas
   * indicaciones posteriores
   ****************************************************************************/
    private static void imprimeDisponible() {

        System.out.println("""
                           El Servidor WEB se está ejecutando y permanece a la escucha por el puerto 8066.

                           Escribe en la barra de direcciones de tu explorador preferido:

                           http://localhost:8066
                           para solicitar la p\u00e1gina de bienvenida

                           http://localhost:8066/quijote
                            para solicitar una p\u00e1gina del Quijote,

                           http://localhost:8066/q
                            para simular un error""");
        System.out.println("\n\nLa gestión de la concurrencia mediante hilos, le permite atender"
                + " varias peticiones a la vez.");
    }
}
  
