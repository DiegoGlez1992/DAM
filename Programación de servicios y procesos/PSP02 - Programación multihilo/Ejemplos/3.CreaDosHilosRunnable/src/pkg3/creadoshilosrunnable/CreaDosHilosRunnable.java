/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3.creadoshilosrunnable;


public class CreaDosHilosRunnable {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //Creamos dos instancias de la clase SimpleThreadRunnable.
        SimpleThreadRunnable hilo1=new SimpleThreadRunnable("Hilo 1");
        SimpleThreadRunnable hilo2=new SimpleThreadRunnable("Hilo 2");
        
   
        //Creamos dos instancias de Thread utilizando los objetos de la interfaz Runnable.
       Thread h1=new Thread(hilo1);
        Thread h2=new Thread(hilo2);
        h1.start();
        h2.start();
     }
}
