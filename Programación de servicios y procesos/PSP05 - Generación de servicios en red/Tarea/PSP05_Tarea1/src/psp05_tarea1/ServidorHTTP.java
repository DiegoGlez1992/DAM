/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package psp05_tarea1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * <strong>Clase ServidorHTTP</strong><br>
 * Servidor HTTP que atiende peticiones de tipo 'GET' recibidas por el puerto
 * 8066.
 * <br><br>
 * <strong>NOTA:</strong> Para probar este código, comprueba primero que no
 * tienes ningún otro servicio por el puerto 8066 (por ejemplo, con el comando
 * 'netstat' si estás utilizando Windows)
 *
 * @author Diego González García
 */
public class ServidorHTTP extends Thread {

    /**
     * Socket para la comunicación con el cliente
     */
    private static Socket clientSocket;

    /**
     * Método constructor
     *
     * @param socket Socket del cliente
     */
    public ServidorHTTP(Socket socket) {
        clientSocket = socket;
    }

    /**
     * Método run
     */
    @Override
    public void run() {
        try {
            procesaPeticion(clientSocket);  //Procesa la petición del cliente
            System.out.println("Cliente atendido: " + clientSocket.toString());
            clientSocket.close();   //Cierra la conexión
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Procesa la petición recibida
     *
     * @param socketCliente
     * @throws IOException
     */
    protected static void procesaPeticion(Socket socketCliente) throws IOException {
        //variables locales
        String peticion;
        String html;

        //Flujo de entrada
        InputStreamReader inSR = new InputStreamReader(
                socketCliente.getInputStream());
        //espacio en memoria para la entrada de peticiones
        BufferedReader bufLeer = new BufferedReader(inSR);

        //objeto de java.io que entre otras características, permite escribir 
        //'línea a línea' en un flujo de salida
        PrintWriter printWriter = new PrintWriter(
                socketCliente.getOutputStream(), true);

        //mensaje petición cliente
        peticion = bufLeer.readLine();

        //para compactar la petición y facilitar así su análisis, suprimimos todos 
        //los espacios en blanco que contenga
        peticion = peticion.replaceAll(" ", "");

        //si realmente se trata de una petición 'GET' (que es la única que vamos a
        //implementar en nuestro Servidor)
        if (peticion.startsWith("GET")) {
            //extrae la subcadena entre 'GET' y 'HTTP/1.1'
            peticion = peticion.substring(3, peticion.lastIndexOf("HTTP"));

            //si corresponde a la página de inicio
            if (peticion.length() == 0 || peticion.equals("/")) {
                //sirve la página
                html = PaquetePrincipal.Paginas.html_index;
                printWriter.println(PaquetePrincipal.Mensajes.lineaInicial_OK);
                printWriter.println(PaquetePrincipal.Paginas.primeraCabecera);
                printWriter.println("Content-Length: " + html.length() + 1);
                printWriter.println("\n");
                printWriter.println(html);
            } //si corresponde a la página del Quijote
            else if (peticion.equals("/quijote")) {
                //sirve la página
                html = PaquetePrincipal.Paginas.html_quijote;
                printWriter.println(PaquetePrincipal.Mensajes.lineaInicial_OK);
                printWriter.println(PaquetePrincipal.Paginas.primeraCabecera);
                printWriter.println("Content-Length: " + html.length() + 1);
                printWriter.println("\n");
                printWriter.println(html);
            } //en cualquier otro caso
            else {
                //sirve la página
                html = PaquetePrincipal.Paginas.html_noEncontrado;
                printWriter.println(PaquetePrincipal.Mensajes.lineaInicial_NotFound);
                printWriter.println(PaquetePrincipal.Paginas.primeraCabecera);
                printWriter.println("Content-Length: " + html.length() + 1);
                printWriter.println("\n");
                printWriter.println(html);
            }

        }
    }

    /**
     * Muestra un mensaje en la Salida que confirma el arranque, y da algunas
     * indicaciones posteriores
     */
    protected static void imprimeDisponible() {
        System.out.println("El Servidor WEB se está ejecutando y permanece a la escucha por el puerto 8066.\n\n"
                + "Escribe en la barra de direcciones de tu explorador preferido:\n"
                + "\thttp://localhost:8066 para solicitar la página de bienvenida.\n"
                + "\thttp://localhost:8066/quijote para solicitar una página del Quijote.\n"
                + "\thttp://localhost:8066/q para simular un error.\n\n");
    }

}
