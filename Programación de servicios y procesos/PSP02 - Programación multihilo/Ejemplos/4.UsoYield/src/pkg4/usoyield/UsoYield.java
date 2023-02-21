/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg4.usoyield;


public class UsoYield {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SimpleThread2 hilo1=new SimpleThread2("Hilo1");
        SimpleThread2 hilo2=new SimpleThread2("Hilo2");
        hilo1.start();
        hilo2.start();
      
        
    }
}
