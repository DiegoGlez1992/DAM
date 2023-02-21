/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturanumeros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaqf
 */
public class LecturaNumeros {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            InputStreamReader isr=new InputStreamReader(System.in);//Leo de la entrada estándar
            BufferedReader br=new BufferedReader(isr);
            
            
            String linea=br.readLine();
            while (linea!=null) {
                System.out.println(linea);
                linea=br.readLine();
            }
        } catch (IOException ex) {
            System.out.println("Fallo de lectura");
        }

    }
    
}
