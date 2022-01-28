/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog02_ejerc6;

import java.util.Arrays;

/**
 *6.- Diseña un programa Java denominado PROG02_Ejerc6 que cree un tipo
 * enumerado para las siguientes razas de perro: Mastín, Terrier, Bulldog, 
 * Pekines, Caniche y Galgo. El programa debe realizar las siguientes 
 * operaciones:
 * Crea una variable denominada var1 del tipo enumerador. Asígnale un valor.
 * Crea una variable denominada var2 del tipo enumerador. Asígnale un valor.
 * Muestra por pantalla el valor obtenido de comparar ambas variables.
 * Investiga sobre la posibilidad averiguar la posición que ocupa un determinado
 * valor en el enumerado así como mostrar la cantidad de valores que contiene. 
 * Si lo consigues, muestra la posición de las dos variables en el tipo 
 * enumerado.
 * 
 * @author diego
 */
public class PROG02_Ejerc6 
{
    public enum Razas {Mastín,Terrier, Bulldog,Pekines,Caniche,Galgo}
    public static void main(String[] args) 
    {
        Razas var1=Razas.Mastín, var2=Razas.Galgo;
        int posVar1, posVar2;
        posVar1=var1.ordinal(); //Devuelve un entero con la posición de la constante según está declarada.
        posVar2=var2.ordinal(); //Devuelve un entero con la posición de la constante según está declarada.
        System.out.print("Razas de perros: " +Arrays.toString(Razas.values()));
        System.out.printf("\nvar1=%s\nvar2=%s\nComparación de las variables: %d",var1,var2,var1.compareTo(var2));
        System.out.printf("\nPosición que ocupa var1= %d\nPosición que ocupa var2= %d",posVar1,posVar2);
    }
    
}
