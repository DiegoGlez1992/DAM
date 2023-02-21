/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3.servidormodificado;


import java.io.* ;
import static java.lang.Thread.sleep;

import java.net.* ;

public class Servidor {
     static final int Puerto=5000;
    public Servidor(){
        try {

        ServerSocket skServidor = new ServerSocket(Puerto);
 

        System.out.println("Escucho el puerto " + Puerto );

        for ( int nCli = 0; nCli < 3; nCli++) {

            Socket sCliente = skServidor.accept(); 
            
                       System.out.println("El time out es: "+ sCliente.getSoTimeout());

            System.out.println("Sirvo al cliente " + nCli);

            OutputStream aux = sCliente.getOutputStream();

            DataOutputStream flujo_salida= new DataOutputStream( aux );
            sleep(20000);

            flujo_salida.writeUTF( "Hola cliente " + nCli );

            sCliente.close();

        }

        System.out.println("Ya se han atendido los 3 clientes");

    } catch( Exception e ) {

        System.out.println( e.getMessage() );

    }
    }

}
