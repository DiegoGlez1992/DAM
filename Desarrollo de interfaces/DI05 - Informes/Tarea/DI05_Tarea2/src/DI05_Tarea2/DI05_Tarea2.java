/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package di05_tarea2;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.*;

/**
 * <strong>Tarea 2</strong><br>
 * Incluir el informe para generar facturas en un aplicación java que lo muestre
 * en formato PDF, teniendo en cuenta que el código del cliente (ID_Cliente) se
 * pasa al informe como parámetro. Por lo tanto, al ejecutarse la aplicación se
 * generará el informe creado en el apartado anterior pero mostrando solo los
 * datos para el Identificador de cliente (Id_cliente) pasado por parámetro.
 *
 * @author Diego González García
 */
public class DI05_Tarea2 {

    static private Scanner teclado;

    // Parámetros para la conexión con la BBDD
    static private String driver, url, user, password;
    static private Connection connection = null;

    //Parámetros para el informe
    static private int idCliente;
    static private String sourceFileName;
    static private Map params;
    static private JasperPrint jasperPrint;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            teclado = new Scanner(System.in); //Crea un objeto scaner para leer el teclado

            driver = "com.mysql.cj.jdbc.Driver";    //Conector para la BD
            url = "jdbc:mysql://localhost/fabrica"; //Dirección de la BD
            user = "root";  //Usuario de la BD
            password = "";  //Contraseña del usuario
            Class.forName(driver);  //Carga el driver
            connection = DriverManager.getConnection(url, user, password);   //Realiza la conexión con la BD

            System.out.print("Indica el ID del cliente: ");
            idCliente = teclado.nextInt();  //Obtiene el ID del cliente del teclado
            sourceFileName = "src\\DI05_Tarea2_Informes\\facturasParametro.jasper"; //Ruta del informe
            params = new HashMap(); //Inicializa la tabla hash para los parámetros
            params.put("ID_Cliente", idCliente);    //Añade el parámetro ID_CLIENTE para el informe

            jasperPrint = JasperFillManager.fillReport(sourceFileName, params, connection); //Crea el informe con los datos de la BD teniendo en cuenta los parámetros indicados
            JasperExportManager.exportReportToPdfFile(jasperPrint, "src\\DI05_Tarea2_Informes\\Facturas" + Integer.toString(idCliente) + ".pdf");  //Exporta el informe a pdf

            //File path = new File("facturas" + Integer.toString(idCliente) + ".pdf");    //Abre el fichero PDF
            //Desktop.getDesktop().open(path);

        } catch (ClassNotFoundException | SQLException | JRException  ex) {
            Logger.getLogger(DI05_Tarea2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
