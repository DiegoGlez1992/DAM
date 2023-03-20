package tareapsp07_g;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;

/**
 * Clase que se utiliza para encriptar el fichero, usando el algoritmo Rijndael o AES, 
 * con las especificaciones de modo y relleno: Rijndael/ECB/PKCS5Padding.
 * (algoritmo/modo/padding)
 * 
 * La clase se utiliza para cifrar la cadena datos en el fichero auxiliar 
 * mediante el método encriptarMensaje(). 
 * 
 * Después utilizamos el método desencriptarMensaje() para recuperar la cadena 
 * en su mensaje original.
 * 
 * @author Gema
 */
public class Encriptar_FICH_AES_G {
    
    //Variables locales
    private String file = null;     //Fichero donde se va a guardar el mensaje cifrado
    private String passwd = null;   //Clave original para encriptar

    private Cipher cipher  = null;  //Para crear un objeto Cipher (cifrado)
    SecretKeySpec secretKeySpec;    //Clave de encriptacion
    private String encriptado = null;
    
     /** 
    * Constructor de la clase
     * @param ficheroCifrado        //Fichero donde se guarda la cadena encriptada
     * @param claveEncriptacion     //String con el que vamos a encriptar
    */
    public Encriptar_FICH_AES_G(String ficheroCifrado, String claveEncriptacion) {
        this.file = ficheroCifrado;
        this.passwd = claveEncriptacion;
    }
    
    /** 
    * La clave cifrada, la debemos generar de la siguiente forma: 
    * Obtener un hash de un password (un String) con el algoritmo "SHA-256".
    * (SHA-256 es una función que transforma cualquier dato entrante en un valor 
    *  de longitud fija único o variable, llamado hash.
    * 
    * Copiar con el método Arrays.copyOf los 192 bits a un array de bytes (192/8 bytes)
    * Utilizar la clase SecretKeySpec para generar una clave a partir del array de bytes.
    */
    
    /**
    * Método generarClave: Crea la clave de encriptacion usada internamente
    * @return SecretKeySpec Clave encriptada
    */
    public SecretKeySpec generarClave(){
        try {
           
            //Obtenemos un hash de un String "password" con el algoritmo "SHA-256".
            byte[] hash = passwd.getBytes("UTF-8");
            
            //MessageDigest para asignar la funcionalidad del algoritmo de 
            //resumen de mensajes SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //Digest completa la obtención del resumen
            hash = md.digest(hash);
            System.out.println("INFO: El valor de key con SHA-256 es: " + hash);
            
            //Se copia el hash a un array de bytes (192/8 bytes).
            //Es decir, nos quedamos con los primeros 24 bytes
            hash = Arrays.copyOf(hash, 24); // Usar solo 24 bytes = 192 bits
            
            //Utilizo la clase SecretKeySpec para generar una clave a partir del array de bytes.
            secretKeySpec = new SecretKeySpec(hash, "AES");
            
            System.out.println("INF: El valor de la secretKeySpec para:"
                    + " '" + passwd + "',  es: " + secretKeySpec);
            
           
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encriptar_FICH_AES_G.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("\tERROR: No se encuentra el algoritmo...");
         } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Encriptar_FICH_AES_G.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("\tERROR: Codificación no admitida...");
            }
        return secretKeySpec;
    }
    
    public void encriptar(String cadenaOriginal, SecretKeySpec claveSecreta) throws NoSuchPaddingException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException  {
      
        //claveSecreta se corresponde a la clave generada encriptada en la 
        //función generarClave().
        SecretKeySpec secretKey = claveSecreta;
        
        byte[] bytesEncriptados = null;

        try{

            /*
            * Creamos un cifrado
            * Para crear un objeto Cipher, la aplicación llama al método 
            * getInstance del cifrado y le pasa el nombre convertido solicitado.
            * Encripta con Rijndael en modo ECB y con el padding PKCS.
            */

            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            //Pone cifrador en modo encriptación   
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            //Guardamos los datos encriptados en el fichero indicado (fichero.cifrado)
            try (FileOutputStream fos = new FileOutputStream(file)) {
                  bytesEncriptados = cipher.doFinal(cadenaOriginal.getBytes("UTF-8"));
                  //La guarda en el fichero auxiliar
                  fos.write(bytesEncriptados);
             }


        } catch (InvalidKeyException ex) {
            System.err.println("ERROR: Clave no válida....");
        } catch (IllegalBlockSizeException ex) {
            System.err.println("ERROR: Longitud de datos proporcionados al cifrado de bloque incorrecta...");
        } catch (BadPaddingException ex) {
            System.err.println("ERROR: Fallo en el relleno de datos...");
        } catch (IOException ex) {
            System.err.println("ERROR: Excepción de entrada/salida...");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
                Logger.getLogger(Encriptar_FICH_AES_G.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("\tERR: No está disponible el esquema de cifrado indicado...\"AES/ECB/PKCS5Padding\"");
         }   
    }

    /**
     * Desencripta la cadena de texto indicada usando la clave de encriptacion
     * 
     * @param claveSecreta Clave de encriptación
     * @return String Cadena de texto desencriptada
     */
    public String desencriptar(SecretKeySpec claveSecreta)  {
        SecretKeySpec secretKey = claveSecreta;
        String datos_desencrip = "";
        byte[] datosDesencriptados = null;
        try{
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //pone cifrador en modo desencriptación   
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try ( 
                //declaro el fichero asignando la ruta file
                FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1000]; //array de bytes
                //lee el fichero de 1k en 1k y pasa los fragmentos leidos al cifrador
                int bytesLeidos = fis.read(buffer, 0, 1000);
                while (bytesLeidos != -1) {//mientras no se llegue al final del fichero
                    //pasa texto del fichero al cifrador y lo descifra, lo deja en datosDesencriptados
                    datosDesencriptados = cipher.update(buffer, 0, bytesLeidos);
                    bytesLeidos = fis.read(buffer, 0, 1000);
                }   
            } //array de bytes
            
            
           //compone en datosDescod la cadena de texto descifrada
           datos_desencrip = new String(datosDesencriptados) + new String(cipher.doFinal());

        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: Fichero no encontrado.");
        } catch (InvalidKeyException ex) {
            System.err.println("ERROR: Clave no válida.");
        } catch (IOException ex) {
             System.err.println("ERROR: Excepción de entrada/salida...");
        } catch (IllegalBlockSizeException ex) {
            System.err.println("ERROR: Longitud de datos proporcionados al cifrado de bloque incorrecta.");
        } catch (BadPaddingException ex) {
            System.err.println("ERROR: Fallo en el relleno de datos...");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
              Logger.getLogger(Encriptar_FICH_AES_G.class.getName()).log(Level.SEVERE, null, ex);
              System.err.println("\tERR_Des: No está disponible el esquema de cifrado indicado...\"AES/ECB/PKCS5Padding\"");
              
       }
        return datos_desencrip;
    }
}
