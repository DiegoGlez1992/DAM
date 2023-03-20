/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gonzalezgarciadiego_ad05_tarea1;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Query;
import java.io.File;

/**
 * <strong>Clase para gestionar las bases de objetos.</strong>
 *
 * @author Diego González García
 */
public class BaseDatos_db4o {

    /**
     * Nombre de la base de objetos
     */
    private static final String DATABASEFILENAME = "BDDepartamentoDocente";

    private static ObjectContainer objectContainer = null;

    /**
     * <strong>Método constructor</strong><br>
     */
    public BaseDatos_db4o() {
        //Define un fichero, que será el que contenga la base de datos física y borra su contenido previo
        File file = new File(DATABASEFILENAME);
        file.delete();

        //Indica la nueva configuración de conexión, con borrado en cascada
        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        config.common().objectClass(Departamento.class).cascadeOnDelete(true);
    }

    /**
     * <strong>Método para almacenar la información de la base de
     * objetos</strong><br>
     */
    public void almacenarInformacion() {
        try {
            objectContainer = Db4oEmbedded.openFile(DATABASEFILENAME);  //Abre la conexión a la base de objetos
            objectContainer.store(new Departamento("Informática", 5, 44, new Docente("Ariadna", 4)));
            objectContainer.store(new Departamento("Lengua", 3, 8, new Docente("Iván", 3)));
            objectContainer.store(new Departamento("FOL", 3, 15, new Docente("Noelia", 5)));
            objectContainer.store(new Departamento("Matemáticas", 5, 8, new Docente("Sergio", 4)));
            objectContainer.store(new Departamento("Latín", 3, 2, null));
            objectContainer.store(new Departamento("Imagen y Sonido", 5, 12, new Docente("Lidia", 4)));
            objectContainer.store(new Departamento("Electrónica", 3, 5, null));
            objectContainer.store(new Departamento("Inglés", 1, 24, new Docente("David", 7)));
            objectContainer.store(new Departamento("Música", 20, 6, new Docente("Paula", 6)));
            objectContainer.store(new Departamento("Francés", 5, 2, new Docente("Rubén", 7)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objectContainer.close();    //Cierra la conexión a la base de objetos
        }
    }

    /**
     * <strong>Método para mostrar objetos recuperados de la Base de
     * Objetos</strong><br>
     *
     * @param objectSet
     */
    public void mostrarConsulta(ObjectSet objectSet) {
        //System.out.println("Recuperados " + objectSet.size() + " objetos");
        while (objectSet.hasNext()) {
            System.out.println(objectSet.next());
        }
    }

    /**
     * <strong>Método que resuelve la consulta 1</strong><br>
     */
    public void departamentosMas20Asignaturas() {
        try {
            objectContainer = Db4oEmbedded.openFile(DATABASEFILENAME);  //Abre la conexión a la base de objetos
            Query query = objectContainer.query();  //Declara un objeto Query
            query.constrain(Departamento.class);    //Indica la clase a la que se aplicarán restricciones
            query.descend("numeroDeModulos").constrain(20).greater();   //Se declara una restricción
            ObjectSet objectSet = query.execute();  //Ejecuta la consulta
            mostrarConsulta(objectSet); //Muestra los resultados de la consulta con el metodo mostrarConsulta
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objectContainer.close();    //Cierra la conexión a la base de objetos
        }
    }

    /**
     * <strong>Método que resuelve la consulta 2</strong><br>
     */
    public void modificarAsignaturasInformatica() {
        try {
            objectContainer = Db4oEmbedded.openFile(DATABASEFILENAME);  //Abre la conexión a la base de objetos
            ObjectSet objectSet = objectContainer.queryByExample(new Departamento("Informática", null, null, null));    //Ejecuta una consulta a través de un patrón de datos
            Departamento departamento = (Departamento) objectSet.next();    //Obtiene el departamento consultado
            System.out.println("DATOS ANTIGUOS -> " + departamento);
            departamento.setNumeroDeModulos(departamento.getNumeroDeModulos() + 2); //Modifica el atributo incrementándolo en 2
            objectContainer.store(departamento);    //Almacena el objeto en la Base de Objetos
            objectSet = objectContainer.queryByExample(new Departamento("Informática", null, null, null));  //Ejecuta una consulta a través de un patrón de datos
            departamento = (Departamento) objectSet.next(); //Obtiene el departamento consultado
            System.out.println("DATOS ACTUALES -> " + departamento);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objectContainer.close();    //Cierra la conexión a la base de objetos
        }
    }

    /**
     * <strong>Método que resuelve la consulta 3</strong><br>
     */
    public void borrarDepartamentosMas4Aulas() {
        try {
            objectContainer = Db4oEmbedded.openFile(DATABASEFILENAME);  //Abre la conexión a la base de objetos
            Query query = objectContainer.query();  //Declara un objeto Query
            query.constrain(Departamento.class);    //Indica la clase a la que se aplicarán restricciones
            query.descend("numeroDeAulas").constrain(4).greater();  //Se declara una restricción
            ObjectSet objectSet = query.execute();  //Ejecuta la consulta
            while (objectSet.hasNext()) {
                Departamento departamento = (Departamento) objectSet.next();    //Obtiene cada departamento de la consulta
                System.out.println("BORRANDO -> " + departamento);
                objectContainer.delete(departamento);   //Borra el departamento
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objectContainer.close();    //Cierra la conexión a la base de objetos
        }
    }

    /**
     * <strong>Método que resuelve la consulta 4</strong><br>
     */
    public void mostrarDepartamentos() {
        try {
            objectContainer = Db4oEmbedded.openFile(DATABASEFILENAME);  //Abre la conexión a la base de objetos
            Query query = objectContainer.query();  //Declara un objeto Query
            query.constrain(Departamento.class);    //Indica la clase a la que se aplicarán restricciones
            ObjectSet objectSet = query.execute();  //Ejecuta la consulta
            mostrarConsulta(objectSet); //Muestra los resultados de la consulta con el metodo mostrarConsulta
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objectContainer.close();    //Cierra la conexión a la base de objetos
        }
    }
}
