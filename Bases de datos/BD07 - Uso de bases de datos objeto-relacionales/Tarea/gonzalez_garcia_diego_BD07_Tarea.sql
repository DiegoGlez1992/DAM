/* Activar la posibilidad de mostrar datos en pantalla */
SET SERVEROUTPUT ON;

/* Borrar tablas y tipos creados*/
DROP TABLE tablaComerciales; 
DROP TYPE listaZonas; 
DROP TABLE tablaResponsables;
DROP TYPE comercial;
DROP TYPE zonas;
DROP TYPE responsable;
DROP TYPE personal;

/*
Crea el tipo de objetos "Personal" con los siguientes atributos:
    codigo INTEGER,
    dni VARCHAR2(10),
    nombre VARCHAR2(30),
    apellidos VARCHAR2(30),
    sexo VARCHAR2(1),
    fecha_nac DATE
*/
/* Declaración del tipo de objeto Personal */
CREATE TYPE Personal AS OBJECT (
    /* Declaración de los atributos */
    codigo INTEGER,
    dni VARCHAR2(10),
    nombre VARCHAR2(30),
    apellidos VARCHAR2(30),
    sexo VARCHAR2(1),
    fecha_nac DATE
) NOT FINAL;
/

/*
Crea, como tipo heredado de "Personal", el tipo de objeto "Responsable" con los 
siguientes atributos:
    tipo  CHAR ,
    antiguedad INTEGER

Crea un método constructor para el tipo de objetos "Responsable", en el que se 
indiquen como parámetros el código, nombre, primer apellido, segundo apellido y 
tipo.

Crea un método getNombreCompleto para el tipo de objetos "Responsable" que 
permita obtener su nombre completo con el formato apellidos nombre.
*/
/* Declaración del tipo de objeto Responsable */
CREATE TYPE Responsable UNDER Personal (
    /* Declaración de los atributos */
	tipo  CHAR ,
    antiguedad INTEGER,
    
    /*Declaración del método constructor */
    CONSTRUCTOR FUNCTION Responsable(codigo INTEGER, nombre VARCHAR2, apellido1 VARCHAR2, apellido2 VARCHAR2, tipo CHAR)
        RETURN SELF AS RESULT,
    
    /*Declaración del método getNombreCompleto */
    MEMBER FUNCTION getNombreCompleto 
        RETURN VARCHAR2
);
/

/*
Crea el tipo de objeto "Zonas" con los siguientes atributos:
    codigo INTEGER, 
    nombre VARCHAR2(20), 
    refRespon REF Responsable, 
    codigoPostal CHAR(5)
    
Crea un método MAP ordenarZonas para el tipo Zonas. Este método debe retornar el
nombre completo del Responsable al que hace referencia cada zona.
*/
/* Declaración del tipo de objeto Zonas */
CREATE TYPE Zonas AS OBJECT (
    /* Declaración de los atributos */
	codigo INTEGER, 
    nombre VARCHAR2(20), 
    refRespon REF Responsable, 
    codigoPostal CHAR(5),
    
    /*Declaración del método MAP */
    MAP MEMBER FUNCTION OrdenarZonas
        RETURN VARCHAR2
);
/

/*
Crea, como tipo heredado de "Personal", el tipo de objeto "Comercial" con los 
siguientes atributos:
    zonaComercial Zonas
*/
/* Declaración del tipo de objeto Comercial */
CREATE TYPE Comercial UNDER Personal (
    /* Declaración de los atributos */
	zonaComercial Zonas
);
/

/*
Crea un método constructor para el tipo de objetos "Responsable", en el que se 
indiquen como parámetros el código, nombre, primer apellido, segundo apellido y 
tipo. Este método debe asignar al atributo apellidos los datos de primer 
apellido y segundo apellido que se han pasado como parámetros, uniéndolos con un
espacio entre ellos.

Crea un método getNombreCompleto para el tipo de objetos "Responsable" que 
permita obtener su nombre completo con el formato apellidos nombre.
*/
/* Cuerpo del tipo de objeto Responsable */
CREATE TYPE BODY Responsable AS
    
    /* Implementación del método constructor */
	CONSTRUCTOR FUNCTION Responsable(codigo INTEGER, nombre VARCHAR2, apellido1 VARCHAR2, apellido2 VARCHAR2, tipo CHAR) 
    RETURN SELF AS 
    RESULT IS
    BEGIN
        SELF.codigo := codigo;
        SELF.nombre := nombre;
        SELF.apellidos := apellido1 || ' ' || apellido2;    -- := CONCAT(apellido1,apellido2);
        SELF.tipo := tipo;
        RETURN;
    END;
    
    /* Implemetación del método getNombreCompleto */
    MEMBER FUNCTION getNombreCompleto 
    RETURN VARCHAR2 IS
    nombreCompleto VARCHAR2(100);
    BEGIN
        nombreCompleto := SELF.apellidos || ' ' || SELF.nombre;
        RETURN nombreCompleto;
    END;
END;
/

/*
Crea un método MAP ordenarZonas para el tipo Zonas. Este método debe retornar el
nombre completo del Responsable al que hace referencia cada zona. Para obtener 
el nombre debes utilizar el método getNombreCompleto que se ha creado 
anteriormente.
*/
CREATE TYPE BODY Zonas AS 
    /* Implementación del método map */
    MAP MEMBER FUNCTION ordenarZonas RETURN VARCHAR2 IS 
        respon Responsable;
    BEGIN
        SELECT DEREF(refRespon) INTO respon FROM DUAL;
        RETURN respon.getNombreCompleto();
    END;
END;
/

/*
Crea una tabla TablaResponsables de objetos Responsable. 
*/
CREATE TABLE TablaResponsables OF Responsable;
/

/*
Crea una tabla TablaComerciales de objetos Comercial. 
*/
CREATE TABLE TablaComerciales OF Comercial;
/

/*
Crea una colección VARRAY llamada ListaZonas en la que se puedan almacenar hasta
10 objetos Zonas.
*/
CREATE TYPE ListaZonas AS VARRAY (10) OF Zonas;
/

/*
Inserta en la TablaResponsables dos objetos Responsable. 
    codigo:  5
    nombre: ELENA
    apellidos:  POSTA LLANOS
    sexo: F
    dni: 51083099F
    fecha_nac: 31/03/1975
    tipo: N
    antiguedad: 4
    
El segundo objeto "Responsable" debes crearlo usando el método constructor que 
has realizado anteriormente. Debes usar los siguientes datos:
    codigo: 6
    nombre: JAVIER
    apellidos: JARAMILLO HERNANDEZ
    tipo: C    
*/
INSERT INTO TablaResponsables VALUES(Responsable(5, '51083099F', 'ELENA', 'POSTA LLANOS', 'F', '31/03/1975', 'N', 4));
INSERT INTO TablaResponsables VALUES(Responsable(6, 'JAVIER', 'JARAMILLO', 'HERNANDEZ', 'C'));
/

/*
Guarda en una instancia listaZonas1 de dicha lista, dos Zonas:
    codigo: 1
    nombre: zona 1
    refResponsable: Referencia al responsable cuyo codigo es 5
    codigo postal: 06834
    
    codigo: 2
    nombre: zona 2
    refResponsable: Referencia al responsable cuyo DNI es 51083099F.
    codigo postal: 28003
    
Inserta en la tabla TablaComerciales las siguientes filas:
    codigo: 100
    dni: 23401092Z
    nombre: MARCOS
    apellidos: SUAREZ LOPEZ
    sexo: M
    fecha_nac: 30/3/1990
    zonacomercial: objeto creado anteriormente para la zona 1

    codigo: 102
    dni: 6932288V
    nombre: ANASTASIA
    apellidos:  GOMES PEREZ
    sexo: F
    fecha_nac: 28/11/1984
    zonacomercial: objeto que se encuentre en la segunda posición de "listaZonas1" 
            (debe tomarse de la lista)

Obtener, de la tabla TablaComerciales, el Comercial que tiene el código 100, 
asignándoselo a una variable unComercial.

Modifica el código del Comercial guardado en esa variable unComercial asignando 
el valor 101, y su zona debe ser la segunda que se había creado anteriormente. 
Inserta ese Comercial en la tabla TablaComerciales.
*/
DECLARE
    ListaZonas1 ListaZonas;
    RefResp1 REF Responsable;
    RefResp2 REF Responsable;
    
    unComercial Comercial;
BEGIN
    /* Guarda zonas en la instancia listaZonas1 */
    SELECT REF(Resp) INTO RefResp1 FROM TablaResponsables Resp WHERE Resp.codigo=5;
    SELECT REF(Resp) INTO RefResp2 FROM TablaResponsables Resp WHERE Resp.dni='51083099F';
    ListaZonas1 := ListaZonas(Zonas(1, 'zona 1', RefResp1, '06834'), Zonas(2, 'zona 2', RefResp2, '28003'));
    
    /*Inserta datos en la tabla TablaComerciales */
    INSERT INTO TablaComerciales VALUES(Comercial(100, '23401092Z', 'MARCOS', 'SUAREZ LOPEZ', 'M', '30/03/1990', ListaZonas1(1)));
    INSERT INTO TablaComerciales VALUES(Comercial(102, '6932288V', 'ANASTASIA', 'GOMES PEREZ', 'F', '28/11/1984', ListaZonas1(2)));
    
    /* Obtener comercial con código 100 en unComercial */
    SELECT VALUE(c) INTO unComercial FROM TablaComerciales c WHERE c.codigo=100;
    
    /* Modificar unComercial e insertarlo */
    unComercial.codigo := 101;
    unComercial.zonaComercial := ListaZonas1(2);
    INSERT INTO TablaComerciales VALUES(unComercial);
END;
/

/*
Realiza una consulta de la tabla TablaComerciales ordenada por zonaComercial 
para comprobar el funcionamiento del método MAP.  
*/
SELECT * FROM tablaComerciales ORDER BY zonaComercial;
/