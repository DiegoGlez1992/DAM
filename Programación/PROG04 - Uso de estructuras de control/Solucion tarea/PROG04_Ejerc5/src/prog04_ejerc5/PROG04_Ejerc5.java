/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog04_ejerc5;

import java.util.Scanner;

/**
 *
 * @author 
 */
public class PROG04_Ejerc5 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sca=new Scanner (System.in);
        int dividendo, divisor, numoperaciones=0;
        int resul;
        
        do {
            System.out.println ("Introduce el valor del dividendo");
            dividendo=sca.nextInt();
            System.out.println("Introduce el valor del divisor");
            divisor=sca.nextInt();
            
            if (dividendo!=-1 && divisor!=-1){
                try {
                    resul=dividendo/divisor;
                    numoperaciones++;
                    System.out.println("El resultado de la división es " + resul);
                }
                catch (ArithmeticException e){
                    System.out.println ("El valor del divisor no puede ser 0. Resultado indeterminado");
                }
            }
        }
        while (dividendo!=-1 && divisor!=-1);
        
        System.out.println("El número de operaciones calculadas es " + numoperaciones);

    }
    
}
