
package clienteftp;

//librerías de apache para FTP
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

//librerías de java
import java.io.IOException;
import java.io.FileOutputStream;
import java.net.SocketException;

/**
 * clase para recuperar un fichero de un Servidor FTP. El fichero se deposita en
 * el primer nivel de la carpeta del proyecto
 *
 * ¡¡¡IMPORTANTE!!!
 *
 * Para probar el ejemplo puede que tengas que deshabilitar cualquier
 * cortafuegos que tengas activado
 *
 * ¡¡¡NO OLVIDARSE DE DESHACER LOS CAMBIOS!!! La seguridad del Sistema podría
 * verse comprometida
 *
 * @author IMCG
 */
public class Main {

  //objeto de la clase FTPClient de Apache, con diversos métodos para
  //interactuar y recuperar un archivo de un servidor FTP
  private static FTPClient clienteFTP;
  //flujo de salida para la escritura de datos en un fichero
  private static FileOutputStream ficheroObtenido;
  //URL del servidor
  private static String servidorURL = "ftp.rediris.es";
  //ruta relativa (en Servidor FTP) de la carpeta que contiene
  //el fichero que vamos a descargar
  private static String rutaFichero = "debian";
  //nombre del fichero (aunque carece de extensión, se trata de un fichero de
  //texto que puede abrise con el bloc de notas)
  private static String nombreFichero = "README";
  //usuario
  private static String usuario = "anonymous";
  //contraseña
  private static String password = "";
  //array de carpetas disponibles
  private static String[] nombreCarpeta;

  /**
   * **************************************************************************
   * recupera el contenido de un fichero desde un Servidor FTP, y lo deposita en
   * un nuevo fichero en el directorio de nuestro proyecto
   *
   * @param args
   */
  public static void main(String[] args) {
    try {
      int reply;
      //creación del objeto cliente FTP
      clienteFTP = new FTPClient();
      //conexión del cliente al servidor FTP
      clienteFTP.connect("ftp.rediris.es");
      //omprobación de la conexión
      reply = clienteFTP.getReplyCode();
      //si la conexión  es satisfactoria
      if (FTPReply.isPositiveCompletion(reply)) {
        //abre una sesión con el usuario anónimo
        clienteFTP.login(usuario, password);
        //lista las carpetas de primer nivel del servidor FTP
        System.out.println("Carpetas disponibles en el Servidor:");
        nombreCarpeta = clienteFTP.listNames();
        for (int i = 0; i < nombreCarpeta.length; i++) {
          System.out.println(nombreCarpeta[i]);
        }
        //nombre que el que va a recuperarse
        ficheroObtenido = new FileOutputStream(nombreFichero);
        //mensaje
        System.out.println("\nDescargando el fichero " + nombreFichero + " de "
                + "la carpeta " + rutaFichero);
        //recupera el contenido del fichero en el Servidor, y lo escribe en el
        //nuevo fichero del directorio del proyecto
        clienteFTP.retrieveFile("/" + rutaFichero + "/"
               + nombreFichero, ficheroObtenido);
        //cierra el nuevo fichero
        ficheroObtenido.close();
        //cierra la conexión con el Servidor
        clienteFTP.disconnect();
        //
        System.out.println("Descarga finalizada correctamente");
      } else {
        //desconecta
        clienteFTP.disconnect();
        System.err.println("FTP ha rechazado la conexión esblecida");
        System.exit(1);
      }
    } catch (SocketException ex) {
      //error de Socket
      System.out.println(ex.toString());
    } catch (IOException ex) {
      //error de fichero
      System.out.println(ex.toString());
    }
  }
}
