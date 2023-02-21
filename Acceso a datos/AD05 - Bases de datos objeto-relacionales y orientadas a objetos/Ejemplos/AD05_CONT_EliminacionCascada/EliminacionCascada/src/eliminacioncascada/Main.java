/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eliminacioncascada;

/**
 *
 * @author IMCG
 */
//API necesaria de db4o
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration; //establecer configuración de conexión
import com.db4o.query.Query;

public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
//Conexión a la base de objetos y apertura de la base de objetos congreso.db4o
//Se CONFIGURA la apertura de manera que al eliminar un objeto estructurado
//de tipo charla, se eliminen también los objeto hilo tipo ponente.

//Se indica la nueva configuración de conexión, con borrado en cascada
    EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    config.common().objectClass(charla.class).cascadeOnDelete(true);
//Se abre la conexión a la base de objetos congreso.db4o
    ObjectContainer db = Db4oEmbedded.openFile(config, "congreso.db4o");


    //llamada a métodos para operar con la base de objetos
    try {
      almacenarCharlas(db);
      System.out.println("CHARLAS EXISTENTES EN LA BDOO:");
      mostrarCharlasQBE(db);
      System.out.println("ELIMINACIÓN de la charla XML:");
      borrarCharlaporTitulo(db, "XML");
      System.out.println("CHARLAS Y PONENTES EXISTENTES tras borrar charla XML:");
      mostrarCharlasQBE(db);
      mostrarPonentes(db);
    } catch (Exception e) {
      //código para el tratamiento de la excepción
    } finally {
      db.close(); // cerrar la base de datos antes de salir
    }
  }

  //Almacenar objetos estructurados
  //Método para insertar charlas en la Base de Objetos y almacenarlas
  public static void almacenarCharlas(ObjectContainer db) {
    //se crean 4 objetos tipo charla
    charla c1 = new charla("Bases de Datos Orientadas a Objetos", 2);
    charla c2 = new charla("MySQL y PostGreSQL", 3);
    charla c3 = new charla("XML", 2);
    charla c4 = new charla("Db4o", 3);

    //se crean 4 objetos ponente
    ponente p1 = new ponente("11A", "Antonio Camaco", "acamacho@gmail.es", 300);
    ponente p2 = new ponente("22B", "Isabel Pérez", "iperez@hotmail.es", 100);
    ponente p3 = new ponente("33C", "Ana Navarro", "anavarro@yahoo.com", 200);

    //se le asigna un ponente a cada charla
    c1.setPonente(p1);
    c2.setPonente(p2);
    c3.setPonente(p3);
    c4.setPonente(p1);

//Persistir Objetos: almacenamos los objetos con el método store()
    db.store(c1);
    db.store(c2);
    db.store(c3);
    db.store(c4);

  }

  //Método para mostrar objetos recuperados de la Base de Objetos
  public static void mostrarConsulta(ObjectSet resul) {
    //mensaje indicando el total de objetos recuperados
    System.out.println("Recuperados " + resul.size() + " Objetos");
    while (resul.hasNext()) {//bucle que obtiene objeto a objeto
      System.out.println(resul.next());
    }
  }
  //Consulta QBE de objetos estructurados. Consulta de todas las charlas.

  public static void mostrarCharlasQBE(ObjectContainer db) {
    //se crea el objeto charla patrón de búsqueda
    charla c = new charla(null, 0);
    //Consulta las charlas con patrones indicados
    ObjectSet resul = db.queryByExample(c);
    mostrarConsulta(resul);//método que muestra los objetos recuperados de BDOO
  }
  //Borrado de objetos estructurados. Se utiliza Consulta SODA
  //Se elimina la charla de título tit sin eliminar al ponente asociado

  public static void borrarCharlaporTitulo(ObjectContainer db, String tit) {
    Query query = db.query(); //declaración de un objeto query().
    query.constrain(charla.class);//establece la clase a la que se aplicará la restricción
    query.descend("titulo").constrain(tit);//establece la restricción de búsqueda
    ObjectSet resul = query.execute();//ejecuta consulta con restricción búsqueda
    while (resul.hasNext()) { //bucle que recupera los objetos charla y elimina de la BDOO
      charla c = (charla) resul.next();
      System.out.println("Eliminando: " + c);
      db.delete(c);
    }
  }
//Consulta de todos los objetos ponente. Consulta QBE

  public static void mostrarPonentes(ObjectContainer db) {
    //se crea objeto p con patrón de búsqueda (el ejemplo)
    ponente p = new ponente(null, null, null, 0);
    //consulta los ponentes de patrón ponente(null, null, null, 0). Consulta QBE
    ObjectSet res = db.queryByExample(p);
    mostrarConsulta(res); //método que muestra los objetos recuperados de BDOO
  }
}
