/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PROG05_Ejerc1_util;

import java.time.LocalDate;

/**
 * Clase validación para validar todos los datos necesarios para el correcto 
 * funcionamiento del programa de gestión de vehículos.
 *
 * @author diego
 * @version 1.0
 */
public class Validacion {
    //Tabla con las letras de validación para el calculo del DNI
    private static final String LETRAS_DNI= "TRWAGMYFPDXBNJZSQVHLCKE";
    
    /*
    Método que calcula la letra del NIF.
    */
    private static char calcularLetraNIF (int dni) {
        char letra;
        letra = LETRAS_DNI.charAt(dni % 23); 
        return letra;
    }
    
    /*
    Método extrae la letra del NIF
    */
    private static char extraerLetraNIF (String nif) {
        char letra=   nif.charAt(nif.length()-1);
        return letra;
    }

    /*
    Método extrae el número del NIF
    */
    private static int extraerNumeroNIF (String nif) {
        int numero= Integer.parseInt(nif.substring(0, nif.length()-1));
        return numero;
    }
    
    /**
     * Método que comprueba que el dni indicado es correcto
     * 
     * @param nif Número de NIF a comprobar
     * @return <code>true</code>/<code>false</code>
     * @exception Exception El número del DNI no es correcto
     */
    public static boolean validarNIF (String nif) throws Exception {
        boolean valido;   // Suponemos el NIF válido mientras no se encuentre algún fallo
        char letra_calculada;
        char letra_leida;
        int  dni_leido;
        if (nif == null) {  // El parámetro debe ser un objeto no vacío
            valido= false;
            throw new Exception ("NIF " + nif + " inválido.");
        }
        else if (nif.length()<8 || nif.length()>9) {    // La cadena debe estar entre 8(7+1) y 9(8+1) caracteres
            valido= false;
            throw new Exception ("NIF " + nif + " inválido.");
        }
        else {
            letra_leida= extraerLetraNIF (nif);    // Extraemos la letra de NIF (letra)
            dni_leido= extraerNumeroNIF (nif);  // Extraemos el número de DNI (int)
            letra_calculada= calcularLetraNIF(dni_leido);  // Calculamos la letra de NIF a partir del número extraído
            if (letra_leida == letra_calculada) {   // Comparamos la letra extraída con la calculada
                // Todas las comprobaciones han resultado válidas. El NIF es válido.
                valido= true;
            }
            else { 
                valido= false;
                throw new Exception ("NIF " + nif + " inválido.");
            }
        }
        return valido;
    }
    
    /**
     * Método que comprueba que la fecha de matriculación sea válida
     * 
     * @param fechaMatriculacion Fecha de matriculación indicada
     * @return <code>true</code>/<code>false</code>
     */
    public static boolean fechaMatriculacionValida(LocalDate fechaMatriculacion) {
        return (fechaMatriculacion.getYear()) < LocalDate.now().getYear() ||
                (fechaMatriculacion.getYear() == LocalDate.now().getYear() && fechaMatriculacion.getMonthValue() < LocalDate.now().getMonthValue()) ||
                (fechaMatriculacion.getYear() == LocalDate.now().getYear() && fechaMatriculacion.getMonthValue() == LocalDate.now().getMonthValue() && fechaMatriculacion.getDayOfMonth() <= LocalDate.now().getDayOfMonth());
    }
    
    /**
     * Método que comprueba si el número de kilometros es válido al ser superior al anterior
     * 
     * @param numKilometros Número de kilómetros nuevos
     * @param numKilometrosReal Número de kilómetros actuales en el sistema
     * @return <code>true</code>/<code>false</code>
     */
    public static boolean numKilometrosValida(long numKilometros, long numKilometrosReal) {
        return (numKilometros > numKilometrosReal);
    }
}
