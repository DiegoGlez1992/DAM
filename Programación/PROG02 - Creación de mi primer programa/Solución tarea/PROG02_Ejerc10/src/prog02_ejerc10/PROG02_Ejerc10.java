/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog02_ejerc10;

/**
 *
 * @author
 */
public class PROG02_Ejerc10 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        // al inicializar las variables debemos indicar que son float
        // ya que si no el compilador da error porque los literales
        // en coma flotante son double por defecto
        float x = 4.5f;
        float y = (float) 3.0;

        System.out.println("------- Conversiones entre enteros y coma flotante -------");
        int i = 2;
        int j = (int) (i * x);
        System.out.println(" Producto de int por float: j= i*x = " + j);

        double dx = 2.0;
        double dz = dx * y;
        System.out.println(" Producto de float por double: dz=dx * y = " + dz);

        System.out.println("\n------- Operaciones con byte -------");
        byte bx = 5;    // literal int convertido sin cast a byte
        // si el valor está dentro de los permitidos para byte -128 to 127
        byte by = 2;

        // Aunque usemos operandos byte, el cálculo en la JVM se hace en int
        // debemos hacer un cast a byte de nuevo
        byte bz = (byte) (bx - by);
        System.out.println(" byte: 5 - 2 = " + bz);

        bx = -128;
        by = 1;
        bz = (byte) (bx - by);
        System.out.println(" byte -128 - 1 = " + bz);

        j = (int) (bx - by);
        System.out.println(" (int)(-128 - 1) = " + j);

        System.out.println("\n------- Operaciones con short -------");
        short sx = 5; // literal int convertido sin cast a
        // byte si está dentro del rango de short
        short sy = 2;

        // Aunque usemos operandos short, el cálculo en la JVM se hace en int
        // debemos hacer de nuevo el casting a short
        short sz = (short) (sx - sy);
        System.out.println(" short: 10 - 1 = " + sz);

        sx = 32767;
        sy = 1;
        sz = (short) (sx + sy);
        System.out.println(" short 32767 + 1 = " + sz);

        System.out.println("\n------- Operaciones con char -------");

        /*char cx = '\u007D';// el código Unicode en hexadecimal
    char cy = '\u0063';*/
        char cx = '\u000F';// el código Unicode en hexadecimal
        char cy = '\u0001';

        // Podemos hacer operaciones con tipos char
        // porque lo que realmente se almacena es el codigo Unicode
        int z = cx - cy; // literales char 'convertidos' a int sin cast
        System.out.println(" char: \u000F - \u0001 = " + z);

        z = cx - 1;
        System.out.println(" char(0x000F) - 1 = " + z);

        cx = '\uFFFF';
        z = cx;    // literal char convertido a int sin cast
        System.out.println(" (int)(\uFFFF) = " + z);

        sx = (short) cx;
        System.out.println(" (short)(\uFFFF) = " + sx);

        sx = -32768;    // literal short
        cx = (char) sx;  // convertido a char
        z = cx;         // convertido a int
        System.out.println(" -32768 short-char-int = " + z);

        sx = -1;        // literal short
        cx = (char) sx;  // convertido a char
        z = cx;         // convertido a int
        System.out.println(" -1 short-char-int = " + z);
    }

}
