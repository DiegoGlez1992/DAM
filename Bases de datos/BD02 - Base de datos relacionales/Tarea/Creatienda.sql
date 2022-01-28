--EJERCICIO 1: 
--Vamos a crear las tablas para una tienda virtual que distribuye 
--productos agrupados en familias en varias tiendas. Realiza un script llamado 
--Creatienda.sql que implemente los ejercicios descritos a continuaci�n. Precede
--cada una de las sentencias SQL de los ejercicios con un comentario que incluya
--el enunciado del ejercicio correspondiente. Recuerda que los comentarios van 
--precedidos del s�mbolo -- al inicio de la l�nea. Con las sentencias DDL de SQL
--crea la tablas especificadas a continuaci�n aplicando las restricciones 
--(constraints) pedidas. Se debe cumplir la integridad referencial.

--TABLA FAMILIA: Contiene las familias a las que pertenecen los productos, como 
--por ejemplo ordenadores, impresoras,etc.
--1.    Nombre Columna: Codfamilia 
--      Descripci�n: C�digo que distingue una familia de otra.
--      Tipo de dato: Num�rico de 3 d�gitos.
--      Restricciones: Clave primaria.
--2.    Nombre Columna: Denofamilia
--      Descripci�n: Denominaci�n de la familia
--      Tipo dato: Alfanum�rico de 50 caracteres
--      Restricciones: No puede haber dos familias con la misma denominaci�n. 
--                     Debe tener contenido.
CREATE TABLE FAMILIA 
(
  CODFAMILIA NUMBER(3) NOT NULL 
, DENOFAMILIA VARCHAR2(50) NOT NULL UNIQUE
, CONSTRAINT FAMILIA_PK PRIMARY KEY 
  (
    CODFAMILIA 
  )
  ENABLE
);

COMMENT ON COLUMN FAMILIA.CODFAMILIA IS 'C�digo que distingue una familia de otra';
COMMENT ON COLUMN FAMILIA.DENOFAMILIA IS 'Denominaci�n de la familia';

--TABLA PRODUCTO: Contendr� informaci�n general sobre los productos que 
--distribuye la empresa a las tiendas.
--1.    Nombre Columna: Codproducto
--      Descripci�n: C�digo que distingue un producto de otro
--      Tipo de dato: Num�rico de 5 d�gitos
--      Restricciones: Clave primaria.
--2.    Nombre Columna: Denoproducto
--      Descripci�n: Denominaci�n del producto
--      Tipo de dato: Alfanum�rico de 20 caracteres
--      Restricciones: Debe tener contenido.
--3.    Nombre Columna: Descripcion
--      Descripci�n: Descripci�n del producto
--      Tipo de dato: Alfanum�rico de 100 caracteres
--      Restricciones: 
--4.    Nombre Columna: PrecioBase
--      Descripci�n: Precio base del producto
--      Tipo de dato: Num�rico de 8 d�gitos dos de ellos decimales
--      Restricciones: Mayor que 0. Debe tener contenido.
--5.    Nombre Columna: PorcReposici�n
--      Descripci�n: Porcentaje de reposici�n aplicado a ese producto. Se 
--                   utilizar� para aplicar a las unidades m�nimas y obtener el 
--                   n�mero total de unidades a reponer cuando el stock est� bajo
--                   m�nimo
--      Tipo de dato: Num�rico de 3 d�gitos
--      Restricciones: Mayor que 0
--6.    Nombre Columna: UnidadesMinimas
--      Descripci�n: Unidades m�nimas recomendables en almacen
--      Tipo de dato: Num�rico de 4 d�gitos
--      Restricciones: Mayor que 0. Debe tener contenido.
--7.    Nombre Columna: Codfamilia
--      Descripci�n: C�digo de la familia a la que pertenece el producto
--      Tipo de dato: Num�rico de 3 d�gitos
--      Restricciones: Clave ajena, referencia a Codfamilia de la tabla FAMILIA.
--                     Debe tener contenido.
CREATE TABLE PRODUCTO 
(
  CODPRODUCTO NUMBER(5) NOT NULL 
, DENOPRODUCTO VARCHAR2(20) NOT NULL 
, DESCRIPCION VARCHAR2(100) 
, PRECIOBASE NUMBER(8,2) NOT NULL CHECK (PRECIOBASE > 0)
, PORCREPOSICION NUMBER(3) CHECK (PORCREPOSICION > 0)
, UNIDADESMINIMAS NUMBER(4) NOT NULL CHECK (UNIDADESMINIMAS > 0)
, CODFAMILIA NUMBER(3) NOT NULL 
, CONSTRAINT PRODUCTO_PK PRIMARY KEY 
  (
    CODPRODUCTO 
  )
  ENABLE 
);

ALTER TABLE PRODUCTO ADD CONSTRAINT PRODUCTO_CODFAMILIA_FK 
FOREIGN KEY (CODFAMILIA)
REFERENCES FAMILIA (CODFAMILIA)
ON DELETE CASCADE ENABLE;

COMMENT ON COLUMN PRODUCTO.CODPRODUCTO IS '	C�digo que distingue un producto de otro';
COMMENT ON COLUMN PRODUCTO.DENOPRODUCTO IS 'Denominaci�n del producto';
COMMENT ON COLUMN PRODUCTO.DESCRIPCION IS '	Descripci�n del producto';
COMMENT ON COLUMN PRODUCTO.PRECIOBASE IS 'Precio base del producto';
COMMENT ON COLUMN PRODUCTO.PORCREPOSICION IS 'Porcentaje de reposici�n aplicado a ese producto. Se utilizar� para aplicar a las unidades m�nimas y obtener el n�mero total de unidades a reponer cuando el stock est� bajo m�nimo';
COMMENT ON COLUMN PRODUCTO.UNIDADESMINIMAS IS 'Unidades m�nimas recomendables en almacen';
COMMENT ON COLUMN PRODUCTO.CODFAMILIA IS 'C�digo de la familia a la que pertenece el producto';

--TABLA TIENDA: Contendr� informaci�n b�sica sobre las tiendas que distribuyen 
--los productos.
--1.    Nombre Columna: Codtienda
--      Descripci�n: C�digo que distingue una tienda de otra.
--      Tipo de dato: Num�rico de 3 d�gitos
--      Restricciones: Clave primaria.
--2.    Nombre Columna: Denotienda
--      Descripci�n: Denominaci�n o nombre de la tienda.
--      Tipo de dato: Alfanum�rico de 20 caracteres
--      Restricciones: Debe tener contenido.
--3.    Nombre Columna: Telefono
--      Descripci�n: Tel�fono de la tienda.
--      Tipo de dato: Alfanum�rico de 11 caracteres
--      Restricciones: 
--4.    Nombre Columna: CodigoPostal
--      Descripci�n: Codigo Postal donde se ubica la tienda.
--      Tipo de dato: Alfanum�rico de 5 caracteres
--      Restricciones: Debe tener contenido.
--5.    Nombre Columna: Provincia
--      Descripci�n: Provincia donde se ubica la tienda.
--      Tipo de dato: Alfanum�rico de 5 caracteres
--      Restricciones: Debe tener contenido.
CREATE TABLE TIENDA 
(
  CODTIENDA NUMBER(3) NOT NULL 
, DENOTIENDA VARCHAR2(20) NOT NULL 
, TELEFONO VARCHAR2(11) 
, CODIGOPOSTAL VARCHAR2(5) NOT NULL 
, PROVINCIA VARCHAR2(5) NOT NULL 
, CONSTRAINT TIENDA_PK PRIMARY KEY 
  (
    CODTIENDA 
  )
  ENABLE 
);

COMMENT ON COLUMN TIENDA.CODTIENDA IS 'C�digo que distingue una tienda de otra.';
COMMENT ON COLUMN TIENDA.DENOTIENDA IS '	Denominaci�n o nombre de la tienda.';
COMMENT ON COLUMN TIENDA.TELEFONO IS '	Tel�fono de la tienda.';
COMMENT ON COLUMN TIENDA.CODIGOPOSTAL IS 'Codigo Postal donde se ubica la tienda.';
COMMENT ON COLUMN TIENDA.PROVINCIA IS 'Provincia donde se ubica la tienda';

--TABLA STOCK: Contendr� para cada tienda el n�mero de unidades disponibles de 
--cada producto. La clave primaria est� formada por la concatenaci�n de los 
--campos Codtienda y Codproducto.
--1.    Nombre Columna: Codtienda
--      Descripci�n: C�digo de la tienda.
--      Tipo de dato: Num�rico de 3 d�gitos
--      Restricciones: Clave primaria: (Codtienda,Codproducto). Permite que un 
--                     producto pueda aparecer en varias tiendas, y que en una 
--                     tienda puedan haber varios productos. Clave ajena, 
--                     referencia a Codtienda de la tabla tienda. Debe tener 
--                     contenido.
--2.    Nombre Columna: Codproducto
--      Descripci�n: C�digo del producto
--      Tipo de dato: Num�rico de 5 d�gitos
--      Restricciones: Clave primaria: (Codtienda,Codproducto). Permite que un 
--                     producto pueda aparecer en varias tiendas, y que en una 
--                     tienda puedan haber varios productos. Clave ajena, 
--                     referencia a Codproducto de la tabla PRODUCTO. Debe tener
--                     contenido.
--3.    Nombre Columna: Unidades
--      Descripci�n: Unidades de ese producto en esa tienda
--      Tipo de dato: Num�rico de 6 d�gitos.
--      Restricciones: Mayor o igual a 0. Debe tener contenido.
CREATE TABLE STOCK 
(
  CODTIENDA NUMBER(3) NOT NULL 
, CODPRODUCTO NUMBER(5) NOT NULL 
, UNIDADES NUMBER(6) NOT NULL CHECK (UNIDADES >= 0)
, CONSTRAINT STOCK_PK PRIMARY KEY 
  (
    CODTIENDA 
  , CODPRODUCTO 
  )
  ENABLE 
);

ALTER TABLE STOCK ADD CONSTRAINT STOCK_CODTIENDA_FK 
FOREIGN KEY (CODTIENDA)
REFERENCES TIENDA (CODTIENDA)
ON DELETE CASCADE ENABLE;

ALTER TABLE STOCK ADD CONSTRAINT STOCK_CODPRODUCTO_FK 
FOREIGN KEY (CODPRODUCTO)
REFERENCES PRODUCTO (CODPRODUCTO)
ON DELETE CASCADE ENABLE;

COMMENT ON COLUMN STOCK.CODTIENDA IS '	C�digo de la tienda.';
COMMENT ON COLUMN STOCK.CODPRODUCTO IS 'C�digo del producto';
COMMENT ON COLUMN STOCK.UNIDADES IS 'Unidades de ese producto en esa tienda';
