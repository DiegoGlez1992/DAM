CREATE OR REPLACE PROCEDURE CambiarAgentesFamilia( id_FamiliaOrigen NUMBER, id_FamiliaDestino NUMBER)  IS 
	NumAgentes NUMBER (3,0); -- Almacenará el número de agentes cambiados.
	NombreFamiliaOrigen familias.nombre%TYPE;
	NombreFamiliaDestino familias.nombre%TYPE;
--Función auxiliar que nos devuelve True si la familia   existe y False  en caso contrario. También se podría realizar la comprobación con un cursor.
	FUNCTION existe(IdFamilia NUMBER) RETURN BOOLEAN IS 
		cont NUMBER; 
	BEGIN 
		cont:=0;
		select count(*) into cont from familias where idfamilia=identificador;
		IF (cont = 0) THEN 
			RETURN FALSE; 
		ELSE
			RETURN TRUE; 
		END IF; 
	END; 
BEGIN 
	--Comprobamos si las familias origen y destino son distintas.   
	IF ( id_FamiliaOrigen=id_FamiliaDestino) THEN
		RAISE_APPLICATION_ERROR(-20011, 'Las familias origen y destino no pueden ser iguales'); 
	END IF;
-- Comprobamos si existen las familias pasadas llamando a la función EXISTE
	IF NOT existe( id_FamiliaOrigen) THEN
		RAISE_APPLICATION_ERROR(-20012, 'La familia '||  id_FamiliaOrigen|| ' no existe');  
	END IF;
	IF NOT existe(id_FamiliaDestino) THEN
		RAISE_APPLICATION_ERROR(-20013, 'La familia '||  id_FamiliaDestino|| ' no existe');  
	END IF;
-- Obtenemos los datos para la visualización del mensaje
SELECT COUNT(*) INTO  NumAgentes  FROM agentes WHERE familia= id_FamiliaOrigen;	
-- Actualizamos a los agentes con la familia  destino 
IF NumAgentes>0 then
    UPDATE agentes SET familia =  id_FamiliaDestino WHERE familia = id_FamiliaOrigen; 	
    
    -- Visualizamos el mensaje informativo
    SELECT nombre into  NombreFamiliaOrigen FROM familias where identificador= id_FamiliaOrigen;
    SELECT nombre into  NombreFamiliaDestino FROM familias where identificador=  id_FamiliaDestino;
    DBMS_OUTPUT.PUT_LINE(' Se han trasladado '||NumAgentes||'  agentes de la familia  '|| NombreFamiliaOrigen || ' a la familia '||  NombreFamiliaDestino);
    COMMIT; 
ELSE
    DBMS_OUTPUT.PUT_LINE(' La familia  '|| NombreFamiliaOrigen || ' no tiene agentes asociados');
END IF;
-- controlamos errores con excepciones
EXCEPTION
		WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE(' Se ha producido el error ' ||SQLCODE|| ' correspondiente a '|| SUBSTR(SQLERRM,1,200));
            ROLLBACK;
END CambiarAgentesFamilia; 