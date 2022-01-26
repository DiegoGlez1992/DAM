package com.prog03Ejerc1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 
 */
public class Ejerc01Principal {
    public static void main (String[] args){
        
        //Instanciamos un objeto Fecha mediante el primer constructor
        System.out.println("Primera fecha, inicializada con el primer constructor");
        Fecha objFecha1=new Fecha(enumMes.febrero);
        
        objFecha1.setDia(20);
        objFecha1.setAnio(2000);
        
        System.out.println(objFecha1.toString());
        System.out.println((objFecha1.isSummer()?"Es verano":"No es verano"));
        
        System.out.println("\nSegunda fecha, inicializada con el segundo constructor");

        //Instanciamos un objeto Fecha con el segundo constructor
        Fecha objFecha2=new Fecha (15, enumMes.julio, 2015);
        
        System.out.println("La fecha 2 contiene el año " + objFecha2.getAnio());
        System.out.println(objFecha2.toString());
        System.out.println((objFecha2.isSummer()?"Es verano":"No es verano"));
        
    }
}
