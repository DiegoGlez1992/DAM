/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog02_ejerc8;

/**
 *
 * @author 
 */
public class PROG02_Ejerc8 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        int num_alum_prog = 28;
        int num_alum_ed = 24;
        int num_alum_bbdd = 35;

        //calculamos el total de alumnos
        int total = num_alum_bbdd + num_alum_ed + num_alum_prog;

        //calculamos los porcentajes para mostrarlos por pantalla. La clase String permite limitar el número de decimales a mostrar.
        System.out.println("El " + String.format("%.1f",((float)(num_alum_bbdd * 100) / total)) + " % de los alumnos están matriculados en BBDD");
        System.out.println("El " + String.format("%.1f",((float)(num_alum_ed * 100) / total)) + " % de los alumnos están matriculados en Entornos de desarrollo");
        System.out.println("El " + String.format("%.1f",((float)(num_alum_prog * 100) / total)) + " % de los alumnos están matriculados en programación"
    

);

    }
    
}
