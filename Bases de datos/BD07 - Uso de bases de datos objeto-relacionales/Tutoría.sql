CREATE OR REPLACE TYPE nombre_tipo AS OBJECT (
Declaración_atributos
Declaración_métodos
);

--Ojo, no permite constantes (CONSTANTS), excepciones (EXCEPTIONS), cursores (CURSORS) o tipos (TYPES)
--puede almacenar un determinado atributo puede ser cualquiera de los tipos de Oracle excepto los siguientes:
--LONG y LONG RAW.
--ROWID y UROWID.
--Los tipos específicos PL/SQL BINARY_INTEGER (y sus subtipos), BOOLEAN, PLS_INTEGER, RECORD, REF CURSOR, %TYPE, y %ROWTYPE.
--Los tipos definidos dentro de un paquete PL/SQL.
--no puedes inicializar los atributos usando el operador de asignación, 
--ni la cláusula DEFAULT, ni asignar la restricción NOT NULL.

DROP TYPE nombre_tipo;


CREATE OR REPLACE TYPE Usuario AS OBJECT (
	login VARCHAR2(10),
	nombre VARCHAR2(30),
	f_ingreso DATE,
	credito NUMBER
);


ALTER TYPE Usuario DROP ATTRIBUTE f_ingreso;
ALTER TYPE Usuario ADD ATTRIBUTE (apellidos VARCHAR2(40), localidad VARCHAR2(50));
ALTER TYPE Usuario
	ADD ATTRIBUTE cp VARCHAR2(5),
	MODIFY ATTRIBUTE nombre VARCHAR2(35);
   
CREATE OR REPLACE TYPE Usuario AS OBJECT (
	login VARCHAR2(10),
	nombre VARCHAR2(30),
	f_ingreso DATE,
	credito NUMBER,
    STATIC PROCEDURE cuenta(inc NUMBER),
	MEMBER PROCEDURE incrementoCredito(inc NUMBER),
    MEMBER PROCEDURE decrementaCredito(inc NUMBER)    
);

CREATE OR REPLACE TYPE BODY Usuario AS

    CONSTRUCTOR FUNCTION Usuario(login VARCHAR2, credito NUMBER)
		RETURN SELF AS RESULT
	IS
		BEGIN
			IF (credito >= 0) THEN
				SELF.credito := credito;
			ELSE
				SELF.credito := 0;
			END IF;
			RETURN;
    END;
    STATIC PROCEDURE cuenta(inc NUMBER) IS
		BEGIN
			credito :=  inc;
		END cuenta;
        
	MEMBER PROCEDURE incrementoCredito(inc NUMBER) IS
		BEGIN
			credito := credito + inc;
		END incrementoCredito;
        
    MEMBER PROCEDURE decrementaCredito(inc NUMBER) IS
		BEGIN
			credito := credito - inc;
		END incrementoCredito;
END;

--El tipo de dato correspondiente al parámetro SELF será el mismo que el del objeto original. 
--En las funciones MEMBER, si no declaras el parámetro SELF, su modo por defecto se toma como IN. 
--En cambio, en los procedimientos MEMBER, si no se declara, se toma como IN OUT. 
--Ten en cuenta que no puedes especificar el modo OUT para este parámetro SELF, y que los métodos STATIC no pueden utilizar este parámetro especial.

CONSTRUCTOR FUNCTION Usuario(login VARCHAR2, credito NUMBER)
	RETURN SELF AS RESULT
 
CREATE OR REPLACE TYPE BODY Usuario AS
	CONSTRUCTOR FUNCTION Usuario(login VARCHAR2, credito NUMBER)
		RETURN SELF AS RESULT
	IS
		BEGIN
			IF (credito >= 0) THEN
				SELF.credito := credito;
			ELSE
				SELF.credito := 0;
			END IF;
			RETURN;
		END;
END;


variable_objeto := NEW Nombre_Tipo_Objeto (valor_atributo1, valor_atributo2, ...);

u1 := NEW Usuario('luitom64', 'LUIS ', '24/10/07', 100);
u1.incrementoCredito(10);
u1 Usuario := NEW Usuario('luitom64', 'LUIS ', '24/10/07', 100);


--Los métodos MEMBER son invocados utilizando una instancia del tipo de objeto:
nombre_objeto.metodo()
--En cambio, los métodos STATIC se invocan usando el tipo de objeto, en lugar de una de sus instancias:
nombre_tipo_objeto.metodo()


--Para indicar que un tipo de objeto es heredado de otro hay que usar la palabra reservada UNDER,
--y además hay que tener en cuenta que el tipo de objeto del que hereda debe tener la propiedad NOT FINAL. 
-- Por defecto, los tipos de objeto se declaran como FINAL, es decir, que no se puede crear un tipo de objeto 
--que herede de él.

CREATE TYPE Persona AS OBJECT (
	nombre VARCHAR2(20),
	apellidos VARCHAR2(30)
) NOT FINAL;
CREATE TYPE UsuarioPersona UNDER Persona (
	login VARCHAR(30),
	f_ingreso DATE,
	credito NUMBER
); 
DECLARE
	u1 Persona;
BEGIN
	u1 := NEW UsuarioPersona('nombre1', 'apellidos1', 'user1', '01/01/2001', 100);
	dbms_output.put_line(u1.nombre);
END;

DECLARE
	u1 UsuarioPersona;
BEGIN
	u1 := NEW UsuarioPersona('nombre1', 'apellidos1', 'user1', '01/01/2001', 100);
	dbms_output.put_line(u1.nombre);
END;

--Para comparar objetos
CREATE OR REPLACE TYPE Usuario AS OBJECT (
	login VARCHAR2(30),
	nombre VARCHAR2(30),
	apellidos VARCHAR2(40),
	f_ingreso DATE,
	credito NUMBER,
	MAP MEMBER FUNCTION ordenarUsuario RETURN VARCHAR2, --puedes declarar un método MAP o un método ORDER, pero no los dos.
    ORDER MEMBER FUNCTION ordenUsuario(u Usuario) RETURN INTEGER --puedes declarar un método MAP o un método ORDER, pero no los dos.
);

CREATE OR REPLACE TYPE BODY Usuario AS
	MAP MEMBER FUNCTION ordenarUsuario RETURN VARCHAR2 IS --puedes declarar un método MAP o un método ORDER, pero no los dos.
	BEGIN
		RETURN (apellidos || ' ' || nombre);
	END ordenarUsuario;
--Para ordenar
	ORDER MEMBER FUNCTION ordenUsuario(u Usuario) RETURN INTEGER IS --puedes declarar un método MAP o un método ORDER, pero no los dos.
	BEGIN
		/* La función substr obtiene una subcadena desde la posición indicada hasta el final*/
		IF substr(SELF.login, 7) < substr(u.login, 7) THEN
			RETURN -1;
		ELSIF substr(SELF.login, 7) > substr(u.login, 7) THEN
			RETURN 1;
		ELSE
			RETURN 0;
		END IF;
	END;
END;

--COLECCIONES:
--puedes declarar un método MAP o un método ORDER, pero no los dos.
--las colecciones sólo pueden tener una dimensión y los elementos se indexan mediante un valor de tipo numérico o cadena de caracteres.
--La base de datos de Oracle proporciona los tipos VARRAY y NESTED TABLE (tabla anidada) como tipos de datos colección.

--Un VARRAY es una colección de elementos a la que se le establece una dimensión máxima que debe indicarse al declararla.
--Al tener una longitud fija, la eliminación de elementos no ahorra espacio en la memoria del ordenador.
--Una NESTED TABLE (tabla anidada) puede almacenar cualquier número de elementos. 
--Tienen, por tanto, un tamaño dinámico, y no tienen que existir forzosamente valores para todas las posiciones de la colección.
--Una variación de las tablas anidadas son los arrays asociativos, que utilizan valores arbitrarios para sus índices. 
--En este caso, los índices no tienen que ser necesariamente consecutivos.

--Los elementos de las colecciones declaradas en SQL, además no pueden ser de los tipos:
--BINARY_INTEGER, PLS_INTEGER, BOOLEAN, LONG, LONG RAW, NATURAL, NATURALN, POSITIVE, POSITIVEN, REF CURSOR, SIGNTYPE, STRING.

TYPE nombre_tipo IS VARRAY (tamaño_max) OF tipo_elemento;
TYPE nombre_tipo IS TABLE OF tipo_elemento;
TYPE nombre_tipo IS TABLE OF tipo_elemento INDEX BY tipo_índice;

--tipo_índice representa el tipo de dato que se va a utilizar para el índice. 
--Puede ser PLS_INTEGER, BINARY_INTEGER o VARCHAR2. 
--En este último tipo se debe indicar entre paréntesis el tamaño que se va a utilizar para el índice, por ejemplo, VARCHAR2(5).

--Hasta que no sea inicializada una colección, ésta es NULL. 
--Para inicializar una colección debes utilizar el constructor, que es una función con el mismo nombre que la colección.
--A esta función se le pasa como parámetros los valores iniciales de la colección. Por ejemplo:
--el primer elemento tiene el índice 1.

DECLARE
	TYPE Colores IS TABLE OF VARCHAR(10);
	misColores Colores;
BEGIN
	misColores := Colores('Rojo', 'Naranja', 'Amarillo', 'Verde', 'Azul');
END;

DECLARE
	TYPE Colores IS TABLE OF VARCHAR(10);
	misColores Colores := Colores('Rojo', 'Naranja', 'Amarillo', 'Verde', 'Azul');
BEGIN   
    dbms_output.put_line(misColores(2));
END;


CREATE or replace TYPE ListaColores1 AS TABLE OF VARCHAR2(20);
CREATE or replace TYPE ListaTonos AS TABLE OF VARCHAR2(3);

CREATE TABLE flores1 (
nombre VARCHAR2(20), 
coloresFlor1 ListaColores1,
tonosFlor ListaTonos
)
	NESTED TABLE coloresFlor1 STORE AS colores_tab1;

DECLARE
	colores ListaColores1;
BEGIN
	INSERT INTO flores1 VALUES('Rosa', ListaColores1('Rojo','Amarillo','Blanco'));
	colores := ListaColores1('Rojo','Amarillo','Blanco','Rosa Claro');
    dbms_output.put_line(colores(2));
	UPDATE flores1 SET coloresFlor1 = colores WHERE nombre = 'Rosa';
	SELECT coloresFlor1 INTO colores FROM flores1 WHERE nombre = 'Rosa';    
END;

DROP table flores1;


CREATE OR REPLACE TYPE Usuario AS OBJECT (
	login VARCHAR2(10),
	nombre VARCHAR2(30),
	f_ingreso DATE,
	credito NUMBER
);

-- Tablas de objetos

-- Creamos una tabla de objetos usuario

CREATE TABLE UsuariosObj OF Usuario;

--DROP TABLE UsuariosObj;

--Para manejarla

DECLARE
	u1 Usuario;
	u2 Usuario;
BEGIN
	u1 := NEW Usuario('luitom64', 'LUIS',  '24/10/2007', 50); --Constructor objeto
	u2 := NEW Usuario('caragu72', 'CARLOS',  '06/07/2007', 100); -- Constructor objeto
	INSERT INTO UsuariosObj VALUES (u1);
	INSERT INTO UsuariosObj VALUES (u2);
END;

INSERT INTO UsuariosObj VALUES (Usuario('luitom64', 'LUIS', '24/10/2007', 50));

select * from UsuariosObj;

delete from UsuariosObj;

-- Creamos una tabla con uno de los campos de tipo objeto.

CREATE TABLE Gente (
	dni VARCHAR2(10),
	unUsuario Usuario,
	partidasJugadas SMALLINT
);

INSERT INTO Gente VALUES ('22900970P', Usuario('luitom64', 'LUIS', '24/10/2007', 50), 54);
select * from Gente;

--podemos acceder a cada uno de los atributos de usuario
SELECT g.unUsuario.nombre, g.UnUsuario.login FROM Gente g;

UPDATE Gente g SET g.unUsuario.credito = 0 	WHERE g.unUsuario.login = 'luitom64';

create table favoritos of usuario;

INSERT INTO Favoritos SELECT VALUE(u) FROM UsuariosObj u WHERE u.credito >= 0;

select * from favoritos;

-- Para hacer comparaciones podemos usar tb la función value

SELECT u.login FROM UsuariosObj u JOIN Favoritos f ON VALUE(u)=VALUE(f);


SELECT g.dni FROM Gente g JOIN Favoritos f ON g.unUsuario=VALUE(f);

select * from UsuariosObj;

DECLARE
	u1 Usuario;
	u2 Usuario;
BEGIN
	SELECT VALUE(u) INTO u1 FROM UsuariosObj u WHERE u.login = 'luitom64';
	dbms_output.put_line(u1.nombre);
	u2 := u1;
	dbms_output.put_line(u2.nombre);
END;

-- Punteros a objetos --REF para obtener la referencia/puntero al objeto
CREATE OR REPLACE TYPE Partida AS OBJECT (
	codigo INTEGER,
	nombre VARCHAR2(20),
	usuarioCreador REF Usuario
);


select * from usuariosobj
DECLARE
	u_ref REF Usuario; --posición memoria objeto tipo usuario
	p1 Partida;
BEGIN
	SELECT REF(u) INTO u_ref FROM UsuariosObj u WHERE u.login = 'luitom64';
	p1 := NEW Partida(1, 'partida1', u_ref);
END;

--Para referencias mutuas entre objetos
CREATE OR REPLACE TYPE tipo2;
/
CREATE OR REPLACE TYPE tipo1 AS OBJECT (
	tipo2_ref REF tipo2
	/*Declaración del resto de atributos del tipo1*/
);
/
CREATE OR REPLACE TYPE tipo2 AS OBJECT (
	tipo1_ref REF tipo1
	/*Declaración del resto de atributos del tipo2*/
);

-- Si en una tabla tenemos guardadas referencias a objetos(ref) para obtener el valor del objeto hay que usar DEREF
-- Tabla comodin DUAL

select * from dual;


select * from UsuariosObj

DECLARE
usuario1 USUARIO;
usuario_posicion_memoria REF USUARIO; --posicion de memoria de un objeto tipo usuario
BEGIN
    SELECT DEREF(REF(U)) INTO usuario1 FROM UsuariosObj u WHERE u.login = 'luitom64';
    --SELECT DEREF(usuario_posicion_memoria) INTO usuario1 FROM dual; -- SELECT
    dbms_output.put_line(usuario1.nombre);
END;