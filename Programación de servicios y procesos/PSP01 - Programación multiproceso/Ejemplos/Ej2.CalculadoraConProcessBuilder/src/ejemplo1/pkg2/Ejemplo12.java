/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejemplo1.pkg2;

import java.io.IOException;

/**
 *
 * @author joaqf
 */

public class Ejemplo12 {

     public static void main(String[] args) {

        String [] comando = {"calc.exe"}; // comando 
        ProcessBuilder pb = new ProcessBuilder(comando); //Objeto ProcessBuilder asociado a la aplicacion
        Process p; //Para el objeto proceso
        
        try {
            
            //bp.command(comando); //Otra forma de asignar el comando en PB
            p=pb.start();
        }catch (IOException e){
            System.out.println ("Error al ejecutar el comnado: " + comando);
            e.printStackTrace(); //para que me printe error estandar
        }
        
    }    
}
