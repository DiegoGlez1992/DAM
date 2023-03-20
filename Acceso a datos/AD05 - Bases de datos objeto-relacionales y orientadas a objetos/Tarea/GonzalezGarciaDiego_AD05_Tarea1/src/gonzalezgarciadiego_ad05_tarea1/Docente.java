/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gonzalezgarciadiego_ad05_tarea1;

/**
 * <strong>Clase para el objeto Docente.</strong>
 *
 * @author Diego González García
 */
public class Docente {

    /**
     * Nombre del docente
     */
    private String nombre;
    /**
     * Número de módulos que imparte
     */
    private int numeroDeModulos;

    /**
     * <strong>Método constructor parametrizado</strong><br>
     *
     * @param nombre Nombre del docente
     * @param númeroDeModulos Número de módulos
     */
    public Docente(String nombre, int númeroDeModulos) {
        this.nombre = nombre;
        this.numeroDeModulos = númeroDeModulos;
    }

    /**
     * <strong>Método getter para nombre</strong><br>
     *
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * <strong>Método setter para nombre</strong><br>
     *
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * <strong>Método getter para número de módulos</strong><br>
     *
     * @return
     */
    public int getNúmeroDeModulos() {
        return numeroDeModulos;
    }

    /**
     * <strong>Método setter para número de módulos</strong><br>
     *
     * @param numeroDeModulos
     */
    public void setNúmeroDeModulos(int numeroDeModulos) {
        this.numeroDeModulos = numeroDeModulos;
    }

    /**
     * <strong>Sobre escritura del método toString()</strong><br>
     *
     * @return
     */
    @Override
    public String toString() {
        return "Nombre: " + nombre + " Nº módulos: " + numeroDeModulos;
    }

}
