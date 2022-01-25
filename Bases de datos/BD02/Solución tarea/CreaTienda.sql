-- Crea la tabla Familia
 ---------------------------------------------------------------------------------
 CREATE TABLE FAMILIA(
 Codfamilia number(3),
 Denofamilia varchar2(50) unique not null,
 CONSTRAINT pk_codfamilia PRIMARY KEY (Codfamilia),
 CONSTRAINT ck_familia_Deno CHECK(length(ltrim(rtrim(Denofamilia)))>0));
 -----------------------------------------------------------------------------------
-- crea la tabla Producto
 CREATE TABLE PRODUCTO(
 Codproducto number(5),
 Denoproducto varchar2(20) not null,
 Descripcion varchar2(100),
 PrecioBase number(8,2) not null,
 PorcReposicion number(3),
 UnidadesMinimas number(4) not null,
 Codfamilia number(3) not null,	  
 CONSTRAINT ck_producto_Deno CHECK(length(ltrim(rtrim(Denoproducto)))>0),
 CONSTRAINT pk_codproducto PRIMARY KEY(Codproducto),
 CONSTRAINT chk_preciobase check (PrecioBase >0),
 CONSTRAINT fk_codfamilia FOREIGN KEY (Codfamilia) REFERENCES FAMILIA(Codfamilia),
 CONSTRAINT chk_PorcReposicion CHECK (PorcReposicion >0),
 CONSTRAINT chk_UnidadesMinimas CHECK (UnidadesMinimas>0));


 -- crea la tabla TIENDA
 --------------------------------------------------
 CREATE TABLE TIENDA(
 Codtienda number(3),
 Denotienda varchar2(20) NOT NULL,
 Telefono CHAR(11),
 CodigoPostal CHAR(5) NOT NULL,
 Provincia varchar2(5) NOT NULL,
 CONSTRAINT Pk_codtienda PRIMARY KEY (Codtienda),
 CONSTRAINT ck_tienda_Deno CHECK(length(ltrim(rtrim(Denotienda)))>0));
 -------------------------------------------------------------
 -- crea la tabla STOCK
 CREATE TABLE STOCK(
 Codtienda number(3) NOT NULL,
 Codproducto number(5) NOT NULL,
 Unidades number(6) NOT NULL,
 CONSTRAINT chk_Unidades CHECK (Unidades >=0),
 CONSTRAINT FK_codtienda FOREIGN KEY (Codtienda) REFERENCES tienda(Codtienda),
 CONSTRAINT FK_codproducto FOREIGN KEY (codproducto) REFERENCES producto(codproducto),
 CONSTRAINT PK_stock PRIMARY KEY (Codtienda,Codproducto));