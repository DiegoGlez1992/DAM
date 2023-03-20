
package buzoncorreosincronizado;

public class Main {

    
    public static void main(String[] args) {
        
        //Creamos el buzón. Es único. Solo se puede crear en el main para que
        //sea un recurso compartido por lectores y escritores.
        BuzonCorreo bc = new BuzonCorreo();
        
        //Se crean los hilos
        EscritorHilo escritor1 = new EscritorHilo ("Javier", bc);
        EscritorHilo escritor2 = new EscritorHilo ("Ana", bc);
        LectorHilo lector1 = new LectorHilo ("Vega", bc);
        LectorHilo lector2 = new LectorHilo ("Pablo", bc);
        
        //Lanzamos todos los hilos en paralelo.
        escritor1.start();
        escritor2.start();
        lector1.start();
        lector2.start();
        
        //Si no se pone los join, como el MAIN es también un hilo que dura muy
        //poco ya que solo tiene que lanzar los hilos, el mensaje de "FINAL" 
        //saldría antes de que se acabaran el resto de los hilos.
        try{
            escritor1.join();
            lector1.join();
            escritor2.join();
            lector2.join();
        }catch(InterruptedException e){
            System.err.println("Error. Ejecución interrumpida");
        }
        System.out.println("FINAL");
    }
    
}
