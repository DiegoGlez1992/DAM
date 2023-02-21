/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejemplo1;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author msr
 */
public class Ejemplo1 {

    /**
     * @param args the command line arguments
     */
        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            URL url = new URL ("http://www.google.es");
           // URLConnection openConnection = url.openConnection();
            //utilizo openStream() para tener un flujo de entrada a la url
            DataInputStream dis = 
                    new DataInputStream (url.openStream());
            //uso BufferedReader para leer linea a linea
            BufferedReader br = 
                    new BufferedReader (new InputStreamReader (dis));
            String linea;
            while ((linea = br.readLine())!=null){
                System.out.println (linea);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Ejemplo1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) { 
            Logger.getLogger(Ejemplo1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
