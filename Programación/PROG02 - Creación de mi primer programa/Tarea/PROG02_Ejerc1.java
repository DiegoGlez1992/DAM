/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog02_ejerc1;

/**
 * 1.- Crea un proyecto en Netbeans denominado PROG02_Ejerc1 con una clase clase
 * y método main y declara e inicializa una variable para almacenar cada uno de 
 * los siguientes valores. Trata de utilizar el tipo de datos de mas se ajuste a
 * los datos. Justifica tu elección.
 * 
 * a. Valor máximo no modificable: 5000.
 * b. Si el nuevo empleado tiene carnet de conducir o no.
 * c. Un mes del año en formato numérido y como cadena.
 * d. El nombre y apellidos de una persona.
 * e. Sexo: con dos valores posibles 'V' o 'M'.
 * f. Milisegundos transcurridos desde el 01/01/1970 hasta nuestros días.
 * g. Saldo de una cuenta bancaria.
 * h. Distancia en kms desde la Tierra a Júpiter.
 * 
 * Muestra el valor de cada variable en pantalla de forma que cada valor 
 * aparezca en una línea, teniendo en cuenta que NO puedes utilizar la orden 
 * println.
 * 
 * @author diego
 */
public class PROG02_Ejerc1 
{
    public enum Sexo {V,M}
    public static void main(String[] args) 
    {
        //a. Valor máximo no modificable: 5000.
        final short MAXIMO=5000;
        /**Con "final" declaramos la variable como constante. El tipo de variable 
         * "short" tiene un rango de valores de -32.767 a 32.767 (2 bytes).
         */
        //b. Si el nuevo empleado tiene carnet de conducir o no.
        boolean carnet=true; 
        /**Solo puede contener dos varoles, TRUE o FALSE, por lo que el tipo de 
         * variable es un booleano.
         */
        //c. Un mes del año en formato numérico y como cadena.
        byte[] mes1={1,2,3,4,5,6,7,8,9,10,11,12};
        String[] mes2={"enero","febrero","marzo","abril","mayo","junio","julio","agosto","septiembre","octubre","noviembre","diciembre"};
        /**Meses del año en formato numerico(1-12) es suficiente con un byte.
         * Meses del año en formato cadena lo hacemos con un String:
         */
        //d. El nombre y apellidos de una persona.
        String nombreApellidos="Diego Gonzalez Garcia"; 
        /**Se trata de caracteres y contiene una longitud variable, por lo que 
         * lo adecuado es una variable de tipo String.
         */
        //e. Sexo: con dos valores posibles 'V' o 'M'.
        Sexo genero=Sexo.V;
        /**Variable de tipo enumerado con dos posibles valores (V - M). Está 
         * declarada al principio del programa, antes de declarar el main, ya 
         * que las variables tipo enum no pueden ser locales.
         */
        //f. Milisegundos transcurridos desde el 01/01/1970 hasta nuestros días.
        long milisegundos=((long)51*(long)365*(long)24*(long)60*(long)60*(long)1000)+((long)9*(long)30*(long)24*(long)60*(long)60*(long)1000)+((long)27*(long)24*(long)60*(long)60*(long)1000);
        /** Los milisegundos transcurridos desde el 01/01/1970 son unos 51 años, 9
         * meses(x 30 días) y 27 días, que x 24 horas x 60 min. x 60 seg. x 1000 
         * milisegundos, daría un resultado de 1,634e+12. Este dato entra en el 
         * tipo de rango de las variables long (8 bytes).
         */
        //g. Saldo de una cuenta bancaria.
        float saldoCuenta=(float)11507.21;
        /**Suficiente con el rango de valores que nos permite una variable de tipo 
         * float para representar un valor con 2 decimales.
         */
        //h. Distancia en kms desde la Tierra a Júpiter.
        int distanciaTierraJupiter=660000000;
        /** La distancia entre la tierra y jupiter es de 660 millones de kilometros
         * por lo que necesitamos una variable tipo int (4bytes) para contener este 
         * número.
         */
    
         System.out.print("\n----- EJERCICIO DE VARIABLES -----");
         System.out.print("\nEl valor de la variable maximo es " + MAXIMO);
         System.out.print("\nEl valor de la variable carnet es " + carnet);
         System.out.print("\nEl valor de la variable mes en formato numérico es " + mes1[9]);
         System.out.print("\nEl valor de la variable mes en formato cadena es " + mes2[9]);
         System.out.print("\nEl valor de la variable nombre y apellidos es " + nombreApellidos);
         System.out.print("\nEl valor de la variable sexo es " + genero);
         System.out.print("\nEl valor de la variable milisegundos es " + milisegundos);
         System.out.print("\nEl valor de la variable saldo en cuenta es " + saldoCuenta + " €");
         System.out.print("\nEl valor de la variable distancia es " + distanciaTierraJupiter + " KM");
    }
    
}
