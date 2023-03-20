/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gonzalezgarciadiego_ad06_tarea3;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

/**
 * <strong>Clase para el método main.</strong>
 *
 * @author Diego González García
 */
public class GonzalezGarciaDiego_AD06_Tarea3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BaseDatos_eXist baseDatos_eXist;
        XQConnection xqConnection = null;   //Para la conexión con la BD
        XQExpression xqExpression = null;   //Para la ejecución inmediata de sentencias
        XQResultSequence xqResultSequence = null;   //Para los resultados de la consulta
        String consulta = "for $x in collection(/ejercicios)/bib/libro"
                + " return $x/titulo";
        try {
            baseDatos_eXist = new BaseDatos_eXist();    //Crea el objeto para la base de datos
            xqConnection = baseDatos_eXist.xqConnection();  //Toma la conexión creada
            if (xqConnection == null) {
                throw new IllegalArgumentException("Fallo al conectar con eXist.");
            }
            xqExpression = xqConnection.createExpression(); //Instancia un objeto de expresión
            xqResultSequence = xqExpression.executeQuery(consulta); //Ejecuta la consulta
            System.out.println("Libros del fichero 'libros.xml'\n"
                    + "-------------------------------");
            while (xqResultSequence.next()) {   //Recorre los resultados obtenidos
                System.out.println(xqResultSequence.getItemAsString(null)); //Muestra el resultado por consola
            }
        } catch (XQException xqe) {
            xqe.getMessage();
        } finally {
            try {
                xqResultSequence.close();
                xqExpression.close();
                xqConnection.close();
            } catch (XQException xqe) {
                xqe.getMessage();
            }
        }
    }
}
