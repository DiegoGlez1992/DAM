/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cifrar_clavepublicarsa;

/**
 *
 * @author IMCG
 */
import java.io.IOException;
import java.security.*;
import javax.crypto.*;

//Encriptar y desencriptar un texto mediante clave pública RSA
public class Main {

  public static void main(String[] args) throws Exception {

    System.out.println("Crear clave pública y privada");
    //Crea e inicializa el generador de claves RSA
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(512);//tamaño de la clave
    KeyPair clavesRSA = keyGen.generateKeyPair();
    PrivateKey clavePrivada = clavesRSA.getPrivate();//obtiene clave privada
    PublicKey clavePublica = clavesRSA.getPublic();//obtiene clave pública

    //muestra las claves generadas
    System.out.println("clavePublica: " + clavePublica);
    System.out.println("clavePrivada: " + clavePrivada);
    //texto a encriptar o cifrar
    byte[] bufferClaro = "Este es el mensaje secreto\n".getBytes();

    //Crea cifrador RSA
    Cipher cifrador = Cipher.getInstance("RSA");
    //Pone cifrador en modo ENCRIPTACIÓN utilizando la clave pública
    cifrador.init(Cipher.ENCRYPT_MODE, clavePublica);
    System.out.println("Cifrar con clave pública el Texto:");
    mostrarBytes(bufferClaro);

    //obtiene todo el texto cifrado
    byte[] bufferCifrado = cifrador.doFinal(bufferClaro);
    System.out.println("Texto CIFRADO");
    mostrarBytes(bufferCifrado); //muestra texto cifrado
    System.out.println("\n_______________________________");

    //Pone cifrador en modo DESENCRIPTACIÓN utilizando la clave privada
    cifrador.init(Cipher.DECRYPT_MODE, clavePrivada);
    System.out.println("Descifrar con clave privada");

    //obtiene el texto descifrado
    bufferClaro = cifrador.doFinal(bufferCifrado);
    System.out.println("Texto DESCIFRADO");
    mostrarBytes(bufferClaro);//muestra texto descifrado
    System.out.println("\n_______________________________");
  }

  public static void mostrarBytes(byte[] buffer) throws IOException {
    System.out.write(buffer);
  }
}
