/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package doctoradomatisseoql;

//bibliotecas necesarias
import com.matisse.*;
import com.matisse.sql.MtResultSet;
import java.sql.*;

/**
 *
 * @author IMCG
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//Crea un objeto MtDatabase asociando la cadena de conexión
        MtDatabase dbcon = new MtDatabase("localhost", "doctorado");
// Abrir una conexión a la base de objetos
        dbcon.open();
        try {
            Connection jdbcon = dbcon.getJDBCConnection();
// Crea la sentencia
            Statement stmt = jdbcon.createStatement();
           //Define la consulta SELECT
            String query = "SELECT nombre, email FROM Profesor;";
           //Ejecuta la consulta y obtiene un EesulSet
            ResultSet rset = stmt.executeQuery(query);

// Imprime total de objetos recuperados
            System.out.println("Total selected: "
                    + ((MtResultSet) rset).getTotalNumObjects());
            System.out.println("");
// Imprime los nombre de columna
            ResultSetMetaData rsMetaData = rset.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();
// Obtiene los nombres de columna; indexa las columnas comenzando desde 1
            for (int i = 0; i < numberOfColumns; i++) {
                System.out.print(String.format("%30s",
                        rsMetaData.getColumnName(i + 1)) + " ");
            }
            System.out.println("");
            for (int i = 0; i < numberOfColumns; ++i) {
                System.out.print(" ----------------------------- ");
            }
            System.out.println("");
            String nombrep, emailp;
            // Read filas (objetos) one by one
            while (rset.next()) {
// Obtiene los valores de la primera y segunda columna
                nombrep = rset.getString(1);
                emailp = rset.getString(2);


// Imprime la fila en curso
                System.out.println(String.format("%30s", nombrep) + " "
                        + String.format("%30s", emailp));
            }
// resetea y cierra la conexión a la base de objetos
            rset.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        if (dbcon.isVersionAccessInProgress()) {
            dbcon.endVersionAccess();
        } else if (dbcon.isTransactionInProgress()) {
            dbcon.rollback();
        }
        dbcon.close();
    }
}
