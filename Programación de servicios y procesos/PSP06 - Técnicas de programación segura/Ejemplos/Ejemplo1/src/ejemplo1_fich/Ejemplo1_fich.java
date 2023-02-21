/*
 * Crear fichero en carpeta determinad por ejemplo d:\prueba, insertar una linea,
 * leer el contenido del fichero y mostrarlo en pantalla.
 * El comportamiento del programa varía si usamos o no el gestor de seguridad y 
 * ficheros de políticas.
 */
package ejemplo1_fich;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jfranco
 */
public class Ejemplo1_fich {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        //añadiendo el gestor de seguridad me negarían acceso de escritura
       /* System.setProperty("java.security.policy",
                "./ejemplo2.policy");*/
       /* System.setSecurityManager(
                new SecurityManager());*/
          
        String nombreFichero = "ejemplo1.txt";
        try {
            FileWriter fichero = new FileWriter(nombreFichero);
            BufferedWriter bw = new BufferedWriter(fichero);
            //escribo una linea en el fichero
            bw.write("He escrito una linea en el fichero");
            bw.newLine(); //salto de linea
            bw.close(); //cierro flujo escritura en fichero
            LeerFichero (nombreFichero);
        } catch (IOException ex) {
            Logger.getLogger(Ejemplo1_fich.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void LeerFichero (String fichero){
        try {
            BufferedReader br = new BufferedReader (
                    new FileReader (fichero));
            //leemos la linea y la printamos 
            String linea = br.readLine();
            System.out.println ("Leo el contenido del fichero. \n Linea = " +linea);
            br.close(); //cierro flujo lectura fichero
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Ejemplo1_fich.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Ejemplo1_fich.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
