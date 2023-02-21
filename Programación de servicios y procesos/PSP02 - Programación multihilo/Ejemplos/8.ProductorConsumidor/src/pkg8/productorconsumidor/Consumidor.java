/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg8.productorconsumidor;

/**
 *
 * @author jfranco
 */
public class Consumidor extends Thread{
    private Recipiente rec;  //referencia al recipiente que tienen en comun
    private int num;//identificador del hilo consumidor
    public Consumidor(Recipiente p_rec, int p_num){
        rec=p_rec;
        num=p_num;
    }
    /*El hilo consumidor invoca al get del recipiente para vaciarlo, y se bloquea unos segundos.*/
    public void run(){
        int val=0;
        for(int i=0;i<10;i++){
            val=rec.get();
            System.out.println("Consumidor "+this.num+" toma: "+val);
            try{
                sleep((int)(Math.random()*1000));
            }catch(InterruptedException e){
                System.out.println("Interrupción del hilo...");
            }
        }
    }
}
