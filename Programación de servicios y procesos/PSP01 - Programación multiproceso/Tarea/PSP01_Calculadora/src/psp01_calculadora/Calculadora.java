/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package psp01_calculadora;

import java.io.IOException;

/**
 * EJERCICIO 1.1
 * Proyecto que ejecuta la calculadora lanzando un proceso que ejecuta el comando Calc.
 *
 * @author Diego González García
 */
public class Calculadora {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            //Lanzamos el proceso para ejecutar el comando "Calc" que abre la calculadora de Windows
            Process p = Runtime.getRuntime().exec("calc.exe");
            p.waitFor();    //Esperamos al proceso
        } catch (IOException ex) {
            ex.printStackTrace();   //Errores de entrada/salida
        } catch (InterruptedException ex) {
            ex.printStackTrace();   //Errores estandar
        }
    }
}
