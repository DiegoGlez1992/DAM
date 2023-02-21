package PaquetePrincipal;

import java.net.InetAddress;
import java.net.UnknownHostException;

/******************************************************************************
 * Para que este programa funcione correctamente tendrás que tener salida a
 * Internet mediante un router
 * 
 * @author IMCG
 */
public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    try {
      //RED LOCAL
      System.out.println("**********LA RED LOCAL*************");





      //Obtiene el objeto InetAddress de localhost
      InetAddress objetoLocalhost = InetAddress.getByName("localhost");

      System.out.println("IP de localhost:");
      System.out.println(objetoLocalhost.getHostAddress());

      //obtiene dirección de mi Equipo-- puedo utilizar:
      //getLocalHost() o getByName("nombredemiEquipo")
      InetAddress MiEquipoLan = InetAddress.getLocalHost();

      System.out.println("\nNombre de mi Equipo en la red local:");
      System.out.println(MiEquipoLan.getHostName());
      System.out.println("\nIP de mi Equipo en la red local:");
      System.out.println(MiEquipoLan.getHostAddress());


      //En INTERNET
      System.out.println("\n********* INTERNET************");

      //Obtener objeto InetAddress  de www.iesalandalus.org
      InetAddress objetoAlandalus_1 =
              InetAddress.getByName("ftp.iesalandalus.org");
      //Obtener objeto InetAddress  de ftp.iesalandalus.org
      InetAddress objetoAlandalus_2 =
              InetAddress.getByName("ftp.iesalandalus.org");

      //Obtiene y muestra la IP del nombre de dominio 
      System.out.println("\nIP de www.iesalandalus.org:");
      System.out.println(objetoAlandalus_1.getHostAddress());

      System.out.println("\nIP de ftp.iesalandalus.org:");
      System.out.println(objetoAlandalus_2.getHostAddress());

      //encapsula google.com
      InetAddress[] matrizAddress = InetAddress.getAllByName("google.com");

      //Obtiene y muestras todas las IP asociadas a ese host
      System.out.println("\nImprime todas las IP disponibles para google.com: ");
      for (int i = 0; i < matrizAddress.length; i++) {
        System.out.println(matrizAddress[i].getHostAddress());
      }

    } catch (UnknownHostException e) {
      System.out.println(e);
      System.out.println(
              "Parece que no estás conectado, o que el servidor DNS no ha "
              + "podido tramitar tu solicitud");
    }
  }
}
