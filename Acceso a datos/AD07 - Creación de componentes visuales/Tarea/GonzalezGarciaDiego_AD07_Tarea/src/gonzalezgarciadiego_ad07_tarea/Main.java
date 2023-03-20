/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package gonzalezgarciadiego_ad07_tarea;

/**
 *
 * @author Diego González García
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AccedeBD gestion = new AccedeBD();
        
        gestion.anadeMatricula();       //Añade una matrícula
        gestion.listadoMatriculas();    //Lista todas las matriculas
        gestion.listadoMatriculasDNI(); //Lista las matrículas de un DNI
    }
}
