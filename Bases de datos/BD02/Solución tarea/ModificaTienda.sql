--------------------------------------------------------------------------------------EJERCICIO 2
 -- Añadir a la tabla STOCK
-- Una columna de tipo fecha llamada FechaUltimaEntrada que por defecto tome el
-- valor de la fecha actual.
 ALTER TABLE STOCK ADD FechaUltimaEntrada DATE DEFAULT SYSDATE;
 -- Una columna llamada Beneficio que contendrá el tipo de porcentaje de beneficio
 -- que esa tienda aplica en ese producto
--. Se debe controlar que el valor que almacene sea 1,2, 3, 4 o 5.
 ALTER TABLE STOCK ADD Beneficio NUMBER(1) CONSTRAINT CHK_BENE_STOCK CHECK(Beneficio in (1,2,3,4,5));
 --En la tabla PRODUCTO
 -- Eliminar de la tabla producto la columna Descripción.
 ALTER TABLE producto DROP COLUMN descripcion;
 -- Añadir una columna llamada perecedero que únicamente acepte los valores: S o N.
ALTER TABLE producto ADD perecedero char CONSTRAINT CHK_PERE_STOCK CHECK(perecedero IN ('S','N'));
-- Modificar el tamaño de la columna Denoproducto a 50.
 ALTER TABLE PRODUCTO MODIFY Denoproducto VARCHAR2(50);
 -- En la tabla FAMILIA Añadir una columna llamada IVA, que represente el porcentaje
-- de IVA y únicamente pueda contener los valores 21,10,ó 4.
 ALTER TABLE familia ADD IVA NUMBER(2) CONSTRAINT CHK_IVA_FAMI CHECK(IVA IN(21,10,4));
 -- En la tabla tienda: La empresa desea restringir el número de tiendas con las que
 -- trabaja, de forma que no pueda haber más de una tienda en una misma zona
 -- la zona se identifica por el código postal). Definir mediante DDL las
 -- restricciones necesarias para que se cumpla en el campo correspondiente..
 ALTER TABLE TIENDA ADD CONSTRAINT UNI_TIEN_COD UNIQUE(CodigoPostal);
 -- B. Renombra la tabla STOCK por PRODXTIENDAS.
 RENAME STOCK TO PRODXTIENDAS;
 --C. Elimina la tabla FAMILIA y constraints relacionadas.
 DROP TABLE FAMILIA CASCADE CONSTRAINTS;
 -- D. Crea un usuario llamado C##INVITADO siguiendo los pasos de la unidad 1 y dale
 -- todos los privilegios sobre la tabla PRODUCTO.
 CONNECT sys as sysdba
 CREATE USER C##INVITADO IDENTIFIED BY BD02 default tablespace Users;
 GRANT ALL ON c##prueba.PRODUCTO TO C##INVITADO;
 -- E. Retira los permisos de modificar la estructura de la tabla y borrar contenido de
 -- la tabla PRODUCTO al usuario anterior.
 REVOKE ALTER, DELETE, UPDATE ON c##prueba.PRODUCTO FROM C##INVITADO;