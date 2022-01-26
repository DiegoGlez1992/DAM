/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog04_ejerc2;

import java.util.Scanner;

/**
 *
 * @author 
 */
public class PROG04_Ejerc2 {

    /**
     * @param args the command line arguments
     */
    /* Comprueba si un número recibido por parámetro es primo. Para ello, busca divisores mayores que 1 hasta la mitad del número
    En caso de encontrar uno, el número no es primo.*/
    public static boolean es_primo (int num){
        int i=2;
        
        while (i<=(num/2)){
            if (num%i==0)
                return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        Scanner sca=new Scanner (System.in);
        
        int i=0, num=0;
        do {
            System.out.println ("Introduce un número");
            num=sca.nextInt();
            
            if (num<0) System.out.println ("El número es negativo");
            else {
                if (es_primo(num)) System.out.println ("El número es primo");
                else System.out.println ("El número no es primo");
            }
            i++;
        } while (i<5);
        }
}
    
