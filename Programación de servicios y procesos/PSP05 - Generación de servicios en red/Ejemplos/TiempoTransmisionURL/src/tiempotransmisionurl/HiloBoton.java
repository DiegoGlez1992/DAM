package tiempotransmisionurl;

import java.io.*;
//
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
/**
 * ****************************************************************************
 * hilo para medir el tiempo que tarda en transmitirse un recurso URL tecleado 
 * por el usuario, desde el servidor hasta el cliente
 *
 * se basa en el encabezado 'Date', donde el servidor le envía al cliente el
 * tiempo transcurrido (en milisegundos) desde el 1 de enero de 1970 GMT, hasta
 * el inicio de la transmisión
 *
 * @author IMCG
 */
class HiloBoton extends Thread {
  //variables locales
  private final String cadenaURL;
  /**
   * **************************************************************************
   * constructor
   *
   * @param cadenaURL
   */
  public HiloBoton(String cadenaURL) {
    //
    this.cadenaURL = cadenaURL;
  }
  /**
   * **************************************************************************
   * código del hilo
   *
   */
  @Override
  public void run() {
    //
    try {
      //conexión implícita con el Servidor para el acceso al recurso
      URL url = new URL(cadenaURL);
      URLConnection conexion = url.openConnection();
      conexion.connect();
      //fuerza la transmisión del recurso mediante su lectura byte a byte
      InputStream inputStream = conexion.getInputStream();
      while(inputStream.read()>-1){}
      //instante 'fin de transmisión', con respecto al 1 de enero de 1970 GTM
      long tiempoCliente = System.currentTimeMillis();
      //instante 'inicio de transmisión', con respecto al 1 de enero de 1970 GTM
      long tiempoServidor = conexion.getDate();
      //tiempo transcurrido
      System.out.println(String.format("El tiempo de transmisión del recurso "
              + "ha sido de %sms", Math.round(tiempoCliente - tiempoServidor)));
    } catch (MalformedURLException e) {
      //
      System.err.println("URL sin sentido");
    } catch (IOException e) {
      //
      System.err.println("Error de lectura/escritura");
    } finally {
      //termina la aplicación
      System.exit(0);
    }
  }
}
