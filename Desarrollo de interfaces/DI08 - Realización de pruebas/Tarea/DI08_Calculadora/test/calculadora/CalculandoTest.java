/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit3TestClass.java to edit this template
 */
package calculadora;

import static java.lang.Double.NaN;
import junit.framework.TestCase;

/**
 *
 * @author Diego
 */
public class CalculandoTest extends TestCase {
    
    public CalculandoTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of add method, of class Calculando.
     */
    public void testAdd() {
        System.out.println("add");
        double number1 = 5.0;
        double number2 = 0.5;
        Calculando instance = new Calculando();
        double expResult = 5.5;
        double result = instance.add(number1, number2);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of subtract method, of class Calculando.
     */
    public void testSubtract() {
        System.out.println("subtract");
        double number1 = 10.0;
        double number2 = 7.0;
        Calculando instance = new Calculando();
        double expResult = 3.0;
        double result = instance.subtract(number1, number2);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of multiply method, of class Calculando.
     */
    public void testMultiply() {
        System.out.println("multiply");
        double number1 = 4.0;
        double number2 = 2.5;
        Calculando instance = new Calculando();
        double expResult = 10.0;
        double result = instance.multiply(number1, number2);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of divide method, of class Calculando.
     */
    public void testDivide() {
        System.out.println("divide");
        double number1 = 0.0;
        double number2 = 0.0;
        Calculando instance = new Calculando();
        double expResult = NaN;
        double result = instance.divide(number1, number2);
        assertEquals(expResult, result, 0.0);
    }
    
    /**
     * Test de integración
     */
    public void testIntegration() {
        System.out.println("integración");
        Main main = new Main();
        assertEquals("OK", main.testIntegration("OK"), "OK");
    }
    
}
