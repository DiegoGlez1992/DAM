/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gonzalezgarciadiego_ad06_tarea3;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import net.xqj.exist.ExistXQDataSource;

/**
 * <strong>Clase para gestionar las bases de datos de eXist.</strong>
 *
 * @author Diego González García
 */
public class BaseDatos_eXist {

    XQConnection xqConnection() {
        XQDataSource xqDataSource;  //Fábrica para obtener objetos XQConnection
        XQConnection xqConnection = null;   //Para referenciar conexiones
        try {
            xqDataSource = new ExistXQDataSource(); //Instancia el objeto
            xqDataSource.setProperty("serverName", "localhost");    //Define la direccion de la base de datos
            xqDataSource.setProperty("port", "8080");   //Define el puerto
            xqDataSource.setProperty("user", "admin"); //Define el usuario
            xqDataSource.setProperty("password", "admin"); //Define la contraseña
            xqConnection = xqDataSource.getConnection();    //Obtiene la conexión
        } catch (XQException ex) {
            Logger.getLogger(BaseDatos_eXist.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xqConnection;
    }
}
