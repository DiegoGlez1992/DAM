/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package verdir;

import java.io.File;

/**
 *
 * @author joaqf
 */
public class VerDir {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Ficheros de directorio actual:");
        File f=new File(".");
        File f2=new File("build.xml");
        
        System.out.println(f2.getName());
        System.out.println(f2.getPath());
         System.out.println(f2.getAbsolutePath());  
        System.out.println(f2.canRead());
        System.out.println(f2.canWrite());
        System.out.println(f2.isDirectory());
        
        String[] archivos=f.list();
        for (String archivo: archivos) {
            System.out.println(archivo);
        }
    }    
}
