/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog04_03;

import java.util.Scanner;

/**
 *
 * @author
 */
public class PROG04_03 {

    /**
     * @param args the command line arguments
     */
    public static int mcm(int num1, int num2) {
        int mcm = 1;
        int aux = 2;
        //Funcionamiento del algoritmo
        /*
            Redución de los números utilizando sus divisores hasta 1. La multiplicación de los divisores da el MCM.
        */
        while (aux <= num1 || aux <= num2) {
            if (num1 % aux == 0 || num2 % aux == 0) { //Si aux es divisor de algún número.
                mcm = mcm * aux;
                if (num1 % aux == 0) { //Si aux es divisor del num1 le reducimos
                    num1 = num1 / aux;
                }
                if (num2 % aux == 0) { //Si aux es divisor del num2 le reducimos.
                    num2 = num2 / aux;
                }
            } else {
                aux = aux + 1;
            }
        }
        return mcm;
               
    }

    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sca = new Scanner(System.in);

        System.out.println("Vamos a calcular el MCM de dos números");
        System.out.println("Introduce el primer número");
        int num1 = sca.nextInt();
        System.out.println("Introduce el segundo número");
        int num2 = sca.nextInt();

        if (num1 < 0 || num2 < 0) {
            System.out.println("Alguno de los números es negativo");
        } else {
            System.out.println("El MCM de " + num1 + " y " + num2 + " es " + mcm(num1, num2));
        }

    }

}
