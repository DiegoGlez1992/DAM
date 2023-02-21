/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2.cliente;


import java.net.*;

class Clie {

    static final String Host = "localhost";

    static final int Puerto=5000;

    public Clie( ) {

        try{

            // Me conecto al servidor en un detrminado puerto
            Socket sCliente = new Socket( Host, Puerto );
            System.out.println("se conecta");

            // TAREAS QUE REALIZA EL  CLIENTE

            // Cierro el socket

            sCliente.close();


        } catch( Exception e ) {

            System.out.println( e.getMessage() );

        }

    }
}

