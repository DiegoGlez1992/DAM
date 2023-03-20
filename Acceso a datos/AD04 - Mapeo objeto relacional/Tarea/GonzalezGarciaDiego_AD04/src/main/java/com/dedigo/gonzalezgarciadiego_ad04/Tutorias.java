/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dedigo.gonzalezgarciadiego_ad04;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego González García
 */
@Entity
@Table(name = "tutorias", catalog = "idiomas", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tutorias.findAll", query = "SELECT t FROM Tutorias t"),
    @NamedQuery(name = "Tutorias.findByIdTutoria", query = "SELECT t FROM Tutorias t WHERE t.idTutoria = :idTutoria"),
    @NamedQuery(name = "Tutorias.findByCurso", query = "SELECT t FROM Tutorias t WHERE t.curso = :curso"),
    @NamedQuery(name = "Tutorias.findByDiaSemana", query = "SELECT t FROM Tutorias t WHERE t.diaSemana = :diaSemana"),
    @NamedQuery(name = "Tutorias.findByHoraTutoria", query = "SELECT t FROM Tutorias t WHERE t.horaTutoria = :horaTutoria")})
public class Tutorias implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idTutoria")
    private String idTutoria;
    @Column(name = "curso")
    private String curso;
    @Column(name = "diaSemana")
    private String diaSemana;
    @Column(name = "horaTutoria")
    @Temporal(TemporalType.TIME)
    private Date horaTutoria;
    @JoinColumn(name = "profesor", referencedColumnName = "codProfe")
    @ManyToOne
    private Profesores profesor;

    public Tutorias() {
    }

    public Tutorias(String idTutoria) {
        this.idTutoria = idTutoria;
    }

    public String getIdTutoria() {
        return idTutoria;
    }

    public void setIdTutoria(String idTutoria) {
        this.idTutoria = idTutoria;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Date getHoraTutoria() {
        return horaTutoria;
    }

    public void setHoraTutoria(Date horaTutoria) {
        this.horaTutoria = horaTutoria;
    }

    public Profesores getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesores profesor) {
        this.profesor = profesor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTutoria != null ? idTutoria.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tutorias)) {
            return false;
        }
        Tutorias other = (Tutorias) object;
        if ((this.idTutoria == null && other.idTutoria != null) || (this.idTutoria != null && !this.idTutoria.equals(other.idTutoria))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dedigo.gonzalezgarciadiego_ad04.Tutorias[ idTutoria=" + idTutoria + " ]";
    }
    
}
