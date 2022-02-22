/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;


import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.junit.runner.RunWith;
import tienda.Tienda;


/**
 *
 * @author angela
 */
@RunWith(Parameterized.class)
public class TiendaTestStock {
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
	/*
	 * Introducir en la línea siguiente los valores de prueba de entrada y
	 *  la salida esperada en forma de matriz de 2 dimensiones. 
	 *  IMPORTANTE: Los valores que están no son correctos y debes modificarlos
	 */
            {-3234, 224}, {2210, 42}, {3434, -2} , {23289,-9}
        });
    }
    
    private final Tienda miTienda;
    private final int cantPedida;
    private final int expResult;
 
      public TiendaTestStock(int cantPedida, int expResult) {

        this.miTienda = new Tienda(1000.0, 20.0, 10.0);
        this.cantPedida = cantPedida;
        this.expResult = expResult;
    }
     @Test
    public void testStock() {
        System.out.println("pedir stock");

        int result = miTienda.pedirStock(cantPedida);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
