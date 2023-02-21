/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lanzasaluda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joaqf
 */
public class LanzaSaluda {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Runtime rt=Runtime.getRuntime();
        
        try {
            //Lanzo el programa saluda y obtengo una instancia del proceso
            Process proceso=rt.exec("java -jar Saluda.jar Joaquin");
            
            //Obtengo un flujo de entrada con la salida estándar del proceso creado (proceso hijo)
            InputStream is=proceso.getInputStream();
            //Convierto el flujo de bytes a caracteres
            InputStreamReader isr=new InputStreamReader(is);
            //Convierto el flujo de caracteres a un flujo de caracteres con buffer
            BufferedReader br=new BufferedReader(isr);
            
            //Leo la salida del proceso hijo
            System.out.println(br.readLine());
            
            br.close();
            
                    } catch (IOException ex) {
            System.out.println("Error al lanzar al proceso");
        }
    }
    
}
