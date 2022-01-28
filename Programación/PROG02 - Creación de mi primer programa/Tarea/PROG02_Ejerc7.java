/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog02_ejerc7;

/**
 * 7.- Diseña un programa Java denominado PROG02_Ejerc7 para resolver una 
 * ecuación de primer grado con una incógnita (x), suponiendo que los 
 * coeficientes de la ecuación son C1 y C2 se inicializan en el método main:
 * C1x + C2 = 0
 * Se debe mostrar el resultado con 4 decimales.
 *
 * @author diego
 */
public class PROG02_Ejerc7 
{
    public static void main(String[] args) 
    {
        float c1=37, c2=50;
        System.out.printf("%.0fx + %.0f = 0",c1,c2);
        System.out.printf("\nx = %.4f",(-(c2/c1)));
    }
    
}
