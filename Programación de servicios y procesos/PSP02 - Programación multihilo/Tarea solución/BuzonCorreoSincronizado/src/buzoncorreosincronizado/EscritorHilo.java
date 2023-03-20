/* Instrucciones que ejecutan los hilos que escriben el mensaje.

    El escritor escribe 20 mensajes. Para ello hace uso del método 
    almacena(mensaje) del buzón.
    En el mensaje escribe el número de mensajes enviados y su nombre
    Al final, escribe en la salida un mensaje diciendo que ha terminado.
      
 */
package buzoncorreosincronizado;

public class EscritorHilo extends Thread {
    BuzonCorreo bc;
    String mensaje;
    
    public EscritorHilo(String nombre, BuzonCorreo bc){
        this.bc = bc;
        this.mensaje = nombre;
    }
    @Override
    public void run(){
        //Se mandan 20 mensajes (10 por cada escritor)
        for(int i=1; i<=10; i++){
            bc.almacena(mensaje + " escribe el mensaje número " + i);
            
            try{    //Tiempo entre escrituras
                sleep((int)(Math.random()*1000));
            }catch (InterruptedException e){
                System.err.println ("Hilo interrumpido");
            }
        }
        System.out.println (mensaje + " ha terminado de escribir");
    }
}
