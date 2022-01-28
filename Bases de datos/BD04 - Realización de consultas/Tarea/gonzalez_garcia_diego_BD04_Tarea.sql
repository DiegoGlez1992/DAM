/*1. Obtener los nombres y salarios de los empleados con más de 1000 euros de salario por orden alfabético.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos, SALARIO
FROM EMPLEADO
WHERE SALARIO > 1000
ORDER BY NOMBRE;

/*2. Obtener el nombre de los empleados cuya comisión es superior al 20% de su salario.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE COMISION>0.2*SALARIO;

/*3. Obtener el código de empleado, código de departamento, nombre y sueldo total en pesetas de aquellos empleados cuyo sueldo total (salario más comisión) supera los 1800 euros. Presentarlos ordenados por código de departamento y dentro de éstos por orden alfabético.*/
SELECT CODEMPLE AS Codigo_Empleado, CODDPTO AS Codigo_Departamento, NOMBRE, APE1 || ' ' ||APE2 AS Apellidos, (SALARIO + NVL(COMISION, 0))*166 AS Sueldo_Total_Ptas
FROM EMPLEADO
WHERE (SALARIO + NVL(COMISION, 0)) > 1800
ORDER BY CODDPTO, NOMBRE;

/*4. Obtener por orden alfabético los nombres de empleados cuyo salario igualen o superen en más de un 5% al salario de la empleada ‘MARIA JAZMIN’.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE SALARIO >= (SELECT SALARIO FROM EMPLEADO WHERE NOMBRE = 'MARIA' AND APE1 = 'JAZMIN')*1.05
ORDER BY NOMBRE;

/*5. Obtener un listado ordenado por años en la empresa con los nombres, y apellidos de los empleados y los años de antigüedad en la empresa.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos, TRUNC(MONTHS_BETWEEN(SYSDATE, FECHAINGRESO)/12, 0) AS Antiguedad
FROM EMPLEADO
ORDER BY Antiguedad DESC;

/*6. Obtener el nombre de los empleados que trabajan en un departamento con presupuesto superior a 50.000 euros. Hay que usar predicado cuantificado.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE CODDPTO = SOME(SELECT CODDPTO FROM DPTO WHERE PRESUPUESTO > 50000);

/*7. Obtener los nombres y apellidos de empleados que más cobran en la empresa. Considerar el salario más la comisión.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE (SALARIO + NVL(COMISION, 0)) = (SELECT MAX(SALARIO + NVL(COMISION, 0)) FROM EMPLEADO);

/*8. Obtener en orden alfabético los nombres de empleado cuyo salario es inferior al mínimo de los empleados del departamento 1.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE SALARIO < (SELECT MIN(SALARIO) FROM EMPLEADO WHERE CODDPTO = 1)
ORDER BY NOMBRE;

/*9. Obtener los nombre de empleados que trabajan en el departamento del cuál es jefe el empleado con código 1.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE CODDPTO = (SELECT CODDPTO FROM DPTO WHERE CODEMPLEJEFE=1);

/*10. Obtener los nombres de los empleados cuyo primer apellido empiece por las letras p, q, r, s.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE LOWER(APE1) LIKE 'p%' OR LOWER(APE1) LIKE 'q%' OR LOWER(APE1) LIKE 'r%' OR LOWER(APE1) LIKE 's%';

/*11. Obtener los empleados cuyo nombre de pila contenga el nombre JUAN.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE UPPER(NOMBRE) LIKE '%JUAN%';

/*12. Obtener los nombres de los empleados que viven en ciudades en las que hay algún centro de trabajo.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE UPPER(LOCALIDAD) IN(SELECT UPPER(LOCALIDAD) FROM CENTRO);

/*13. Obtener el nombre del jefe de departamento que tiene mayor salario de entre los jefes de departamento.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO JOIN DPTO ON (EMPLEADO.CODEMPLE = DPTO.CODEMPLEJEFE)
WHERE EMPLEADO.CODEMPLE = CODEMPLEJEFE
AND SALARIO = (SELECT MAX(SALARIO) FROM EMPLEADO);

/*14. Obtener en orden alfabético los salarios y nombres de los empleados cuyo salario sea superior al 60% del máximo salario de la empresa.*/
SELECT SALARIO, NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE SALARIO > (SELECT MAX(SALARIO)*0.6 FROM EMPLEADO)
ORDER BY NOMBRE;

/*15. Obtener en cuántas ciudades distintas viven los empleados.*/
SELECT LOCALIDAD
FROM EMPLEADO
GROUP BY LOCALIDAD;

/*16. El nombre y apellidos del empleado que más salario cobra.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE SALARIO = (SELECT MAX(SALARIO) FROM EMPLEADO);

/*17. Obtener las localidades y número de empleados de aquellas en las que viven más de 3 empleados.*/
SELECT LOCALIDAD, COUNT(CODEMPLE) AS Num_Empleados
FROM EMPLEADO
GROUP BY LOCALIDAD
HAVING COUNT(LOCALIDAD)>3;

/*18. Obtener para cada departamento cuántos empleados trabajan, la suma de sus salarios y la suma de sus comisiones para aquellos departamentos en los que hay algún empleado cuyo salario es superior a 1700 euros.*/
SELECT DENOMINACION, COUNT(CODEMPLE) AS Num_Empleados, SUM(SALARIO) AS Total_Salarios, SUM(NVL(COMISION, 0)) AS Total_Comisiones
FROM EMPLEADO JOIN DPTO ON (EMPLEADO.CODDPTO = DPTO.CODDPTO)
WHERE DPTO.CODDPTO = SOME(SELECT CODDPTO FROM EMPLEADO WHERE SALARIO > 1700)
GROUP BY DENOMINACION;

/*19. Obtener el departamento que más empleados tiene.*/
SELECT DENOMINACION AS Departamento
FROM DPTO NATURAL JOIN (SELECT CODDPTO, Num_Empleados
                        FROM (SELECT CODDPTO, COUNT(CODEMPLE) AS Num_Empleados 
                              FROM EMPLEADO
                              GROUP BY CODDPTO))
WHERE Num_Empleados = (SELECT MAX(Num_Empleados) FROM (SELECT CODDPTO, COUNT(CODEMPLE) AS Num_Empleados 
                                                       FROM EMPLEADO
                                                       GROUP BY CODDPTO));

/*20. Obtener los nombres de todos los centros y los departamentos que se ubican en cada uno,así como aquellos centros que no tienen departamentos.*/
SELECT CENTRO.CODCENTRO, LOCALIDAD, DENOMINACION AS Departamento
FROM CENTRO LEFT OUTER JOIN DPTO ON (CENTRO.CODCENTRO = DPTO.CODCENTRO);

/*21. Obtener el nombre del departamento de más alto nivel, es decir, aquel que no depende de ningún otro.*/
SELECT DENOMINACION Departamento
FROM DPTO
WHERE CODDPTODEPENDE IS NULL;

/*22. Obtener todos los departamentos existentes en la empresa y los empleados (si los tiene) que pertenecen a él.*/
SELECT DENOMINACION Departamento, NOMBRE, APE1 || ' ' ||APE2 Apellidos
FROM EMPLEADO RIGHT OUTER JOIN DPTO ON (EMPLEADO.CODDPTO = DPTO.CODDPTO);

/*23. Obtener un listado en el que aparezcan todos los departamentos existentes y el departamento del cual depende,si depende de alguno.*/
SELECT DPTO.DENOMINACION Departamento, DPTODEPENDE.DENOMINACION Depende_de
FROM DPTO LEFT JOIN DPTO DPTODEPENDE ON (DPTO.CODDPTODEPENDE = DPTODEPENDE.CODDPTO);

/*24. Obtener un listado ordenado alfabéticamente donde aparezcan los nombres de los empleados y a continuación el literal "tiene comisión" si la tiene,y "no tiene comisión" si no la tiene.*/
SELECT NOMBRE || ' ' || APE1 || ' ' || APE2 "Nombre completo", DECODE(NVL(COMISION, 0), 0, 'No tiene comisión', 'Tiene comisión') Comisión
FROM EMPLEADO
ORDER BY NOMBRE;

/*25. Obtener un listado de las localidades en las que hay centros y no vive ningún empleado ordenado alfabéticamente.*/
SELECT LOCALIDAD
FROM CENTRO
WHERE UPPER(LOCALIDAD) NOT IN (SELECT UPPER(LOCALIDAD) FROM EMPLEADO GROUP BY LOCALIDAD)
ORDER BY LOCALIDAD;

/*26. Obtener un listado de las localidades en las que hay centros y además vive al menos un empleado ordenado alfabéticamente.*/
SELECT LOCALIDAD
FROM CENTRO
WHERE UPPER(LOCALIDAD) IN (SELECT UPPER(LOCALIDAD) FROM EMPLEADO GROUP BY LOCALIDAD)
ORDER BY LOCALIDAD;

/*27. Esta cuestión puntúa por 2. Se desea dar una gratificación por navidades en función de la antigüedad en la empresa siguiendo estas pautas:
 - Si lleva entre 1 y 5 años, se le dará 100 euros
 - Si lleva entre 6 y 10 años, se le dará 50 euros por año
 - Si lleva entre 11 y 20 años, se le dará 70 euros por año
 - Si lleva más de 21 años, se le dará 100 euros por año
Obtener un listado de los empleados, ordenado alfabéticamente,indicando cuánto le corresponde de gratificación.*/
SELECT NOMBRE || ' ' || APE1 || ' ' || APE2 "Nombre completo", Gratificacion
FROM EMPLEADO NATURAL JOIN (SELECT CODEMPLE, NOMBRE || ' ' || APE1 || ' ' || APE2 "Nombre completo", Antiguedad, 0 Gratificacion
                            FROM EMPLEADO NATURAL JOIN (SELECT CODEMPLE, TRUNC(MONTHS_BETWEEN(SYSDATE, FECHAINGRESO)/12, 0) Antiguedad
                                                        FROM EMPLEADO)
                            WHERE Antiguedad <1
                        UNION
                            SELECT CODEMPLE, NOMBRE || ' ' || APE1 || ' ' || APE2 "Nombre completo", Antiguedad, 100 Gratificacion
                            FROM EMPLEADO NATURAL JOIN (SELECT CODEMPLE, TRUNC(MONTHS_BETWEEN(SYSDATE, FECHAINGRESO)/12, 0) Antiguedad
                                                        FROM EMPLEADO)
                            WHERE Antiguedad BETWEEN 1 AND 5
                        UNION
                            SELECT CODEMPLE, NOMBRE || ' ' || APE1 || ' ' || APE2 "Nombre completo", Antiguedad, Antiguedad*50 Gratificacion
                            FROM EMPLEADO NATURAL JOIN (SELECT CODEMPLE, TRUNC(MONTHS_BETWEEN(SYSDATE, FECHAINGRESO)/12, 0) Antiguedad
                                                        FROM EMPLEADO)
                            WHERE Antiguedad BETWEEN 6 AND 10
                        UNION
                            SELECT CODEMPLE, NOMBRE || ' ' || APE1 || ' ' || APE2 "Nombre completo", Antiguedad, Antiguedad*70 Gratificacion
                            FROM EMPLEADO NATURAL JOIN (SELECT CODEMPLE, TRUNC(MONTHS_BETWEEN(SYSDATE, FECHAINGRESO)/12, 0) Antiguedad
                                                        FROM EMPLEADO)
                            WHERE Antiguedad BETWEEN 11 AND 20
                        UNION
                            SELECT CODEMPLE, NOMBRE || ' ' || APE1 || ' ' || APE2 "Nombre completo", Antiguedad, Antiguedad*100 Gratificacion
                            FROM EMPLEADO NATURAL JOIN (SELECT CODEMPLE, TRUNC(MONTHS_BETWEEN(SYSDATE, FECHAINGRESO)/12, 0) Antiguedad
                                                        FROM EMPLEADO)
                            WHERE Antiguedad >= 21)
ORDER BY NOMBRE;

/*28. Obtener los nombres y apellidos de los empleados que no son jefes de departamento.*/
SELECT NOMBRE || ' ' || APE1 || ' ' || APE2 "Nombre completo"
FROM EMPLEADO
WHERE CODEMPLE IN (SELECT CODEMPLE FROM EMPLEADO
                   MINUS 
                   SELECT CODEMPLEJEFE FROM DPTO);