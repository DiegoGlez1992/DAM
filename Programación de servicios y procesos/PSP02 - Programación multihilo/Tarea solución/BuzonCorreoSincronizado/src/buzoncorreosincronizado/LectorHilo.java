/*  El lector va a leer 20 mensajes, haciendo uso de lee() de Buzon
    Cada mensaje que lee lo imprime en la salida estándar indicando 
    el nombre del hilo que lo ha leido
    Al finalizar escribe un mensaje en la salida estándar indicando 
    que ha terminado.
 */
package buzoncorreosincronizado;

public class LectorHilo extends Thread {
    BuzonCorreo bc;
    String mensaje;
    
    public LectorHilo(String nombre, BuzonCorreo bc){
        this.bc = bc;
        this.mensaje = nombre;
    }
    @Override
    public void run(){
        //Se leen 20 mensajes 
        for(int i=1; i<=10; i++){
            System.out.println(mensaje + " lee mensaje:  " + bc.lee());
            
            try{//Tiempo entre lecturas
                sleep((int)(Math.random()*1000));
            }catch (InterruptedException e){
                System.err.println ("Hilo interrumpido");
            }
        }
        System.out.println (mensaje + " ha terminado de leer");
    }
}