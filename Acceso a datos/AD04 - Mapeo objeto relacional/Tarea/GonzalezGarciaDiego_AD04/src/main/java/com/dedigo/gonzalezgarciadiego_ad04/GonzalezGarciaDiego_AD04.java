/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.dedigo.gonzalezgarciadiego_ad04;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.NoResultException;

/**
 *
 * @author Diego González García
 */
public class GonzalezGarciaDiego_AD04 {

    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;
    private static List<Tutorias> listTutorias;
    private static List<Profesores> listProfesores;
    private static String patron;   //Patrón para el formato de salida de datos por pantalla
    private static String resultado;    //Datos para mostrar por pantalla

    public static void main(String[] args) {
        //Creamos el EntityManager
        entityManagerFactory = Persistence.createEntityManagerFactory("com.dedigo_GonzalezGarciaDiego_AD04_jar_1.0-SNAPSHOTPU");
        entityManager = entityManagerFactory.createEntityManager();

        System.out.println("\n\nEJERCICIO NÚMERO 3:\n\n");
        mostrarTutorias();  //Mostrar un listado de todas las tutorias
        insertarTutoria();  //Insertamos una tutoría
        mostrarTutorias();  //Mostrar un listado de todas las tutorias
        borrarTutoria();    //Borramos una tutoría
        mostrarTutorias();  //Mostrar un listado de todas las tutorias

        System.out.println("\n\nEJERCICIO NÚMERO 4:\n\n");
        mostrarProfesorTutoria();  //Ejercicio número 4
    }

    /**
     * <strong>Método que muestra todas las tutorías que hay en la base de
     * datos</strong>
     */
    static void mostrarTutorias() {
        entityManager.clear();
        //Actualizo la lista de tutorías
        listTutorias = entityManager.createNamedQuery("Tutorias.findAll").getResultList();
        //Defino un patrón de formato para la salida de datos por consola, como un bloque de texto
        patron = """
                    %-10s%-25s%-15s%-15s%-10s
                    ---------------------------------------------------------------------------""";
        //Muestro la cabecera de los datos utilizando el patrón creado
        System.out.println(String.format(patron,
                "CÓDIGO",
                "CURSO",
                "DÍA",
                "HORA",
                "PROFESOR"
        ));
        //Recorro la lista de tutorías mientras voy obteniendo y mostrando sus datos por pantalla
        for (Tutorias tutorias : listTutorias) {
            //Utilizo el patrón creado y añado sus respectivos datos
            resultado = String.format(patron,
                    tutorias.getIdTutoria(),
                    tutorias.getCurso(),
                    tutorias.getDiaSemana(),
                    tutorias.getHoraTutoria(),
                    tutorias.getProfesor().getCodProfe()
            );
            //Muestro la información por pantalla
            System.out.println(resultado);
        }
    }

    /**
     * <strong>Método que inserta una tutoría con id nº22</strong>
     */
    static void insertarTutoria() {
        System.out.println("\n\nInsertamos una tutoría nueva con el id nº 0022\n\n");
        Profesores profesor = null;
        //Obtenemos el profesor cuyo codigo es P001
        try {
            profesor = (Profesores) entityManager.createNamedQuery("Profesores.findByCodProfe")
                    .setParameter("codProfe", "P001").getSingleResult();
        } catch (NoResultException ex) {
            System.err.println("\tEl profesor no existe\n");
        }

        //Creamos una nueva tutoría con el id 0022 y añadimos sus datos
        Tutorias tutoria = new Tutorias("0022");
        tutoria.setCurso("Nivel básico");
        tutoria.setDiaSemana("Lunes");
        tutoria.setHoraTutoria(hora(13, 15));
        tutoria.setProfesor(profesor);

        try {
            //Comenzamos unas transacción con la base de datos
            entityManager.getTransaction().begin();
            //Añadimos la tutoría
            entityManager.persist(tutoria);
            //Finalizamos la transacción
            entityManager.getTransaction().commit();
        } catch (EntityExistsException ex) {
            System.err.println("\tYa existe una tutoría con el mismo identificador\n");
        }
    }

    /**
     * <strong>Método que borra la tutoría con id nº20</strong>
     */
    static void borrarTutoria() {
        System.out.println("\n\nBorramos la tutoría con el id nº 0020\n\n");
        Tutorias tutoria = null;
        //Obtenemos el profesor cuyo codigo es P001
        try {
            tutoria = (Tutorias) entityManager.createNamedQuery("Tutorias.findByIdTutoria")
                    .setParameter("idTutoria", "0020").getSingleResult();
            //Comenzamos unas transacción con la base de datos
            entityManager.getTransaction().begin();
            //Borramos la tutoría
            entityManager.remove(tutoria);
            //Finalizamos la transacción
            entityManager.getTransaction().commit();
        } catch (NoResultException ex) {
            System.err.println("\tLa tutoria no existe\n");
        }
    }

    /**
     * <strong>Método que nos devuelve una hora en formato Date</strong>
     *
     * @param h hora
     * @param m minuto
     * @return
     */
    static Date hora(int h, int m) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, h, m, 0);
        return calendar.getTime();
    }

    /**
     * <strong>Método que resuelve el punta número 4 de la tarea</strong><br>
     * Obtener un listado sobre las tablas profesores y tutorías que visualice
     * codProfe, nombre, apellido, departamento, disSemana y horaTutoria.
     */
    static void mostrarProfesorTutoria() {
        entityManager.clear();
        //Actualizo la lista de tutorías
        listTutorias = entityManager.createNamedQuery("Tutorias.findAll").getResultList();
        //Defino un patrón de formato para la salida de datos por consola, como un bloque de texto
        patron = """
                    %-10s%-15s%-15s%-15s%-15s%-10s
                    --------------------------------------------------------------------------------""";
        //Muestro la cabecera de los datos utilizando el patrón creado
        System.out.println(String.format(patron,
                "CÓDIGO",
                "NOMBRE",
                "APELLIDO",
                "DEPARTAMENTO",
                "DÍA",
                "HORA"
        ));
        //Recorro la lista de tutorías mientras voy obteniendo y mostrando sus datos por pantalla
        for (Tutorias tutorias : listTutorias) {
            //Utilizo el patrón creado y añado sus respectivos datos
            resultado = String.format(patron,
                    tutorias.getProfesor().getCodProfe(),
                    tutorias.getProfesor().getNombre(),
                    tutorias.getProfesor().getApellido(),
                    tutorias.getProfesor().getDepartamento(),
                    tutorias.getDiaSemana(),
                    tutorias.getHoraTutoria()
            );
            //Muestro la información por pantalla
            System.out.println(resultado);
        }
    }
}
