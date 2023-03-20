
package Tarea1_servidor_http_g;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HiloPeticiones extends Thread {
    //Socket cliente
    private Socket sCliente=null;
    
    //Constructor
    public HiloPeticiones (Socket sCliente){
        this.sCliente = sCliente;
    }
    
    @Override
    public void run(){
        try{
            System.out.println("\n\n --> Cliente conectado. Atendiendo ...");
            
            //Llamada al m�todo que procesa las peticiones.
            procesaPeticion(sCliente);
            
            //Una vez atendida la petici�n, cierra la conexi�n entrante
            sCliente.close();
            System.out.println("Cliente desconectado. Fin de la atenci�n.");
                
        }catch(IOException e){
            System.out.println("ERROR: "+ e.getMessage());
        }catch(Exception ex){
             System.out.println("ERROR: "+ ex.getMessage());
        }
    }
    

    private static void procesaPeticion(Socket socketCliente) throws IOException {
        //Variables locales
        String peticion;
        String html;

        //Flujo de entrada
        InputStreamReader inSR = new InputStreamReader(socketCliente.getInputStream());

        //Espacio en memoria para la entrada de peticiones
        BufferedReader buffer_Leer = new BufferedReader(inSR);

        //Objeto de java.io que entre otras caracter�sticas, permite escribir 
        //'l�nea a l�nea' en un flujo de salida.
        PrintWriter printWriter = new PrintWriter(socketCliente.getOutputStream(), true);

        //Mensaje petici�n cliente
        peticion = buffer_Leer.readLine();

        //Para compactar la petici�n y facilitar as� su an�lisis, suprimimos todos 
        //los espacios en blanco que contenga
        peticion = peticion.replaceAll(" ", "");
        System.out.println("peticion = "+peticion);

        //Comprobamos que es una petici�n 'GET' (que es la �nica que vamos a
        //implementar en nuestro Servidor)
        if (peticion.startsWith("GET")) {
            //Extrae la subcadena entre 'GET' y 'HTTP/1.1'
            peticion = peticion.substring(3, peticion.lastIndexOf("HTTP"));

          //Si corresponde con la p�gina de inicio
          if (peticion.length() == 0 || peticion.equals("/")) {
                //Sirve la p�gina (inicial)
                html = Paginas_G.html_index;
                printWriter.println(Mensajes_G.lineaInicial_OK);
                printWriter.println(Paginas_G.primeraCabecera);
                printWriter.println("Content-Length: " + html.length() + 1);
                printWriter.println("\n");
                printWriter.println(html);
          } //Si corresponde a la p�gina del Quijote
          else if (peticion.equals("/quijote")) {
            //Sirve la p�gina (Quijote)
            html = Paginas_G.html_quijote;
            printWriter.println(Mensajes_G.lineaInicial_OK);
            printWriter.println(Paginas_G.primeraCabecera);
            printWriter.println("Content-Length: " + html.length() + 1);
            printWriter.println("\n");
            printWriter.println(html);
          } //En cualquier otro caso
          else {
            //Sirve la p�gina (no encontrado)
            html = Paginas_G.html_noEncontrado;
            printWriter.println(Mensajes_G.lineaInicial_NotFound);
            printWriter.println(Paginas_G.primeraCabecera);
            printWriter.println("Content-Length: " + html.length() + 1);
            printWriter.println("\n");
            printWriter.println(html);
          }

        }
        //Cierra los flujos
        inSR.close();
        printWriter.close();
    }
}
