/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejemplo0;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jfranco
 */
public class Ejemplo0 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        URL aURL = null;
        URLConnection url = null;

        try {
            aURL = new URL(
                    "http://www.google.es");
            System.out.println("protocol = " + aURL.getProtocol()); //devuelve protocolo
            System.out.println("host = " + aURL.getHost()); //host 
            System.out.println("filename = " + aURL.getFile()); //nombre del archivo recuperado
            System.out.println("port = " + aURL.getPort()); //puerto 
            try {
                url = aURL.openConnection();
            } catch (IOException ex) {
                Logger.getLogger(Ejemplo0.class.getName()).log(Level.SEVERE, null, ex);
            }
            //obtiene el tipo de contenido
            String contentType = url.getContentType();
            
            System.out.println(
                  "Encabezados destacados:\n* Content-Type: "
                    + contentType);
            //obtiene la fecha de la  última modificación
            Date fecha = new Date(url.getDate());
            System.out.println("* Fecha de la ultima modificacion: " + fecha);
            //if (contentType.startsWith("text/html"))
            try {
                //flujo para descargar el contenido de la pagina
                InputStream is = url.getInputStream();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                String linea;
                while ((linea = br.readLine()) != null) {
                    System.out.println(linea);
                }
            } catch (IOException ex) {
                Logger.getLogger(Ejemplo0.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Ejemplo0.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
