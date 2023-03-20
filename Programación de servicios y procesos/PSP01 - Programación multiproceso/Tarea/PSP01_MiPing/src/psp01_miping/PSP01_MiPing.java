/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package psp01_miping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Tarea 2 de la asignatura de PSP de la unidad 1.
 *
 * @author Diego González García
 */
public class PSP01_MiPing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        //Argumentos
        final String ip = args[0];  //IP inicial
        final int numeroIps = Integer.parseInt(args[1]);  //Número de IPs a tratar convertido a int
        final String nombreArchivo = args[2]; //Nombre del archivo

        //Variables
        String ipConsulta;  //Variable que vamos a utilizar para calcular las IPs a consultar
        Process p = null;   //Para el objeto proceso
        InputStream is; //Entrada de datos del proceso ejecutado
        InputStreamReader isr;  //Reader para la entrada de datos
        BufferedReader bfr; //Buffer para la entrada de datos

        File file = new File(nombreArchivo);    //Creamos objeto file
         if (file.exists()){    //En caso de que ya exista un archivo
             file.delete();     //Lo borramos
         }
        file.createNewFile();   //Creamos el archivo para que esté vacío
        
        //Bucle para consultar todas las IPs solicitadas
        for (int i = 0; i < numeroIps; i++) {
            ipConsulta = ip.substring(0, 12);   //Guardo la parte fija de la IP
            int i2 = i + Integer.parseInt(ip.substring(12));    //Convierto el número del terminal a entero y le sumo la posición deseada
            ipConsulta += String.valueOf(i2);   //Rearmo la IP completa que quiero consultar
            String[] comando = {"CMD", "/c", "ping", ipConsulta};   //Comando para hacer ping a la IP
            
            try {
                p = new ProcessBuilder(comando).start();    //Creamos el proceso
                System.out.print("\nIP: " + ipConsulta);
            } catch (IOException ex) {
                System.out.println("Error al ejecutar el comando. " + ex.getMessage() );
                
                System.exit(-1);
            }

            is = p.getInputStream();    //Leemos el flujo del stream del proceso
            isr = new InputStreamReader(is);    //Creamos el objeto reader
            bfr = new BufferedReader(isr);  //Creamos el objeto buffer

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));  //Indicamos el archivo a escribir. El true indica adjuntar información al archivo
                String line = bfr.readLine();   //Leemos la primera línea
                while (line != null) {  //Leemos hasta el final del bufferReader
                    System.out.println(line);   //Mostramos la línea por consola
                    bw.write(line); //Escribimos la línea
                    bw.newLine();   //Creamos la línea
                    bw.flush(); //Limpiamos el buffer
                    line = bfr.readLine();  //Leemos siguiente línea
                }
                bw.close(); //Cerramos la escritura
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                int retorno = p.waitFor();  //Esperamos a que termine el proceso hijo
                System.out.println("Valor de finalización: " + retorno);
            } catch (InterruptedException ex) {
                System.out.println("Error");
            }
            bfr.close();
        }
    }
}
