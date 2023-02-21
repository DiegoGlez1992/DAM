/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3.escribirfichtexto;

/**
 *
 * @author jfranco
 */
import java.io.*;
public class EscribirFichTexto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws IOException {
        // TODO code application logic here
        File fichero = new File("FichTexto.txt");
        FileWriter fic = new FileWriter(fichero);
        String cadena ="Esto es una prueba con FileWriter";
        char[] cad=cadena.toCharArray();
        
        for(int i=0;i<cad.length;i++)
            fic.write(cad[i]);
       
        //Escribir array completo
       /*fic.write(cad);*/
       
       /*String prov[]={"Albacete", "Avila", "Badajoz"};
        for(int i=0;i<prov.length;i++)
            fic.write(prov[i]);*/

        fic.append("*");
        fic.close();
    }
}
