/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actualizarcongresoestructurados;

/**
 *
 * @author IMCG
 */
//API necesaria de db4o
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
//Conexión a la base de objetos y apertura de la base de objetos congreso.db4o
    ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
            "congreso.db4o");
    //llamada a métodos para operar con la base de objetos
    try {
      almacenarCharlas(db);
      System.out.println("CHARLAS EXISTENTES EN LA BDOO:");
      mostrarCharlasQBE(db);
      System.out.println("CHARLAS EXISTENTES con título XML:");
      consultaSODACharla_concreta(db, "XML");
      System.out.println("CHARLAS EXISTENTES del ponente ANTONIO CAMACO:");
      mostrarCharlasCamacoQBE(db);
      System.out.println("ELIMINACIÓN de la charla XML:");
      borrarCharlaporTitulo(db, "XML");
      System.out.println("CHARLAS Y PONENTES EXISTENTES tras borrar charla XML:");
      mostrarCharlasQBE(db);
      mostrarPonentes(db);
      System.out.println("MODIFICACIÓN de las horas de la CHARLA Db4o:");
      actualizarHorasCharla(db, "Db4o", 8.0f);
      consultaSODACharla_concreta(db, "Db4o");
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
  //Consulta SODA de objetos estructurados
  //Se consulta la charla cuyo título se pasa en parámetro tit

  public static void consultaSODACharla_concreta(ObjectContainer db, String tit) {
    Query query = db.query(); //declara un objeto query
    query.constrain(charla.class);//establece la clase a la que se aplicará la restricción
    query.descend("titulo").constrain(tit);//establece la restricción de búsqueda
    ObjectSet resul = query.execute();//ejecuta consulta
    mostrarConsulta(resul);//método que muestra los objetos recuperados de la BDOO
  }

  //Consulta de objetos estructurados. Consulta QBE
  //Consulta de las charlas del ponenete Anotnio Camaco
  public static void mostrarCharlasCamacoQBE(ObjectContainer db) {
    //se crea objeto ponente con patrón de búsqueda (el ejemplo)
    ponente p = new ponente(null, "Antonio Camaco", null, 0);
    //se crea el objeto charla con patrón de búsqueda
    charla c = new charla(null, 0);
    c.setPonente(p); //se asocia el ponente de búsqueda a la charla
    ObjectSet resul = db.queryByExample(c); //Consulta las charlas con patrones indicados
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
  //Modificación de Objetos estructurados. Con consulta QBE
  //Actualiza la duración de la charla de título tit en d horas

  public static void actualizarHorasCharla(ObjectContainer db, String tit, float d) {
    //consulta la charla de patrón charla(tit,0). Consulta QBE
    ObjectSet res = db.queryByExample(new charla(tit, 0));
    charla c = (charla) res.next(); //obtiene la charla consultada
    c.setDuracion(d); //asigna la nueva duración
    db.store(c); //almacena la charla modificada
  }
}
