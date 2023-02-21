/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg4.leerfichtexto;

/**
 *
 * @author jfranco
 */
import java.io.*;
public class LeerFichTexto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
        File fic=new File("build.xml");
        FileReader ficReader=new FileReader(fic);
        BufferedReader fichero=new BufferedReader(ficReader);
        String linea;
        while((linea=fichero.readLine())!=null)
            System.out.println(linea);
        fichero.close();
        
        }catch(FileNotFoundException fn){
        System.out.println("No se encuentra el fichero");
        }
        catch(IOException io){
        System.out.println("Error de E/S");
        }
        
    }
}
