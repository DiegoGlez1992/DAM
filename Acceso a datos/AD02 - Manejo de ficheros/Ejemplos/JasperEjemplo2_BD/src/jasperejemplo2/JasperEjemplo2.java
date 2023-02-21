/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasperejemplo2;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author msr
 */
public class JasperEjemplo2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String reportSource = "./report/templates/HolaMundo.jrxml";
        String reportDest = "./report/results/HolaMundo.html";
     
        Map<String, Object> params = new HashMap<>();
        //parametros
        params.put("reportTitle", "Hola mundo");
        params.put("author", "Usuario DDAM");
        //fecha actual
        String date = new java.util.Date().toString();
        params.put("startDate", date);

        try {
            //Compilar la plantilla
            JasperReport jasperReport
                    = JasperCompileManager.compileReport(reportSource);

            //Habilitar el driver necesario
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            //Crear una conexion para pasar el informe 
            java.sql.Connection conn = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/sample", "app", "app");
            //Sustituir el parametro datasource JR vacio por
            //el parametro de conexion conn
            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(
                            jasperReport, params, conn);

            JasperExportManager.exportReportToHtmlFile(
                    jasperPrint, reportDest);

            JasperViewer.viewReport(jasperPrint);
        } catch (JRException ex) {} catch (ClassNotFoundException ex) {
            System.err.println("Error plantilla. " + ex.toString());
            Logger.getLogger(JasperEjemplo2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            System.err.println("Error SQL");
            Logger.getLogger(JasperEjemplo2.class.getName()).log(Level.SEVERE, null, ex);
        }
        }

    }
