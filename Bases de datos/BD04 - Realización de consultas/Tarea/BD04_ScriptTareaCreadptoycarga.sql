-- Conectamos como administradores con conn sys as sysdba
conn sys as sysdba
-- Creamos un usario llamado c##dpto y le concedemos los permisos.
create user c##dpto identified by dpto default tablespace users;
grant connect, resource,DBA to c##dpto;
CONNECT c##dpto/dpto;

CREATE TABLE CENTRO(
codcentro number(2) not null,
direccion varchar2(30) not null,
localidad varchar2(20) not null);
insert into centro values (01,'Rambla Nova','Tarragona');
insert into centro values (02,'Alcala','Madrid');
insert into centro values (03,'Sierpes','Sevilla');
alter table centro add constraints pk_codcentro primary key (codcentro);
CREATE TABLE DPTO(
coddpto number(2) not null,
denominacion varchar2(20) not null,
codcentro number(2) not null,
coddptodepende number(2),
codemplejefe number(3) not null,
tipo char(1) not null ,
presupuesto number(8,2) not null);
insert into dpto values (01,'DIRECCIÓN',01,NULL,01,'P',100000);
insert into dpto values (02,'ADMINISTRACION',01,01,03,'F',50000);
insert into dpto values (03,'RECURSOS HUMANOS',01,01,05,'P',30000);
insert into dpto values (05,'CENTRAL COMERCIAL',01,01,07,'P',100000);
insert into dpto values (06,'COMERCIAL CENTRO',02,05,02,'F',5000);
insert into dpto values (07,'COMERCIAL SUR',03,05,04,'F',40000);
create table empleado(
codemple number(3) not null,
ape1 varchar2(20) not null,
ape2 varchar2(20) not null,
nombre varchar2(15) not null,
direccion varchar2(25) not null,
localidad varchar(25) not null,
telef varchar(9),
coddpto number(2) not null,
codcate number(2) not null,
fechaingreso date not null,
salario number(6,2) not null,
comision number(6,2)
);
insert into empleado values (01,'LOPEZ','GARCIA','ANA','C/ ANAS','MADRID',666666666,
01,01,'01/02/2000',3000,NULL);
insert into empleado values (02,'FERNANDEZ','MORON','JUAN','C/FUENTE','TARRAGONA',
7777777,01,02,'01/02/2002',2000,NULL);
insert into empleado values (03,'CORTES','LOPEZ','ANGEL','C/CIFUENTES','BARACALDO',
888888,02,01,'01/03/2003',2000,NULL);
insert into empleado values (04,'SANCHEZ','LUZ','FABIOLA','C/CARDON','SEVILLA',
99999999,03,02,'21/05/2001',2500,NULL);
insert into empleado values (05,'RAJOY','AZNAR','PAZ','C/MAR','JAEN',88888888,03,01,
'23/02/2000',2000,130);
insert into empleado values (06,'ZAPATERO','GALLARDON','ANGUSTIAS','C/SUR','MADRID',
78787878,05,03,'01/02/2000',2000,NULL);
insert into empleado values (07,'FLOR','LUZ','BLANCA','C/TECLA','SEVILLA',7777777,06,
01,'01/02/2000',3000,130);
insert into empleado values (08,'ROS','SANTON','ALFONSO','C/ LUZ','MADRID',888888,07,
03,'01/02/2003',2000,NULL);
insert into empleado values (09,'LOPEZ','ITURRIALDE','GANDI','C/OASIS','TARRAGONA',
777777,05,01,'01/02/1998',1500,210);
insert into empleado values (10,'JAZMIN','EXPOSITO','MARIA','C/MANDRAGORA','MADRID',
888888,05,03,'01/03/2001',1000,200);

alter table dpto add constraints pk_coddpto primary key (coddpto);
alter table dpto add constraints fk_codcentro foreign key(codcentro) references
centro(codcentro);
alter table dpto add constraints fk_coddptodepende foreign key(coddptodepende)
references dpto(coddpto);
alter table dpto add constraints chk_tipo check(tipo in('P','F'));
alter table empleado add constraints pk_codemple primary key(codemple);

alter table dpto add constraints fk_codemplejefe foreign key (codemplejefe)
references empleado(codemple);

alter table empleado add constraints fk_coddpto foreign key(coddpto) references dpto(
coddpto);

create table categoria(
codcate number(2) not null,
denom varchar2(20) not null,
julio number(6,2) not null,
diciembre number(6,2) not null
);
insert into categoria values (1,'ALTOS DIRECTIVOS',6000,5000);
insert into categoria values (2,'DIRECTIVOS',3000,2000);
insert into categoria values (3,'ADMINISTRATIVOS',2000,1500);

alter table categoria add constraints pk_codcate primary key(codcate);
alter table empleado add constraints fk_codcate foreign key(codcate) references
categoria(codcate);
