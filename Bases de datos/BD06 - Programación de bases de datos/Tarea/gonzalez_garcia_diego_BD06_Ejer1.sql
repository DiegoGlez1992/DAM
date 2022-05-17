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

nombre_FamiliaOrigen familias.nombre%TYPE DEFAULT NULL; -- Variable el nombre de la familia origen
nombre_FamiliaDestino familias.nombre%TYPE DEFAULT NULL;    -- Variable para el nombre de la familia destino

numero_cambios number;  -- Variable para contar los cambios

-- Definimos una excepción
familias_iguales EXCEPTION;
PRAGMA EXCEPTION_INIT(familias_iguales, -20001);

BEGIN
    -- Si los ID de las familias son iguales
    IF id_FamiliaOrigen = id_FamiliaDestino THEN
        RAISE familias_iguales; -- Lanzamos error
    
    -- Si los ID son diferentes
    ELSE
        -- Comprobamos que id_FamiliaOrigen existe y copiamos su nombre
        SELECT nombre INTO nombre_FamiliaOrigen FROM familias WHERE identificador = id_FamiliaOrigen;
        -- Comprobamos que id_FamiliaDestino existe y copiamos su nombre
        SELECT nombre INTO nombre_FamiliaDestino FROM familias WHERE identificador = id_FamiliaDestino;
    END IF;
    
    -- Contamos los registros que tiene la tabla agentes y que su familia es igual a id_FamiliaOrigen
    SELECT COUNT(*) INTO numero_cambios FROM agentes WHERE familia = id_FamiliaOrigen;
    
    -- Si hay agentes para cambiar de familia
    IF numero_cambios > 0 THEN
        -- Cambiamos los ID de la familia y mostramos el mensaje
        UPDATE agentes SET familia = id_FamiliaDestino WHERE familia = id_FamiliaOrigen;
        COMMIT;
        dbms_output.put_line('Se han trasladado ' || numero_cambios || ' agentes de la familia ' || nombre_FamiliaOrigen || ' a la familia ' || nombre_FamiliaDestino);
    -- Si no hay agentes para cambiar de familia
    ELSE
        -- Mostramos mensaje
        dbms_output.put_line('No hay ningún agente de la familia ' || nombre_FamiliaOrigen);
    END IF;
    
-- Control de las excepciones
EXCEPTION
    -- Excepción por familias iguales
    WHEN familias_iguales THEN
        RAISE_APPLICATION_ERROR(-20001,'Error. Las familias son iguales');
    -- Excepción por falta de datos
    WHEN NO_DATA_FOUND THEN
        -- SI la familia origen no existe, mostramos mensaje de error
        IF nombre_FamiliaOrigen IS NULL THEN
            RAISE_APPLICATION_ERROR(-20002,'Error. Familia origen inexistente');
        -- SI la familia destino no existe, mostramos mensaje de error
        ELSIF nombre_FamiliaDestino IS NULL THEN
            RAISE_APPLICATION_ERROR(-20002,'Error. Familia destino inexistente');
        END IF;
    
END;
/

--------------------------------------------------------------------------------
-- Prueba del procedimiento                                                   --
--------------------------------------------------------------------------------
SET SERVEROUTPUT ON;

BEGIN
    CambiarAgentesFamilia(112,111);   -- No hay agentes en la familia
    --CambiarAgentesFamilia(111,112);   -- Se han trasladado 3 agentes
    --CambiarAgentesFamilia(221,111);   -- Error. Familia origen inexistente
    --CambiarAgentesFamilia(1121,221);  -- Error. Familia destino inexistente
END;
/

















