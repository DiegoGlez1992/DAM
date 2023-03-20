/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejercicio1ms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author jfranco
 */
public class TratarArchivos {

    public static final char EOT = (int) 4;//caracter ascii que representa a EOT (End Of Transmission)

    /**
     * Método que lista el contenido de un directorio que recibe como parámentro.     *
     * @param path
     * @return
     */
    public static ArrayList<String> listaDir(String path) {
        ArrayList<String> listaArchivos = new ArrayList<>();
        File file = new File(path);
        if (file.isDirectory()) {
            //Convierte la array de cadenas de archivo en una ArrayList                
            for (String nombreArchivo : file.list()) {
                listaArchivos.add(nombreArchivo);
            }
        }
        return listaArchivos;
    }
    /**
     * Método que comprueba si existe un fichero recibido como parámetro
     * @param nameFile
     * @return 
     */

    public static boolean existFich(String nameFile) {
        if (nameFile == null || nameFile.length() < 1) {
            return false;
        }
        File file = new File(nameFile);
        return file.exists() && file.isFile();
    }
    /**
     * Método que devuelve un arraylist con el contenido de un fichero 
     * El fichero se pasa como parámetro
     * La marca de fin de fichero se añade al final del contenido almacenado 
     * @param fileName
     * @return 
     */
    public static ArrayList<String> getContenido(String fileName) {
        ArrayList<String> contenido = new ArrayList<>();
        BufferedReader br = null;
        try {
            File archivo = new File(fileName);

            if (!archivo.exists() || !archivo.isFile()) {
                System.err.println("Archivo no encontrado");
            }

            br = new BufferedReader(new FileReader(archivo));
            String linea = "";
            while (linea != null) {
                linea = br.readLine();

                contenido.add(linea);
            }

            contenido.add(String.valueOf(EOT));//byte indicador de EOT

        } catch (FileNotFoundException ex) {
            System.err.println("Archivo no encontrado");
        } catch (IOException ex) {
            System.err.println("Error de entrada/salida al leer fichero: " + ex.getMessage());
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
            }
        }

        return contenido;
    }
}
