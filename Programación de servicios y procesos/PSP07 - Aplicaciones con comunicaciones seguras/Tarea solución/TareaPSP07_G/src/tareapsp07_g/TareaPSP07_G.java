
package tareapsp07_g;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Ejercicio que genere una cadena de texto y la deje almacenada en un fichero encriptado, 
 * en la raíz del proyecto hayas creado, con el nombre fichero.cifrado.
 * Para encriptar el fichero, utilizarás el algoritmo Rijndael o AES, 
 * con las especificaciones de modo y relleno siguientes: Rijndael/ECB/PKCS5Padding.
 * 
 * La clave, la debes generar de la siguiente forma: 
 * Obtener un hash de un password (un String) con el algoritmo "SHA-256".
 * Copiar con el método Arrays.copyOf los 192 bits a un array de bytes (192/8 bytes)
 * Utilizar la clase SecretKeySpec para generar una clave a partir del array de bytes.
 * 
 * @author Gema
 */
public class TareaPSP07_G {
    
    //Cadena de texto que se va a cifrar y dejar almacenada en el fichero cifrado.
    private static String cadenaOriginal = "Hoy vamos a conquistar el cielo sin "
                                          + "mirar lo alto que queda el suelo.";
    
    //Password de tipo String para obtener el hash con el algoritmo.
     private static String claveEncriptacion = "password";     
     
    //Nombre del fichero en el que se va a guardar la cadena cifrada situado
    //en la raiz del proyecto.
    private static String ficheroCifrado= "fichero.cifrado";

    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, FileNotFoundException, BadPaddingException {
        //objeto para cifrar
        Encriptar_FICH_AES_G encriptarAES = 
                new Encriptar_FICH_AES_G(ficheroCifrado, claveEncriptacion);

        SecretKeySpec claveEncriptada = encriptarAES.generarClave();
        
        
        // realizamos el cifrado en el fichero cifrado.
        System.out.println("\nCadena Original     : " + cadenaOriginal);
        encriptarAES.encriptar(cadenaOriginal, claveEncriptada);
       
        // obtenemos el valor de la cadena descifrado
        String desencriptado = encriptarAES.desencriptar(claveEncriptada);
        System.out.println("Cadena desencriptada: " + desencriptado);   
        
    }
    
}
