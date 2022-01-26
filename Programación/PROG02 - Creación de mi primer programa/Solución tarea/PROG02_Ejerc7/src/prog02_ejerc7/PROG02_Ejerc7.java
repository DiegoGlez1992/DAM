/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog02_ejerc7;

/**
 *
 * @author 
 */
public class PROG02_Ejerc7 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        int C1=34;
        int C2=56;
        
        //despejamos la x y el valor lo convertimos a float.
        float x=(float)-C2/C1;
        
        System.out.println ("El valor de x en la ecuación C1x + C2 = 0 si C1 es igual a " + C1 + " y C2 es igual a " + C2 + " es");
        //Mostramos el número con 4 decimales.
        System.out.printf ("%.4f", x);
    }
    
}
