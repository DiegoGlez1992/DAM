/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tienda;

/**
 *
 * @author prof entornos desarrollo 
 * La clase tienda se dedica unicamente a la venta de camisetas Tiene el método 
 * vender donde se indica el número de camisetas vendidas y el método pedirStock
 * que solicita más stock de camisetas a los proveedores cuando el stock de 
 * producto sea menor a 3
 *
 * Tenemos también el método getBeneficio que indica cuánto ganamos con la venta
 * de cada camiseta. El beneficio no puede ser menor que 0. Este método no se va
 * a utilizar en esta prueba práctica.
 *
 * Se comienza con un saldo inicial de 1000€ para poder realizar compras y
 * ventas No podemos dejar la caja con menos de 100€ ni tener saldos negativos
 * en la caja No podemos vender camisetas si no las tenemos en stock No podemos
 * pedir un número negativo de camisetas No podemos vender un número negativo de
 * camisetas
 */
public class Tienda {
   // Propiedades de la Clase Tienda
    /**
     * indica el número de camisetas que me quedan
     */
    private int stockCamisetas;

    /**
     * indica el precio de cada camiseta (todas el mismo)
     */
    private double precioCamiseta;

    /**
     * indica el dinero acumulado en caja
     */
    private double dineroEnCaja;

    /**
     * indica el precio que le pago al proveedor por cada camiseta
     */
    private double precioProveedor;

    /**
     * Constructor con parámetros
     * @param caja  dineroEnCaja
     * @param precioVenta   precioCamiseta
     * @param precioCompra  precioProveedor
     */
    public Tienda(double caja, double precioVenta, double precioCompra) {
        this.dineroEnCaja = caja;
        this.precioCamiseta = precioVenta;
        this.precioProveedor = precioCompra;
        this.stockCamisetas = 0;
    }

    public int getStockCamisetas() {
        return stockCamisetas;
    }

    public void setStockCamisetas(int stockCamisetas) {
        this.stockCamisetas = stockCamisetas;
    }

    public double getPrecioCamiseta() {
        return precioCamiseta;
    }

    public void setPrecioCamiseta(double precioCamiseta) {
        this.precioCamiseta = precioCamiseta;
    }

    public double getDineroEnCaja() {
        return dineroEnCaja;
    }

    public void setDineroEnCaja(double dineroEnCaja) {
        this.dineroEnCaja = dineroEnCaja;
    }

    public double getPrecioProveedor() {
        return precioProveedor;
    }

    public void setPrecioProveedor(double precioProveedor) {
        this.precioProveedor = precioProveedor;
    }



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Tienda miTienda = new Tienda(1000.0, 20.0, 10.0);
        int camisetasPedidas = 10;
        int camisetasVendidas = 5;
        System.out.println("Dinero en caja inicial: " + miTienda.getDineroEnCaja() + " euros");
        // Depuracion. Provoca parada por ingreso con cantidad menor de 0
        System.out.println("-------Primera operación pedir stock negativo de camisetas -------");
        miTienda.pedirStock(-3);
        System.out.println("nNúmero inicial de camisetas en stock " + miTienda.stockCamisetas + " camisetas");

        System.out.println("-------segunda operación pedir stock de camisetas -------");
        miTienda.pedirStock(camisetasPedidas);
        System.out.println("Saldo tras comprar stock de " + camisetasPedidas + " camisetas: " + miTienda.dineroEnCaja + " euros");

        System.out.println("-------tercera operación vender camisetas -------");
        miTienda.vender(camisetasVendidas);
        System.out.println("Dinero tras la venta de " + camisetasVendidas + " camisetas: " + miTienda.dineroEnCaja + " euros");
        System.out.println("Número de camisetas en stock: " + miTienda.stockCamisetas + " camisetas");

        System.out.println("-------cuarta operación pedir stock camisetas pero hay suficiente stock -------");
        miTienda.pedirStock(2 * camisetasPedidas);
        System.out.println("Dinero tras pedir " + 2 * camisetasPedidas + " camisetas: " + miTienda.dineroEnCaja + " euros");
        System.out.println("Número de camisetas en stock: " + miTienda.stockCamisetas + " camisetas");

        System.out.println("-------quinta operación vender camisetas se intenta vender un numero de camisetas de las que no se dispone -------");
        miTienda.vender(2 * camisetasVendidas);
        System.out.println("Saldo tras vender" + 2 * camisetasVendidas + ": " + miTienda.dineroEnCaja + " euros");
        System.out.println("Número de camisetas en stock: " + miTienda.stockCamisetas + " camisetas");
    }

 
    /**
     * Indica el número de camisetas vendidas. No se puede vender un número
     * negativo de camisetas ni más camisetas de las disponibles en stock
     *
     * @param numCamisetas indica el número de camisetas que se venden en esa
     * transacción
     * @return iCodErr código de error que indica porqué ha terminado el
     * programa 0 indica que no ha habido error
     */
    public int vender(int numCamisetas) {

        int iCodErr;
        if (numCamisetas < 0) {

            iCodErr = 1;
            System.out.println("No se puede vender un número negativo de camisetas iCodErr: " + iCodErr);

        } else if (numCamisetas > this.stockCamisetas) {

            iCodErr = 2;
            System.out.println("No se pueden vender más camisetas de las que hay en stock. venta:" + numCamisetas + "stock: " + this.stockCamisetas + " iCodErr2:" + iCodErr);

        } else {
            this.stockCamisetas = this.stockCamisetas - numCamisetas;
            this.dineroEnCaja = this.dineroEnCaja + numCamisetas * this.precioCamiseta;

            iCodErr = 0;
            System.out.println("****Camisetas vendidas: " + numCamisetas + " iCodErr: " + iCodErr);
        }
        return iCodErr;
    }

    /**
     * método pedirStock que solicita más stock de camisetas a los proveedores
     * cuando el stock de producto sea menor a 3 No podemos pedir un número
     * negativo de camisetas No podemos dejar la caja con menos de 100€ ni tener
     * saldos negativos en la caja
     *
     * @param num indica el número de camisetas que se compran en esa
     * transacción
     * @return iCodErr2 código de error que indica porqué ha terminado el
     * programa 0 indica que no ha habido error
     *
     */
    public int pedirStock(int num) {

        int iCodErr2;
        if (num > 0) { //comprueba que pide un numero positivo de camisetas
            if (this.stockCamisetas > 3) { //comprueba que hay menos de tres camisetas en stock
                iCodErr2 = 1;
                System.out.println("No se pueden pedir camisetas porque hay suficiente stock. Stock disponible: " + this.stockCamisetas + "iCodErr2=" + iCodErr2);
            } else { //tengo menos de tres camisetas en stock
                if (this.dineroEnCaja >= num * this.precioProveedor) { //comprueba que tengo suficiente dinero en caja 

                    iCodErr2 = 2;
                    System.out.println("iCodErr2: " + iCodErr2);

                    if (this.dineroEnCaja - 100 <= num * this.precioProveedor) { //comprueba que despues de la compra quedan al menos 100€ en caja

                        iCodErr2 = 3;
                        System.out.println("No se pueden pedir tantas camisetas porque no hay bastante margen de dinero \n en caja. Dinero en caja:" + this.dineroEnCaja + " camisetas que se desea comprar: " + num + "iCodErr2=" + iCodErr2);
                        System.out.println("iCodErr2: " + iCodErr2);
                    }
                } //   else 
                else {
                    System.out.println("***** compro camisetas: " + num + "\n dinero en caja:" + this.dineroEnCaja + " camisetas que se desea comprar: " + num);

                    this.stockCamisetas = this.stockCamisetas + num;
                    this.dineroEnCaja = this.dineroEnCaja - num * this.precioProveedor;
                    //System.out.println("***********dinero en caja despues de la compra:"+this.dineroEnCaja);

                    iCodErr2 = 0;
                    System.out.println("iCodErr2: " + iCodErr2);
                }

            }
        } else {

            iCodErr2 = 4;
            System.out.println("Error : No se puede pedir un número negativo de camisetas iCodErr2=" + iCodErr2);
        }
        return iCodErr2;
    }

    public double beneficio() {
        return this.precioCamiseta - this.precioProveedor;
    }

    
}
