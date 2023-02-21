/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3.creadoshilosrunnable;

//Implementamos la interfaz Runnable.
public class SimpleThreadRunnable implements Runnable{
    String nombre;
    public SimpleThreadRunnable(String nom){
        this.nombre=nom;
    }
    public void run(){
        for(int i=0;i<10;i++){
            System.out.println(i+" "+nombre);
        }
        System.out.println("Fin! "+nombre);
    }
}
