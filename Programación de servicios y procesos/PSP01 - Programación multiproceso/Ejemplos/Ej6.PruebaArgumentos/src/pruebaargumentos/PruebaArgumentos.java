/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebaargumentos;

/**
 *
 * @author joaqf
 */
public class PruebaArgumentos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if (args.length!=3) {
            System.out.println("Debes pasar tres argumentos");
            System.exit(-1);
        }
        
        System.out.println("El primer argumento es: " + args[0]);
        System.out.println("El segundo argmento es: "+args[1]);
        System.out.println("El tercer argumento es: "+args[2]);
    }
    
}
