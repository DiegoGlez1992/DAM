/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package psp06_tarea1;

import java.io.*;
import java.util.regex.*;
import java.util.logging.*;

/**
 *
 * @author Diego González García
 */
public class PSP06_Tarea1 {

    static BufferedReader bufferedReaderTeclado = null;
    static MyLogger myLogger;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String usuario, nombreArchivo;
        bufferedReaderTeclado = new BufferedReader(new InputStreamReader(System.in));  //Abre un buffer para el teclado
        myLogger = new MyLogger();
        
        usuario = validarUsuario(); //Apartado 1
        nombreArchivo = validarArchivo();   //Apartado 2
        leerArchivo(nombreArchivo); //Apartado 3
        
        if (bufferedReaderTeclado != null) {
            try {
                bufferedReaderTeclado.close(); //Cierra el buffer de lectura del teclado
            } catch (IOException ioe) {
                myLogger.addLog(Level.SEVERE, ioe.getMessage());
            }
        }
        myLogger.addLog(Level.INFO, "Programa cerrado");
    }

    /**
     * Método que adquiere y comprueba el formato del nombre de usuario.<br>
     * El usuario debe de tener una longitud de 6 caracteres y estar compuesto
     * únicamente por letras minúsculas.
     *
     * @return Nombre de usuario
     */
    public static String validarUsuario() {
        String usuario = null;
        Pattern pattern;
        Matcher matcher;
        boolean valido;

        try {
            do {
                System.out.print("Introduce tu nombre de usuario: ");
                usuario = bufferedReaderTeclado.readLine();    //Adquiere el nombre de usuario por teclado
                pattern = Pattern.compile("^([a-z]{6})$");  //Pattern para el formato del usuario
                matcher = pattern.matcher(usuario); //Comprueba el formato del nombre de usuario
                if (!matcher.find()) {  //Si no coincide
                    valido = false;
                    System.out.println("El nombre de usuario no es valido, debe estar compuesto por 6 letras minusculas.");
                    myLogger.addLog(Level.WARNING, "Formato de usuario erroneo: " + usuario);
                } else {    //Si coincide
                    valido = true;
                    System.out.println("El nombre de usuario es valido.\n");
                    myLogger.addLog(Level.INFO, "Usuario conectado: " + usuario);
                }
            } while (!valido);
        } catch (IOException ioe) {
            myLogger.addLog(Level.SEVERE, ioe.getMessage());
        }
        return usuario;
    }

    /**
     * Método que adquiere y comprueba el formato del nombre del fichero.<br>
     * El nombre del fichero debe de tener una longitud de 8 caracteres y tener
     * una extensión de 3 caracteres.
     *
     * @return Nombre del fichero
     */
    public static String validarArchivo() {
        String nombreFichero = null;
        Pattern pattern;
        Matcher matcher;
        boolean valido;

        try {
            do {
                System.out.print("Introduce el nombre del fichero a consultar: ");
                nombreFichero = bufferedReaderTeclado.readLine();  //Adquiere el nombre del fichero
                pattern = Pattern.compile("^([a-zA-Z0-9]{1,8}.[a-zA-Z]{3})$"); //Pattern para el formato del nombre del fichero
                matcher = pattern.matcher(nombreFichero);   //Comprueba el formato del nombre del fichero
                if (!matcher.find()) {  //Si no coincide
                    valido = false;
                    System.out.println("El nombre del fichero no es valido, debe estar compuesto como máximo por 8 caracteres y contener una extensión de 3.");
                    myLogger.addLog(Level.WARNING, "Formato de fichero erroneo: " + nombreFichero);
                } else {    //Si coincide
                    valido = true;
                    System.out.println("El nombre del fichero es valido.\n");
                    myLogger.addLog(Level.INFO, "Fichero solicitado: " + nombreFichero);
                }
            } while (!valido);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            myLogger.addLog(Level.SEVERE, ioe.getMessage());
        }
        return nombreFichero;
    }

    /**
     * Método que muestra por pantalla el contenido del fichero.<br>
     *
     * @param nombreFichero
     */
    public static void leerArchivo(String nombreFichero) {
        String aux;
        File file;
        FileReader fileReader = null;
        BufferedReader bufferedReaderFile = null;

        try {
            file = new File(nombreFichero); //Instancia el archivo
            fileReader = new FileReader(file);  //Crea el lector del archivo
            bufferedReaderFile = new BufferedReader(fileReader);    //Crea el buffer de lectura
            myLogger.addLog(Level.INFO, "Muestra el fichero: " + nombreFichero);
            while ((aux = bufferedReaderFile.readLine()) != null) { //Lee el archivo línea a línea
                System.out.println(aux);  //Muestra el contenido del archivo
            }
        } catch (FileNotFoundException fne) {   //En caso de error al buscar el archivo
            System.err.println(fne.getMessage());
            myLogger.addLog(Level.WARNING, "Fichero " + nombreFichero + " no encontrado en el sistema");
        } catch (IOException ioe) {
            myLogger.addLog(Level.SEVERE, ioe.getMessage());
        } catch (Exception e) {
            myLogger.addLog(Level.SEVERE, e.getMessage());
        } finally {
            if (bufferedReaderFile != null) {
                try {
                    bufferedReaderFile.close(); //Cierra el buffer del archivo
                } catch (IOException ioe) {
                    myLogger.addLog(Level.SEVERE, ioe.getMessage());
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close(); //Cierra el lector del archivo
                } catch (IOException ioe) {
                    myLogger.addLog(Level.SEVERE, ioe.getMessage());
                }
            }
        }
    }
}
