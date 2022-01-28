/*1. Obtener los nombres y salarios de los empleados con m�s de 1000 euros de salario por orden alfab�tico.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos, SALARIO
FROM EMPLEADO
WHERE SALARIO > 1000
ORDER BY NOMBRE;

/*2. Obtener el nombre de los empleados cuya comisi�n es superior al 20% de su salario.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE COMISION>0.2*SALARIO;

/*3. Obtener el c�digo de empleado, c�digo de departamento, nombre y sueldo total en pesetas de aquellos empleados cuyo sueldo total (salario m�s comisi�n) supera los 1800 euros. Presentarlos ordenados por c�digo de departamento y dentro de �stos por orden alfab�tico.*/
SELECT CODEMPLE AS Codigo_Empleado, CODDPTO AS Codigo_Departamento, NOMBRE, APE1 || ' ' ||APE2 AS Apellidos, (SALARIO + NVL(COMISION, 0))*166 AS Sueldo_Total_Ptas
FROM EMPLEADO
WHERE (SALARIO + NVL(COMISION, 0)) > 1800
ORDER BY CODDPTO, NOMBRE;

/*4. Obtener por orden alfab�tico los nombres de empleados cuyo salario igualen o superen en m�s de un 5% al salario de la empleada �MARIA JAZMIN�.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE SALARIO >= (SELECT SALARIO FROM EMPLEADO WHERE NOMBRE = 'MARIA' AND APE1 = 'JAZMIN')*1.05
ORDER BY NOMBRE;

/*5. Obtener un listado ordenado por a�os en la empresa con los nombres, y apellidos de los empleados y los a�os de antig�edad en la empresa.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos, TRUNC(MONTHS_BETWEEN(SYSDATE, FECHAINGRESO)/12, 0) AS Antiguedad
FROM EMPLEADO
ORDER BY Antiguedad DESC;

/*6. Obtener el nombre de los empleados que trabajan en un departamento con presupuesto superior a 50.000 euros. Hay que usar predicado cuantificado.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE CODDPTO = SOME(SELECT CODDPTO FROM DPTO WHERE PRESUPUESTO > 50000);

/*7. Obtener los nombres y apellidos de empleados que m�s cobran en la empresa. Considerar el salario m�s la comisi�n.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE (SALARIO + NVL(COMISION, 0)) = (SELECT MAX(SALARIO + NVL(COMISION, 0)) FROM EMPLEADO);

/*8. Obtener en orden alfab�tico los nombres de empleado cuyo salario es inferior al m�nimo de los empleados del departamento 1.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE SALARIO < (SELECT MIN(SALARIO) FROM EMPLEADO WHERE CODDPTO = 1)
ORDER BY NOMBRE;

/*9. Obtener los nombre de empleados que trabajan en el departamento del cu�l es jefe el empleado con c�digo 1.*/
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

/*12. Obtener los nombres de los empleados que viven en ciudades en las que hay alg�n centro de trabajo.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE UPPER(LOCALIDAD) IN(SELECT UPPER(LOCALIDAD) FROM CENTRO);

/*13. Obtener el nombre del jefe de departamento que tiene mayor salario de entre los jefes de departamento.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO JOIN DPTO ON (EMPLEADO.CODEMPLE = DPTO.CODEMPLEJEFE)
WHERE EMPLEADO.CODEMPLE = CODEMPLEJEFE
AND SALARIO = (SELECT MAX(SALARIO) FROM EMPLEADO);

/*14. Obtener en orden alfab�tico los salarios y nombres de los empleados cuyo salario sea superior al 60% del m�ximo salario de la empresa.*/
SELECT SALARIO, NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE SALARIO > (SELECT MAX(SALARIO)*0.6 FROM EMPLEADO)
ORDER BY NOMBRE;

/*15. Obtener en cu�ntas ciudades distintas viven los empleados.*/
SELECT LOCALIDAD
FROM EMPLEADO
GROUP BY LOCALIDAD;

/*16. El nombre y apellidos del empleado que m�s salario cobra.*/
SELECT NOMBRE, APE1 || ' ' ||APE2 AS Apellidos
FROM EMPLEADO
WHERE SALARIO = (SELECT MAX(SALARIO) FROM EMPLEADO);

/*17. Obtener las localidades y n�mero de empleados de aquellas en las que viven m�s de 3 empleados.*/
SELECT LOCALIDAD, COUNT(CODEMPLE) AS Num_Empleados
FROM EMPLEADO
GROUP BY LOCALIDAD
HAVING COUNT(LOCALIDAD)>3;

/*18. Obtener para cada departamento cu�ntos empleados trabajan, la suma de sus salarios y la suma de sus comisiones para aquellos departamentos en los que hay alg�n empleado cuyo salario es superior a 1700 euros.*/
SELECT DENOMINACION, COUNT(CODEMPLE) AS Num_Empleados, SUM(SALARIO) AS Total_Salarios, SUM(NVL(COMISION, 0)) AS Total_Comisiones
FROM EMPLEADO JOIN DPTO ON (EMPLEADO.CODDPTO = DPTO.CODDPTO)
WHERE DPTO.CODDPTO = SOME(SELECT CODDPTO FROM EMPLEADO WHERE SALARIO > 1700)
GROUP BY DENOMINACION;

/*19. Obtener el departamento que m�s empleados tiene.*/
SELECT DENOMINACION AS Departamento
FROM DPTO NATURAL JOIN (SELECT CODDPTO, Num_Empleados
                        FROM (SELECT CODDPTO, COUNT(CODEMPLE) AS Num_Empleados 
                              FROM EMPLEADO
                              GROUP BY CODDPTO))
WHERE Num_Empleados = (SELECT MAX(Num_Empleados) FROM (SELECT CODDPTO, COUNT(CODEMPLE) AS Num_Empleados 
                                                       FROM EMPLEADO
                                                       GROUP BY CODDPTO));

/*20. Obtener los nombres de todos los centros y los departamentos que se ubican en cada uno,as� como aquellos centros que no tienen departamentos.*/
SELECT CENTRO.CODCENTRO, LOCALIDAD, DENOMINACION AS Departamento
FROM CENTRO LEFT OUTER JOIN DPTO ON (CENTRO.CODCENTRO = DPTO.CODCENTRO);

/*21. Obtener el nombre del departamento de m�s alto nivel, es decir, aquel que no depende de ning�n otro.*/
SELECT DENOMINACION Departamento
FROM DPTO
WHERE CODDPTODEPENDE IS NULL;

/*22. Obtener todos los departamentos existentes en la empresa y los empleados (si los tiene) que pertenecen a �l.*/
SELECT DENOMINACION Departamento, NOMBRE, APE1 || ' ' ||APE2 Apellidos
FROM EMPLEADO RIGHT OUTER JOIN DPTO ON (EMPLEADO.CODDPTO = DPTO.CODDPTO);

/*23. Obtener un listado en el que aparezcan todos los departamentos existentes y el departamento del cual depende,si depende de alguno.*/
SELECT DPTO.DENOMINACION Departamento, DPTODEPENDE.DENOMINACION Depende_de
FROM DPTO LEFT JOIN DPTO DPTODEPENDE ON (DPTO.CODDPTODEPENDE = DPTODEPENDE.CODDPTO);

/*24. Obtener un listado ordenado alfab�ticamente donde aparezcan los nombres de los empleados y a continuaci�n el literal "tiene comisi�n" si la tiene,y "no tiene comisi�n" si no la tiene.*/
SELECT NOMBRE || ' ' || APE1 || ' ' || APE2 "Nombre completo", DECODE(NVL(COMISION, 0), 0, 'No tiene comisi�n', 'Tiene comisi�n') Comisi�n
FROM EMPLEADO
ORDER BY NOMBRE;

/*25. Obtener un listado de las localidades en las que hay centros y no vive ning�n empleado ordenado alfab�ticamente.*/
SELECT LOCALIDAD
FROM CENTRO
WHERE UPPER(LOCALIDAD) NOT IN (SELECT UPPER(LOCALIDAD) FROM EMPLEADO GROUP BY LOCALIDAD)
ORDER BY LOCALIDAD;

/*26. Obtener un listado de las localidades en las que hay centros y adem�s vive al menos un empleado ordenado alfab�ticamente.*/
SELECT LOCALIDAD
FROM CENTRO
WHERE UPPER(LOCALIDAD) IN (SELECT UPPER(LOCALIDAD) FROM EMPLEADO GROUP BY LOCALIDAD)
ORDER BY LOCALIDAD;

/*27. Esta cuesti�n punt�a por 2. Se desea dar una gratificaci�n por navidades en funci�n de la antig�edad en la empresa siguiendo estas pautas:
 - Si lleva entre 1 y 5 a�os, se le dar� 100 euros
 - Si lleva entre 6 y 10 a�os, se le dar� 50 euros por a�o
 - Si lleva entre 11 y 20 a�os, se le dar� 70 euros por a�o
 - Si lleva m�s de 21 a�os, se le dar� 100 euros por a�o
Obtener un listado de los empleados, ordenado alfab�ticamente,indicando cu�nto le corresponde de gratificaci�n.*/
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