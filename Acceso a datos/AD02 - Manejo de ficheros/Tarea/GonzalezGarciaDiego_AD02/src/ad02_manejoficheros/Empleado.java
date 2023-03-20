/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ad02_manejoficheros;

/**
 * Objeto Empleado con sus atributos
 *
 * @author Diego González García
 */
public class Empleado {

    private int numero_dpto;
    private String nombre;
    private String localidad;

    /**
     * Método constructor
     * 
     * @param numero_dpto Número de departamento
     * @param nombre Nombre del empleado
     * @param localidad Localidad del empleado
     */
    public Empleado(int numero_dpto, String nombre, String localidad) {
        this.numero_dpto = numero_dpto;
        this.nombre = nombre;
        this.localidad = localidad;
    }

    public int getNumero_dpto() {
        return numero_dpto;
    }

    public void setNumero_dpto(int numero_dpto) {
        this.numero_dpto = numero_dpto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

}
