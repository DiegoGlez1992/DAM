/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog02_ejerc5;

/**
 *
 * @author 
 */
public class PROG02_Ejerc5 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        int segundos = 650000;

        int minutos, resto_segundos, horas, resto_minutos, dias, resto_horas = 0;

        //Calculamos minutos
        minutos = segundos / 60;

        //Calculamos horas
        horas = minutos / 60;

        //Calculamos el resto de minutos tras calcular las horas.
        resto_minutos = minutos % 60;

        //Calculamos los días.
        dias = horas / 24;

        //Calculamos el resto de horas tras culcular los días.
        resto_horas = horas % 24;

        System.out.println(segundos + " segundos contienen:");
        System.out.println(dias + " dias.");
        System.out.println((dias==0?horas:resto_horas)  + " horas."); //Si se ha llegado a un día, mostramos el resto de horas.
        System.out.println(resto_minutos + " minutos.");

    }

}
