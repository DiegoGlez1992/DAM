package com.prog03Ejerc1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 
 */

enum enumMes {enero, febrero, marzo, abril, mayo, junio, julio, agosto, septiembre, noviembre, diciembre};

public class Fecha {
    //propiedad de clase
    
    int dia;
    enumMes mes;
    int anio;
    
    //Constructores 
    
    Fecha (enumMes mes){  //Inicializa al valor recibido el mes y por defecto dia y anio a 0
        this.mes=mes;
        dia=0;
        anio=0;
    }
    
    Fecha (int dia, enumMes mes, int anio){  // Inicializa todos los parámetros.
        this.dia=dia;
        this.mes=mes;
        this.anio=anio;
    }

    //Generación automática de métodos getters y setters
    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public enumMes getMes() {
        return mes;
    }

    public void setMes(enumMes mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }
   
    //Devuelve true si la fecha se corresponde con el período de verano.
    public boolean isSummer(){  
        return ((dia>20 && mes==enumMes.junio) || mes==enumMes.julio || mes==enumMes.agosto || (dia<22 && mes==enumMes.septiembre));
    }
 
    //Devuelve un String con la fecha en formato largo.
    public String toString (){
        return ("La fecha es: " + dia + " de " + mes + " del " + anio);
    }
    
}
