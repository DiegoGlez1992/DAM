--EJERCICIO 1: 
--Vamos a crear las tablas para una tienda virtual que distribuye 
--productos agrupados en familias en varias tiendas. Realiza un script llamado 
--Creatienda.sql que implemente los ejercicios descritos a continuación. Precede
--cada una de las sentencias SQL de los ejercicios con un comentario que incluya
--el enunciado del ejercicio correspondiente. Recuerda que los comentarios van 
--precedidos del símbolo -- al inicio de la línea. Con las sentencias DDL de SQL
--crea la tablas especificadas a continuación aplicando las restricciones 
--(constraints) pedidas. Se debe cumplir la integridad referencial.

--TABLA FAMILIA: Contiene las familias a las que pertenecen los productos, como 
--por ejemplo ordenadores, impresoras,etc.
--1.    Nombre Columna: Codfamilia 
--      Descripción: Código que distingue una familia de otra.
--      Tipo de dato: Numérico de 3 dígitos.
--      Restricciones: Clave primaria.
--2.    Nombre Columna: Denofamilia
--      Descripción: Denominación de la familia
--      Tipo dato: Alfanumérico de 50 caracteres
--      Restricciones: No puede haber dos familias con la misma denominación. 
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

COMMENT ON COLUMN FAMILIA.CODFAMILIA IS 'Código que distingue una familia de otra';
COMMENT ON COLUMN FAMILIA.DENOFAMILIA IS 'Denominación de la familia';

--TABLA PRODUCTO: Contendrá información general sobre los productos que 
--distribuye la empresa a las tiendas.
--1.    Nombre Columna: Codproducto
--      Descripción: Código que distingue un producto de otro
--      Tipo de dato: Numérico de 5 dígitos
--      Restricciones: Clave primaria.
--2.    Nombre Columna: Denoproducto
--      Descripción: Denominación del producto
--      Tipo de dato: Alfanumérico de 20 caracteres
--      Restricciones: Debe tener contenido.
--3.    Nombre Columna: Descripcion
--      Descripción: Descripción del producto
--      Tipo de dato: Alfanumérico de 100 caracteres
--      Restricciones: 
--4.    Nombre Columna: PrecioBase
--      Descripción: Precio base del producto
--      Tipo de dato: Numérico de 8 dígitos dos de ellos decimales
--      Restricciones: Mayor que 0. Debe tener contenido.
--5.    Nombre Columna: PorcReposición
--      Descripción: Porcentaje de reposición aplicado a ese producto. Se 
--                   utilizará para aplicar a las unidades mínimas y obtener el 
--                   número total de unidades a reponer cuando el stock esté bajo
--                   mínimo
--      Tipo de dato: Numérico de 3 dígitos
--      Restricciones: Mayor que 0
--6.    Nombre Columna: UnidadesMinimas
--      Descripción: Unidades mínimas recomendables en almacen
--      Tipo de dato: Numérico de 4 dígitos
--      Restricciones: Mayor que 0. Debe tener contenido.
--7.    Nombre Columna: Codfamilia
--      Descripción: Código de la familia a la que pertenece el producto
--      Tipo de dato: Numérico de 3 dígitos
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

COMMENT ON COLUMN PRODUCTO.CODPRODUCTO IS '	Código que distingue un producto de otro';
COMMENT ON COLUMN PRODUCTO.DENOPRODUCTO IS 'Denominación del producto';
COMMENT ON COLUMN PRODUCTO.DESCRIPCION IS '	Descripción del producto';
COMMENT ON COLUMN PRODUCTO.PRECIOBASE IS 'Precio base del producto';
COMMENT ON COLUMN PRODUCTO.PORCREPOSICION IS 'Porcentaje de reposición aplicado a ese producto. Se utilizará para aplicar a las unidades mínimas y obtener el número total de unidades a reponer cuando el stock esté bajo mínimo';
COMMENT ON COLUMN PRODUCTO.UNIDADESMINIMAS IS 'Unidades mínimas recomendables en almacen';
COMMENT ON COLUMN PRODUCTO.CODFAMILIA IS 'Código de la familia a la que pertenece el producto';

--TABLA TIENDA: Contendrá información básica sobre las tiendas que distribuyen 
--los productos.
--1.    Nombre Columna: Codtienda
--      Descripción: Código que distingue una tienda de otra.
--      Tipo de dato: Numérico de 3 dígitos
--      Restricciones: Clave primaria.
--2.    Nombre Columna: Denotienda
--      Descripción: Denominación o nombre de la tienda.
--      Tipo de dato: Alfanumérico de 20 caracteres
--      Restricciones: Debe tener contenido.
--3.    Nombre Columna: Telefono
--      Descripción: Teléfono de la tienda.
--      Tipo de dato: Alfanumérico de 11 caracteres
--      Restricciones: 
--4.    Nombre Columna: CodigoPostal
--      Descripción: Codigo Postal donde se ubica la tienda.
--      Tipo de dato: Alfanumérico de 5 caracteres
--      Restricciones: Debe tener contenido.
--5.    Nombre Columna: Provincia
--      Descripción: Provincia donde se ubica la tienda.
--      Tipo de dato: Alfanumérico de 5 caracteres
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

COMMENT ON COLUMN TIENDA.CODTIENDA IS 'Código que distingue una tienda de otra.';
COMMENT ON COLUMN TIENDA.DENOTIENDA IS '	Denominación o nombre de la tienda.';
COMMENT ON COLUMN TIENDA.TELEFONO IS '	Teléfono de la tienda.';
COMMENT ON COLUMN TIENDA.CODIGOPOSTAL IS 'Codigo Postal donde se ubica la tienda.';
COMMENT ON COLUMN TIENDA.PROVINCIA IS 'Provincia donde se ubica la tienda';

--TABLA STOCK: Contendrá para cada tienda el número de unidades disponibles de 
--cada producto. La clave primaria está formada por la concatenación de los 
--campos Codtienda y Codproducto.
--1.    Nombre Columna: Codtienda
--      Descripción: Código de la tienda.
--      Tipo de dato: Numérico de 3 dígitos
--      Restricciones: Clave primaria: (Codtienda,Codproducto). Permite que un 
--                     producto pueda aparecer en varias tiendas, y que en una 
--                     tienda puedan haber varios productos. Clave ajena, 
--                     referencia a Codtienda de la tabla tienda. Debe tener 
--                     contenido.
--2.    Nombre Columna: Codproducto
--      Descripción: Código del producto
--      Tipo de dato: Numérico de 5 dígitos
--      Restricciones: Clave primaria: (Codtienda,Codproducto). Permite que un 
--                     producto pueda aparecer en varias tiendas, y que en una 
--                     tienda puedan haber varios productos. Clave ajena, 
--                     referencia a Codproducto de la tabla PRODUCTO. Debe tener
--                     contenido.
--3.    Nombre Columna: Unidades
--      Descripción: Unidades de ese producto en esa tienda
--      Tipo de dato: Numérico de 6 dígitos.
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

COMMENT ON COLUMN STOCK.CODTIENDA IS '	Código de la tienda.';
COMMENT ON COLUMN STOCK.CODPRODUCTO IS 'Código del producto';
COMMENT ON COLUMN STOCK.UNIDADES IS 'Unidades de ese producto en esa tienda';
