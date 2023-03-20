/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gonzalezgarciadiego_ad05_tarea1;

/**
 * <strong>Clase para el objeto Departamento.</strong>
 *
 * @author Diego González García
 */
public class Departamento {

    /**
     * Nombre del departamento
     */
    private String nombreDepartamento;
    /**
     * Número de aulas pertenecientes al departamento
     */
    private Integer numeroDeAulas;
    /**
     * Número de módulos impartidos por el departamento
     */
    private Integer numeroDeModulos;
    /**
     * DOcente asociado al departamento
     */
    private Docente docente;

    /**
     * <strong>Método constructor parametrizado</strong><br>
     *
     * @param nombreDepartamento Nombre del departamento
     * @param numeroDeAulas Número de aulas
     * @param numeroDeModulos Número de módulos
     * @param docente Docente
     */
    public Departamento(String nombreDepartamento, Integer numeroDeAulas, Integer numeroDeModulos, Docente docente) {
        this.nombreDepartamento = nombreDepartamento;
        this.numeroDeAulas = numeroDeAulas;
        this.numeroDeModulos = numeroDeModulos;
        this.docente = docente;
    }

    /**
     * <strong>Método getter para el nombre del departamento</strong><br>
     *
     * @return
     */
    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    /**
     * <strong>Método setter para el nombre del departamento</strong><br>
     *
     * @param nombreDepartamento
     */
    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }

    /**
     * <strong>Método getter para número de aulas</strong><br>
     *
     * @return
     */
    public int getNumeroDeAulas() {
        return numeroDeAulas;
    }

    /**
     * <strong>Método setter para número de aulas</strong><br>
     *
     * @param numeroDeAulas
     */
    public void setNumeroDeAulas(int numeroDeAulas) {
        this.numeroDeAulas = numeroDeAulas;
    }

    /**
     * <strong>Método getter para número de módulos</strong><br>
     *
     * @return
     */
    public int getNumeroDeModulos() {
        return numeroDeModulos;
    }

    /**
     * <strong>Método setter para número de módulos</strong><br>
     *
     * @param numeroDeModulos
     */
    public void setNumeroDeModulos(int numeroDeModulos) {
        this.numeroDeModulos = numeroDeModulos;
    }

    /**
     * <strong>Método getter para docente</strong><br>
     *
     * @return
     */
    public Docente getDocente() {
        return docente;
    }

    /**
     * <strong>Método setter para docente</strong><br>
     *
     * @param docente
     */
    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    /**
     * <strong>Sobre escritura del método toString()</strong><br>
     *
     * @return
     */
    @Override
    public String toString() {
        String info = ("Nombre departamento: " + nombreDepartamento + " Nº aulas: " + numeroDeAulas + " Nº módulos: " + numeroDeModulos);
        if (this.getDocente() != null) {
            info += " (Docente - " + this.getDocente() + ")";
        }
        return info;
    }

}
