/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog02_ejerc4;
import java.util.Scanner;

/**
 * 4.- Diseña un programa Java denominado PROG02_Ejerc4 que dada la edad de una 
 * persona, muestre un mensaje indicando si es mayor de edad. NO se puede 
 * utilizar el operador condicional if.
 *
 * @author diego
 */
public class PROG02_Ejerc4 
{
    public static void main(String[] args) 
    {
        Scanner teclado = new Scanner( System.in ); //Clase Scanner para la introducción de datos por teclado
        System.out.printf( "Introducir edad: ");
        short edad = teclado.nextShort(); // pedimos la edad
        String respuesta = (edad>=18)?"es mayor de edad.":"no es mayor de edad.";
        System.out.printf("Según la edad indicada, %d, %s",edad,respuesta);
    }
    
}
