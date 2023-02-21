/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7.ejemplosinsincronizar;


/*Clase contador: recurso compartido por los procesos. Región crítica*/
public class Contador {
    protected int contador=0;
    //Devuelve el valor actual del contador.
    public int getContador(){
        return this.contador;
    }
    //Establece el valor actual del contador
    public void setContador(int val){
        this.contador=val;
    }
}
