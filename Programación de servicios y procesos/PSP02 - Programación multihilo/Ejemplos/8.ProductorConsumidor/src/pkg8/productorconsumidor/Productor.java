/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg8.productorconsumidor;

/**
 *
 * @author jfranco
 */
public class Productor extends Thread {
    private Recipiente rec; //almacena la referencia al recipiente comun.
    private int num; //Sirve como identificador del hilo, en caso de que haya más de un 
                     //hilo productor
    public Productor(Recipiente p_rec, int p_num){
        rec=p_rec;
        num=p_num;
    }
    /*El metodo run invoca a put del recipiente para llenarlo y espera un tiempo determinado
     antes de volver a llenar el recipiente*/
    public void run(){
        for(int i=0;i<10;i++){
            rec.put(i);
            System.out.println("Productor "+this.num+" pone: "+i);
            try{
                sleep((int)(Math.random()*1000));
            }catch(InterruptedException e){
                System.out.println("Interrupción del hilo...");
            }
        }
    }
    
}
