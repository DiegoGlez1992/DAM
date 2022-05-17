CREATE OR REPLACE TRIGGER integridad_agentes 
BEFORE INSERT OR UPDATE ON agentes 
FOR EACH ROW 
BEGIN 
--Comprobamos que la longitud de la clave no es inferior a  6
IF (LENGTH(:new.clave) < 6) THEN 
	RAISE_APPLICATION_ERROR(-20021, 'La longitud de la clave no puede ser inferior a 6 caracteres'); 
END IF; 

--Comprobamos que la habilidad del agente está comprendida entre 0 y 9 
IF (:new.habilidad < 0 OR :new.habilidad > 9) THEN 
	RAISE_APPLICATION_ERROR(-20022, 'La habilidad del agente es errónea'); 
END IF; 

--Comprobamos que la categoria del agente está comprendida entre 0 y 2 
IF (:new.categoria < 0 OR :new.categoria > 2) THEN 
	RAISE_APPLICATION_ERROR(-20023, 'La categoría del agente es errónea'); 
END IF;

IF ((:new.categoria = 2 AND :new.oficina IS NULL) OR (:new.categoria = 2 AND :new.familia IS NOT NULL)) THEN 
	RAISE_APPLICATION_ERROR(-20026, 'Un agente con categoría 2 no debe pertenecer a ninguna familia y debe pertenecer a una oficina'); 
END IF;

IF ((:new.categoria = 1 AND :new.oficina IS NOT NULL) OR (:new.categoria = 2 AND :new.familia IS NULL)) THEN 
	RAISE_APPLICATION_ERROR(-20026, 'Un agente con categoría 1 no debe pertenecer a ninguna oficina y debe pertenecer a una familia'); 
END IF; 
 
-- Todos los agentes deben pertenecer a una oficina o a una familia pero nunca a ambas a la vez.
IF (:new.oficina IS NOT NULL and :new.familia IS NOT NULL) or ( :new.oficina IS   NULL and :new.familia IS  NULL)  THEN 
	RAISE_APPLICATION_ERROR(-20024, 'Los agentes deben pertenecer a una oficina o a una familia según su categoría'); 
END IF;
END; 
