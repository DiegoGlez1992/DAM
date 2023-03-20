/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculadora;

import java.util.Scanner;

/**
 *
 * @author usuario
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        testConfiguracion();
        testRecuperacion();
    }

    /**
     * Método para el test de integración
     *
     * @param cmd
     * @return
     */
    public String testIntegration(String cmd) {
        System.out.println("Método testIntegration() de la clase principal");

        Calculando calculando = new Calculando();
        System.out.println("Operaciones:");
        System.out.println(calculando.add(5, 2));
        System.out.println(calculando.divide(10, 5));
        System.out.println(calculando.multiply(2, 3));
        System.out.println(calculando.subtract(8, 5));

        return cmd;
    }

    /**
     * Método para el test de configuracion
     */
    static public void testConfiguracion() {
        Calculando calculando = new Calculando();

        System.out.println("Metodo testConfiguracion().");

        System.out.println("Operaciones matematicas");
        System.out.print("5+2=: ");
        System.out.println(calculando.add(5, 2));
        System.out.print("10/5=: ");
        System.out.println(calculando.divide(10, 5));
        System.out.print("2x3=: ");
        System.out.println(calculando.multiply(2, 3));
        System.out.print("8-5=: ");
        System.out.println(calculando.subtract(8, 5));

        System.out.println("Fin del test de configuracion.");
    }

    /**
     * Método para el test de recuperacion
     */
    static public void testRecuperacion() {
        Scanner teclado = new Scanner(System.in);
        int a, b;
        Calculando calculando = new Calculando();

        System.out.println("\nMetodo testRecuperacion().");

        System.out.println("Operaciones forzando un error");
        System.out.print("Introduzca primer sumando: ");
        a = teclado.nextInt();
        System.out.print("Introduzca segundo sumando: ");
        b = teclado.nextInt();
        System.out.println(calculando.add(a, b));

        System.out.println("Fin del test de recuperacion.");
    }

}
