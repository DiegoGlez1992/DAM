/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg5.usosleep;


public class SimpleThread3 extends Thread {
    public SimpleThread3(String nombre){
        super(nombre);
    }
    public void run(){
        for(int i=0;i<10;i++){
            System.out.println(i+" "+getName());
            try{
               //sleep((long)(Math.random()*1000));
                sleep(1000);
               /*Con math.random se genera un número entre 0,0 y 0,1, que al 
                multiplicarlo por 1000 produce una espera entre 0 y 1 segundo*/
            }catch(InterruptedException e){}
            
        }
        System.out.println("Fin! "+getName());
    }
}
