/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog04_ejerc1;

import java.util.Scanner;

/**
 *
 * @author 
 */
public class PROG04_Ejerc1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sca=new Scanner(System.in);
        
        System.out.println("Introduce un número para mostrar su tabla de multiplicar");
        
        int num=sca.nextInt();
        
        if (num>=30) System.out.println("El número introducido en mayor que 30");
        else {
            
            //tabla de multiplicar con bucle for
            System.out.println("Tabla de multiplicar el número " + num + " con bucle FOR.");
            
            for (int i=0; i<=10; i++){
                System.out.println (num + " * " + i + " = " + i*num);
            }
            
             //tabla de multiplicar con bucle while
            System.out.println("Tabla de multiplicar el número " + num + " con bucle WHILE.");
            
            int i=0;
            while (i<=10){
                System.out.println (num + " * " + i + " = " + i*num);
                i++;
            }
            
            //tabla de multiplicar con bucle do-while
            System.out.println("Tabla de multiplicar el número " + num + " con bucle DO-WHILE.");
            
            i=0;
            do {
                System.out.println (num + " * " + i + " = " + i*num);
                i++;
            } while (i<=10);
        }
    }
    
}
