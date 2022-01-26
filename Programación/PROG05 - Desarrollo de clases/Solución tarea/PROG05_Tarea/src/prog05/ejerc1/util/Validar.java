/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog05.ejerc1.util;

import java.time.LocalDate;

/**
 *
 * @author 
 */
public class Validar {
    
    public static boolean comparaFecha (LocalDate fecha_mat){
        return fecha_mat.isBefore(LocalDate.now());
    }
    
    public static void validaDNI (String dni) throws Exception{
        DNI.validarNIF(dni);
    }
}
