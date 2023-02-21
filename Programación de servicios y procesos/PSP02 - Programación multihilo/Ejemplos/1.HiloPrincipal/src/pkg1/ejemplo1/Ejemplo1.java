/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1.ejemplo1;

public class Ejemplo1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Thread t =Thread.currentThread();
        //definimos el hilo e invocamos a currentThread, para darle la referencia al hilo
        //que se está ejecutando en este momento.
        System.out.println("Hilo Actual: "+t);
        System.out.println("===========================");
        System.out.println(t.getName());
        //devuelve el nombre del hilo
        System.out.println(t.toString());
        //devuelve un string donde aparece el nombre del hilo, la prioridad y el grupo
        System.out.println(t.activeCount());
        //devuelve un número estimado de hilos dentro de este grupo
        System.out.println("===========================");
        t.setName("Mi hilo");
       
        System.out.println("Despues del cambio de nombre: "+t);
        try{
            for(int n=5;n>0;n--){
               System.out.println(n);
               Thread.sleep(1000);//podemos dormir al hilo durante un segundo
            }        
        }catch(InterruptedException e){
            System.out.println("Interrupcion del hilo principal");
        }
    }
}
