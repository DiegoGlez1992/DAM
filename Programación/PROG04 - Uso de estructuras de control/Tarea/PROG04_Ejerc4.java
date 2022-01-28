/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog04_ejerc4;

import java.util.Scanner;

/**
 * Deseamos implementar un juego en Java que permita al usuario adivinar un 
 * número oculto (que será aleatorio).  El funcionamiento será el siguiente:
 *  - El programa mostrará un pequeño menú en pantalla con las siguientes 
 * opciones (1. Configurar, 2. Jugar, 3. Salir).
 *      - Si el usuario selecciona el la primera opción, se le solicitará por 
 *      teclado el número de intentos permitidos (numInt) y el número máximo  
 *      (numMax) generado.
 *      - Si el usuario selecciona la opción 2,  el programa generará un número 
 *      aleatorio entre 0 y numMax que será el número a adivinar (numOculto). A 
 *      partir de este momento, se le solicitarán al usuario números hasta 
 *      adivinar el número oculto.
 *          - Si el usuario adivina el número, se mostrará un mensaje "Has 
 *          ganado!. Has necesitado X intentos".
 *          - Si se consume el número de intentos sin adivinar el número, se 
 *          mostrará el mensaje "Perdiste!. Intentos consumidos".
 *          - En cada intento, si el usuario no adivina el número se le 
 *          proporcionará una pista, por ejemplo, "El número oculto es menor".
 *          - En ambos casos, la siguiente acción será mostrar el menú.
 *          - Si el usuario selecciona Jugar sin configurar previamente el 
 *          número de intentos y el número máximo generado, se tomarán como 
 *          valores por defecto: numInt=5 y numMax=10.
 *      - Si el usuario pulsa la opción 3, el programa finaliza.
 *  - Para generar un número aleatorio en java puedes utilizar el siguiente 
 *  código: int numOculto = (int)Math.floor(Math.random()*20+1); //genera un 
 *  número aleatorio entre 0 y 20, ambos incluidos.
 * 
 * @author diego
 * @version 1.0
 */
public class PROG04_Ejerc4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int tecla=0, numInt=5, numMax=10;
        Scanner teclado = new Scanner( System.in ); //Clase Scanner para la introducción de datos por teclado
        while(true){    //Bucle infinito
            System.out.printf( "\n1. Configurar");
            System.out.printf( "\n2. Jugar");
            System.out.printf( "\n3. Salir");
            System.out.printf( "\nSeleccione una opción: ");
            tecla = teclado.nextInt(); //Solicitamos el número
            switch(tecla){
                case 1:
                    System.out.printf( "\tNúmero de intentos permitidos: ");
                    numInt = teclado.nextInt(); //Solicitamos el número
                    System.out.printf( "\tNúmero máximo generado: ");
                    numMax = teclado.nextInt(); //Solicitamos el número
                    break;
                case 2:
                    int numOculto = (int)Math.floor(Math.random()*numMax+1); //Genera un número aleatorio
                    int respuesta=0;
                    for (int i=1; i<=numInt; i++){  //Bucle para controlar el número de intentos para adivinar el número
                        System.out.printf( "\tIntento nº%d: ", i);
                        respuesta = teclado.nextInt();  //Solicitamos el número
                        if(numOculto == respuesta){
                            System.out.printf( "\tHas ganado!. Has necesitado %d intentos.\n", i);
                            break;
                        }
                        else if(numOculto < respuesta && i < numInt)
                            System.out.printf( "\tEl número oculto es menor.\n");
                        else if(numOculto > respuesta && i < numInt)
                            System.out.printf( "\tEl número oculto es mayor.\n");
                    }
                    if(numOculto != respuesta)
                        System.out.printf( "\tPerdiste!. %d intentos consumidos.\n", numInt);
                    break;
                case 3:
                    return; //Termina el programa
                default:
                    System.out.printf( "\tOpción no válida.\n");
                    break;
            }
        }
    }
}
