/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg6.usojoin;


public class UsoJoin {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Inicio - main");
        ThreadJoin hilo1=new ThreadJoin("Hilo1");
        ThreadJoin hilo2=new ThreadJoin("Hilo2");
        ThreadJoin hilo3=new ThreadJoin("Hilo3");
        ThreadJoin hilo4=new ThreadJoin("Hilo4");
        
        hilo1.start();
        hilo2.start();
        hilo3.start();
        hilo4.start();
        
        try{
            hilo1.join();
            hilo2.join();
            hilo3.join();
            hilo4.join();

        }catch(InterruptedException e){}
        
                    System.out.println("Fin - main");
    }
}
