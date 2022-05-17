/* 
ACTIVIDAD 2 - APARTADO A.
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