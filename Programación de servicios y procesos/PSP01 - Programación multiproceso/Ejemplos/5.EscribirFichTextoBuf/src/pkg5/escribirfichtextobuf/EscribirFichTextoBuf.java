/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg5.escribirfichtextobuf;

/**
 *
 * @author jfranco
 */
import java.io.*;
public class EscribirFichTextoBuf {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
        BufferedWriter fichero=new BufferedWriter(new FileWriter("FichTexto.txt"));
        for(int i=1;i<11;i++){
            fichero.write("Fila numero: "+i);
            fichero.newLine();
        }
        fichero.close();
        }
        catch(FileNotFoundException fn){
            System.out.println("No se encuentra el fichero");}
        catch(IOException io){
            System.out.println("Error de E/S");}
        }
        
    }

