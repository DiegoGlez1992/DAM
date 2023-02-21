/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg8.productorconsumidor;

/**
 *
 * @author jfranco
 */
public class ProductorConsumidor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Recipiente rec=new Recipiente();
        Productor p1=new Productor(rec,1);
        Consumidor c1=new Consumidor(rec,1);
        p1.start();
        c1.start();
        
    }
}
