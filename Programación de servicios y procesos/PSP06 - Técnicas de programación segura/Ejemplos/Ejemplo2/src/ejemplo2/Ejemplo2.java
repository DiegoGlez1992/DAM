
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejemplo2;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jfranco
 */
public class Ejemplo2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //String nomfichero = "D:\\prueba\\ejemplo2\\ejemplo2.txt";
        String d = System.getProperty("user.home", "not specified");
        String f = "\\ejemplo2.txt";
        String fichero = d + f;
      
        /***** GESTOR SEGURIDAD*******/
        /* Descomentar para ejecutar en un entorno seguro*/ 
        System.setProperty("java.security.policy",
                "./ejemplo22.policy");
        System.setSecurityManager(new SecurityManager());
        try {
            FileWriter fw = new FileWriter(fichero);
            BufferedWriter bw = new BufferedWriter(fw);
            //escribo una linea en el nomfichero
            bw.write("He escrito una linea en el fichero");
            bw.newLine(); //salto de linea
            bw.close(); //cierro flujo escritura en nomfichero
            System.out.println("Fin proceso escritura");
            LeerFichero(fichero);
        } catch (IOException ex) {
            Logger.getLogger(Ejemplo2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void LeerFichero(String fichero) {
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(fichero));
            //leemos la linea y la printamos 
            String linea = br.readLine();
            System.out.println("Leo el contendio del fichero. \n Linea = " + linea);
            br.close(); //cierro flujo lectura nomfichero
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Ejemplo2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Ejemplo2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
