/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog02_ejerc8;
import java.util.Scanner;

/**
 * 8.- Diseña un programa Java denominado PROG02_Ejerc8 que dados el número de 
 * alumnos matriculados en Programación, número de alumnos matriculados  en 
 * Entornos de Desarrollo y número de alumnos matriculados en Base de datos. El 
 * programa deberá mostrar el % de alumnos matriculado en cada uno de los tres 
 * módulos. Se supone que un alumno sólo puede estar matrículado en un módulo. 
 * Trata de mostrar un solo decimal en los porcentajes.
 *
 * @author diego
 */
public class PROG02_Ejerc8 
{
    public static void main(String[] args) 
    {
        int alumnosProgramacion, alumnosEntornosDeDesarrollo, alumnosBaseDeDatos,totalAlumnos;
        Scanner teclado = new Scanner( System.in ); //Clase Scanner para la introducción de datos por teclado
        System.out.print("Nº de alumnos matriculados en programación: ");
        alumnosProgramacion = teclado.nextInt(); //Pedimos los datos
        System.out.print("Nº de alumnos matriculados en entornos de desarrollo: ");
        alumnosEntornosDeDesarrollo = teclado.nextInt(); //Pedimos los datos
        System.out.print("Nº de alumnos matriculados en base de datos: ");
        alumnosBaseDeDatos = teclado.nextInt(); //Pedimos los datos
        totalAlumnos = alumnosProgramacion + alumnosEntornosDeDesarrollo + alumnosBaseDeDatos;
        System.out.printf("Programación: %.1f%c de los alumnos", (((float)alumnosProgramacion/(float)totalAlumnos)*(float)100),'%');
        System.out.printf("\nEntornos de desarrollo: %.1f%c de los alumnos", (((float)alumnosEntornosDeDesarrollo/(float)totalAlumnos)*(float)100),'%');
        System.out.printf("\nBase de datos: %.1f%c de los alumnos", (((float)alumnosBaseDeDatos/(float)totalAlumnos)*(float)100),'%');
        
    }
    
}
