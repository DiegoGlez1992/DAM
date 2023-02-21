/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7.ejemplosincronizadoobjetos;

/**
 *
 * @author Nuria
 */
public class ContadorHilo extends Thread{
    Contador cont;
    ContadorHilo(String p_nombre,Contador p_cont){
        super(p_nombre);
        this.cont=p_cont;
    }
    //Metodo para incrementar el contador
    public void run(){
        for(int i=0;i<3;i++){
            try{
                //Usamos el metodo synchronized con el objeto que queremos sincronizar.
                synchronized(cont){
                    int a=cont.getContador();
                    sleep((long)(Math.random()*1000));
                    cont.setContador(a+1);
                    System.out.println(getName()+" - contador: "+cont.getContador());
                    sleep((long)(Math.random()*1000));
                }
            }catch(InterruptedException e){
                System.out.println("Interrupción del hilo...");
            }
            
        }System.out.println("Fin "+getName()+"...");
    }
}
