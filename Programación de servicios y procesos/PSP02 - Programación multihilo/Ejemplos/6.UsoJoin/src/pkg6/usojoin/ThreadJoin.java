/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg6.usojoin;


public class ThreadJoin extends Thread{
    public ThreadJoin(String nombre){
        super(nombre);
    }
    public void run(){
        try{
            for(int i=0;i<5;i++){
                System.out.println(getName()+" "+i);
                sleep(1000);
            }
        }catch (InterruptedException e){}
    }
}
