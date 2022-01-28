/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog02_ejerc9;
import java.util.Scanner;

/**
 * 9.-  Aunque aparentemente los años bisiestos son aquellos que son múltiplos 
 * de 4,  no es del todo preciso, ya que años como 1900 y como 2100 no fueron, 
 * ni serán, bisiestos respectivamente. Existe una explicación física que tiene 
 * que ver con que el año del calendario no coincide exactamente, en duración, 
 * con el año solar. Así, un cálculo más exacto indica que la duración real de 
 * un año es de 365,2425 días. Para corregir este desfase, se utiliza el 
 * criterio de que se considerará año bisiesto aquel que sea divisible por 4 
 * pero no por 100 salvo que sea divisible por 400. Por esto 1900 no es 
 * bisiesto, 1904 sí y 2000 también.  Diseña un programa Java, denominado 
 * PROG02_Ejerc9, que dado un año indique si es bisiesto o no. 
 *
 * @author diego
 */
public class PROG02_Ejerc9 
{
    public static void main(String[] args) 
    {
        Scanner teclado = new Scanner( System.in ); //Clase Scanner para la introducción de datos por teclado
        int año;
        System.out.print("Introduzca un año para comprobar si es bisiesto:");
        año = teclado.nextInt(); //Pedimos los datos
        if(((año%4==0) && (año%100!=0)) || (año%400==0))
            System.out.printf("El año %d es bisiesto.",año);
        else
            System.out.printf("El año %d no es bisiesto.",año);
        //String respuesta = (año/)?"El año es bisiesto.":"El año no es bisiesto.";
    }
    
}
