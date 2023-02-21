/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2.leerfichtext;

/**
 *
 * @author jfranco
 */
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class LeerFichText {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        // declarar el fichero
        File fichero = new File("prueba.txt");
        FileReader fic=null;
   
        
        try {
             fic = new FileReader(fichero); //crea el flujo de entrada
             
             BufferedReader br= new BufferedReader(fic);
             int i;
             //Leo caracter a caracter
              while((i=fic.read())!=-1)
              System.out.println((char)i);
              
              fic.close();
             
              //Leo de 20 en 20 caracteres
             /*char b[]=new char[20];
             while((i=fic.read(b))!=-1)
              System.out.println(b);
              fic.close();
              */
             
             //Leo línea a línea usando BufferedReader ( más usual)
             /*String cadena;
             while ((cadena=br.readLine())!=null) {
                 System.out.println(cadena);
             }
             br.close();*/
            
        } catch (FileNotFoundException ex) {
            System.out.println("El fichero pasado no existe "+ex.getMessage());
            System.exit(-1);
        } catch (IOException ex) {
            Logger.getLogger(LeerFichText.class.getName()).log(Level.SEVERE, null, ex);
        }

             

    }
}
