/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog04_ejerc4;

import java.util.Scanner;

/**
 *
 * @author 
 */
public class PROG04_Ejerc4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int opt;
        int numInt = 5, numMax = 10;
        int numOculto;

        Scanner sca = new Scanner(System.in);

        do {

            System.out.println(" ADIVINA EL NÚMERO OCULTO");

            System.out.println(" Selecciona una opción del Juego.");

            System.out.println(" 1. Configurar");
            System.out.println(" 2. Jugar");
            System.out.println(" 3. Salir");

            opt = sca.nextInt();

            switch (opt) {
                case 1: //Opción configurar
                    System.out.println("Introduce el número de intentos");
                    numInt = sca.nextInt();
                    System.out.println("Introduce el número máximo a adivinar");
                    numMax = sca.nextInt();

                    break;
                case 2:
                    //generamos número oculto
                    numOculto = (int) Math.floor(Math.random() * numMax + 1);
                    int numIntroducido;
                    int intentos=0;
                    
                    while (intentos < numInt) {
                        System.out.println("Introduce el número buscado");
                        numIntroducido = sca.nextInt();

                        if (numIntroducido == numOculto) {
                            System.out.println("Has ganado!. Has necesitado " + intentos + " intentos");
                            intentos=numInt;
                        } else {
                            if (numIntroducido < numOculto) {
                                System.out.println("El número buscado es mayor. Inténtalo de nuevo");
                               
                            } else {
                                System.out.println("El número buscado es menor. Inténtalo de nuevo");
                            }
                            intentos++;
                        }
                    }
                    break;
            }

        } while (opt != 3);
    }

}
