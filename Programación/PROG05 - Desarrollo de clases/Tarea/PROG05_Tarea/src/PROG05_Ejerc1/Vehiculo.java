/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PROG05_Ejerc1;

import java.time.LocalDate;

/**
 * Esta clase contiene los atributos y metodos de un vehículo
 *
 * @author diego
 * @version 1.0
 */
public class Vehiculo {
    
    //Variables para la clase
    private String marca, matricula, descripcion, nombrePropietario, dniPropietario;
    private long numKilometros;
    private double precio;
    private LocalDate fechaMatriculacion;

     /**
     * Metodo constructor por defecto
     */
    public Vehiculo() {
        
    }
    
    /**
     * Metodo constructor parametrizado
     * 
     * @param marca Marca del vehículo
     * @param matricula Matricula del vehículo
     * @param descripcion Descripción del vehículo
     * @param nombrePropietario Nombre del propietario del vehículo
     * @param dniPropietario DNI del propietario del vehículo
     * @param numKilometros Número de kilómetros del vehículo
     * @param precio Precio del vehículo
     * @param fechaMatriculacion Fecha de matriculación del vehículo
     */
    public Vehiculo(String marca, String matricula, String descripcion, String nombrePropietario, String dniPropietario, long numKilometros, double precio, LocalDate fechaMatriculacion) {
        this.marca = marca;
        this.matricula = matricula;
        this.descripcion = descripcion;
        this.nombrePropietario = nombrePropietario;
        this.dniPropietario = dniPropietario;
        this.numKilometros = numKilometros;
        this.precio = precio;
        this.fechaMatriculacion = fechaMatriculacion;
    }

    /**
     * Método que devuelve el valor del atributo marca
     * @return Marca
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Método que actualiza el atributo marca
     * @param marca Marca del coche
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Método que devuelve el valor del atributo matrícula
     * @return Matrícula
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Método que actualiza el atributo matrícula
     * @param matricula Matrícula del coche
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Método que devuelve el valor del atributo fecha de matriculación
     * @return Fecha de matriculación
     */
    public LocalDate getFechaMatriculacion() {
        return fechaMatriculacion;
    }

    /**
     * Método que actualiza el atributo fecha de matriculación
     * @param fechaMatriculacion Fecha de matriculación
     */
    public void setFechaMatriculacion(LocalDate fechaMatriculacion) {
        this.fechaMatriculacion = fechaMatriculacion;
    }

    /**
     * Método que devuelve el valor del atributo descripción
     * @return Descripción
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Método que actualiza el atributo descripción
     * @param descripcion Descripción del vehículo
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Método que devuelve el valor del atributo nombre del propietario
     * @return Nombre del propietario
     */
    public String getNombrePropietario() {
        return nombrePropietario;
    }

    /**
     * Método que actualiza el atributo nombre del propietario
     * @param nombrePropietario Nombre del propietario
     */
    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    /**
     * Método que devuelve el valor del atributo DNI del propietario
     * @return DNI del propietario
     */
    public String getDniPropietario() {
        return dniPropietario;
    }

    /**
     * Método que actualiza el atributo DNI del propietario
     * @param dniPropietario DNI del propietario
     */
    public void setDniPropietario(String dniPropietario) {
        this.dniPropietario = dniPropietario;
    }

    /**
     * Método que devuelve el valor del atributo numero de kilómetros
     * @return Número de kilómetros
     */
    public long getNumKilometros() {
        return numKilometros;
    }

    /**
     * Método que actualiza el atributo número de kilómetros
     * @param numKilometros Número de kilómetros
     */
    public void setNumKilometros(long numKilometros) {
        this.numKilometros = numKilometros;
    }

    /**
     * Método que devuelve el valor del atributo precio
     * @return Precio
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Método que actualiza el atributo precio
     * @param precio Precio del vehículo
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    /**
     * Método que devuelve los años del vehículo.
     * 
     * Para este método se ha tenido en cuenta que los años mostrados sean 
     * reales(teniendo en cuenta mes y día) y no únicamente la resta del año actual menos la de matriculación,
     * ya que podría calcular un dato erróneo.
     * @return Número de años del vehículo
     */
    public int get_Anios(){
        int anios;
        anios = LocalDate.now().getYear()-fechaMatriculacion.getYear();
        if (LocalDate.now().getMonthValue() < fechaMatriculacion.getMonthValue())
            anios-=1;
        else if ((LocalDate.now().getMonthValue() == fechaMatriculacion.getMonthValue()) && (LocalDate.now().getDayOfMonth() < fechaMatriculacion.getDayOfMonth()))
            anios-=1;
    return anios;
    }
}
