/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg8.productorconsumidor;


/*Java implementa un monitor. Se compone de un atributo recipiente donde almacena un valor
 entero. Debe contar con dos métodos para almacenar y descargar el contenido.
 Si el recipiente no se ha vaciado, hay que esperar a que un hilo consumidor lo vacie.
 Una vez vaciado, debe notificar que está vacio, por si hay algún productor esperando a llenarlo.
 Si no se ha llenado el recipiente, el consumidor espera a que se llene. Una vez llenado, se notifica
 que está lleno, por si hay algún hilo consumidor bloqueado para poder vaciar el contenido del 
 recipiente.*/
public class Recipiente {
    private int contenido; //almacenamos el contenido del recipiente
    private boolean lleno=false;//nos indica si el recipiente está lleno o no
    /*Método get, lo usan los consumidores. Si no está lleno, se bloquea hasta que se llene.
     Cuando está lleno, sale del bloqueo, indica que coge el contenido del recipiente, lo deja
     vacio y notifica a los procesos que puedan estar bloqueados esperando para llenarlo.*/
    public synchronized int get(){
        while(lleno==false){
            try{
                wait();
            }catch(InterruptedException e){
                System.out.println("Interrupcion del hilo...");
            }
        }
        lleno=false;
        notifyAll();
        return contenido;
    }
    /*Método put, lo usan los productores. Si el recipiente está lleno, se queda bloqueado
     esperando la notificación de que está vacio. Cuando se le comunica, llena el recipiente
     y notifica que está lleno a los hilos que puedan estar bloqueados a la espera de que
     se llenara.*/
    public synchronized void put(int valor){
        while(lleno==true){
            try{
                wait();
            }catch(InterruptedException e){
                System.out.println("Interrupcion del hilo...");
            }
        }
        contenido=valor;
        lleno=true;
        notifyAll();
    }
/*OJO: DIFERENCIA ENTRE WAIT Y SLEEP. Con wait, el hilo se bloquea y el control del monitor
 queda liberado para que otro hilo pueda llamar al otro proceso de la clase. Con sleep, el hilo se
 bloquea y no libera el monitor, con lo que impide que lo use otro hilo.*/    
}
