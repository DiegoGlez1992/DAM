 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg4.usoyield;


public class SimpleThread2 extends Thread {
    public SimpleThread2(String nombre){
        super(nombre);
    }
    public void run(){
        for(int i=0;i<10;i++){
            System.out.println(i+" "+getName());
            yield();
            /*Cada vez que un hilo imprime su correspondiente número, su nombre da paso al 
             otro hilo.*/
        }
        System.out.println("Fin! "+getName());
    }
}
