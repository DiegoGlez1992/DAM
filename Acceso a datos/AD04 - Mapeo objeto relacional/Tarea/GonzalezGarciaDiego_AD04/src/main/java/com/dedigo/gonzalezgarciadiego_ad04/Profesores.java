/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dedigo.gonzalezgarciadiego_ad04;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego González García
 */
@Entity
@Table(name = "profesores", catalog = "idiomas", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Profesores.findAll", query = "SELECT p FROM Profesores p"),
    @NamedQuery(name = "Profesores.findByCodProfe", query = "SELECT p FROM Profesores p WHERE p.codProfe = :codProfe"),
    @NamedQuery(name = "Profesores.findByNombre", query = "SELECT p FROM Profesores p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Profesores.findByApellido", query = "SELECT p FROM Profesores p WHERE p.apellido = :apellido"),
    @NamedQuery(name = "Profesores.findByDepartamento", query = "SELECT p FROM Profesores p WHERE p.departamento = :departamento"),
    @NamedQuery(name = "Profesores.findBySueldomes", query = "SELECT p FROM Profesores p WHERE p.sueldomes = :sueldomes"),
    @NamedQuery(name = "Profesores.findByCargo", query = "SELECT p FROM Profesores p WHERE p.cargo = :cargo")})
public class Profesores implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codProfe")
    private String codProfe;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "apellido")
    private String apellido;
    @Column(name = "departamento")
    private String departamento;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "sueldomes")
    private BigDecimal sueldomes;
    @Column(name = "cargo")
    private String cargo;
    @OneToMany(mappedBy = "profesor")
    private List<Tutorias> tutoriasList;

    public Profesores() {
    }

    public Profesores(String codProfe) {
        this.codProfe = codProfe;
    }

    public String getCodProfe() {
        return codProfe;
    }

    public void setCodProfe(String codProfe) {
        this.codProfe = codProfe;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public BigDecimal getSueldomes() {
        return sueldomes;
    }

    public void setSueldomes(BigDecimal sueldomes) {
        this.sueldomes = sueldomes;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @XmlTransient
    public List<Tutorias> getTutoriasList() {
        return tutoriasList;
    }

    public void setTutoriasList(List<Tutorias> tutoriasList) {
        this.tutoriasList = tutoriasList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codProfe != null ? codProfe.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Profesores)) {
            return false;
        }
        Profesores other = (Profesores) object;
        if ((this.codProfe == null && other.codProfe != null) || (this.codProfe != null && !this.codProfe.equals(other.codProfe))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dedigo.gonzalezgarciadiego_ad04.Profesores[ codProfe=" + codProfe + " ]";
    }
    
}
