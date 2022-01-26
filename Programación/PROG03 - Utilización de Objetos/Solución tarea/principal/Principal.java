/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prog03.principal;

import com.prog03.figuras.Rectangulo;

/**
 *
 * @author 
 */
public class Principal {
    
    static public void main (String args[]){
        
        Rectangulo r1=new Rectangulo();
        
        r1.setBase(10);
        r1.setAltura(4);
        
        System.out.println (r1.toString());
        
        System.out.println ("El rectángulo r1 tiene un área de " + r1.area());
        
        Rectangulo r2=new Rectangulo (34, 40);
        
        System.out.println(r2.toString());
        
    }
}
