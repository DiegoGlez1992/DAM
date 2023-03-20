/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package psp02_buzoncorreo;

/**
 * <strong>Clase EscritorHilo</strong><br>
 * Contiene las instrucciones que deben ejecutar los hilos encargados de
 * escribir mensajes en el recurso compartido buzón.
 *
 * @author Diego González García
 */
public class EscritorHilo extends Thread {

    /**
     * Almacena la referencia al buzón
     */
    private BuzonCorreo bc;

    /**
     * Nombre del escritor
     */
    private String nombre;

    /**
     * <strong>Método constructor parametrizado.</strong><br>
     *
     * @param p_nombre Nombre del escritor
     * @param p_bc Buzón de correo
     */
    public EscritorHilo(String p_nombre, BuzonCorreo p_bc) {
        nombre = p_nombre;
        bc = p_bc;
    }

    /**
     * <strong>Método de escritura.</strong><br>
     * El escritor va a escribir 20 mensajes. En cada mensaje se escribe el
     * número de mensajes que lleva enviados y su nombre.<br>
     * Al finalizar, escribe en la salida estándar un mensaje indicando que ha
     * terminado.
     */
    @Override
    public void run() {
        for (int i = 1; i <= 20; i++) { //Bucle para escribir los 20 mensajes
            bc.almacena(nombre + " escribe mensaje numero " + i);   //Escribe el mensaje en el buzón
            try {
                sleep((int) (Math.random() * 1000)); //Esperamos
            } catch (InterruptedException ex) {
                System.err.println("Interrupción del hilo...");
                System.err.println(ex.toString());
            }
        }
        System.out.println("\t" + nombre + " ha dejado de escribir");  //Indica que ha terminado de estribir
    }

}
