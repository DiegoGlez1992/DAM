/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*Construye un nuevo proyecto Java denominado Prog03_Ejerc1. En el proyecto debe
aparecer un paquete, que no puede ser el paquete por defecto, ponle el nombre 
que creas oportuno. Dentro de dicho paquete:*/
package fecha_calendario;

/**
 *
 * @author diego
 */

/*Declara en el fichero de la clase un tipo enumerado, denominado enumMes, para 
los meses del año.*/
enum EnumMes {ENERO, FEBRERO, MARZO, ABRIL, MAYO, JUNIO, JULIO, AGOSTO, SEPTIEMBRE, OCTUBRE, NOVIEMBRE, DICIEMBRE };

/*Crea una clase denominada Fecha. Esta clase no debe contener método main.*/
public class Fecha {
    
    /*La clase debe contener un atributo para el día, otro para mes (del tipo 
    enumerado declarado) y un tercero para el año.*/
    private int dia;
    private EnumMes mes;
    private int año;
    
    /*Implementa un constructor que inicialice el mes al valor recibido por 
    parámetro y los demás atributos a 0. Observa su cabecera en el siguiente 
    código: 
    Fecha (enumMes mes){*/
    public Fecha (EnumMes mes){
        this.dia=0;
        this.mes=mes;
        this.año=0;
    }
    
    /*Declara otro constructor que inicialice todos los atributos de la clase. 
    Su cabecera podría ser la siguiente: 
    Fecha (int dia, enumMes mes, int anio){*/
    public Fecha (int dia, EnumMes mes, int año){
        this.dia=dia;
        this.mes=mes;
        this.año=año;
    }
    
    /*Implementa los métodos que permitan acceder y modificar cada uno de los 
    atributos de la clase. Los nombres de dichos métodos serán: getXXX () para 
    obtener el valor del atributo XXX y setXXX (v) para actualizar el atributo 
    XXX con el valor v. Puedes observar la cabecera de algún método en el 
    siguiente código:
    public int getDia() {
    }
    public void setDia(int dia) {
    }*/
    public void setDia(int dia) {
        this.dia = dia;
    }
    
    public int getDia() {
        return dia;
    }
    
    public void setMes(EnumMes mes){
        this.mes = mes;
    }
     
    public EnumMes getMes(){
        return mes;
    }
    
    public void setAño(int año) {
        this.año = año;
    }
    
    public int getAño() {
        return año;
    }
    
    /*Implementa un método que devuelva true si el valor contenido en la fecha 
    es verano y false en caso contrario. Observa su cabecera en el siguiente 
    código:
    public boolean isSummer(){
    No es necesario utilizar estructuras condicionales pues aún no las hemos 
    trabajado. Se puede implementar este método utilizando operadores de 
    comparación.*/
    public boolean isSummer(){
        return ((mes==EnumMes.JUNIO && dia>= 21)||(mes==EnumMes.JULIO)||(mes==EnumMes.AGOSTO)||(mes==EnumMes.SEPTIEMBRE && dia<21));
    }
    
    /*Implementa un método que devuelva una cadena con la fecha en formato 
    largo, por ejemplo, 15 de julio de 2020. Observa su cabecera:
    public String toString (){*/
    @Override
    public String toString(){
        return dia+" de "+mes+" de "+año;
    }
    
    /*Ya tenemos nuestra clase Fecha implementada.*/
}
