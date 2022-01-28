/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*Ahora vamos a probar su funcionalidad desde otra clase, denominada Principal, 
que contendrá el método main. Esta clase la debes crear en el mismo paquete que 
la clase Fecha. Dentro de dicha clase:*/
package fecha_calendario;

/**
 *
 * @author diego
 */

public class Principal {
    public static void main(String[] args) {
        /*Instancia un objeto de la clase Fecha denominado objFecha1 con el 
        primer constructor.*/
        Fecha objFecha1 = new Fecha(EnumMes.NOVIEMBRE);
        
        /*Actualiza los atributos dia y año para dicho objeto.*/
        objFecha1.setDia(30);
        objFecha1.setAño(2021);
        
        /*Muestra la fecha por pantalla en formato largo.*/
        System.out.println("Primera fecha, inicializada con el primer constructor");
        System.out.println("La fecha es: "+ objFecha1.toString());
        
        /*Muestra un mensaje por pantalla indicando si la fecha es verano 
        (puedes utilizar el operador de comparación ternario. Punto 5.3 de la 
        Unidad 2).*/
        System.out.println((objFecha1.isSummer()) ? "Es verano\n" : "No es verano\n");
        
        /*Instancia otro objeto de la clase Fecha denomiando objFecha2 con el 
        segundo constructor.*/
        Fecha objFecha2 = new Fecha(30, EnumMes.JUNIO, 1992);
        
        /*Muestra el año de esta fecha por pantalla.*/
        System.out.println("Segunda fecha, inicializada con el segundo constructor");
        System.out.println("La fecha 2 contiene el año "+objFecha2.getAño());
        
        /*Muestra la fecha en formato largo por pantalla.*/
        System.out.println("La fecha es: "+ objFecha2.toString());
        
        /*Muestra un mensaje por pantalla indicando si la fecha es verano o no.*/
        System.out.println((objFecha2.isSummer()) ? "Es verano" : "No es verano");
    }
    
}
