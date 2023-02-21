/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3.pkg1.clientemodificado;


import java.io.*;
import java.net.*;
public class Cliente {
    static final String HOST = "localhost";
    static final int Puerto=5000;
        public Cliente( ) {
    try{
        Socket sCliente = new Socket( HOST , Puerto );
        System.out.println("El timeout en el cliente es: "+ sCliente.getSoTimeout());
        InputStream aux = sCliente.getInputStream();
        DataInputStream flujo_entrada = new DataInputStream( aux );
        System.out.println( flujo_entrada.readUTF() );
        sCliente.close();
    } catch( Exception e ) {
        System.out.println( e.getMessage() );
    }
}
}
