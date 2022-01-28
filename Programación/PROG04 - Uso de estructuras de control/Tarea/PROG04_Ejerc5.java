/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog04_ejerc5;

import java.util.Scanner;

/**
 * Cuando dividimos un número entre 0 se genera un valor indeterminado. En 
 * cualquier lenguaje de programación este tipo de operaciones genera un error 
 * de ejecución que debe ser controlado desde el código para evitar malas 
 * experiencias al usuario. En Java, cuando se produce esta operación se genera 
 * la excepción ArithmeticException. Queremos implementar un programa Java que 
 * calcule la división de dos números solicitados por teclado (dividendo y 
 * divisor). El programa solicitará números indefinidamente hasta que los dos 
 * números solicitados sean -1. Se debe controlar mediante excepciones que el 
 * divisor no sea 0. En caso de serlo, se mostrará un mensaje por pantalla. 
 * También habrá que mostrar por pantalla el número de divisiones calculadas. 
 * Utiliza número enteros para las variables.
 * 
 * @author diego
 * @version 1.0
 */
public class PROG04_Ejerc5 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int dividendo, divisor, resultado, numDivisiones=0;
        Scanner teclado = new Scanner( System.in ); //Clase Scanner para la introducción de datos por teclado
        for(;;){    //Bucle infinito
                System.out.print("Dividendo: ");
                dividendo=teclado.nextInt();    //Solicitamos el número
                System.out.print("Divisor: ");
                divisor=teclado.nextInt();  //Solicitamos el número
            try{    //Buscamos si se produce una excepción
                resultado=dividendo/divisor;
                numDivisiones++;    //Incrementamos el número de diviones realizadas
            }catch(ArithmeticException exc){    //Tratamos la excepción capturada "ArithmeticException" por dividir entre 0
                System.out.println("Error! El divisor no puede ser 0");
            }
            if (dividendo == -1 && divisor == -1)   //Si dividendo y divisor son = -1
                break;  //Se termina el programa al salir del bucle
            System.out.println("Número de divisiones calculadas: "+numDivisiones);  //Mostramos el número de divisiones realizadas
        }
    }
    
}



