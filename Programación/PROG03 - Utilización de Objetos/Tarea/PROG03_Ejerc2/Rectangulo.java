/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*Crea un proyecto Java denominado Prog03_Ejerc2.
Dentro del proyecto, crea un paquete denominado com.prog03.figuras.*/
package com.prog03.figuras;

/**
 *
 * @author diego
 */

/*Dentro de dicho paquete, crea una clase denominada Rectangulo que:*/
public class Rectangulo {
    
    /*Declare atributos para la base y la altura de un rectángulo.*/
    private float base, altura;
    
    /*Declare un constructor vacío que inicialice los atributos a 0.*/
    public Rectangulo (){
        base=0;
        altura=0;
    }
    
    /*Declara un constructor que inicialice base y altura.*/
    public Rectangulo (float base, float altura){
        this.base=base;
        this.altura=altura;
    }
    
    /*Declare los siguientes métodos:*/
    /*Métodos para actualizar y obtener el valor de cada atributo.*/
    public void setBase(float base){
        this.base = base;
    }
    
    public float getBase(){
        return base;
    }
    
    public void setAltura(float altura){
        this.altura = altura;
    }
    
    public float getAltura(){
        return altura;
    }
    
    /*float getArea(): devuelve el área del rectángulo.*/
    public float getArea(){
        return(base*altura);
    }
    
    /*String toString(): devuelve una cadena conteniendo su área y su altura.*/
    @Override
    public String toString(){
        return "Area: "+ getArea() + " Altura: "+ altura;
    }
    
    /*boolean isCuadrado(): devuelve un booleadno indicando si el rectángulo es 
    cuadrado.*/
    public boolean isCuadrado(){
        return (base==altura)&&((base!=0 && altura!=0));
    }
    
}
