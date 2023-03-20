/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PSP04_Tarea3_Servidor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * <strong>Clase para el objeto servidor.</strong><br>
 *
 * @author Diego González García
 */
public class Servidor extends Thread {

    /**
     * Socket para la comunicación con el cliente
     */
    private static Socket clientSocket;
    /**
     * Nombre del archivo
     */
    private String fileName = "";
    /**
     * Variable auxiliar para la lectura del fichero
     */
    private String aux = "";
    /**
     * Variable auxiliar para la lectura de los comandos
     */
    private String comando = "";
    /**
     * Variable auxiliar para controlar la máquina de estados
     */
    private int estado;
    /**
     * Lista de usuarios existentes en el servidor
     */
    private ArrayList<User> listaUsuarios;
    /**
     * Variable auxiliar para controlar el nombre de usuario
     */
    private String user = "";
    /**
     * Variable auxiliar para controlar la contraseña de usuario
     */
    private String pass = "";
    /**
     * Variable auxiliar para controlar el accesso
     */
    private String access = "";
    /**
     * Objeto para el archivo
     */
    private File file = null;
    /**
     * Lector de archivo
     */
    private FileReader fileReader;
    /**
     * Buffer de lectura
     */
    private BufferedReader bufferedReader;
    /**
     * Stream para la salida de datos
     */
    private DataOutputStream dataOutputStream = null;
    /**
     * Stream para la entrada de datos
     */
    private DataInputStream dataInputStream = null;

    /**
     * Método constructor
     *
     * @param sCliente Socket del cliente
     * @param listaUsuarios Lista de usuarios autorizados
     */
    public Servidor(Socket sCliente, ArrayList<User> listaUsuarios) {
        clientSocket = sCliente;
        this.listaUsuarios = listaUsuarios;
    }

    /**
     * Método run
     */
    @Override
    public void run() {
        try {
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());    //Crea el stream de salida
            dataInputStream = new DataInputStream(clientSocket.getInputStream());   //Crea el stream de entrada

            dataOutputStream.writeUTF("SERVIDOR: Se ha conectado correctamente");
            estado = 0;
            do {
                switch (estado) {
                    case 0: //Log in
                        dataOutputStream.writeUTF("SERVIDOR: Introduce su nombre de usuario. ");
                        user = dataInputStream.readUTF();    //Recibe el nombre de usuario
                        dataOutputStream.writeUTF("SERVIDOR: Introduce su contraseña. ");
                        pass = dataInputStream.readUTF();    //Recibe la contraseña de usuario

                        for (int i = 0; i < this.listaUsuarios.size(); i++) {   //Comprueba si el usuario existe en la lista de usuarios autorizados
                            if (this.listaUsuarios.get(i).getUser().equals(user) && this.listaUsuarios.get(i).getPass().equals(pass)) { //Acceso autorizado
                                access = "ACK";
                                dataOutputStream.writeUTF("SERVIDOR: Acceso concedido. ");
                                dataOutputStream.writeUTF("ACK");
                                System.out.println("USUARIO " + user + " AUTORIZADO");
                                estado = 1;
                                break;
                            }
                        }
                        if (!access.equalsIgnoreCase("ACK")) {  //Acceso no autorizado
                            dataOutputStream.writeUTF("SERVIDOR: Acceso denegado. ");
                            dataOutputStream.writeUTF("NACK");
                            System.out.println("USUARIO " + user + " NO AUTORIZADO");
                            estado = 0;
                        }
                        break;
                    case 1: //Opciones disponibles
                        dataOutputStream.writeUTF("SERVIDOR: Introduce un comando (ls/get/exit). ");
                        comando = dataInputStream.readUTF();    //Recibe el comando a ejecutar
                        if (comando.equals("ls")) {
                            System.out.println("El cliente " + user + " quiere ver el contenido del directorio");
                            estado = 2;
                        } else if (comando.equals("get")) {
                            System.out.println("El cliente " + user + " quiere ver el contenido de un archivo");
                            estado = 3;
                        } else if (comando.equals("exit")) {
                            System.out.println("El cliente " + user + " quiere salir");
                            estado = -1;
                        }
                        break;
                    case 2: //Contenido del directorio
                        file = new File(".");  //Instancia el archivo
                        if (file.exists()) { //Comprueba que existe
                            File[] files = file.listFiles();    //Guarda el contenido del directorio
                            for (File fileAux : files) {
                                dataOutputStream.writeUTF(fileAux.getName()); //Envía los nombres
                            }
                            dataOutputStream.writeUTF("EOT");   //Envía la clave de final de transmisión
                        }
                        estado = 1;
                        break;
                    case 3: //Solicita nombre del archivo
                        dataOutputStream.writeUTF("SERVIDOR: Introduce el nombre del archivo. ");
                        fileName = dataInputStream.readUTF();   //Recibe el nombre del archivo a consultar
                        System.out.println("El cliente " + user + " quiere ver el contenido del archivo: " + fileName);
                        estado = 4;
                        break;
                    case 4: //Contenido del archivo
                        file = new File(fileName);  //Instancia el archivo
                        fileReader = new FileReader(file);  //Crea el lector del archivo
                        bufferedReader = new BufferedReader(fileReader);    //Crea el buffer de lectura
                        while ((aux = bufferedReader.readLine()) != null) { //Lee el archivo línea a línea
                            dataOutputStream.writeUTF(aux);  //Envía el contenído de la línea
                        }
                        dataOutputStream.writeUTF("EOF");   //Envía la clave de final de archivo
                        estado = 1;
                        break;
                    default:
                        estado = 1;
                        break;
                }
            } while (estado != -1);
            //clientSocket.close();   //Cierra el socket con el cliente
            //System.out.println("Cliente desconectado");
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close(); //Cierra el buffer de lectura
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar bufferedReader");
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close(); //Cierra el lector del archivo
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar fileReader");
                }
            }
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();    //Cierra el stream de entrada
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar dataInputStream");
                }
            }
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();   //Cierra el stream de salida
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar dataOutputStream");
                }
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();   //Cierra el socket con el cliente
                    System.out.println("CLIENTE " + user + " DESCONECTADO");
                } catch (IOException e) {
                    System.out.println("Excepción de E/S al cerrar clientSocket");
                }
            }
        }
    }
}
