package ejercicio1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Ejercicio1 {

    private String login;
    private String fichero;
    static final Logger logger = Logger.getLogger("MyLog");

    public Ejercicio1() {
        // Se inicia el log
        IniciarLog();
        
        //Petición del login
        LeerLogin();
        System.out.println("\n *** Usuario " + login + " se ha conectado ***\n");
        logger.log(Level.INFO, "Se ha conectado el usuario = {0}", login);

        PedirNombreFichero();
        System.out.println("\nEl usuario quiere ver el fichero: " + fichero);
        logger.log(Level.INFO, "El usuario quiere ver el fichero = {0}", fichero);

        VerFichero(fichero);
    }
    
    //Pide el login del usuario y comprueba que cumple los requerimientos.
    private void LeerLogin() {
        login = "";      //String para guardar el nombre de usuario
        
        //Pattern --> Clase para representar una expresión regular
        Pattern pattern = null;
         //Matcher --> Clase que permite comprobar si un dato se ajusta a un patrón
        Matcher matcher = null;

        //Lectura a través del teclado
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            do {
                System.out.print("Nombre de usuario en minúsculas (6 caracteres en minúscula): ");
                login = br.readLine();
                //Patron para login de usuario
                pattern = Pattern.compile("[a-z]{6}");
                matcher = pattern.matcher(login);
                
                if(!matcher.matches()){
                    System.out.println("     Nombre de usuario incorrecto. Introdúcelo de nuevo.");
                }
            } while (!matcher.matches());

        } catch (IOException ex) {
            System.err.println ("Error IO en LeerLogin()");
            Logger.getLogger(Ejercicio1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void PedirNombreFichero() {
        fichero = new String();

        Pattern pattern = null;
        Matcher matcher = null;

        //Lectura del  nombre del fichero
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            do {
                System.out.print ("Escribe el nombre del fichero.extension "
                        + "(máximo 8 caracteres para el nombre y "
                        + "obligatoriamente 3 para la extensión): ");
                fichero = br.readLine();
                //patron para el nombre del fichero
                pattern = Pattern.compile("[a-z]{1,8}.[a-z]{3}");
                matcher = pattern.matcher(fichero);
                
                if(!matcher.matches()){
                    System.out.println("     Nombre de fichero incorrecto. Introdúcelo de nuevo.");
                }    
            } while (!matcher.matches());
        } catch (IOException ex) {
            System.err.println("Error IO al leer nombre del fichero");
            Logger.getLogger(Ejercicio1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void VerFichero(String fichero) {
        try {
            
           // String d = System.getProperty("user.dir", "not specified");
           // fichero = d + "\\" + fichero;
           // System.out.println("\nRuta completa del fichero: "+fichero+"\n");
            FileInputStream fis = new FileInputStream(fichero);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(dis));
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }
            dis.close();
        } catch (FileNotFoundException ex) {
            System.err.println("No se ha encontrado el fichero");
            logger.log(Level.INFO, "Fichero no encontrado = {1}", fichero);
        } catch (IOException ex) {
            System.err.println("Error IO al leer del fichero");
            Logger.getLogger(Ejercicio1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void IniciarLog() {
        FileHandler fh;
        try {
            // Configuro el logger y establezco el formato
            fh = new FileHandler("./log_actividad.log", true);
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);

            SimpleFormatter formatter = new SimpleFormatter();

            fh.setFormatter(formatter);

        } catch (SecurityException | IOException e) {
            System.err.println ("Error al iniciar el log" + e.toString());
        }
    }

    public static void main(String[] args) {
        new Ejercicio1();
    }

}
