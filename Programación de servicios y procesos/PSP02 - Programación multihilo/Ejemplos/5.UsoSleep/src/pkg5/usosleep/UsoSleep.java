/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg5.usosleep;


public class UsoSleep {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SimpleThread3 hilo1=new SimpleThread3("Hilo1");
        SimpleThread3 hilo2=new SimpleThread3("Hilo2");
        hilo1.start();
        hilo2.start();
    }
}
