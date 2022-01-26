/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog05.ejerc1;

import java.time.LocalDate;
import java.time.Period;

/**
 *
 * @author
 */
public class Vehiculo {

    String marca;
    String matricula;
    int num_kms;
    LocalDate fecha_mat;
    String descripcion;
    int precio;
    String propietario;
    String dni_propietario;

    public Vehiculo(String marca, String matricula, int num_kms, LocalDate fecha_mat, String descripcion, int precio, String propietario, String dni_propietario) {
        this.marca = marca;
        this.matricula = matricula;
        this.num_kms = num_kms;
        this.fecha_mat = fecha_mat;
        this.descripcion = descripcion;
        this.precio = precio;
        this.propietario = propietario;
        this.dni_propietario = dni_propietario;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public int getNum_kms() {
        return num_kms;
    }

    public void setNum_kms(int num_kms) {
        this.num_kms = num_kms;
    }

    public LocalDate getFecha_mat() {
        return fecha_mat;
    }

    public void setFecha_mat(LocalDate fecha_mat) {
        this.fecha_mat = fecha_mat;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getDni_propietario() {
        return dni_propietario;
    }

    public void setDni_propietario(String dni_propietario) {
        this.dni_propietario = dni_propietario;
    }

    public int get_Anios() {
        LocalDate hoy = LocalDate.now();
        return  (Period.between(this.fecha_mat, hoy).getYears());
    }
    
    public void act_kms (int nuevos_kms){
        this.num_kms=this.num_kms+nuevos_kms;
    }

}
