/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package psp02_buzoncorreo;

/**
 * <strong>Clase LectorHilo</strong><br>
 * Contiene las instrucciones que deben ejecutar los hilos encargados de leer
 * los mensajes en el recurso compartido buzón.
 *
 * @author Diego González García
 */
public class LectorHilo extends Thread {

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
     * @param p_nombre Nombre del lector
     * @param p_bc Buzón de correo
     */
    public LectorHilo(String p_nombre, BuzonCorreo p_bc) {
        nombre = p_nombre;
        bc = p_bc;
    }

    /**
     * <strong>Método de lectura.</strong><br>
     * El lector va a leer 20 mensajes. Cada mensaje que lee, lo imprime en la
     * salida estándar indicando el nombre del hilo que lo ha leído.<br>
     * Al finalizar, escribe en la salida estándar un mensaje indicando que ha
     * terminado.
     */
    @Override
    public void run() {
        for (int i = 1; i <= 20; i++) { //Bucle para leer los 20 mensajes
            System.out.println(nombre + " lee mensaje: " + bc.lee());    //Lee el mensaje del buzón
            try {
                sleep((int) (Math.random() * 1000)); //Esperamos
            } catch (InterruptedException ex) {
                System.err.println("Interrupción del hilo...");
                System.err.println(ex.toString());
            }
        }
        System.out.println("\t" + nombre + " ha dejado de leer");  //Indica que ha terminado de leer
    }

}
