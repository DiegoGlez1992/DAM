/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prog02_ejerc10;

/**
 * Diseña un programa Java, denominado PROG02_Ejerc9, que realice las siguientes
 * operaciones, en el orden que se muestran. Se indica la variable y el tipo de 
 * dato que recibe el valor o resultado de la operación indicada:
 * float    x       4.5
 * float    y       3.0
 * int      i       2
 * int      j       i * x
 * double   dx      2.0
 * double   dz      dx * y
 * byte     bx      5
 * byte     by      2
 * byte     bz      bx - by
 * byte     bx      -128
 * byte     by      1
 * byte     bz      bx – by (resultado tipo byte)
 * byte     bz      bx – by (resultado tipo int)
 * short    sx      5
 * short    sy      2
 * short    sz      sx - sy
 * short    sx      32767
 * short    sy      1
 * short    sz      sx + sy
 * char     cx      \u000F
 * char     cy      \u0001
 * int      z       cx – cy
 * int      z       cx – 1
 * char     cx      \uFFFF
 * int      z       cx
 * short    sx      cx
 * short    sx      -32768
 * char     cx      sx
 * int      z       sx
 * short    sx      -1
 * char     cx      sx
 * int      z       cx
 *
 * @author diego
 */
public class PROG02_Ejerc10 
{
    public static void main(String[] args) 
    {
        float   x;
        float   y;
        int     i;
        int     j;
        double  dx;
        double  dz;
        byte    bx;
        byte    by;
        byte    bz;
        short   sx;
        short   sy;
        short   sz;
        char    cx;
        char    cy;
        int     z;
        System.out.print("----- Conversiones entre enteros y coma flotante -----");
        x = 4.5f;
        y = 3.0f;
        i = 2;
        j = (int) (i*x);    //Conversión explícita
        System.out.printf("\nProducto de int por float: j = i * x = %d", j);
        dx = 2.0d;
        dz = dx*y;
        System.out.printf("\nProducto de float por double: dz = dx * y = %.1f", dz);
        
        System.out.print("\n\n----- Operaciones con byte -----");
        bx = 5;
        by = 2;
        bz = (byte)(bx-by); //Conversión explícita. Las operaciones entre byte dan como resultado int
        System.out.printf("\nbyte: %d - %d = %d", bx, by, bz);
        bx = -128;
        by = 1;
        bz = (byte)(bx-by); //Conversión explícita. Las operaciones entre byte dan como resultado int
        System.out.printf("\nbyte: %d - %d = %d", bx, by, bz);
        System.out.printf("\n(int)(%d - %d) = %d", bx, by, (int)(bx-by));
        
        System.out.print("\n\n----- Operaciones con short -----");
        sx = 5;
        sy = 2;
        sz = (short)(sx-sy);    //Conversión explícita
        System.out.printf("\nshort: %d - %d = %d", sx, sy, sz);
        sx = 32767;
        sy = 1;
        sz = (short)(sx+sy);    //Conversión explícita
        System.out.printf("\nshort: %d + %d = %d", sx, sy, sz);
        
        System.out.print("\n\n----- Operaciones con char -----");
        cx = '\u000F';
        cy = '\u0001';
        z = (cx-cy);
        System.out.printf("\nchar: %c - %c = %d", cx, cy, z);
        z = ((char)cx-1);   //Conversión explícita
        System.out.printf("\nchar: %c - 1 = %d", cx, z);
        cx = '\uFFFF';
        z = cx;
        System.out.printf("\n(int) = %d", z);
        sx = (short)cx; //Conversión explícita
        System.out.printf("\n(short) = %d", sx);
        sx = -32768;
        cx = (char)sx;  //Conversión explícita
        z=cx;
        System.out.printf("\n-32768 short-char-int = %d", z);
        sx = -1;
        cx = (char)sx;  //Conversión explícita
        z = cx;
        System.out.printf("\n-1 short-char-int = %d", z);
    }
    
}
