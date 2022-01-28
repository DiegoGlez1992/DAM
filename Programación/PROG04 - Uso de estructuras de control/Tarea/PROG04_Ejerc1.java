/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog04_ejerc1;

import java.util.Scanner;

/**
 * Implementar un programa que muestre la tabla de multiplicar de un número 
 * leido desde teclado utilizando al menos tres bucles diferentes. El número 
 * leído desde teclado debe ser menor que 30. En caso contrario se mostrará un 
 * mensaje por pantalla y el programa finalizará.
 *
 * @author diego
 * @version 1.0
 */
public class PROG04_Ejerc1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int num;
        int a=0, b=0, c=0;
        Scanner teclado = new Scanner( System.in ); //Clase Scanner para la introducción de datos por teclado
        System.out.printf( "Introduzca un número menor que 30: ");
        num = teclado.nextInt(); //Solicitamos el número
        while(num<30){
            System.out.printf("Bucle for:\n");
            for(a=0; a<=10; a++)    //Creamos bucle for para mostrar la tabla de multiplicar
                System.out.printf("%d x %d = %d\n",num,a, num*a);
            System.out.printf("Bucle while:\n");
            while(b<=10){   //Creamos bucle while para mostrar la tabla de multiplicar
                System.out.printf("%d x %d = %d\n",num,b, num*b);
                b++;
            }
            b=0;    //Reiniciamos la variable
            System.out.printf("Bucle do while:\n");
            do{ //Creamos bucle do while para mostrar la tabla de multiplicar
                System.out.printf("%d x %d = %d\n",num,c, num*c);
                c++;
            }
            while(c<=10);
            System.out.printf( "Introduzca un número menor que 30: ");
            num = teclado.nextInt(); //Solicitamos el número
        }
        System.out.printf( "El número indicado no es menor que 30\n");
    }
    
}
