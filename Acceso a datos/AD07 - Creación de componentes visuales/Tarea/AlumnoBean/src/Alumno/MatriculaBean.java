/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Alumno;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase del componente MatriculaBean
 *
 * @author Diego González García
 */
public class MatriculaBean {

    private PropertyChangeSupport propertySupport;

    /**
     * Método constructor por defecto del componente
     */
    public MatriculaBean() {
        propertySupport = new PropertyChangeSupport(this);
    }

    private String dni;

    /**
     * Get the value of dni
     *
     * @return the value of dni
     */
    public String getDni() {
        return dni;
    }

    /**
     * Set the value of dni
     *
     * @param dni new value of dni
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    private String nombreModulo;

    /**
     * Get the value of nombreModulo
     *
     * @return the value of nombreModulo
     */
    public String getNombreModulo() {
        return nombreModulo;
    }

    /**
     * Set the value of nombreModulo
     *
     * @param nombreModulo new value of nombreModulo
     */
    public void setNombreModulo(String nombreModulo) {
        this.nombreModulo = nombreModulo;
    }

    private String curso;

    /**
     * Get the value of curso
     *
     * @return the value of curso
     */
    public String getCurso() {
        return curso;
    }

    /**
     * Set the value of curso
     *
     * @param curso new value of curso
     */
    public void setCurso(String curso) {
        this.curso = curso;
    }

    private double nota;

    /**
     * Get the value of nota
     *
     * @return the value of nota
     */
    public double getNota() {
        return nota;
    }

    /**
     * Set the value of nota
     *
     * @param nota new value of nota
     */
    public void setNota(double nota) {
        this.nota = nota;
    }

    /**
     * Clase Matricula
     */
    private class Matricula {

        private final String dni;
        private final String nombreModulo;
        private final String curso;
        private final double nota;

        /**
         * Método constructor de matrículas por defecto
         */
        public Matricula() {
            this.dni = "";
            this.nombreModulo = "";
            this.curso = "";
            this.nota = -1;
        }

        /**
         * Método constructor de matrículas parametrizado
         *
         * @param nDNI Número de dni
         * @param nNombreModulo Nombre del módulo
         * @param nCurso Curso
         * @param nNota Nota final
         */
        public Matricula(String nDNI, String nNombreModulo, String nCurso, double nNota) {
            this.dni = nDNI;
            this.nombreModulo = nNombreModulo;
            this.curso = nCurso;
            this.nota = nNota;
        }
    }

    /**
     * Arraylist auxiliar para cargar la información de la base de datos.
     */
    private ArrayList<Matricula> matriculas = new ArrayList<>();

    /**
     * Método que devuelve el tamaño de la lista de matrículas.
     *
     * @return Tamaño de la lista
     */
    public int size() {
        return matriculas.size();
    }

    /**
     * Método que recarga en las propiedades del componente la matrícula que se
     * encuentra en la fila indicada del vector.
     *
     * @param i Número de fila
     */
    public void seleccionarFila(int i) {
        Matricula matricula;

        if (i < matriculas.size()) {   //Comprueba que existe esa fila y carga sus datos
            matricula = matriculas.get(i);
            this.dni = matricula.dni;
            this.nombreModulo = matricula.nombreModulo;
            this.curso = matricula.curso;
            this.nota = matricula.nota;
        }
    }

    /**
     * Método que recarga la estructura del vector con las matriculas referentes
     * al dni indicado.<br>
     * Si no se indica ninguna matrícula, se cargan todas las que existen en la
     * base de datos.
     *
     * @param nDni Número de dni a consultar
     */
    public void recargarDNI(String nDni) {
        Connection con;
        PreparedStatement pstmt;
        ResultSet rs;
        matriculas.clear(); //Borra el arraylist
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alumnos", "root", "");
            if (nDni.isBlank() || nDni.isEmpty()) { //Si no se indica un número de dni
                receptor.capturarRecarga(new recargaEvent(this));  //Genera un evento
                pstmt = con.prepareStatement("select * from matriculas");
            } else {    //Si se indica un número de dni
                receptor.capturarRecargaDNI(new recargaDniEvent(this));   //Genera un evento
                pstmt = con.prepareStatement("select * from matriculas where dni = ?");
                pstmt.setString(1, nDni);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) { //Copia los datos de cada matricula
                Matricula matricula = new MatriculaBean.Matricula(
                        rs.getString("DNI"),
                        rs.getString("NombreModulo"),
                        rs.getString("Curso"),
                        rs.getDouble("Nota"));
                matriculas.add(matricula);  //Añade la matrícula al arraylist
            }
            rs.close();
            pstmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(MatriculaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Método que añade una matricula a la base de datos con la información
     * almacenada en las propiedades del componente.
     */
    public void addMatricula() {
        Connection con;
        PreparedStatement pstmt;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alumnos", "root", "");///////////////////////////////////////
            pstmt = con.prepareStatement("insert into matriculas values (?,?,?,?)");
            pstmt.setString(1, this.dni);
            pstmt.setString(2, this.nombreModulo);
            pstmt.setString(3, this.curso);
            pstmt.setDouble(4, this.nota);
            pstmt.executeUpdate();  //Añade la matrícula a la base de datos
            receptor.capturarBDModificada(new bdModificadaEvent(this)); //Genera un evento
        } catch (SQLException ex) {
            Logger.getLogger(MatriculaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private interfaceListener receptor;

    /**
     * Evento de modificación de la base de datos
     */
    public class bdModificadaEvent extends java.util.EventObject {

        public bdModificadaEvent(Object source) {
            super(source);
        }
    }

    /**
     * Evento de listado de matriculas completo
     */
    public class recargaEvent extends java.util.EventObject {

        public recargaEvent(Object source) {
            super(source);
        }
    }

    /**
     * Evento de listado de matriculas de un DNI
     */
    public class recargaDniEvent extends java.util.EventObject {

        public recargaDniEvent(Object source) {
            super(source);
        }
    }

    /**
     * Define las interface para los eventos
     */
    public interface interfaceListener extends EventListener {

        public void capturarBDModificada(bdModificadaEvent ev);

        public void capturarRecarga(recargaEvent ev);

        public void capturarRecargaDNI(recargaDniEvent ev);

    }

    /**
     * Añade un listener
     *
     * @param receptor
     */
    public void addInterfaceListener(interfaceListener receptor) {
        this.receptor = receptor;
    }

    /**
     * Elimina un listener
     *
     * @param receptor
     */
    public void removeInterfaceListener(interfaceListener receptor) {
        this.receptor = null;
    }

    /**
     * *****************************************************
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
}
