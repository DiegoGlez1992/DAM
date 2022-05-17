/*
ACTIVIDAD 1.
Crear un procedimiento que permita cambiar a todos los agentes de una familia 
determinada (familia origen) a otra familia (familia destino).

El procedimiento tendrá la siguiente cabecera:
    CambiarAgentesFamilia(id_FamiliaOrigen, id_FamiliaDestino),  
donde cada uno de los argumentos corresponde a un identificador de Familia. 
Cambiará la columna  Identificador de Familia de todos los agentes, de la tabla 
AGENTES, que pertenecen a la Familia con código id_FamiliaOrigen por el código 
id_FamiliaDestino

Previamente comprobará que ambas familias existen y que no son iguales.

Para la comprobación de la existencia de las familias se puede utilizar un 
cursor variable, o contar el número de filas y en caso de que no exista, se 
visualizará el mensaje correspondiente mediante una excepción del tipo 
RAISE_APPLICATION_ERROR. También se mostrará un mensaje en caso de que ambos 
argumentos tengan el mismo valor.         

El procedimiento visualizará  el mensaje "Se han trasladado XXX agentes de la 
familia  XXXXXX a la familia ZZZZZZ" donde XXX es el número de agentes que se 
han cambiado de familia, XXXXXX es el nombre de la familia origen y ZZZZZZ es 
el nombre de la familia destino.
*/

CREATE OR REPLACE PROCEDURE CambiarAgentesFamilia( id_FamiliaOrigen familias.identificador%TYPE, id_FamiliaDestino familias.identificador%TYPE) IS

v_FamiliaOrigen familias.identificador%TYPE DEFAULT NULL;
v_FamiliaDestino familias.identificador%TYPE DEFAULT NULL;

nombre_FamiliaOrigen familias.nombre%TYPE DEFAULT NULL;
nombre_FamiliaDestino familias.nombre%TYPE DEFAULT NULL;

numero_cambios number;

familias_iguales EXCEPTION;
PRAGMA EXCEPTION_INIT(familias_iguales, -20001);

BEGIN
    -- Si los ID de las familias son iguales
    IF id_FamiliaOrigen = id_FamiliaDestino THEN
        RAISE familias_iguales; -- Lanzamos error
    -- Si los ID son diferentes
    ELSE
        -- Comprobamos que id_FamiliaOrigen existe y copiamos su nombre
        -- SELECT identificador INTO v_FamiliaOrigen FROM familias WHERE identificador = id_FamiliaOrigen;
        SELECT nombre INTO nombre_FamiliaOrigen FROM familias WHERE identificador = id_FamiliaOrigen;
        -- Comprobamos que id_FamiliaDestino existe y copiamos su nombre
        -- SELECT identificador INTO v_FamiliaDestino FROM familias WHERE identificador = id_FamiliaDestino;
        SELECT nombre INTO nombre_FamiliaDestino FROM familias WHERE identificador = id_FamiliaDestino;
    END IF;
    
    -- Contamos los registros que tiene la tabla agentes y que su familia es igual a id_FamiliaOrigen
    SELECT COUNT(*) INTO numero_cambios FROM agentes WHERE familia = id_FamiliaOrigen;
    
    --dbms_output.put_line('id_FamiliaOrigen: ' || id_FamiliaOrigen || ' ' || nombre_FamiliaOrigen);
    --dbms_output.put_line('id_FamiliaDestino: ' || id_FamiliaDestino || ' ' || nombre_FamiliaDestino);
    
    -- Si hay agentes para cambiar de familia
    IF numero_cambios > 0 THEN
        UPDATE agentes SET familia = id_FamiliaDestino WHERE familia = id_FamiliaOrigen;
        dbms_output.put_line('Se han trasladado ' || numero_cambios || ' agentes de la familia ' || nombre_FamiliaOrigen || ' a la familia ' || nombre_FamiliaDestino);
    ELSE
        dbms_output.put_line('No hay ningún agente de la familia ' || nombre_FamiliaOrigen);
    END IF;
    
EXCEPTION
    WHEN familias_iguales THEN
        RAISE_APPLICATION_ERROR(-20001,'Error. Las familias son iguales');
    WHEN NO_DATA_FOUND THEN
        -- IF v_FamiliaOrigen IS NULL THEN
        IF nombre_FamiliaOrigen IS NULL THEN
            RAISE_APPLICATION_ERROR(-20002,'Error. Familia origen inexistente');
        -- ELSIF v_FamiliaDestino IS NULL THEN
        ELSIF nombre_FamiliaDestino IS NULL THEN
            RAISE_APPLICATION_ERROR(-20002,'Error. Familia destino inexistente');
        END IF;
    
END;
/


-- set SERVEROUTPUT on;

BEGIN
    --CambiarAgentesFamilia(21,112);
    CambiarAgentesFamilia(112,21);
END;
/


/* 
ACTIVIDAD 2.
Queremos controlar algunas restricciones a la hora de trabajar con agentes:
    - La longitud de la clave de un agente no puede ser inferior a 6.
    - La habilidad de un agente debe estar comprendida entre 0 y 9 (ambos inclusive).
    - La categoría de un agente sólo puede ser igual a 0, 1 o 2.
    - Si un agente tiene categoría 2 no puede pertenecer a ninguna familia y debe
    pertenecer a una oficina.  
    - Si un agente tiene categoría 1 no puede pertenecer a ninguna oficina y debe
    pertenecer  a una familia.  
    - Todos los agentes deben pertenecer  a una oficina o a una familia pero 
    nunca a ambas a la vez.
Se pide crear un disparador para asegurar estas restricciones. El disparador 
deberá lanzar todos los errores que se puedan producir en su ejecución mediante 
errores que identifiquen con un mensaje adecuado por qué se ha producido dicho error.

Algunas de las restricciones implementadas con el disparador se pueden incorporar
a la definición del esquema de la tabla utilizando el Lenguaje de Definición de 
Datos (Check,Unique,..).Identifica cuáles son y con qué tipo de restricciones 
las implementarías.
*/

CREATE OR REPLACE TRIGGER RestriccionesAgentes BEFORE INSERT OR UPDATE ON agentes
FOR EACH ROW

BEGIN

    -- La longitud de la clave de un agente no puede ser inferior a 6
    IF (LENGTH(:new.clave) < 6) THEN
        RAISE_APPLICATION_ERROR(-20201, 'Error. La clave no puede contener menos de 6 caracteres');    -- Lanzamos excepción
    END IF;
    
    -- La habilidad de un agente debe estar comprendida entre 0 y 9
    IF ((:new.habilidad < 0) OR (:new.habilidad > 9)) THEN
        RAISE_APPLICATION_ERROR(-20202, 'Error. La habilidad debe estar comprendida entre 0 y 9'); -- Lanzamos excepción
    END IF;
    
    -- La categoría de un agente sólo puede ser igual a 0, 1 o 2
    IF ((:new.categoria < 0) OR (:new.categoria > 2)) THEN
        RAISE_APPLICATION_ERROR(-20203, 'Error. Solo existen las categorías 0, 1 y 2');    -- Lanzamos excepción
    END IF;
    
    -- Si un agente tiene categoría 2 no puede pertenecer a ninguna familia y debe pertenecer a una oficina.
    IF (:new.categoria = 2) THEN
        IF (:new.familia IS NOT NULL) THEN
            RAISE_APPLICATION_ERROR(-20204, 'Error. Un agente con categoría = 2 no puede pertenecer a ninguna familia');   -- Lanzamos excepción
        END IF;
        IF (:new.oficina IS NULL) THEN
            RAISE_APPLICATION_ERROR(-20205, 'Error. Un agente con categoría = 2 debe pertenecer a una oficina');   -- Lanzamos excepción
        END IF;
    END IF;
    
    -- Si un agente tiene categoría 1 no puede pertenecer a ninguna oficina y debe pertenecer a una familia.  
    IF (:new.categoria = 1) THEN
        IF (:new.oficina IS NOT NULL) THEN
            RAISE_APPLICATION_ERROR(-20206, 'Error. Un agente con categoría = 1 no puede pertenecer a ninguna oficina');   -- Lanzamos excepción
        END IF;
        IF (:new.familia IS NULL) THEN
            RAISE_APPLICATION_ERROR(-20207, 'Error. Un agente con categoría = 1 debe pertenecer a una familia');   -- Lanzamos excepción
        END IF;
    END IF;
    
    -- Todos los agentes deben pertenecer a una oficina o a una familia pero nunca a ambas a la vez
    IF ((:new.oficina IS NOT NULL) AND (:new.familia IS NOT NULL)) THEN
        RAISE_APPLICATION_ERROR(-20208, 'Error. Un agente no puede pertenecer a una oficina y a una familia al mismo tiempo'); -- Lanzamos excepción
    ELSIF ((:new.oficina IS NULL) AND (:new.familia IS NULL)) THEN
        RAISE_APPLICATION_ERROR(-20209, 'Error. Un agente debe pertenecer a una oficina o a una familia'); -- Lanzamos excepción
    END IF;
    
END;
/

--------------------------------------------------------------------------------
-- Prueba del disparador                                                      --
--------------------------------------------------------------------------------
SET SERVEROUTPUT ON;

-- Sin errores
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg123', 9, 2, null, 1);

-- Error clave
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg12', 9, 2, null, 1);

-- Error habilidad
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg123', -1, 2, null, 1);

-- Error categoria
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg123', 9, 3, null, 1);

-- Error categoría = 2 y familia
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg123', 9, 2, 21, null);

-- Error categoría = 2 sin oficina
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg123', 9, 2, null, null);

-- Error categoría = 1 y oficina
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg123', 9, 1, 21, 1);

-- Error categoría = 1 sin familia
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg123', 9, 1, null, null);

-- Error sin familia ni oficina
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg123', 9, 0, null, null);

-- Error con familia y oficina
INSERT INTO agentes (identificador, nombre, usuario, clave, habilidad, categoria, familia, oficina)
    VALUES (90001, 'Diego Gonzalez Garcia', 'dgg', 'dgg123', 9, 0, 21, 2);


/*
ACTIVIDAD 2 APARTADO B.
Algunas de las restricciones implementadas con el disparador se pueden 
incorporar a la definición del esquema de la tabla utilizando el Lenguaje de 
Definición de Datos (Check,Unique,..).Identifica cuáles son y con qué tipo de 
restricciones las implementarías.
*/

-- La longitud de la clave de un agente no puede ser inferior a 6
ALTER TABLE agentes ADD CONSTRAINT RestLongClave CHECK (LENGTH(LTRIM(RTRIM(clave))) > 6);

-- La habilidad de un agente debe estar comprendida entre 0 y 9
ALTER TABLE agentes ADD CONSTRAINT RestHabilidad CHECK (habilidad BETWEEN 0 AND 9);

-- La categoría de un agente sólo puede ser igual a 0, 1 o 2
ALTER TABLE agentes ADD CONSTRAINT RestCategoria CHECK (categoria BETWEEN 0 AND 2);

-- Si un agente tiene categoría 2 no puede pertenecer a ninguna familia y debe pertenecer a una oficina.
ALTER TABLE agentes ADD CONSTRAINT RestCategoria2 CHECK (categoria = 2 AND familia IS NULL AND oficina IS NOT NULL);

-- Si un agente tiene categoría 1 no puede pertenecer a ninguna oficina y debe pertenecer a una familia.  
ALTER TABLE agentes ADD CONSTRAINT RestCategoria1 CHECK (categoria = 1 AND oficina IS NULL AND familia IS NOT NULL);

-- Todos los agentes deben pertenecer a una oficina o a una familia pero nunca a ambas a la vez
ALTER TABLE agentes ADD CONSTRAINT RestPertenecer CHECK ((familia IS NULL AND oficina IS NOT NULL) OR
                                                        (familia IS NOT NULL AND oficina IS NULL));













