-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 07-01-2020 a las 22:57:04
-- Versión del servidor: 10.4.8-MariaDB
-- Versión de PHP: 7.3.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `fabrica`
--
CREATE DATABASE IF NOT EXISTS `fabrica` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `fabrica`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `articulos`
--

DROP TABLE IF EXISTS `articulos`;
CREATE TABLE `articulos` (
  `Id_articulo` int(11) NOT NULL,
  `Descripcion` varchar(45) NOT NULL,
  `Precio` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `articulos`
--

INSERT INTO `articulos` (`Id_articulo`, `Descripcion`, `Precio`) VALUES
(1, 'Anclaje andamios', '16.00'),
(2, 'Angulo PVC Negro 15mm x 15mm 1M ', '15.75'),
(3, 'Taco clavo', '2.57'),
(4, 'Taco con tornillo 100 Piezas', '24.81'),
(5, 'Taco 6x50 100 Piezas', '9.32'),
(6, 'Clavo 65mm x 200 mm', '1.00'),
(7, 'Tubo Hierro Forjado 70 x 105', '1.54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

DROP TABLE IF EXISTS `clientes`;
CREATE TABLE `clientes` (
  `ID_Cliente` int(11) NOT NULL,
  `Nombre` varchar(80) NOT NULL,
  `Direccion` varchar(80) NOT NULL,
  `Ciudad` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `clientes`
--

INSERT INTO `clientes` (`ID_Cliente`, `Nombre`, `Direccion`, `Ciudad`) VALUES
(1, 'Ferretería Almendralejo S.L', 'Carretera de Badajoz, nº 181', 'Almendralejo'),
(2, 'Ferretería La Madrila S.L', 'Parque del Príncipe, nº 18', 'Cáceres'),
(3, 'Ferretería Lavapies', 'Calle Canarias, Nº 89', 'Madrid'),
(4, 'Ferretería Ramirez', 'Calle Pizarro, nº 78', 'Sevilla'),
(5, 'Bricos Hierro', 'Calle La Piedad, nº 5', 'Salamanca'),
(6, 'Hierros Salmantina', 'Calle Gran Vía, nº 13', 'Madrid'),
(7, 'Armaduras de Hierro', 'Avenida Almonte, nº 59', 'Santander');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_pedidos`
--

DROP TABLE IF EXISTS `detalle_pedidos`;
CREATE TABLE `detalle_pedidos` (
  `ID_Venta` int(11) NOT NULL,
  `ID_Articulo` int(11) NOT NULL,
  `Precio` decimal(10,2) NOT NULL,
  `Unidades` int(11) NOT NULL,
  `ID_Pedido` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `detalle_pedidos`
--

INSERT INTO `detalle_pedidos` (`ID_Venta`, `ID_Articulo`, `Precio`, `Unidades`, `ID_Pedido`) VALUES
(1, 1, '16.00', 102, 1),
(2, 2, '15.75', 405, 1),
(3, 7, '1.54', 1080, 1),
(4, 1, '16.00', 395, 2),
(5, 3, '2.57', 99, 2),
(6, 6, '1.00', 125, 2),
(7, 4, '24.00', 580, 3),
(8, 5, '9.32', 76, 3),
(9, 6, '3.00', 87, 4),
(10, 7, '1.54', 8, 4),
(11, 7, '1.54', 10, 5),
(12, 3, '2.57', 6, 5),
(13, 1, '1.00', 5, 4),
(14, 7, '1.54', 5, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `emails`
--

DROP TABLE IF EXISTS `emails`;
CREATE TABLE `emails` (
  `Id_cliente` int(11) NOT NULL,
  `Email` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `emails`
--

INSERT INTO `emails` (`Id_cliente`, `Email`) VALUES
(1, 'piedad.coranado@almenfer.es'),
(2, 'madrila.caceres@lamadrilacaceres,es'),
(3, 'ferlava@lavapiesferr.es'),
(4, 'triana@ferramirez.es'),
(6, 'ana.tejuan@salmantina.es'),
(6, 'misabel.tejuan@salmantina.es'),
(7, 'manuel.roldan@armadurashierro.com'),
(7, 'rrhh@armadurashierro.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
CREATE TABLE `pedidos` (
  `ID_Pedido` int(11) NOT NULL,
  `ID_Cliente` int(11) DEFAULT NULL,
  `Fecha_Pedido` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`ID_Pedido`, `ID_Cliente`, `Fecha_Pedido`) VALUES
(1, 1, '2019-12-09 10:17:00'),
(2, 1, '2019-01-01 08:06:00'),
(3, 2, '2019-11-12 13:13:00'),
(4, 2, '2018-01-02 17:09:00'),
(5, 3, '2018-11-04 10:09:00'),
(6, 4, '2017-11-04 15:05:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `telefonos`
--

DROP TABLE IF EXISTS `telefonos`;
CREATE TABLE `telefonos` (
  `Id_Cliente` int(11) NOT NULL,
  `Telefono` varchar(12) NOT NULL,
  `Persona_Contacto` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `telefonos`
--

INSERT INTO `telefonos` (`Id_Cliente`, `Telefono`, `Persona_Contacto`) VALUES
(1, '924660001', 'Piedad Espronceda '),
(2, '924314959', 'David Rodríguez'),
(3, '91333444', 'José Luis Pérez'),
(4, '955123987', 'Ramón Montero'),
(4, '955123988', 'Francisco Ramírez'),
(6, '91340340', 'María Isabel Tejuán'),
(6, '91341341', 'Ana María Tejuán'),
(7, '942661570', 'Manuel Roldán'),
(7, '942661571', 'Ana Roldán');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `articulos`
--
ALTER TABLE `articulos`
  ADD PRIMARY KEY (`Id_articulo`);

--
-- Indices de la tabla `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`ID_Cliente`);

--
-- Indices de la tabla `detalle_pedidos`
--
ALTER TABLE `detalle_pedidos`
  ADD PRIMARY KEY (`ID_Venta`),
  ADD KEY `ID_PEDIDO_FK` (`ID_Pedido`),
  ADD KEY `ID_ARTICULO_FK` (`ID_Articulo`);

--
-- Indices de la tabla `emails`
--
ALTER TABLE `emails`
  ADD PRIMARY KEY (`Id_cliente`,`Email`);

--
-- Indices de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD PRIMARY KEY (`ID_Pedido`),
  ADD KEY `ID_Cliente` (`ID_Cliente`);

--
-- Indices de la tabla `telefonos`
--
ALTER TABLE `telefonos`
  ADD PRIMARY KEY (`Id_Cliente`,`Telefono`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `articulos`
--
ALTER TABLE `articulos`
  MODIFY `Id_articulo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `clientes`
--
ALTER TABLE `clientes`
  MODIFY `ID_Cliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `detalle_pedidos`
--
ALTER TABLE `detalle_pedidos`
  MODIFY `ID_Venta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  MODIFY `ID_Pedido` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `detalle_pedidos`
--
ALTER TABLE `detalle_pedidos`
  ADD CONSTRAINT `ID_ARTICULO_FK` FOREIGN KEY (`ID_Articulo`) REFERENCES `articulos` (`Id_articulo`) ON UPDATE CASCADE,
  ADD CONSTRAINT `ID_PEDIDO_FK` FOREIGN KEY (`ID_Pedido`) REFERENCES `pedidos` (`ID_Pedido`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `emails`
--
ALTER TABLE `emails`
  ADD CONSTRAINT `ID_cliente_email_FK` FOREIGN KEY (`Id_cliente`) REFERENCES `clientes` (`ID_Cliente`);

--
-- Filtros para la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD CONSTRAINT `ID_CLIENTES_FK` FOREIGN KEY (`ID_Cliente`) REFERENCES `clientes` (`ID_Cliente`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `telefonos`
--
ALTER TABLE `telefonos`
  ADD CONSTRAINT `ID_Cliente_FK` FOREIGN KEY (`Id_Cliente`) REFERENCES `clientes` (`ID_Cliente`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
