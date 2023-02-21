/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package congreso;
/**
 *
 * @author IMCG
 */
//paquetes necesarios del API Db4o para Java
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
public class Main {
    /**
     * @param args the command line arguments
     */
   public static void main(String[] args) {
    ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
            "congreso.db4o");
    //La base de datos física es el fichero "congreso.db4o" almacenado en la
    //carpeta raíz del proyecto creado
    try {
      almacenarPonentes(db);
    } finally {
      db.close(); //cerrar la conexión a la base de datos
    }
  }
  //Método para almacenar datos en la Base de Objetos.
  public static void almacenarPonentes(ObjectContainer db) {
    //se crean cuatro objetos tipo alumno con valores asignados
    ponente p1 = new ponente("11A", "Antonio Camaco", "acamacho@gmail.es", 300);
    ponente p2 = new ponente("22B","Isabel Pérez", "iperez@hotmail.es", 100);
    ponente p3 = new ponente("33C","Ana Navarro", "anavarro@yahoo.com", 200);
    ponente p4 = new ponente("44D","Pedro Sánchez", "psanchez@mixmail.com", 90);
    //Persistir Objetos: almacenamos los objetos con el método store()
    db.store(p1);
    db.store(p2);
    db.store(p3);
    db.store(p4);
  }
}
