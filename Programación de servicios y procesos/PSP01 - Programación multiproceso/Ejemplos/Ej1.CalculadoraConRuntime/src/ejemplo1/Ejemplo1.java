/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejemplo1;
// @author 

import java.io.IOException;

public class Ejemplo1 {    
    public static void main(String[] args) {
        Runtime rt = Runtime.getRuntime(); //Objeto runtime asociado a la aplicacion
        String [] comando = {"calc.exe"}; // comando 
        Process p; //Para el objeto proceso
        
        try {
            p= rt.exec(comando);
        }catch (IOException e){
            System.out.println ("Error al ejecutar el comnado: " + comando);
            e.printStackTrace(); //para que me printe error estandar
        }
        
    }    
}
