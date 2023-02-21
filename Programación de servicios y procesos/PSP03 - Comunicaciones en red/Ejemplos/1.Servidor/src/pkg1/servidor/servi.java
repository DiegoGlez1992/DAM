/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1.servidor;

import java.io.* ;

import java.net.* ;

class Servi {

    static final int Puerto=5000;

    public Servi( ) {

    try {

        // Inicio la escucha del servidor en un determinado puerto

        ServerSocket skServidor = new ServerSocket(Puerto);

        System.out.println("Escucho el puerto " + Puerto );

        // Espero a que se conecte un cliente y creo un nuevo socket para el cliente

        Socket sCliente = skServidor.accept(); 

        // ATENDER PETICIÓN DEL CLIENTE
        

        // Cierro el socket

        sCliente.close();

        

    } catch( Exception e ) {

        System.out.println( e.getMessage() );

}

    }
}