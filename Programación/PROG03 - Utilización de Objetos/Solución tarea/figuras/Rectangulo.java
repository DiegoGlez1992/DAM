/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prog03.figuras;

/**
 *
 * @author 
 */
public class Rectangulo {
    
    float base, altura;
    
    public Rectangulo (){
        base=altura=0;
    }
    
    public Rectangulo (float base, float altura){
        this.base=base;
        this.altura=altura;
    }

    public float getBase() {
        return base;
    }

    public void setBase(float base) {
        this.base = base;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }
    
    public float area (){
        return base * altura;
    }
    
    public String toString (){
        return "El rectángulo tiene una base de " + base + " y una altura de " + altura;
    }
    
    public boolean esCuadraro (){
        return (altura==base);
    }
}
