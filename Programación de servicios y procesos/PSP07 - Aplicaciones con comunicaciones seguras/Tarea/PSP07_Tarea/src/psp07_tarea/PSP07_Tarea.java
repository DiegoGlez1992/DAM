/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package psp07_tarea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Diego González García
 */
public class PSP07_Tarea {

    /**
     * Método main
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String password = "Mi clave supersecreta";
        String mensaje = "Este es un texto de prueba, que va a ser cifrado, "
                + "guardado en un fichero, leido el fichero y mostrado su "
                + "contenido por pantalla.";

        String mensajeCifrado;
        String mensajeDescifrado;

        SecretKeySpec secretKeySpec = null;
        File file = new File("fichero.cifrado");    //Fichero cifrado

        System.out.println("\tMensaje original:\n" + mensaje); //Muestra mensaje cifrado
        secretKeySpec = crearClaveAES(password);    //Genera la clave de encriptación
        mensajeCifrado = cifrarMensaje(secretKeySpec, file, mensaje);   //Cifra el mensaje
        System.out.println("\n\tMensaje cifrado:\n" + mensajeCifrado); //Muestra mensaje cifrado
        mensajeDescifrado = descifrarMensaje(secretKeySpec, file);  //Descifra el mensaje
        System.out.println("\n\tMensaje descifrado:\n" + mensajeDescifrado);  //Muestra mensaje descifrado

    }

    /**
     * Método que genera una clave con encriptación AES
     *
     * @param password Clave original
     * @return Clave de encriptación
     */
    private static SecretKeySpec crearClaveAES(String password) {
        MessageDigest messageDigest;
        byte[] digest;
        byte[] key;
        SecretKeySpec secretKeySpec;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");   //Instancia con el algoritmo SHA-256
            digest = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));   //Hash con el algoritmo SHA-256
            key = Arrays.copyOf(digest, 24);    //Copia los bits de la clave
            secretKeySpec = new SecretKeySpec(key, "AES");  //Crea el objeto SecretKeySpec
            return secretKeySpec;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Método que encripta un mensaje y lo guarda en un fichero
     *
     * @param secretKeySpec Clave de encriptación
     * @param file Fichero de salida para el mensaje
     * @param mensaje Texto a cifrar
     * @return Mensaje cifrado
     */
    private static String cifrarMensaje(SecretKeySpec secretKeySpec, File file, String mensaje) {
        byte[] mensajeCifrado;
        Cipher cipher;
        FileOutputStream fos = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); //Crea el objeto Cipher para cifrar
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);    //Se inicializa el cifrador en modo encriptación
            mensajeCifrado = cipher.doFinal(mensaje.getBytes()); //Cifra el mensaje
            fos = new FileOutputStream(file);  //Crea el objeto FileOutputStream para el mensaje cifrado en un fichero
            fos.write(mensajeCifrado);  //Escribe el fichero
            fos.close();    //Cierra la comunicación
            return new String(mensajeCifrado);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
        } catch (BadPaddingException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Método que desencripta un fichero y devuelve su mensaje
     *
     * @param secretKeySpec Clave de encriptación
     * @param file Fichero encriptado
     * @return Mensaje descifrado
     */
    private static String descifrarMensaje(SecretKeySpec secretKeySpec, File file) {
        byte[] mensajeCifrado;
        byte[] mensajeDescifrado;
        Cipher cipher;
        FileInputStream fis = null;
        int tamanoFichero;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); //Crea el objeto Cipher para descifrar
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);    //Se inicializa el cifrador en modo desencriptación
            fis = new FileInputStream(file);    //Crea el objeto FileInputStream para leer el fichero cifrado
            tamanoFichero = (int) file.length();    //Lee el tamaño del fichero
            mensajeCifrado = new byte[tamanoFichero];   //Crea un array de bytes
            fis.read(mensajeCifrado);   //Lee el archivo cifrado
            mensajeDescifrado = cipher.doFinal(mensajeCifrado);   //Descifra el mensaje
            return new String(mensajeDescifrado);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
        } catch (BadPaddingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

}
