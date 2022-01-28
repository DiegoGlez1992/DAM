/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*Crea otro paquete con el nombre com.prog03.Principal.*/
package com.prog03.Principal;
import com.prog03.figuras.Rectangulo;

/**
 *
 * @author diego
 */

/*Dentro de dicho paquete crea una clase denominada principal que contenga el 
método main.*/
public class Principal {
    public static void main(String[] args) {
        
        /*En el método main, instancia al menos dos objetos de la clase 
        Rectangulo y comprueba su funcionamiento.*/
        Rectangulo objRectangulo1 = new Rectangulo();
        System.out.println("Primer objeto:");
        System.out.println("Base= " + objRectangulo1.getBase() + " Altura= " + objRectangulo1.getAltura() + "\nArea= " + objRectangulo1.getArea());
        System.out.println((objRectangulo1.isCuadrado()) ? "Es un cuadrado" : "No es un cuadrado");
        
        Rectangulo objRectangulo2 = new Rectangulo(6.0f, 8.3f);
        System.out.println("\nSegundo objeto:");
        System.out.println("Base= " + objRectangulo2.getBase() + " Altura= " + objRectangulo2.getAltura() + "\nArea= " + objRectangulo2.getArea());
        System.out.println((objRectangulo2.isCuadrado()) ? "Es un cuadrado" : "No es un cuadrado");
    
        Rectangulo objRectangulo3 = new Rectangulo(3.5f, 4.0f);
        objRectangulo3.setAltura(3.5f);
        System.out.println("\nTercer objeto:");
        System.out.println("Base= " + objRectangulo3.getBase() + " Altura= " + objRectangulo3.getAltura() + "\nArea= " + objRectangulo3.getArea());
        System.out.println((objRectangulo3.isCuadrado()) ? "Es un cuadrado" : "No es un cuadrado");
    
    }
}
