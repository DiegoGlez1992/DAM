/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package psp06_tarea1;

import java.io.*;
import java.util.logging.*;

/**
 * <strong>Clase para crear fichero de registro</strong>
 *
 * @author Diego González García
 */
public class MyLogger {
    
    private Logger logger;
    private FileHandler fileHandler;
    
    /**
     * <strong>Método constructor</strong><br>
     */
    public MyLogger() {
        
        try {
            //Busca o crea el logger que queremos utilizar
            this.logger = Logger.getLogger("MyLog");
            //Busca o crea el fichero de logs. Append = true indica que escriba en el mismo fichero si ya existe
            this.fileHandler = new FileHandler("MyLogFile.log", true);
            //Asocia el fichero al Log
            this.logger.addHandler(this.fileHandler);
            //Visualización de mensajes de log por pantalla
            this.logger.setUseParentHandlers(false);
            //Formato del fichero [SimpleFormatter() / XMLFormatter()]
            SimpleFormatter formatter = new SimpleFormatter();
            //Asocia el formato al fichero
            this.fileHandler.setFormatter(formatter);
            //Añade el nivel de seguridad de las actividades a registrar
            this.logger.setLevel(Level.ALL); //ALL registra todos los eventos
            //Añade un mensaje al log
            this.logger.log(Level.INFO, "Programa ejecutado");
        } catch (IOException | SecurityException ex) {
            ex.printStackTrace();
        }
    }
    
    public void addLog(Level level, String msg){
        this.logger.log(level, msg);
    }
}
