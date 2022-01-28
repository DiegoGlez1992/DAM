/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog04_ejerc2;

import java.util.Scanner;

/**
 * Un número es primo si solo tiene dos divisores: el 1 y el propio número. 
 * Implementa un programa Java que pida por teclado 5 números. Para cada uno de 
 * ellos:
 * - Comprueba si es negativo. En caso afirmativo, muestra el mensaje por 
 *   pantalla "El número es negativo".
 * - Si es positivo, deberá mostrar por pantalla si es primo o no.
 * - Procesados los 5 números, el programa finaliza.
 * 
 * @author diego
 * @version 1.0
 */
public class PROG04_Ejerc2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int num[] = new int[6]; //Creamos un array para guardar los 5 números a comprobar. Se le da valor 6 para utilizar las posiciones de 1 a 5, ya que los arrays comienzan con la posición 0
        Scanner teclado = new Scanner( System.in ); //Clase Scanner para la introducción de datos por teclado
        for (int a=1; a<=5; a++){   //Solicitamos los 5 números a comprobar
            System.out.printf( "Número a comprobar %d: ", a);
            num[a] = teclado.nextInt(); //Solicitamos el número
        }
        for (int b=1; b<=5; b++){   //Mostramos si los números indicados son negativo, primo o no primo
            if (num[b]>=0){ //Si es positivo, comprobamos si es primo
                if (esPrimo(num[b]))
                    System.out.printf( "El número %d es primo\n", num[b]);
                else
                    System.out.printf( "El número %d no es primo\n", num[b]);
            }
            else    //Si es negativo, lo mostramos por pantalla
                System.out.printf( "El número %d es negativo\n", num[b]);
        }
    }
    
    /**
     * Comprueba si el numero dado es primo o no.
     * 
     * @param numero Número a comprobar.
     * @return <code>verdadero</code> si es primo y <code>falso</code> si no lo es.
    */
    public static boolean esPrimo(int numero) {
    if (numero == 0 || numero == 1) {   //Los numeros 0 y 1 no se consideran primos
      return false;
    }
    for (int c=2; c<=numero/2; c++) {    //Comenzamos a comprobar sus divisores
      if (numero % c == 0)  //Si alguna de sus divisiones tiene resto cero, el numero no es primo
        return false;
    }
    // Si todas sus divisiones tienen resto, el número es primo
    return true;
    }
}
