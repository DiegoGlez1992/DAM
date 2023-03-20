/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gonzalezgarciadiego_ad03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * <strong>Clase para gestionar las bases de datos.</strong>
 *
 * @author Diego González García
 */
public class BaseDatos {

    // Parámetros para la conexión con la BBDD
    private final String driver, url, usuario, password;
    private Connection conection = null;
    private ResultSet resultSet = null;
    //private Statement statement;

    /**
     * <strong>Método constructor por defecto</strong><br>
     * Construye el objeto de base de datos con los parámetros por defecto.
     */
    public BaseDatos() {
        this.driver = "com.mysql.cj.jdbc.Driver";
        this.url = "jdbc:mysql://localhost:3306/test";
        this.usuario = "root";
        this.password = "";
    }

    /**
     * <strong>Método constructor parametrizado.</strong><br>
     * Construye el objeto de base de datos con los parámetros indicados.
     *
     * @param driver Nombre del driver
     * @param url Dirección de la base de datos
     * @param usuario Usuario de la base de datos
     * @param password Contraseña del usuario de la base de datos
     */
    public BaseDatos(String driver, String url, String usuario, String password) {
        this.driver = driver;
        this.url = url;
        this.usuario = usuario;
        this.password = password;
    }

    /**
     * <strong>Método que comprueba la conexión con la base de datos.</strong>
     */
    public void testConnection() {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error en la conexión: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
    }

    /**
     * <strong>Método que crea la base de datos.</strong>
     *
     * @param name Nombre de la base de datos
     */
    public void createDataBase(String name) {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            statement.executeUpdate("DROP DATABASE " + name);
            statement.executeUpdate("CREATE DATABASE " + name);
            statement.executeUpdate("USE " + name);
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error en la creacción de la base de datos: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
    }

    /**
     * <strong>Método que crea las tablas de la base de datos.</strong>
     */
    public void createTables() {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            statement.executeUpdate("CREATE TABLE USUARIOS(P_USUARIO INT(8) PRIMARY KEY, NOMBRE VARCHAR(10), CONTRASENA VARCHAR(8))ENGINE=InnoDB");
            statement.executeUpdate("CREATE TABLE CARRITOS(P_CARRITO INT(8) PRIMARY KEY, A_USUARIO INT(8), FECHA DATE, FOREIGN KEY (A_USUARIO) REFERENCES USUARIOS(P_USUARIO))ENGINE=InnoDB");
            statement.executeUpdate("CREATE TABLE SECCIONES(P_SECCION INT(8) PRIMARY KEY, NOMBRE VARCHAR(20))ENGINE=InnoDB");
            statement.executeUpdate("CREATE TABLE PRODUCTOS(P_PRODUCTO INT(8) PRIMARY KEY, A_SECCION INT(8), DESCRIPCION VARCHAR(20), PVP FLOAT(6,2), STOCK INT(4), FOREIGN KEY (A_SECCION) REFERENCES secciones(p_SECCION))ENGINE=InnoDB");
            statement.executeUpdate("CREATE TABLE CARR_PRO(P_CARR_PRO INT(8) PRIMARY KEY, A_CARRITO INT(8), A_PRODUCTO INT(8), CANTIDAD INT(4), FOREIGN KEY (A_CARRITO) REFERENCES CARRITOS(P_CARRITO), FOREIGN KEY (A_PRODUCTO) REFERENCES PRODUCTOS(P_PRODUCTO))ENGINE=InnoDB");
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error en la creacción de tablas: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
    }

    /**
     * <strong>Método que añade los valores a las tablas.</strong>
     */
    public void addValors() {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            //Valores de la tabla usuarios
            statement.executeUpdate("INSERT INTO USUARIOS VALUES (11,'USUARIO1','SECRETO')");
            statement.executeUpdate("INSERT INTO USUARIOS VALUES (22,'USUARIO2','OCULTO')");
            statement.executeUpdate("INSERT INTO USUARIOS VALUES (33,'USUARIO3','CUIDADO')");

            //Valores de la tabla carritos
            statement.executeUpdate("INSERT INTO CARRITOS VALUES (11,11,'2013/01/01')");
            statement.executeUpdate("INSERT INTO CARRITOS VALUES (22,11,'2013/01/01')");
            statement.executeUpdate("INSERT INTO CARRITOS VALUES (33,22,'2013/02/02')");
            statement.executeUpdate("INSERT INTO CARRITOS VALUES (44,22,'2013/02/03')");
            statement.executeUpdate("INSERT INTO CARRITOS VALUES (55,33,'2013/02/04')");
            statement.executeUpdate("INSERT INTO CARRITOS VALUES (66,33,'2013/01/05')");

            //Valores de la tabla secciones
            statement.executeUpdate("INSERT INTO SECCIONES VALUES (11,'SECCION1')");
            statement.executeUpdate("INSERT INTO SECCIONES VALUES (22,'SECCION2')");
            statement.executeUpdate("INSERT INTO SECCIONES VALUES (33,'SECCION3')");

            //Valores de la tabla productos
            statement.executeUpdate("INSERT INTO PRODUCTOS VALUES (11,11,'S1P1',11,20)");
            statement.executeUpdate("INSERT INTO PRODUCTOS VALUES (22,11,'S1P2',12,13)");
            statement.executeUpdate("INSERT INTO PRODUCTOS VALUES (33,22,'S2P1',21,23)");
            statement.executeUpdate("INSERT INTO PRODUCTOS VALUES (44,22,'S2P2',22,71)");
            statement.executeUpdate("INSERT INTO PRODUCTOS VALUES (55,33,'S3P1',31,23)");
            statement.executeUpdate("INSERT INTO PRODUCTOS VALUES (66,33,'S3P2',32,12)");

            //Valores de la tabla carr_pro
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (11,11,11,6)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (22,11,33,4)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (33,22,22,5)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (44,22,44,4)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (55,33,11,3)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (66,33,44,7)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (77,44,55,4)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (88,44,66,3)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (99,55,22,5)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (100,55,55,6)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (111,66,66,8)");
            statement.executeUpdate("INSERT INTO CARR_PRO VALUES (122,66,44,3)");
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al añadir valores: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
    }

    /**
     * <strong>Método para revolver la consulta 1.</strong><br>
     * Nombre de sección, descripción de producto, pvp, stock de los productos
     * para productos cuyo pvp>11.
     */
    void consulta1() {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            resultSet = statement.executeQuery("select NOMBRE, DESCRIPCION, PVP, STOCK "
                    + "                         from PRODUCTOS inner join SECCIONES on PRODUCTOS.A_SECCION = SECCIONES.P_SECCION "
                    + "                         where PVP > 11");
            System.out.println("\nCONSULTA 1:");
            while (resultSet.next()) {
                System.out.println("Nombre: " + resultSet.getString(1) + "\tDescripción: " + resultSet.getString(2) + "\tPVP: " + resultSet.getString(3) + "\tStock: " + resultSet.getString(4));
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar la consulta1: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
    }

    /**
     * <strong>Método para revolver la consulta 2.</strong><br>
     * Nombre de sección y precio medio de sus productos, teniendo en cuenta
     * únicamente aquellos productos cuyo pvp>11.
     */
    void consulta2() {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            resultSet = statement.executeQuery("select NOMBRE, cast(avg(PVP) as dec(10,2))"
                    + "                         from PRODUCTOS inner join SECCIONES on PRODUCTOS.A_SECCION = SECCIONES.P_SECCION"
                    + "                         where PVP > 11"
                    + "                         group by A_SECCION"
            );
            System.out.println("\nCONSULTA 2:");
            while (resultSet.next()) {
                System.out.println("Nombre: " + resultSet.getString(1) + "\tPVP: " + resultSet.getString(2));
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar la consulta2: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
    }

    /**
     * <strong>Método para revolver la consulta 3.</strong><br>
     * Nombre de sección y precio medio de sus productos, teniendo en cuenta
     * únicamente aquellos productos cuyo pvp>11 y cuya media de pvp>21.
     */
    void consulta3() {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            resultSet = statement.executeQuery("select NOMBRE, cast(avg(PVP) as dec(10,2))"
                    + "                         from PRODUCTOS inner join SECCIONES on PRODUCTOS.A_SECCION = SECCIONES.P_SECCION"
                    + "                         where PVP > 11 "
                    + "                         group by A_SECCION"
                    + "                         having avg(PVP) > 21"
            );
            System.out.println("\nCONSULTA 3:");
            while (resultSet.next()) {
                System.out.println("Nombre: " + resultSet.getString(1) + "\tPVP: " + resultSet.getString(2));
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar la consulta3: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
    }

    /**
     * <strong>Método para revolver la consulta 4.</strong><br>
     * Nombre de la sección con el mayor precio medio.
     */
    void consulta4() {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            resultSet = statement.executeQuery("select NOMBRE"
                    + "                         from PRODUCTOS inner join SECCIONES on PRODUCTOS.A_SECCION = SECCIONES.P_SECCION"
                    + "                         group by A_SECCION"
                    + "                         having avg(PVP) = (SELECT max(AVG_PRODUCTOS.AVG_PVP) "
                    + "                                           from (select avg(PVP) as AVG_PVP"
                    + "                                                 from PRODUCTOS"
                    + "                                                 group by A_Seccion) AVG_PRODUCTOS)"
            );
            System.out.println("\nCONSULTA 4:");
            while (resultSet.next()) {
                System.out.println("Nombre: " + resultSet.getString(1));
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar la consulta4: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
    }

    /**
     * <strong>Método para revolver la consulta 5.</strong><br>
     * El programa nos solicita que le demos el nombre de la sección de la que
     * queremos los datos y el programa nos devuelve los datos por consola. Si
     * el nombre de sección no existe nos lo comunica mediante mensaje y nos
     * siguie pidiendo nombres.<br>
     * El programa termina cuando tecleemos retorno vacío (campos de compra
     * vacíos; o un botón de finalizar. Cualquiera de las dos opciones).
     */
    void consulta5() {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            Scanner teclado = new Scanner(System.in);  //Se crea un objeto scaner para leer el teclado
            String entradaTeclado = ""; //String para guardar los datos del teclado
            ArrayList<String> aux = new ArrayList<>();  //Array auxiliar para guardar las claves de producto
            PreparedStatement preparedStatement;

            System.out.println("\nCONSULTA 5:");

            //Comprobar si existe la sección
            while (true) {
                System.out.print("Indique el nombre de la sección que desea consultar: ");
                entradaTeclado = teclado.nextLine();    //Leemos los datos del teclado
                preparedStatement = conection.prepareStatement("select P_SECCION from SECCIONES where NOMBRE = ?");
                preparedStatement.setString(1, entradaTeclado); // Incluir valores
                resultSet = preparedStatement.executeQuery();   // Ejecutar sentencia
                if (resultSet.next()) { //Si hay datos, existe la sección y pasamos a realizar la consulta
                    break;
                }
                System.err.println("\tLa sección indicada no existe!");
            }

            //Mostrar productos
            preparedStatement = conection.prepareStatement("select P.DESCRIPCION, P.PVP, P.STOCK"
                    + "                                     from PRODUCTOS P inner join SECCIONES S on P.A_SECCION = S.P_SECCION"
                    + "                                     where S.NOMBRE = ?");
            preparedStatement.setString(1, entradaTeclado); // Incluir valores
            resultSet = preparedStatement.executeQuery();   // Ejecutar sentencia
            System.out.println("\n\tProductos de la sección " + entradaTeclado + ": ");
            while (resultSet.next()) {  //Mostramos los resultados
                System.out.println("\tDescripción: " + resultSet.getString(1) + "\tPVP: " + resultSet.getString(2) + "\tStock: " + resultSet.getString(3));
                aux.add(resultSet.getString(1));    //Guarda la clave de producto
            }

            //Mostrar información adicional
            for (String clave : aux) {
                preparedStatement = conection.prepareStatement("select CP.CANTIDAD, C.FECHA, U.NOMBRE"
                        + "                                     from PRODUCTOS P inner join CARR_PRO CP on P.P_PRODUCTO = CP.A_PRODUCTO"
                        + "                                                      inner join CARRITOS C on CP.A_CARRITO = C.P_CARRITO"
                        + "                                                      inner join USUARIOS U on C.A_USUARIO = U.P_USUARIO"
                        + "                                     where P.DESCRIPCION = ?");
                preparedStatement.setString(1, clave); // Incluir valores
                resultSet = preparedStatement.executeQuery();   // Ejecutar sentencia
                System.out.println("\n\tInformación adicional sobre el producto " + clave + ": ");
                while (resultSet.next()) {  //Mostramos los resultados
                    System.out.println("\tEl usuario " + resultSet.getString(3) + " compró " + resultSet.getString(1) + " unidades el día " + resultSet.getString(2));
                }
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar la consulta5: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
    }

    /**
     * <strong>Método para comprobar las claves de un usuario.</strong><br>
     * Se consulta la tabla USUARIOS con los datos de usuario y contraseña
     * introducidos.<br>
     * En caso que estos datos coincidan, se devuleve el número del usuario. En
     * caso contrario, se devuelve vacío.
     *
     * @param usuario Nombre del usuario
     * @param contraseña Contraseña del usuario
     * @return Código de usuario
     */
    String checkUsuario(String usuario, String contraseña) {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            PreparedStatement preparedStatement;
            preparedStatement = conection.prepareStatement("select P_USUARIO from USUARIOS where NOMBRE = ? and CONTRASENA = ?");
            preparedStatement.setString(1, usuario); // Incluir valores
            preparedStatement.setString(2, contraseña); // Incluir valores
            resultSet = preparedStatement.executeQuery();   // Ejecutar sentencia
            if (resultSet.next()) { //Si hay datos, existe el usuario y devolvemos su código
                return resultSet.getString(1);
            } else {
                return "";
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar checkUsuario: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
        return null;
    }

    /**
     * <strong>Método para comprobar el código del último carrito.</strong><br>
     * Se consulta la tabla CARRITOS y se devuelve el código del último carrito
     * creado (P_CARRITO más alto).<br>
     * En caso de no encontrar resultados, se entiende que todavía no existe
     * ningún carrito y se devuelve "0".
     *
     * @return Código del último carrito
     */
    String lastCarrito() {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            resultSet = statement.executeQuery("select max(P_CARRITO) from CARRITOS");   // Ejecutar sentencia
            if (resultSet.next()) { //Si hay datos
                return resultSet.getString(1);
            } else {    //Si no hay datos, entendemos que todavía no se creó ningún carrito
                return "0";
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar lastCarrito: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
        return null;
    }

    /**
     * <strong>Método para comprobar un producto.</strong><br>
     * Se consulta la tabla PRODUCTOS con los datos de descripción
     * introducidos.<br>
     * En caso que estos datos coincidan, se devuleve el número de producto, pvp
     * y stock. En caso contrario, se devuelve vacío.
     *
     * @param descripcion Descripción del producto
     * @return (código producto)/(pvp)/(stock)
     */
    String checkProducto(String descripcion) {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            PreparedStatement preparedStatement;
            preparedStatement = conection.prepareStatement("select P_PRODUCTO, PVP, STOCK from PRODUCTOS where DESCRIPCION = ?");
            preparedStatement.setString(1, descripcion); // Incluir valores
            resultSet = preparedStatement.executeQuery();   // Ejecutar sentencia
            if (resultSet.next()) { //Si hay datos, existe el producto y devolvemos su código
                return resultSet.getString(1) + "/" + resultSet.getString(2) + "/" + resultSet.getString(3);
            } else {
                return "";
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar checkProducto: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
        return null;
    }

    /**
     * <strong>Método para comprobar el stock de un producto.</strong><br>
     * Se consulta la tabla PRODUCTOS con lel código de producto
     * introducido.<br>
     * En caso de que el producto exista, se devuleve su stock. En caso
     * contrario, se devuelve vacío.
     *
     * @param producto Código de producto
     * @return Stock disponible
     */
    String checkStockProducto(String producto) {
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            PreparedStatement preparedStatement;
            preparedStatement = conection.prepareStatement("select STOCK from PRODUCTOS where P_PRODUCTO = ?");
            preparedStatement.setString(1, producto); // Incluir valores
            resultSet = preparedStatement.executeQuery();   // Ejecutar sentencia
            if (resultSet.next()) { //Si hay datos, existe el producto y devolvemos su stock
                return resultSet.getString(1);
            } else {
                return "";
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar checkStockProducto: " + ex.toString());
        } finally {
            try {
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }
        return null;
    }

    /**
     * <strong>Método para realizar la transacción de una compra con la
     * BD.</strong><br>
     * Primero se comprueba si el carrito que estamos utilizando ya existe en la
     * BD. En caso contrario, se crea.<br>
     * A continuación, se comprueba el último código de CARR_PRO y se crea uno
     * nuevo lara esta compra sumandole 2 al.<br>
     * Después, añadimos el registro de la compra a la tabla CARR_PRO.<br>
     * Por último, actualizamos el stock disponible del producto en la
     * BD.<br><br>
     * Todo el proceso se realiza mediante una transacción. En caso de un fallo
     * durante el proceso, se realiza un rollback.
     *
     * @param carrito Código de carrito
     * @param usuario Código de usuario
     * @param fecha Fecha
     * @param producto Producto sobre el que realizar la compra
     * @param stock Stock disponible antes de realizar la comra
     * @param cantidad Unidades del producto que se compran
     */
    void compra(String carrito, String usuario, String fecha, String producto, String stock, String cantidad) {
        String carPro;  //Variable para añadir productos al carrito
        try {
            Class.forName(this.driver);  //Carga el driver
            conection = DriverManager.getConnection(this.url, this.usuario, this.password);   //Realiza la conexión
            conection.setAutoCommit(false); //Desactiva el autocommit para que las consultas estén en la misma transacción
            Statement statement = conection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); //Se crea un Statement para realizar la consulta y se declara que pueda actualizarse
            PreparedStatement preparedStatement;

            //Comprobar si ya existe el carrito
            preparedStatement = conection.prepareStatement("select P_CARRITO from CARRITOS where P_CARRITO = ?");
            preparedStatement.setString(1, carrito); // Incluir valores
            resultSet = preparedStatement.executeQuery();   // Ejecutar sentencia
            if (!resultSet.next()) { //Si no hay datos, el carrito no existe y por lo tanto lo creamos
                preparedStatement = conection.prepareStatement("insert into CARRITOS values (?, ?, ?)");
                preparedStatement.setString(1, carrito); // Incluir valores
                preparedStatement.setString(2, usuario); // Incluir valores
                preparedStatement.setString(3, fecha); // Incluir valores
                preparedStatement.executeUpdate();  // Ejecutar sentencia
            }

            //Consultar última clave de CARR_PRO
            resultSet = statement.executeQuery("select max(P_CARR_PRO) from CARR_PRO");   // Ejecutar sentencia
            if (resultSet.next()) { //Si hay datos, creamos una nuevo código de CARR_PRO sumando 2 al anterior
                carPro = String.valueOf(Integer.parseInt(resultSet.getString(1)) + 2);
            } else {    //Si no hay datos, creamos el primer CARR_PRO con valor de 2
                carPro = "2";
            }

            //Añadimos el registro de la compra en CARR_PRO
            preparedStatement = conection.prepareStatement("insert into CARR_PRO values (?, ?, ?, ?)");
            preparedStatement.setString(1, carPro); // Incluir valores
            preparedStatement.setString(2, carrito); // Incluir valores
            preparedStatement.setString(3, producto); // Incluir valores
            preparedStatement.setString(4, cantidad); // Incluir valores
            preparedStatement.executeUpdate();  // Ejecutar sentencia

            //Modificamos el stock actual del producto
            preparedStatement = conection.prepareStatement("update PRODUCTOS set STOCK = ? where P_PRODUCTO = ?");
            preparedStatement.setString(1, String.valueOf((Integer.parseInt(stock) - Integer.parseInt(cantidad)))); // Incluir valores
            preparedStatement.setString(2, producto); // Incluir valores
            preparedStatement.executeUpdate();  // Ejecutar sentencia

            //Finaliza la transacción
            conection.commit();
        } catch (ClassNotFoundException ex) {
            System.err.println("Error en el driver: " + ex.toString());
        } catch (SQLException ex) {
            System.err.println("Error al realizar compra: " + ex.toString());
            if (conection != null) {
                try {
                    System.err.println("Transaction is being rolled back.");
                    conection.rollback();
                } catch (SQLException ex2) {
                    System.err.println("Error en el rollback: " + ex2.toString());
                }
            }
        } finally {
            try {
                conection.setAutoCommit(true);  //Activa de nuevo el autocommit
                conection.close();  //Cierra la conexión
            } catch (SQLException ex) {
                System.err.println("Error en el cierre de conexión: " + ex.toString());
            }
        }

    }
}
