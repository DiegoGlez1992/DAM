/* 
ACTIVIDAD 2 - APARTADO B.
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
    
Algunas de las restricciones implementadas con el disparador se pueden incorporar
a la definición del esquema de la tabla utilizando el Lenguaje de Definición de 
Datos (Check,Unique,..). Identifica cuáles son y con qué tipo de restricciones 
las implementarías.
*/

-- La longitud de la clave de un agente no puede ser inferior a 6
ALTER TABLE agentes ADD CONSTRAINT CK_LongClave CHECK (LENGTH(LTRIM(RTRIM(clave))) >= 6);

-- La habilidad de un agente debe estar comprendida entre 0 y 9
ALTER TABLE agentes ADD CONSTRAINT CK_Habilidad CHECK (habilidad BETWEEN 0 AND 9);

-- La categoría de un agente sólo puede ser igual a 0, 1 o 2
-- Si un agente tiene categoría 1 no puede pertenecer a ninguna oficina y debe pertenecer a una familia.
-- Si un agente tiene categoría 2 no puede pertenecer a ninguna familia y debe pertenecer a una oficina.
ALTER TABLE agentes ADD CONSTRAINT CK_Categoria CHECK ((categoria = 0)
                                                    OR (categoria =1 AND oficina IS NULL AND familia IS NOT NULL)
                                                    OR (categoria = 2 AND familia IS NULL AND oficina IS NOT NULL));

-- Todos los agentes deben pertenecer a una oficina o a una familia pero nunca a ambas a la vez
ALTER TABLE agentes ADD CONSTRAINT CK_Pertenecer CHECK ((familia IS NULL AND oficina IS NOT NULL) OR
                                                        (familia IS NOT NULL AND oficina IS NULL));