/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package saluda;

/**
 *
 * @author joaqf
 */
public class Saluda {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if (args.length==0) {
            System.out.println("Tienes que pasar el nombre a saludar");
            System.exit(-1);
        }
        
        System.out.println("Hola " + args[0]);
    }
    
}
