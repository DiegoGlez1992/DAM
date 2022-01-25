--1.- Obtener los nombres y salarios de los empleados con más de 1000 euros de salario por orden alfabético.

select nombre,ape1,salario from empleado where salario > 1000 order by 2,1;

--2.- Obtener el nombre de los empleados cuya comisión es superior al 20% de su salario.

select nombre,ape1,comision, salario from empleado where nvl(comision,0) > salario * 0.2;

--3.- Obtener el código de empleado, código de departamento, nombre y sueldo total en pesetas de aquellos empleados cuyo sueldo total (salario más comisión) supera los 1800 euros. Presentarlos ordenados por código de departamento y dentro de éstos por orden alfabético.

select coddpto,codemple,nombre,ape1,(salario+nvl(comision,0))*166.386 as sueldopts
from empleado where salario+nvl(comision,0)>1800
order by coddpto,nombre,ape1;

--4.- Obtener por orden alfabético los nombres de empleados cuyo salario igualen o superen en más de un 5% al salario de la empleada ‘MARIA JAZMIN’.

select nombre, ape1, salario from empleado
where salario >= (select salario * 1.05 from empleado
where ape1='JAZMIN' and nombre='MARIA')
order by 2,1 asc;

--5.- Obtener una listado ordenado por años en la empresa con los nombres, y apellidos de los empleados y los años de antigüedad en la empresa

SELECT nombre,ape1,ape2, months_between(sysdate,fechaingreso)/12 as "Antigüedad" from empleado order by 4 asc;

--6. Obtener el nombre de los empleados que trabajan en un departamento con presupuesto superior a 50.000 euros. Hay que usar predicado cuantificado

select ape1,nombre from empleado where coddpto=some(select coddpto from dpto where presupuesto>50000);

--De otra forma sin predicado cuantificado
select ape1,nombre from empleado where coddpto in (select coddpto from dpto where presupuesto>50000);

--7.-Obtener los nombres y apellidos de empleados que más cobran en la empresa.Considerar el salario más la comisión.

select ape1,nombre, nvl(comision,0)+salario from empleado where nvl(comision,0)+salario > =all(select nvl(comision,0)+salario from empleado);

--De otra forma sin predicado
select ape1, nombre,  nvl(comision,0)+salario from empleado where nvl(comision,0)+salario in (select max(nvl(comision,0)+salario ) from empleado);

--8.- Obtener en orden alfabético los nombres de empleado cuyo salario es inferior al mínimo de los empleados del departamento 1.

select nombre,ape1,salario from empleado where salario < all(select salario from empleado where coddpto=1) order by 2,1;

--De otra forma

select nombre,ape1,salario from empleado where salario < (select min(salario) from empleado where coddpto=1) order by 2,1;

--9.- Obtener los nombre de empleados que trabajan en el departamento del cuál es jefe el empleado con código 1.

select nombre,ape1 from empleado where coddpto = some (select coddpto from dpto where codemplejefe=1);

--De otra forma

select nombre,ape1 from empleado where coddpto in (select coddpto from dpto where codemplejefe=1);

--10.- Obtener los nombres de los empleados cuyo primer apellido empiece por las letras p, q, r, s.

select nombre,ape1 from empleado where substr(ape1,1,1) between 'P' and 'S';

--De otra forma

select nombre,ape1 from empleado where substr(ape1,1,1) in ('P','Q','R','S');

--11.- Obtener los empleados cuyo nombre de pila contenga el nombre JUAN.

select nombre,ape1 from empleado where nombre like '%JUAN%';

--12.- Obtener los nombres de los empleados que viven en ciudades en las que hay algún centro de trabajo

select ape1, nombre from empleado where upper(localidad) in (select upper(localidad) from centro);

--13.- Obtener el nombre del jefe de departamento que tiene mayor salario de entre los jefes de departamento.

select nombre,ape1 from empleado where codemple in (select codemplejefe from dpto) and 
salario >= all (select salario from empleado where codemple in (select codemplejefe from dpto));

--De otra forma

select nombre,ape1 from empleado where codemple in (select codemplejefe from dpto) and 
salario =  (select Max(salario) from empleado where codemple in (select codemplejefe from dpto));

--14.- Obtener en orden alfabético los salarios y nombres de los empleados cuyo salario sea superior al 60% del máximo salario de la empresa.

select salario,nombre, ape1 from empleado where salario > (select 0.6* max(salario)  from empleado) order by ape1, nombre;

--15.- Obtener en cuántas ciudades distintas viven los empleados

select count(distinct localidad) from empleado;

--16.- El nombre y apellidos del empleado que más salario cobra

select nombre,ape1,ape2, salario from empleado where salario=(select MAX(salario) from empleado);

--De otra forma

select nombre,ape1,ape2, salario from empleado where salario>=all(select salario from empleado);

--17.- Obtener las localidades y número de empleados de aquellas en las que viven más de 3 empleados

select localidad,count(*) from empleado group by localidad having count(*) > 3 order by 2;

--18.- Obtener para cada departamento cuántos empleados trabajan, la suma de sus salarios y la suma de sus comisiones para aquellos departamento en los que hay algún empleado cuyo salario es superior a 1700 euros.

select coddpto, sum(salario) as salarioT, sum(nvl(comision,0)) as comisionT, count(codemple) as numEmple 
from empleado 
group by coddpto
having coddpto=some(select coddpto from empleado where salario>1700);

--De otra forma

select coddpto, sum(salario) as salarioT, sum(nvl(comision,0)) as comisionT, count(codemple) as numEmple 
from empleado 
where coddpto in (select distinct coddpto from empleado where salario>1700)
group by coddpto;


--19.- Obtener el departamento que más empleados tiene

select denominacion from dpto,empleado 
where empleado.coddpto=dpto.coddpto 
group by dpto.coddpto,denominacion 
having count(empleado.codemple)>=all(select count(codemple) from empleado group by coddpto);

--Ejercicios outer join, minus, union, interesect

--20.- Obtener los nombres de todos los centros y los departamentos que se ubican en cada uno,así como aquellos centros que no tienen departamentos.

select direccion as nomcentro,denominacion as dpto from centro tc,dpto td where tc.codcentro=td.codcentro(+) order by 1,2;

--o bien

select direccion as nomcentro,denominacion as dpto from centro tc left join dpto td on tc.codcentro=td.codcentro order by 1,2;

--21.- Obtener el nombre del departamento de más alto nivel, es decir, aquel que no depende de ningún otro.

select denominacion as nomdpto from dpto where coddptodepende is null;

--22.- Obtener todos los departamentos existentes en la empresa y los empleados (si los tiene) que pertenecen a él.

select denominacion,nombre,ape1,ape2 from dpto td left join empleado te on td.coddpto=te.coddpto order by 1;

--23.- Obtener un listado en el que aparezcan todos los departamentos existentes y el departamento del cual depende,si depende de alguno.

select td.denominacion as nomdpto,  nvl(A.denominacion,' ') as nomdptodelqdepnde from dpto td, dpto A
where A.coddpto(+)=td.coddptodepende;

--De otra forma

select dpt1.denominacion as nomdpto, nvl(dptdelqdepende.denominacion,' ') as nomdptodelqdepnde from dpto dpt1 left join dpto dptdelqdepende on dpt1.coddptodepende=dptdelqdepende.coddpto;

--24.-Obtener un listado ordenado alfabéticamente donde aparezcan los nombres de los empleados y a continuación el literal tiene comision si la tiene,y no tiene comisión si no la tiene.

Select nombre, ape1,ape2, 'tiene comision' from empleado where comision is not null
UNION
Select nombre, ape1,ape2, 'no tiene comision' from empleado where comision is null
order by 4,2;

--O bien con la función decode

Select nombre, ape1,ape2,decode(nvl(comision,0),0,'no tiene comision','tiene comision') from empleado order by 4,2;

--25.- Obtener un listado de las localidades en las que hay centros y no vive ningún empleado ordenado alfabéticamente.

select rtrim(ltrim(upper(tc.localidad))) from centro tc
minus
select rtrim(ltrim(upper(te.localidad))) from empleado te order by 1;

--De otra forma

select upper(tc.localidad) from centro tc where rtrim(ltrim(upper(tc.localidad))) not in (select distinct rtrim(ltrim(upper(te.localidad))) from empleado te);

--26.- Obtener un listado de las localidades en las que hay centros y además vive al menos un empleado ordenado alfabéticamente.

select distinct rtrim(ltrim(upper(localidad))) from centro
intersect
select distinct rtrim(ltrim(upper(localidad))) from empleado
order by 1 asc;

--De otra forma

select upper(tc.localidad) from centro tc where rtrim(ltrim(upper(tc.localidad))) in (select distinct rtrim(ltrim(upper(te.localidad))) from empleado te);


--27.- Esta cuestión puntúa por 3. Se desea dar una gratificación por navidades en función de la antigüedad en la empresa siguiendo estas pautas:
-- Si lleva entre 1 y 5 años, se le dará 100 euros
-- Si lleva entre 6 y 10 años, se le dará 50 euros por año
-- Si lleva entre 11 y 20 años, se le dará 70 euros por año
-- Si lleva más de 21 años, se le dará 100 euros por año
--Obtener un listado de los empleados,ordenado alfabéticamente,indicando cuánto le corresponde de gratificación.

--Con union

select nombre,ape1,ape2, 100 as Gratificacion from empleado where trunc((sysdate-fechaingreso)/365) between 1 and 5
union
select nombre,ape1,ape2, 50*trunc((sysdate-fechaingreso)/365) from empleado where trunc((sysdate-fechaingreso)/365) between 6 and 10
union
select nombre,ape1,ape2, 70*trunc((sysdate-fechaingreso)/365) from empleado where trunc((sysdate-fechaingreso)/365) between 11 and 20
union
select nombre,ape1,ape2, 100*trunc((sysdate-fechaingreso)/365) from empleado where (sysdate-fechaingreso)/365>=21
order by 2,3,1;

--Con case, os he añadido formas de cálculo para que tengáis las diferencias entre unas y otras

select nombre,ape1,ape2,fechaingreso,((sysdate-fechaingreso)/365),(MONTHS_BETWEEN(SYSDATE, fechaingreso)/12),EXTRACT(YEAR FROM SYSDATE)-EXTRACT(YEAR FROM fechaingreso),case when trunc((sysdate-fechaingreso)/365) between 1 and 5 then 100
            when trunc((sysdate-fechaingreso)/365) between 6 and 10 then 50*trunc((sysdate-fechaingreso)/365)
            when trunc((sysdate-fechaingreso)/365) between 11 and 20 then 70*trunc((sysdate-fechaingreso)/365)
            when trunc((sysdate-fechaingreso)/365) >=21 then 100*trunc((sysdate-fechaingreso)/365) end as gratificacion
            from empleado where trunc((sysdate-fechaingreso)/365) >=1
            order by 2,3,1;

--29.- Obtener a los nombres, apellidos de los empleados que no son jefes de departamento.

select nombre,ape1,ape2 from empleado where codemple not in (select codemplejefe from dpto);