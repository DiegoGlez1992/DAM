--EJERCICIO 2:
--A) Modificar las tablas creadas en el ejercicio anterior siguiendo las 
--indicaciones. Los ejercicios se incluirán en un script llamado 
--ModificaTienda.sql. Cada uno de ellos, como en el ejercicio anterior, irá 
--precedido de un comentario con el enunciado.
--  Añadir a la tabla STOCK
--      Una columna de tipo fecha llamada FechaUltimaEntrada que por defecto 
--      tome el valor de la fecha actual.
ALTER TABLE STOCK 
ADD (FECHAULTIMAENTRADA DATE DEFAULT SYSDATE );

--      Una columna llamada Beneficio que contendrá el tipo de porcentaje de 
--      beneficio que esa tienda aplica en ese producto. Se debe controlar que 
--      el valor que almacene sea 1,2, 3, 4 o 5.
ALTER TABLE STOCK 
ADD (BENEFICIO NUMBER(1) NOT NULL );

ALTER TABLE STOCK ADD CONSTRAINT STOCK_BENEFICIO_CK 
CHECK (BENEFICIO in ('1','2','3','4','5'))
ENABLE;

--  En la tabla PRODUCTO
--      Eliminar de la tabla producto la columna Descripción.
ALTER TABLE PRODUCTO 
DROP COLUMN DESCRIPCION;

--      Añadir una columna llamada perecedero que únicamente acepte los valores:
--      S o N.
ALTER TABLE PRODUCTO 
ADD (PERECEDERO VARCHAR2(1) NOT NULL);

ALTER TABLE PRODUCTO ADD CONSTRAINT PRODUCTO_PERECEDERO_CK 
CHECK (PERECEDERO in ('S','N'))
ENABLE;

--      Modificar el tamaño de la columna Denoproducto a 50.
ALTER TABLE PRODUCTO  
MODIFY (DENOPRODUCTO VARCHAR2(50 BYTE) );

--  En la tabla FAMILIA
--      Añadir una columna llamada IVA, que represente el porcentaje de IVA y 
--      únicamente pueda contener los valores 21,10,ó 4.
ALTER TABLE FAMILIA 
ADD (IVA NUMBER NOT NULL);

ALTER TABLE FAMILIA ADD CONSTRAINT FAMILIA_IVA_CK 
CHECK (IVA in ('21','10','4'))
ENABLE;

--  En la tabla tienda
--      La empresa desea restringir el número de tiendas con las que trabaja, de
--      forma que no pueda haber más de una tienda en una misma zona (la zona se
--      identifica por el código postal). Definir mediante DDL las restricciones
--      necesarias para que se cumpla en el campo correspondiente..
ALTER TABLE TIENDA ADD CONSTRAINT TIENDA_CODIGOPOSTAL_UK 
UNIQUE (CODIGOPOSTAL)
ENABLE;

--B) Renombra la tabla STOCK por PRODXTIENDAS.
ALTER TABLE STOCK 
RENAME TO PRODXTIENDAS;

--C) Elimina la tabla FAMILIA y su contenido si lo tuviera.
DROP TABLE FAMILIA cascade constraints; 

--D) Crea un usuario llamado C##INVITADO siguiendo los pasos de la unidad 1 y 
--dale todos los privilegios sobre la tabla PRODUCTO.
CREATE USER C##INVITADO DEFAULT TABLESPACE USERS;

GRANT ALL ON PRODUCTO TO C##INVITADO;

--E) Retira los permisos de modificar la estructura de la tabla y borrar 
--contenido de la tabla PRODUCTO al usuario anterior.
REVOKE ALTER, DELETE, UPDATE ON PRODUCTO FROM C##INVITADO;
