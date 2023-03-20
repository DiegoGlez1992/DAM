CREATE DATABASE IF NOT EXISTS alumnos; 
USE alumnos;

DROP TABLE IF EXISTS matriculas;
DROP TABLE IF EXISTS alumnos;

CREATE TABLE alumnos (
 DNI VARCHAR(9) NOT NULL,
 Nombre VARCHAR(50) NOT NULL,
 Apellidos VARCHAR(70) NOT NULL,
 Direccion VARCHAR(100) NOT NULL,
 FechaNac DATE NOT NULL, PRIMARY KEY (DNI)
) ENGINE=InnoDB;

INSERT INTO alumnos VALUES
('12345678A', 'José Alberto', 'González Pérez', 'C/Albahaca, nº14, 1ºD', '1986-07-15'),
('23456789B', 'Almudena', 'Cantero Verdemar', 'Avd/ Profesor Alvarado, n27, 8ºA', '1988-11-04'),
('14785236d', 'Martín', 'Díaz Jiménez', 'C/Luis de Gongora, nº2.', '1987-03-09'),
('96385274f', 'Lucas', 'Buendia Portes', 'C/Pintor Sorolla, nº 16, 4ºB', '1988-07-10');

CREATE TABLE matriculas (
 DNI VARCHAR(9) NOT NULL,
 NombreModulo VARCHAR(60) NOT NULL,
 Curso VARCHAR(5) NOT NULL,
 Nota DOUBLE NOT NULL, PRIMARY KEY (DNI,NombreModulo,Curso), CONSTRAINT DNI_FK FOREIGN KEY (DNI) REFERENCES alumnos (DNI) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

INSERT INTO matriculas(DNI, NombreModulo, Curso, Nota) VALUES 
 ('12345678A', 'DAM', '21-22', 5),
 ('96385274f', 'AD', '21-22', 10),
 ('12345678A', 'AD', '21-22', 10),
 ('23456789B', 'PSP', '22-23', 5),
 ('14785236d', 'FOL', '22-23', 9),
 ('12345678A', 'PSP', '22-23', 7);
 