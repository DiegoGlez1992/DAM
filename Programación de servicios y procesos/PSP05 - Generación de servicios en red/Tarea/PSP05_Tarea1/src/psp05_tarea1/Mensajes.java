/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package psp05_tarea1;

/**
 * Mensajes que intercambia el Servidor con el Cliente según protocolo HTTP
 *
 * @author Diego González García
 */
public class Mensajes {

    public static final String lineaInicial_OK = "HTTP/1.1 200 OK";
    public static final String lineaInicial_NotFound = "HTTP/1.1 404 Not Found";
    public static final String lineaInicial_BadRequest = "HTTP/1.1 400 Bad Request";
}
