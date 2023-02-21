/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pkg4.receptorudp;

import java.io.*;
import java.net.*;
public class ReceptorUDP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            // Crea el socket
            DatagramSocket sSocket = new DatagramSocket(1500);
            // Crea el espacio para los mensajes
            byte [] cadena = new byte[1000] ;
            DatagramPacket mensaje = new DatagramPacket(cadena, cadena.length);
            System.out.println("Esperando mensajes..");
            while(true){
                // Recibe y muestra el mensaje
                sSocket.receive(mensaje);
                String datos=new String(mensaje.getData(),0,mensaje.getLength());
                System.out.println("\tMensaje Recibido: " +datos);
            }
        } catch(SocketException e) {
            System.err.println("Socket: " + e.getMessage());
        } catch(IOException e) {
        System.err.println("E/S: " + e.getMessage()); }
        }
}
 
