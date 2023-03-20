/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gonzalezgarciadiego_ad07_tarea;

import Alumno.MatriculaBean;

/**
 *
 * @author Diego González García
 */
public class AccedeBD implements MatriculaBean.interfaceListener {

    MatriculaBean matriculas;

    AccedeBD() {
        matriculas = new MatriculaBean();
        matriculas.addInterfaceListener((MatriculaBean.interfaceListener) this);
    }

    /**
     * Método que lista todas las matrículas de la base de datos
     */
    public void listadoMatriculas() {
        matriculas.recargarDNI("");
        for (int i = 0; i < matriculas.size(); i++) {
            matriculas.seleccionarFila(i);
            System.out.println("Matricula " + i);
            System.out.println("\tDNI:" + matriculas.getDni());
            System.out.println("\tNombre modulo: " + matriculas.getNombreModulo());
            System.out.println("\tCurso: " + matriculas.getCurso());
            System.out.println("\tNota: " + matriculas.getNota());
        }
    }

    /**
     * Método que lista las matrículas de un DNI especificado
     */
    public void listadoMatriculasDNI() {
        matriculas.recargarDNI("12345678A");
        for (int i = 0; i < matriculas.size(); i++) {
            matriculas.seleccionarFila(i);
            System.out.println("Matricula " + i);
            System.out.println("\tDNI:" + matriculas.getDni());
            System.out.println("\tNombre modulo: " + matriculas.getNombreModulo());
            System.out.println("\tCurso: " + matriculas.getCurso());
            System.out.println("\tNota: " + matriculas.getNota());
        }
    }

    /**
     * Método que añade una matrícula nueva
     */
    void anadeMatricula() {
        matriculas.setDni("98765432A");
        matriculas.setNombreModulo("DI");
        matriculas.setCurso("22-23");
        matriculas.setNota(8);
        System.out.println("Matricula nueva: ");
        System.out.println("\tDNI:" + matriculas.getDni());
        System.out.println("\tNombre modulo: " + matriculas.getNombreModulo());
        System.out.println("\tCurso: " + matriculas.getCurso());
        System.out.println("\tNota: " + matriculas.getNota());
        matriculas.addMatricula();
    }

    @Override
    public void capturarBDModificada(MatriculaBean.bdModificadaEvent bdme) {
        System.out.println("\n\t-----Se ha añadido una matricula a la base de datos-----\n");
    }

    @Override
    public void capturarRecarga(MatriculaBean.recargaEvent re) {
        System.out.println("\n\t-----Se han recargado todas las matriculas-----\n");
    }

    @Override
    public void capturarRecargaDNI(MatriculaBean.recargaDniEvent rde) {
        System.out.println("\n\t-----Se han recargado las matriculas de un dni-----\n");
    }
}
