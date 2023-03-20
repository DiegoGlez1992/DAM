package ejercicio2ms;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Crea una aplicación que realice los siguientes pasos: Solicita el nombre del
 * usuario que va a utilizar la aplicación. El login tiene una longitud de 8
 * caracteres y está compuesto únicamente por letras minúsculas. Solicita al
 * usuario el nombre de un fichero que quiere mostrar. El nombre del fichero es
 * como máximo de 8 caracteres y tiene una extensión de 3 caracteres. Visualiza
 * en pantalla el contenido del fichero.
 */
/**
 * Voy a leer del directorio de trabajo del usuario (no se especificaba en el
 * enunciado, por lo que se ha considerado válido cualquier otro). No hago
 * búsqueda recursiva del fichero que quiero ver. Si no se encuentra, en el
 * directorio user.home, aviso y termino.
 */
/**
 *
 * @author jfranco
 */
public class Ejercicio2 {

    private String login;
    private String fichero;
    private final String logfich = "ejercicio2.log"; //fichero de log
    static final Logger logger = Logger.getLogger(Ejercicio2.class.getName());
    //escogo como directorio c:\\datos
    String directorio = "c:\\datos";

    public Ejercicio2() {
        //inicio el fichero de log de nombre "logfich"
        IniciarLog(logfich);
        //método para pedir usuario
        LeerLogin();
        System.out.println("Usuario " + login + " se ha conectado");
        //recogo la actividad en el fichero de log
        logger.log(Level.INFO, "Se ha conectado el usuario = {0}", login);
        //método para pedir nombre fichero
        LeerFichero();
        System.out.println("El usuario quiere ver el fichero: " + fichero);
        //recogo la actividad en el fichero de log
        logger.log(Level.INFO, "El usuario quiere ver el fichero = {0}", fichero);
        //método para ver el fichero en el directorio 
        VerFichero(directorio, fichero);
    }

    private void VerFichero(String path, String fichero) {
        fichero = path + "\\" + fichero;
        System.out.println("Path = " + path);
        File f = new File(fichero);
        //si existe el fichero, muestro el contenido
        if (TratarArchivos.existFich(fichero)) {
            ArrayList<String> contenido = new ArrayList<>();
            contenido = TratarArchivos.getContenido(fichero);
            for (String linea : contenido) {
                if (linea != null) {
                    System.out.println(linea);
                }
            }
        } else if (f.isDirectory()) {
            //si el fichero es un directorio, muestro el contenido
            System.out.println("***** " + fichero + " es un directorio. Contiene: ");
            ArrayList<String> listaArchivos = new ArrayList<>();
            listaArchivos = TratarArchivos.listaDir(fichero);
            for (String linea : listaArchivos) {
                if (linea != null) {
                    System.out.println(linea);
                }
            }
        } else {
            //si no existe, aviso por pantalla y lo reflejo en el log
            System.out.println("No se ha encontrado el fichero " + fichero);
            logger.log(Level.WARNING, "No se ha encontrado el fichero");
        }
    }

    /**
     * LeerFichero() se asegura de comprobar el patrón que hemos marcado.
     */
    private void LeerFichero() {
        fichero = new String();

        Pattern pattern = null;
        Matcher matcher = null;

        //leo del teclado
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            do {
                System.out.println("Escribe el nombre del fichero.extension: ");
                fichero = br.readLine();
                //patron para el nombre del fichero
                pattern = Pattern.compile("[a-z]{1,8}.[a-z]{3}");
                matcher = pattern.matcher(fichero);
            } while (!matcher.matches());
        } catch (IOException ex) {
            System.err.println("Error IO al leer del fichero");
            logger.log(Level.SEVERE, "Error IO al leer del fichero");
        }
    }

    /**
     * LeerLogin() se asegura el patrón que hemos marcado
     */
    private void LeerLogin() {
        login = new String();
        Pattern pattern = null;
        Matcher matcher = null;

        //leo del teclado
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            do {
                System.out.println("Nombre de usuario en minúsculas (no más de 8 caracteres): ");
                login = br.readLine();
                //patron para el nombre del fichero
                pattern = Pattern.compile("[a-z]{8}");
                matcher = pattern.matcher(login);
            } while (!matcher.matches());

        } catch (IOException ex) {
            System.err.println("Error IO en LeerLogin()");
            Logger.getLogger(Ejercicio2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Inicia el archivo de log de nombre pasado como parámetro
     *
     * @param nombrelog
     */
    private void IniciarLog(String nombrelog) {
        FileHandler fh;
        try {
            // Configuro el logger y establezco el formato
            fh = new FileHandler(nombrelog, true);
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false); //no quiero ver mensajes por pantala

            SimpleFormatter formatter = new SimpleFormatter(); //formato texto

            fh.setFormatter(formatter);

        } catch (SecurityException | IOException e) {
            System.err.println("Error al iniciar el log" + e.toString());
            Logger.getLogger(Ejercicio2.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Ejercicio2 ejercicio = new Ejercicio2();
    }

}
