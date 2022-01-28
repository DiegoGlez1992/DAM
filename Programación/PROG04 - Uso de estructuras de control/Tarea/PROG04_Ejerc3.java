/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog04_ejerc3;

import java.util.Scanner;

/**
 * El Mínimo Común Múltiplo (MCM) de un conjunto de dos números es el número 
 * positivo más pequeño que es múltiplo de los dos números. Es posible calcular 
 * el MCM de tres o más números. Por ejemplo, el MCM (2,3) es 6. El 6 es el 
 * múltiplo mas pequeño de 2 y de 3. Implementa un programa Java que pida dos 
 * números por teclado, compruebe que son positivos y calcule su MCM. En caso de
 * no ser ambos números positivos, el programa mostrará un mensaje por pantalla 
 * y finalizará.
 * 
 * @author diego
 * @version 1.0
 */
public class PROG04_Ejerc3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int num1=0, num2=0; //Variables para guardar los números leídos por teclado
        Scanner teclado = new Scanner( System.in ); //Clase Scanner para la introducción de datos por teclado
        while(true){
            System.out.printf( "Introduzca el primer número: ");
            num1 = teclado.nextInt(); //Solicitamos el número
            System.out.printf( "Introduzca el segundo número: ");
            num2 = teclado.nextInt(); //Solicitamos el número
            if(num1>0 && num2>0)
                System.out.printf( "El MCM de %d y %d es %d \n", num1, num2, calcularMCM(num1, num2));
            else
                break;
        }
        System.out.printf( "Error. Número negativo detectado.\n");
    }
    
    /**
     * Calcula el MCM de dos números dados.
     * 
     * Para obtener el MCM de dos números o más, se tiene que llegar a reducir 
     * cada número a su mínima simplificación que es el 1. Para lo cual, se hace 
     * uso de los divisores que tiene cada número. Una vez terminado la 
     * operación, los divisores se multiplican y obtenemos como resultado el MCM
     * de los números. Se debe dejar en claro que para comenzar a simplificar 
     * cada número, se debe comenzar con el número dos como divisor, luego el 
     * tres y así sucesivamente. Se entiende que para que un número sea divisor 
     * de otro, la división entre ambos números tiene que ser exacta.
     * 
     * @param numero1 Primer número a comprobar.
     * @param numero2 Segundo número a comprobar.
     * @return MCM de los dos números dados.
    */
    public static int calcularMCM(int numero1, int numero2) {
        int mcm=1, i=2; //Variables auxiliares para calcular el MCM de dos números
        while(i<=numero1 || i<=numero2) //El divisor buscado debe de ser menor que al menos uno de los numeros dados
        {
            if(numero1%i==0 || numero2%i==0){   //Se comprueba si el número comprobado es divisor de alguno de los números dados
                mcm=mcm*i;  //Se actualiza el valor del MCM
                if(numero1%i==0) numero1/=i;    //Se factoriza el número 1
                if(numero2%i==0) numero2/=i;    //Se factoriza el número 2
            }
            else    //Si no es divisor de ninguno de los dos número, se incremente su valor
                i++;
        }
        return +mcm;    //Devuelve el MCM calculado
    }
}
