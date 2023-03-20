
CREATE DATABASE IF NOT EXISTS idiomas;
USE idiomas;

CREATE TABLE profesores (
  codProfe varchar(4) NOT NULL,
  nombre varchar(50) DEFAULT NULL,
  apellido varchar(50) DEFAULT NULL,
  departamento varchar(50) DEFAULT NULL,
  sueldomes decimal(6,2) DEFAULT NULL,
  cargo varchar(50) DEFAULT NULL
) ENGINE=InnoDB;

INSERT INTO profesores (codProfe, nombre, apellido, departamento, sueldomes, cargo) VALUES
('P001', 'Isabel', 'González', 'Inglés', '1500.00', 'Profesor titular'),
('P002', 'Antonio', 'López', 'Francés', '1550.00', 'Profesor titular'),
('P003', 'Ana', 'Gracia', 'Italiano', '1500.00', 'Profesor titular'),
('P004', 'Pedro', 'Pérez', 'Inglés', '1660.00', 'Jefe Departamento'),
('P005', 'Rosa', 'Pita', 'Francés', '1660.00', 'Jefe Departamento'),
('P006', 'Marta', 'Salazar', 'Italiano', '1660.00', 'Jefe Departamento'),
('P007', 'Jose', 'Segundo', 'Inglés', '1101.25', 'Auxiliar'),
('P008', 'Ainhoa', 'Sanz', 'Francés', '1101.25', 'Auxiliar'),
('P009', 'Felipe', 'Hermoso', 'Italiano', '1101.25', 'Auxiliar');

CREATE TABLE tutorias (
  idTutoria varchar(4) NOT NULL,
  profesor varchar(4) DEFAULT NULL,
  curso varchar(50) DEFAULT NULL,
  diaSemana enum('Lunes','Martes','Miércoles','Jueves','Viernes') DEFAULT NULL,
  horaTutoria time DEFAULT NULL
) ENGINE=InnoDB;

INSERT INTO tutorias (idTutoria, profesor, curso, diaSemana, horaTutoria) VALUES
('0001', 'P001', 'Nivel básico', 'Lunes', '17:00:00'),
('0002', 'P001', 'Nivel intermedio', 'Lunes', '19:00:00'),
('0003', 'P002', 'Nivel básico', 'Martes', '19:00:00'),
('0004', 'P002', 'Nivel intermedio', 'Lunes', '19:00:00'),
('0005', 'P003', 'Nivel básico', 'Miércoles', '17:00:00'),
('0006', 'P003', 'Nivel básico', 'Miércoles', '19:00:00'),
('0007', 'P004', 'Nivel avanzado', 'Jueves', '19:00:00'),
('0008', 'P004', 'Nivel básico', 'Jueves', '17:00:00'),
('0009', 'P005', 'Nivel intermedio', 'Viernes', '17:00:00'),
('0010', 'P005', 'Nivel básico', 'Martes', '17:00:00'),
('0011', 'P006', 'Nivel intermedio', 'Martes', '17:00:00'),
('0012', 'P006', 'Nivel intermedio', 'Miércoles', '17:00:00'),
('0013', 'P007', 'Club conversación', 'Miércoles', '19:00:00'),
('0014', 'P007', 'Club conversación', 'Jueves', '19:00:00'),
('0015', 'P008', 'Club conversación', 'Viernes', '19:00:00'),
('0016', 'P008', 'Club escritura', 'Viernes', '19:00:00'),
('0017', 'P009', 'Club escritura', 'Lunes', '19:00:00'),
('0018', 'P009', 'Club conversación', 'Jueves', '19:00:00'),
('0019', 'P001', 'Nivel intermedio', 'Jueves', '17:00:00'),
('0020', 'P004', 'Nivel básico', 'Lunes', '17:00:00'),
('0021', 'P004', 'Nivel avanzado', 'Martes', '17:00:00');


ALTER TABLE profesores
  ADD PRIMARY KEY (codProfe);

ALTER TABLE tutorias
  ADD PRIMARY KEY (idTutoria),
  ADD KEY fk (profesor);


ALTER TABLE tutorias
  ADD CONSTRAINT fk FOREIGN KEY (profesor) REFERENCES profesores (codProfe)


