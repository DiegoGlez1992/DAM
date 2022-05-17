/*2. Inserta varios registros en la tabla PROFESORADO.*/
/*CODIGO=2, NOMBRE= MARIA LUISA, APELLIDOS= FABRE BERDUN, DNI= 51083099F, ESPECIALIDAD= TECNOLOGIA, FECHA_NAC= 31/03/1975, ANTIGUEDAD= 4*/
INSERT INTO PROFESORADO (codigo, nombre, apellidos, dni, especialidad, fecha_nac, antiguedad)
VALUES (2, 'MARIA LUISA', 'FABRE BERDUN', '51083099F', 'TECNOLOGIA', '31/03/1975', 4);

/*CODIGO=3, NOMBRE= JAVIER, APELLIDOS= JIMENEZ HERNANDO, ESPECIALIDAD= LENGUA, FECHA_NAC= 04/05/1969, ANTIGUEDAD= 10*/
INSERT INTO PROFESORADO (codigo, nombre, apellidos, especialidad, fecha_nac, antiguedad)
VALUES (3, 'JAVIER', 'JIMENEZ HERNANDO', 'LENGUA', '04/05/1969', 10);

/*CODIGO=4, NOMBRE= ESTEFANIA, APELLIDOS= FERNANDEZ MARTINEZ, DNI= 19964324W, ESPECIALIDAD= INGLES, FECHA_NAC= 22/06/1973, ANTIGUEDAD= 5*/
INSERT INTO PROFESORADO (codigo, nombre, apellidos, dni, especialidad, fecha_nac, antiguedad)
VALUES (4, 'ESTEFANIA', 'FERNANDEZ MARTINEZ', '19964324W', 'INGLES', '22/06/1973', 5);

/*CODIGO=5, NOMBRE= JOSE M., APELLIDOS= ANERO PAYAN*/
INSERT INTO PROFESORADO (codigo, nombre, apellidos)
VALUES (5, 'JOSE M.', 'ANERO PAYAN');


/*4. Modifica el registro de la profesora "ESTEFANIA" y cambia su fecha de nacimiento a "22/06/1974" y la antigüedad a 4.*/
UPDATE PROFESORADO SET fecha_nac = '22/06/1974', antiguedad = 4 WHERE nombre = 'ESTEFANIA';


/*5. Modifica las antigüedades de todos los profesores y profesoras incrementándolas en 1 en todos los registros.*/
UPDATE PROFESORADO SET antiguedad = NVL(antiguedad,0)+1;


/*7. Elimina, de la tabla ALUMNADO, aquellos registros asociados al curso con código 3.*/
DELETE FROM ALUMNADO WHERE cod_curso = 3;


/*8. Inserta los registros de la tabla ALUMNADO_NUEVO en la tabla ALUMNADO.*/
INSERT INTO (SELECT nombre, apellidos, sexo, fecha_nac FROM ALUMNADO)
SELECT * FROM ALUMNADO_NUEVO;


/*9. En la tabla CURSOS, actualiza el campo Max_Alumn del registro del curso con código 2, asignándole el valor correspondiente al número total de alumnos y alumnas que hay en la tabla ALUMNADO y que tienen asignado ese mismo curso.*/
UPDATE CURSOS SET max_alumn = (SELECT COUNT(cod_curso) FROM ALUMNADO WHERE cod_curso = 2)
WHERE codigo = 2;


/*10. Elimina de la tabla ALUMNADO todos los registros asociados a los cursos que imparte la profesora cuyo nombre es "NURIA".*/
DELETE FROM ALUMNADO WHERE cod_curso IN (SELECT codigo FROM CURSOS WHERE cod_profe = (SELECT codigo FROM PROFESORADO WHERE nombre = 'NURIA'));
/*Otra forma de hacerlo*/
DELETE FROM (SELECT cod_curso, cod_profe FROM ALUMNADO, CURSOS WHERE CURSOS.codigo=ALUMNADO.cod_curso)
WHERE cod_profe = (SELECT codigo FROM PROFESORADO WHERE nombre = 'NURIA');
