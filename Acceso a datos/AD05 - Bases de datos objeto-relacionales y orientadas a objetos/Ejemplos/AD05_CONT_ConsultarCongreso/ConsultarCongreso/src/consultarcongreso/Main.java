/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package consultarcongreso;

/**
 *
 * @author IMCG
 */
//API necesaria de db4o
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet; //consultas
import com.db4o.query.Constraint;//establecer restricciones
import com.db4o.query.Query; //consultas SODA
import java.util.List;

public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    //Conexión, apertura o creación de la base de objetos congreso.db4o
    ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
            "congreso.db4o");
    //operaciones sobre la base de objetos congreso
    try {
      almacenarPonentes(db);
      System.out.println("CONSULTAS QBE: ******************************");
      System.out.println("TODOS LOS PONENTES: *****");
      consultarPonentes(db);
      System.out.println("PONENTES con caché 200: *****");
      consultarPonente200(db);
      System.out.println("PONENTE de nombre Antonio Camaco: *****");
      consultarPonentePorNombre(db, "Antonio Camaco");
      System.out.println("CONSULTAS NQ: *******************************");
      System.out.println("PONENTES con caché 200: *****");
      consultarPonenteNQcache200(db);
      System.out.println("CONSULTAS SODA: *****************************");
      System.out.println("TODOS LOS PONENTES: *****");
      consultaSODAponentes(db);
      System.out.println("PONENTES con caché 200: *****");
      consultaSODAcache200(db);
      System.out.println("PONENTES con caché entre 50 y 200: *****");
      consultaSODAcacheEntre50_200(db);
      System.out.println("TODOS los PONENTES de más a menos caché: *****");
      consultaSODAponentesOrdenadosCache(db);
    } catch (Exception e) {
      //código para el tratamiento de la excepción
    } finally {
      db.close(); // cerrar la base de datos antes de salir
    }
  }

  //Método para mostrar objetos recuperados de la Base de Objetos
  public static void mostrarConsulta(ObjectSet resul) {
    System.out.println("Recuperados " + resul.size() + " Objetos");
    while (resul.hasNext()) {
      System.out.println(resul.next());
    }
  }

  //******************    Consultas QBE  ***********************
  //Consultar Objetos. consultas QBE queryByExample()
  //consulta de todos los ponentes
  public static void consultarPonentes(ObjectContainer db) {
    ponente p = new ponente(null, null, null, 0); //prototipo de búsqueda
    ObjectSet res = db.queryByExample(p); //realización de consulta
    mostrarConsulta(res);//obtención de resultados
  }

  //consulta de un ponente en concreto. Consultar ponentes de cache 200.
  public static void consultarPonente200(ObjectContainer db) {
    ponente p = new ponente(null, null, null, 200);
    ObjectSet res = db.queryByExample(p);
    mostrarConsulta(res);
  }

//consulta de ponentes por nombre. Al método se le pasa el parámetro nomb
  public static void consultarPonentePorNombre(ObjectContainer db, String nomb) {
    ponente p = new ponente(null, nomb, null, 0);//prototipo de búsqueda
    ObjectSet res = db.queryByExample(p);
    mostrarConsulta(res);
  }

  //******************   Consultas con la API SODA  ***********************
  //Método para consultar todos los ponentes
  public static void consultaSODAponentes(ObjectContainer db) {
    Query query = db.query();//declara un objeto Query
    //indica la clase a la que se aplicarán restricciones
    query.constrain(ponente.class);
    ObjectSet result = query.execute(); //Ejecuta la consulta
    mostrarConsulta(result);//muestra los resultados de la consulta
  }

  //Consulta SODA de todos los ponentes con caché 200
  public static void consultaSODAcache200(ObjectContainer db) {
    Query query = db.query(); //se declara objeto tipo Query()
    query.constrain(ponente.class); //clase a la que se aplican restricciones
    //establece restricción del valor 200 para cache
    query.descend("cache").constrain(200);
    ObjectSet result = query.execute(); //ejecuta consulta
    mostrarConsulta(result);//muestra resultados de consulta
  }

  //consulta SODA de ponentes con cache entre 50 y 200
  public static void consultaSODAcacheEntre50_200(ObjectContainer db) {
    Query query = db.query();
    query.constrain(ponente.class);
    //se declara una de las restricciones con Constraint
    Constraint constra1 = query.descend("cache").constrain(200).smaller();
    //se enlazan las dos restricciones a aplicar
    query.descend("cache").constrain(50).greater().and(constra1);
    ObjectSet result = query.execute();
    mostrarConsulta(result);
  }
  //consulta SODA de ponentes ordenados por caché, de más a menos

  public static void consultaSODAponentesOrdenadosCache(ObjectContainer db) {
    Query query = db.query();
    query.constrain(ponente.class);
    query.descend("cache").orderDescending();
    ObjectSet result = query.execute();
    mostrarConsulta(result);
  }
  //******************   Consultas Nativas    *****************************
  //consultar los ponentes de caché 200

  public static void consultarPonenteNQcache200(ObjectContainer db) {
    List res = db.query(new com.db4o.query.Predicate() {

      public boolean match(ponente p) {
        return p.getCache() == 200;
      }
      //método abstracto

      @Override
      public boolean match(Object et) {
        throw new UnsupportedOperationException("Not supported yet.");
      }
    });
    mostrarConsulta((ObjectSet) res);
  }

  //Método para almacenar datos en la Base de Objetos.
  public static void almacenarPonentes(ObjectContainer db) {
    //se crean cuatro objetos tipo alumno con valores asignados
    ponente p1 = new ponente("11A", "Antonio Camaco", "acamacho@gmail.es", 300);
    ponente p2 = new ponente("22B", "Isabel Pérez", "iperez@hotmail.es", 100);
    ponente p3 = new ponente("33C", "Ana Navarro", "anavarro@yahoo.com", 200);
    ponente p4 = new ponente("44D", "Pedro Sánchez", "psanchez@mixmail.com", 90);
    //Persistir Objetos: almacenamos los objetos con el método store()
    db.store(p1);
    db.store(p2);
    db.store(p3);
    db.store(p4);
  }
}
