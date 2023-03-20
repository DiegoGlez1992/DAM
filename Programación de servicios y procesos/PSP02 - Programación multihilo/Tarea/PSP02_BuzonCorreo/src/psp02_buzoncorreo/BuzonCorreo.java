/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package psp02_buzoncorreo;

/**
 * <strong>Clase BuzonCorreo</strong><br>
 * Clase que encapsula el recurso compartido buzón, que contiene los métodos
 * disponibles para los lectores y escritores. Estos realizarán un acceso de
 * forma sincronizada al buzón.<br>
 * La clase así concebida posee la estructura de un monitor.<br>
 * <strong>OJO:</strong>Las esperas se realizan con WAIT, de modo que el hilo se
 * bloquea, pero el monitor queda liberado para que otro hilo pueda llamar a
 * otro proceso de la clase. Si lo hiciesemos con SLEEP, el hilo se bloquea y el
 * monitor no es liberado, con lo que impide que lo use otro hilo.
 *
 * @author Diego González García
 */
public class BuzonCorreo {

    /**
     * Almacén de mensaje
     */
    private String mensaje;

    /**
     * Controla si existe un mensaje disponible
     */
    private boolean lleno;

    /**
     * <strong>Método para almacenar mensajes en el buzón.</strong><br>
     * Únicamente se podrán almacenar mensajes si el buzón está vacío. De lo
     * contrario el proceso deberá esperar a que se vacíe el buzón.<br>
     * Una vez que el mensaje se ha almacenado, se avisa a los hilos que estén
     * esperando a leer el buzón.
     *
     * @param men Mensaje para almacenar
     */
    public synchronized void almacena(String men) {
        while (lleno == true) { //Mientras que el buzón esté lleno
            try {
                wait(); //Esperamos
            } catch (InterruptedException ex) {
                System.err.println("Interrupcion del hilo...");
                System.err.println(ex.toString());
            }
        }
        mensaje = men;  //Almacenamos el mensaje
        lleno = true;   //Indicamos que el buzón está lleno
        notifyAll();    //Notificamos al resto de hilos
    }

    /**
     * <strong>Método para leer los mensajes del buzón.</strong><br>
     * Únicamente se podrán leer mensajes cuando el buzón esté lleno. De lo
     * contrario el proceso deberá esperar a que se llene el buzón.<br>
     * Una vez que el mensaje se ha leído, se avisa a los hilos que estén
     * esperando a escribir en el buzón.
     *
     * @return Mensaje almacenado
     */
    public synchronized String lee() {
        while (lleno == false) {    //Mientras que el buzón no esté lleno
            try {
                wait(); //Esperamos
            } catch (InterruptedException ex) {
                System.err.println("Interrupcion del hilo...");
                System.err.println(ex.toString());
            }
        }
        lleno = false;  //Indicamos que el buzón está vacío
        notifyAll();    //Notificamos al resto de hilos
        return mensaje; //Devolvemos el mensaje
    }
}
