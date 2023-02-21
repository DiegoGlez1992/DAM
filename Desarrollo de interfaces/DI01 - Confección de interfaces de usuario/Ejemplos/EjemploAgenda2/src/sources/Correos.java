/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sources;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Agustin
 */
@Entity
@Table(name = "correos", catalog = "agenda", schema = "")
@NamedQueries({
    @NamedQuery(name = "Correos.findAll", query = "SELECT c FROM Correos c")
    , @NamedQuery(name = "Correos.findByCorreoId", query = "SELECT c FROM Correos c WHERE c.correoId = :correoId")
    , @NamedQuery(name = "Correos.findByCorreo", query = "SELECT c FROM Correos c WHERE c.correo = :correo")})
public class Correos implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CORREO_ID")
    private Integer correoId;
    @Basic(optional = false)
    @Column(name = "CORREO")
    private String correo;
    @JoinColumn(name = "ID_CONTACTO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Contactos_1 idContacto;

    public Correos() {
    }

    public Correos(Integer correoId) {
        this.correoId = correoId;
    }

    public Correos(Integer correoId, String correo) {
        this.correoId = correoId;
        this.correo = correo;
    }

    public Integer getCorreoId() {
        return correoId;
    }

    public void setCorreoId(Integer correoId) {
        Integer oldCorreoId = this.correoId;
        this.correoId = correoId;
        changeSupport.firePropertyChange("correoId", oldCorreoId, correoId);
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        String oldCorreo = this.correo;
        this.correo = correo;
        changeSupport.firePropertyChange("correo", oldCorreo, correo);
    }

    public Contactos_1 getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(Contactos_1 idContacto) {
        Contactos_1 oldIdContacto = this.idContacto;
        this.idContacto = idContacto;
        changeSupport.firePropertyChange("idContacto", oldIdContacto, idContacto);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (correoId != null ? correoId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Correos)) {
            return false;
        }
        Correos other = (Correos) object;
        if ((this.correoId == null && other.correoId != null) || (this.correoId != null && !this.correoId.equals(other.correoId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sources.Correos[ correoId=" + correoId + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
