/* Recurso compartido. Encapsulo el buzón y hago visibles los métodos 
   disponibles para LectorHilo y EscritorHilo.
   Posee la estructura de un monitor.
*/
package buzoncorreosincronizado;

public class BuzonCorreo {
    
    //Mensaje que almacena el buzón.
    private String mensaje = "";
    
    //Estado del buzón (true --> lleno / false --> vacío) 
    private boolean lleno = false;
    
    /* Método sincronizado para almacenar el mensaje. Es llamado por los 
       escritores.
       En el caso de que el buzón esté lleno, espera.
       Cuando el buzón está vacío, almacena el mensaje y avisa a los lectores.
    */
    public synchronized void almacena (String sms){
        
        while(lleno){
            //Si está lleno, espera (el proceso se tienen que bloquear)
            try{
                wait();
            }catch(InterruptedException e){
                System.err.println("Interrupción del hilo, no espera");
                System.err.println(e.toString());
            }
        }
        //Cuando ya está vacío
        this.mensaje = sms;
        // Como ya hay mensaje, el buzón está lleno
        lleno = true;
        //Aviso a los lectores (que estarían bloqueados en una cola porque el 
        //buzón estaba vacío), que el buzón ahora está lleno y por lo tanto 
        //se puede leer.
        notifyAll();
        
    }
    
    /* Método sincronizado para leer mensaje. Es utilizado por los lectores.
       Lee del buzon siempre que este lleno. En caso contrario, espera a que 
       se llene.
       Cuando lee, avisa con notify() para que se pueda escribir otro mensaje.
    */
    public synchronized String lee(){
        //Si el buzón está lee el mensaje
        while(!lleno){
            try {
                wait();//Espera hasta que el buzón esté lleno y se pueda leer.
            }catch (InterruptedException e){
                System.err.println ("Interrpción del hilo, no espera");
                System.err.println(e.toString());
            }
        }
        //Lee y cambia el buzón a vacío.
        lleno = false;
        //Avisa a los posibles escritores (que estarían bloqueados en una cola
        //porque el buzón estaba lleno), que el buzón está vacío y por lo tanto 
        //se puede escribir en él.
        notifyAll();
        //Devuelve el mensaje leído.
        return this.mensaje;
    }     
}
    
/*  Donde pone notifyAll() se podría poner solo notify(), entonces notificaría 
    a uno y en casos con muchos hilos, podría no funcionar, ya que desbloquea a 
    uno aleatorio y puede que alguna aplicación no funcione.
    Si hay muchos lectores y escritores puede ocurrir los siguente: Si hay un 
    escritor que almacena y se bloquea y luego hace notify un lector y ese 
    notify (imaginad que hay muchos escritores) notifica aleatoriamente a otro
    escritor, ese escritor se va a bloquear otra vez y se va a quedar el programa
    bloqueado totalmente.

    notifyAll() notifica a todos los hilos que estén bloqueados. Es menos 
    eficente pero más prácticos.
*/
