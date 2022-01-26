/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog02_ejerc6;

/**
 *
 * @author 
 */
public class PROG02_Ejerc6 {

    /**
     * @param args the command line arguments
     */
    enum raza_perros {
        Mastín, Terrier, Bulldog, Pekines, Caniche, Galgo
    }

    public static void main(String[] args) {
        // TODO code application logic here

        raza_perros var1 = raza_perros.Bulldog;
        raza_perros var2 = raza_perros.Galgo;

        System.out.println("El resultado de comparar var1 y var2 es:");
        System.out.println(var1 == var2);

        System.out.println("La posición que ocupa el valor de var1 es " + var1.ordinal());
        System.out.println("La posición que ocupa el valor de var2 es " + var2.ordinal());

    }

}
