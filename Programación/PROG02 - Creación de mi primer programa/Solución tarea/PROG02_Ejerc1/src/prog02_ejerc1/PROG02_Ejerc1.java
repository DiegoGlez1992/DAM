/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog02_ejerc1;

/**
 *
 * @author 
 */
public class PROG02_Ejerc1 {

    /**
     * @param args the command line arguments
     */
    enum sexo {
        M, V
    };

    public static void main(String[] args) {
        // TODO code application logic here

        final int VALOR = 5000;
        boolean tieneCarnet = true;
        String mes = "julio";
        byte mesnum = 2;  //Si el formato para el mes es numérico suficiente con un byte.
        String nomb_apell = "Pepe Pérez";
        sexo sex = sexo.M; //Tambiénp podría declararse como un char
        long miliseg = 2000000000;
        float saldo = 345.50f;
        long distancia = 594000000;

        System.out.print("El valor de la constante es " + VALOR + "\n");
        System.out.print("Si el nuevo empleado tiene carnet de conducir o no " + tieneCarnet + "\n");
        System.out.print("Un mes del año " + mes + "\n");
        System.out.print("El nombre y apellidos de una persona " + nomb_apell + "\n");
        System.out.print("Sexo " + sex + "\n");
        System.out.print("Milisegundos transcurridos desde el 01/01/1970 hasta nuestros días " + miliseg + "\n");
        System.out.print("Saldo de una cuenta bancaria " + saldo + "\n");
        System.out.print("Distancia en kms desde la Tierra a Júpiter. " + distancia + "\n");


    }

}
