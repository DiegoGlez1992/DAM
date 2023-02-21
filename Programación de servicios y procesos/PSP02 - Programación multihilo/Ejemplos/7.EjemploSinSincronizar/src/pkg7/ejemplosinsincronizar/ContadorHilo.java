/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7.ejemplosinsincronizar;


/*Cada hilo accedera tres veces al recurso compartido Contador y lo
 incrementara en 1, durmiendo un tiempo aleatorio en cada iteración*/
public class ContadorHilo extends Thread{
    Contador cont;
    ContadorHilo (String p_nombre, Contador p_cont){
        super(p_nombre);
        this.cont=p_cont;
    }
    //Metodo para incrementar el contador
    public void run(){
        for(int i=0;i<3;i++){
            try{
                int a=cont.getContador();
                sleep((long)(Math.random()*10));
                a=a+1;
                sleep((long)(Math.random()*10));
                cont.setContador(a);
                System.out.println(getName()+" - contador: "+cont.getContador());
                sleep((long)(Math.random()*10));
            }catch(InterruptedException e){
                System.out.println("Interrupcion del hilo...");
            }
            
        }
        System.out.println("Fin "+getName()+"...");
    }
}
