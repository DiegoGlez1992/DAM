DROP TABLE TablaComerciales;
DROP TABLE TablaResponsables;
DROP TYPE Comercial;
DROP TYPE ListaZonas;
DROP TYPE Zonas;
DROP TYPE Responsable;

CREATE OR REPLACE TYPE Personal AS OBJECT (
 codigo INTEGER,
 dni VARCHAR2(10),
 nombre VARCHAR2(30),
 apellidos VARCHAR2(30),
 sexo VARCHAR2(1),
 fecha_nac DATE
 ) NOT FINAL;
/
/* Crea, como tipo heredado de Personal, el tipo de objeto Responsable */
CREATE OR REPLACE TYPE Responsable UNDER Personal (
 tipo CHAR,
 antiguedad INTEGER,
 CONSTRUCTOR FUNCTION Responsable(codigo INTEGER, nombre VARCHAR2, apellido1 VARCHAR2, apellido2 VARCHAR2, tipo CHAR) RETURN SELF AS RESULT,
 MEMBER FUNCTION getNombreCompleto RETURN VARCHAR2
 );
/
CREATE OR REPLACE TYPE BODY Responsable AS
     /* Crea un método constructor para el tipo de objetos Responsable, en el que se
    indiquen como parámetros el código, nombre, primer apellido, segundo apellido y
    tipo. Este método debe asignar al atributo apellidos los datos de primer
    apellido y segundo apellido que se han pasado como parámetros, uniéndolos con un
    espacio entre ellos. */
     CONSTRUCTOR FUNCTION Responsable(codigo INTEGER, nombre VARCHAR2, apellido1 VARCHAR2, apellido2 VARCHAR2, tipo CHAR)
     RETURN SELF AS RESULT IS
     BEGIN
         SELF.codigo := codigo;
         SELF.nombre := nombre;
         SELF.apellidos := CONCAT(apellido1,apellido2);
         SELF.tipo := tipo;
         antiguedad := 1;
         RETURN;
     END;

     /* Crea un método getNombreCompleto para el tipo de objetos Responsable que
    permita obtener su nombre completo con el formato apellidos nombre */
     MEMBER FUNCTION getNombreCompleto RETURN VARCHAR2 IS
     BEGIN
     	RETURN (apellidos || ' ' || nombre);
     END getNombreCompleto;
END;


/* Crea un tabla TablaResponsables de objetos Responsable */
CREATE TABLE TablaResponsables OF Responsable;
/* Inserta en dicha tabla dos objetos Responsable */
INSERT INTO TablaResponsables VALUES (Responsable(5, '51083099F', 'ELENA', 'POSTA LLANOS', 'F', '31/03/1975', 'N', 4));
/* El segundo objeto Responsable debes crearlo usando el método constructor que has realizado anteriormente. */
INSERT INTO TablaResponsables VALUES (Responsable(6, 'JAVIER', 'JARAMILLO','HERNANDEZ', 'C'));

/

/* Crea el tipo de objeto Zonas */
CREATE OR REPLACE TYPE Zonas AS OBJECT (
 codigo INTEGER,
 nombre VARCHAR2(20),
 refRespon REF Responsable,
 codigoPostal CHAR(5),
 MAP MEMBER FUNCTION ordenarZonas RETURN VARCHAR2
 );
/
/* Crea un método MAP ordenarZonas para el tipo Zonas. Este método debe retornar el
nombre completo del Responsable al que hace referencia cada zona. Para obtener el
nombre debes utilizar el método getNombreCompleto que se ha creado anteriormente. */
CREATE OR REPLACE TYPE BODY Zonas AS
 MAP MEMBER FUNCTION ordenarZonas RETURN VARCHAR2 IS unResponsable Responsable;
 BEGIN
     SELECT DEREF(refRespon) INTO unResponsable FROM Dual;
     RETURN (unResponsable.getNombreCompleto());
 END ordenarZonas;
 END;
 /
 
 /* Crea una colección VARRAY llamada ListaZonas en la que se puedan almacenar hasta objetos Zonas */
 CREATE OR REPLACE TYPE ListaZonas IS VARRAY(10) OF Zonas;
 /

 /* Crea, como tipo heredado de Personal, el tipo de objeto Comercial */
 CREATE OR REPLACE TYPE Comercial UNDER Personal (
 zonaComercial Zonas
 );
 /
  /* Crea una tabla TablaComerciales de objetos Comercial */
CREATE TABLE TablaComerciales OF Comercial;
/

DECLARE
 zona1 Zonas;
 zona2 Zonas;
 unComercial Comercial;
 listaZonas1 ListaZonas;
 refUnResponsable REF Responsable;
 BEGIN
     /* Guarda en una instancia listaZonas1 de dicha lista, dos Zonas ... */
     select ref(p) into RefUnResponsable FROM TablaResponsables p WHERE codigo = 5;
     zona1 := Zonas(1, 'zona 1', RefUnResponsable, '06834');
     select ref(p) into RefUnResponsable FROM TablaResponsables p WHERE dni = '51083099F';
     zona2 := Zonas(2, 'zona 2', RefUnResponsable, '28003');
     listaZonas1 := ListaZonas(zona1, zona2);
    
     /* Inserta en dicha tabla las siguientes filas ... */
     INSERT INTO TablaComerciales VALUES (Comercial(100, '23401092Z', 'MARCOS','SUAREZ LOPEZ', 'M', '30/3/1990', zona1));
     INSERT INTO TablaComerciales VALUES (Comercial(102, '6932288V', 'ANASTASIA','GOMES PEREZ', 'F', '28/11/1984', listaZonas1(2)));
    
    /* Obtener, de la tabla TablaComerciales, el Comercial que tiene el código 100,asignándoselo a una variable unComercial */
     SELECT VALUE(a) INTO unComercial FROM TablaComerciales a WHERE codigo = 100;
    /* Modifica el código del Comercial guardado en esa variable unComercial asignando el valor 101, y su zona debe ser la segunda que se había creado
    anteriormente. Inserta ese Comercial en la tabla TablaComerciales */
     unComercial.codigo := 101;
     unComercial.zonaComercial := zona2;
    INSERT INTO TablaComerciales VALUES (unComercial);
 END;
 /

 /* Realiza una consulta de la tabla TablaComerciales ordenada por zonaComercial para
comprobar el funcionamiento del método MAP. */
 SELECT * FROM TablaComerciales ORDER BY zonaComercial;