/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7.ejemplosincronizado;


public class ContadorHilo extends Thread{
    Contador cont;
    ContadorHilo(String p_nombre, Contador p_cont){
        super(p_nombre);
        this.cont=p_cont;
    }
    //Método para incrementar el contador
    public void run(){
        for(int i=0;i<3;i++){
            System.out.println(getName()+ " - contador: "+cont.incContador(1));
        }
        System.out.println("Fin "+getName()+"...");
    }
}
