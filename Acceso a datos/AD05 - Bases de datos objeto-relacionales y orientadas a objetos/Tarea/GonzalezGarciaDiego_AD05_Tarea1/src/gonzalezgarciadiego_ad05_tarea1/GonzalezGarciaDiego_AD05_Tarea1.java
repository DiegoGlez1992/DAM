/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gonzalezgarciadiego_ad05_tarea1;

/**
 * <strong>Clase para el método main.</strong>
 *
 * @author Diego González García
 */
public class GonzalezGarciaDiego_AD05_Tarea1 {

    static BaseDatos_db4o baseDatos_db4o = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            baseDatos_db4o = new BaseDatos_db4o();
            baseDatos_db4o.almacenarInformacion();

            System.out.println("1. Visualizar los departamentos que tengan más de 20 asignaturas.");
            baseDatos_db4o.departamentosMas20Asignaturas();
            System.out.println("-------------------------------------------------------------------------------------------------------\n");
            System.out.println("2. Modificar el número de asignaturas de informática incrementando en dos asignaturas más.");
            baseDatos_db4o.modificarAsignaturasInformatica();
            System.out.println("-------------------------------------------------------------------------------------------------------\n");
            System.out.println("3. Borrar los departamentos que tengan más de 4 aulas asignadas.");
            baseDatos_db4o.borrarDepartamentosMas4Aulas();
            System.out.println("-------------------------------------------------------------------------------------------------------\n");
            System.out.println("4. Visualizar todos los departamentos que quedan, incluidos sus docentes, que no han sido borrados anteriormente.");
            baseDatos_db4o.mostrarDepartamentos();
            System.out.println("-------------------------------------------------------------------------------------------------------\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
