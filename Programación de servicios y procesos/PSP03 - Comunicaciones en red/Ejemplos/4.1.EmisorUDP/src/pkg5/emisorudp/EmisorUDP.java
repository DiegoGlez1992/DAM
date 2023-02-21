/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg5.emisorudp;

import java.net.*;
import java.io.*;
public class EmisorUDP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Necesita dos argumentos de entrada, el equipo al que me voy a conectar
        //y el mensaje que le quieres enviar.
        //localhost hola
        // TODO code application logic here
        try{
            // Crea el socket
            DatagramSocket sSocket = new DatagramSocket();
            // Construye la dirección del socket del receptor
            InetAddress maquina = InetAddress.getByName(args[0]);
            int Puerto = 1500;
            // Crea el mensaje
            byte [] cadena = args[1].getBytes();
            DatagramPacket mensaje = new DatagramPacket(cadena,args[1].length(), maquina, Puerto);
            // Envía el mensaje
            sSocket.send(mensaje);
            sSocket.send(mensaje);
            // Cierra el socket
            sSocket.close();
            } catch(UnknownHostException e) {
                System.err.println("Desconocido: " + e.getMessage());
            } catch(SocketException e) {
                System.err.println("Socket: " + e.getMessage());
            } catch(IOException e) {
                System.err.println("E/S: " + e.getMessage());
            }
    }
}
    

