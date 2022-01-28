/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog02_ejerc5;
import java.util.Scanner;

/**
 *5.- Diseña un programa Java denominado PROG02_Ejerc5 que dado un número de 
 * segundos, muestre en pantalla cuántos minutos, horas y días contiene.
 * 
 * @author diego
 */
public class PROG02_Ejerc5 
{
    public static void main(String[] args) 
    {
        double dias=0, horas=0, minutos=0;
        Scanner teclado = new Scanner( System.in ); //Clase Scanner para la introducción de datos por teclado
        System.out.printf( "Introducir segundos: ");
        long entrada = teclado.nextLong(); //Pedimos los datos
        minutos=(double)entrada/(double)60;
        horas=(double)entrada/(double)3600;
        dias=(double)entrada/(double)86400;
        System.out.printf("\n%d segundos contiene = %.2f minutos",entrada,minutos);
        System.out.printf("\n%d segundos contiene = %.2f horas",entrada,horas);
        System.out.printf("\n%d segundos contiene = %.2f días",entrada,dias);
    }
    
}
