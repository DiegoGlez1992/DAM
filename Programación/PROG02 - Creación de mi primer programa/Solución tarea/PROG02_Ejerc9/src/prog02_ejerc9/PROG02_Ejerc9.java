/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog02_ejerc9;

/**
 *
 * @author 
 */
public class PROG02_Ejerc9 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int anio=2000;
        
        boolean bisiesto=((anio % 4 == 0 && anio % 100 != 0) || (anio % 100 == 0 && anio % 400 == 0));
                
        System.out.println ("El año " + anio + (bisiesto?" Es bisiesto":" No es bisiesto"));
    }
    
}
