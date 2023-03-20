
package Tarea1_servidor_http_g;

//Mensajes que intercambia el Servidor con el Cliente según protocolo HTTP
public class Mensajes_G {
    
    public static final String lineaInicial_OK = "HTTP/1.1 200 OK";
    public static final String lineaInicial_NotFound = "HTTP/1.1 404 Not Found";
    public static final String lineaInicial_BadRequest = "HTTP/1.1 400 Bad Request";
}
