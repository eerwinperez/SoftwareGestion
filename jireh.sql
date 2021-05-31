-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 30-05-2021 a las 22:56:07
-- Versión del servidor: 10.4.18-MariaDB
-- Versión de PHP: 8.0.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `jireh`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `abonos`
--

CREATE TABLE `abonos` (
  `idAbono` int(11) NOT NULL,
  `idVenta` int(11) NOT NULL,
  `valorAbono` int(11) NOT NULL,
  `fechaAbonoSistema` date NOT NULL,
  `fechaAbonoReal` date NOT NULL,
  `observaciones` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `factura` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `abonos`
--

INSERT INTO `abonos` (`idAbono`, `idVenta`, `valorAbono`, `fechaAbonoSistema`, `fechaAbonoReal`, `observaciones`, `factura`, `registradoPor`) VALUES
(93, 94, 20000, '2021-05-10', '2021-05-10', '', NULL, 'Erwin'),
(94, 94, 300000, '2021-05-10', '2021-05-10', 'aBONO RECIBIO LC', NULL, 'Erwin'),
(95, 94, 100000, '2021-05-10', '2021-05-10', 'DGDGG', NULL, 'Erwin'),
(96, 94, 10000, '2021-05-22', '2021-05-22', 'Recibio Erwin', NULL, 'Erwin Perez Alean'),
(97, 94, 10000, '2021-05-22', '2021-05-22', 'Recibio Luis Correa', NULL, 'Erwin Perez Alean'),
(98, 93, 100000, '2021-05-22', '2021-05-22', 'Abono xxx', NULL, 'Erwin Perez Alean');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `abonosfacturas`
--

CREATE TABLE `abonosfacturas` (
  `idAbono` int(11) NOT NULL,
  `idFactura` int(11) NOT NULL,
  `valorAbono` int(11) NOT NULL,
  `fechaAbonoSistema` date NOT NULL,
  `observaciones` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `abonosfacturas`
--

INSERT INTO `abonosfacturas` (`idAbono`, `idFactura`, `valorAbono`, `fechaAbonoSistema`, `observaciones`, `registradoPor`) VALUES
(15, 29, 100000, '2021-05-01', 'hoy', 'Luis'),
(16, 32, 2000000, '2021-05-06', 'Cuenta Bancolombia', 'Luis'),
(17, 30, 11200, '2021-05-07', 'fqwdq', 'Erwin'),
(18, 34, 350000, '2021-05-10', 'Entro a cuenta Bancolombia', 'Erwin'),
(19, 35, 2000000, '2021-05-10', 'LC', 'Erwin'),
(20, 35, 1000000, '2021-05-16', 'CDERFVBGT', 'null'),
(21, 35, 200000, '2021-05-16', 'qwerty', 'null'),
(22, 35, 50000, '2021-05-16', 'qwerty', 'null'),
(23, 34, 100000, '2021-05-21', 'QWERTY', 'Erwin Perez Alean'),
(24, 39, 363750, '2021-05-26', 'CSC', 'Erwin Perez Alean');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `agenda`
--

CREATE TABLE `agenda` (
  `id` int(11) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `telefono` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `direccion` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `observaciones` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(70) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `agenda`
--

INSERT INTO `agenda` (`id`, `nombre`, `telefono`, `email`, `direccion`, `observaciones`, `registradoPor`) VALUES
(1, 'Erwin Perez Alean', '1128369789', 'erwin1808@hotmail.com/eperez.alean@gmail.com', 'Rosario 707 6A', 'Sr Java Developer', 'Erwin'),
(2, 'Keyla Marcela Polo', '113326026', 'polo@gmail.com', 'Rosario 707 6A', '', 'Erwin'),
(3, 'Keyla Polo Jaramillo', '113326026', 'polo@gmail.com', 'Rosario 707 6A', '', 'Erwin');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asignacionespresupuesto`
--

CREATE TABLE `asignacionespresupuesto` (
  `idAsignacion` int(11) NOT NULL,
  `idPrespuesto` int(11) NOT NULL,
  `valorAsignacion` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `descripcion` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asociacionfacturas`
--

CREATE TABLE `asociacionfacturas` (
  `idVenta` int(11) NOT NULL,
  `cantidadFacturada` int(11) NOT NULL,
  `valorFacturado` int(11) NOT NULL,
  `factura` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `asociacionfacturas`
--

INSERT INTO `asociacionfacturas` (`idVenta`, `cantidadFacturada`, `valorFacturado`, `factura`) VALUES
(21, 15, 50000, 45);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

CREATE TABLE `clientes` (
  `idCliente` int(11) NOT NULL,
  `nombreCliente` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `identificacion` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `tipoCliente` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `direccion` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `municipio` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `telefono` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `sector` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `clientes`
--

INSERT INTO `clientes` (`idCliente`, `nombreCliente`, `identificacion`, `tipoCliente`, `direccion`, `municipio`, `telefono`, `sector`, `email`, `registradoPor`) VALUES
(27, 'GRAFICAS JIREH', '123', 'Empresa', 'Calle 18', 'Montería', '312', 'Privado', 'j@j.com', 'Luis'),
(28, 'PROGRAFIX', '1987', 'Empresa', 'Monteria', 'Montería', '1233', 'Privado', 'p@p.com', 'Luis'),
(29, 'ERWIN PEREZ ALEAN', '95731397', 'Persona', 'Buenos Aires', 'Montería', '1128369789', 'Privado', 'erwin1808@hotmail.com', 'Luis'),
(30, 'CONSTRUCTORA JC S.A.S', '800192375-1', 'Empresa', 'Edificio Sexta Avenida', 'Montería', '7721189', 'Privado', 'info@constructorajc.com', 'Luis'),
(31, 'YYYY', 'XXX', 'Persona', 'XXX', 'Monteria', 'XXX', 'Privado', 'XXX', 'Erwin Perez Alean');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comisionistas`
--

CREATE TABLE `comisionistas` (
  `idComisionista` int(11) NOT NULL,
  `nombreComisionista` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `comisionistas`
--

INSERT INTO `comisionistas` (`idComisionista`, `nombreComisionista`, `registradoPor`) VALUES
(5, 'Erwin Perez Alean', 'Luis'),
(6, 'Keyla Polo Jaramillo', 'Luis'),
(7, 'Briceño Montiel', 'Luis'),
(8, 'Luis Correa', 'Erwin');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `elementosfactura`
--

CREATE TABLE `elementosfactura` (
  `idRegistro` int(11) NOT NULL,
  `idVenta` int(11) NOT NULL,
  `cantidadFacturada` int(11) NOT NULL,
  `precioUnitario` double NOT NULL,
  `subtotal` int(11) NOT NULL,
  `factura` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `elementosfactura`
--

INSERT INTO `elementosfactura` (`idRegistro`, `idVenta`, `cantidadFacturada`, `precioUnitario`, `subtotal`, `factura`) VALUES
(20, 92, 10, 35000, 350000, 34),
(21, 95, 25, 4000, 100000, 34),
(22, 92, 90, 35000, 3150000, 35),
(23, 95, 25, 4000, 100000, 35),
(24, 96, 90, 555.5555555555555, 50000, 36),
(25, 99, 12, 3750, 45000, 36),
(26, 97, 90, 555.5555555555555, 50000, 37),
(27, 98, 12, 3750, 45000, 37),
(28, 100, 50, 14550, 727500, 37),
(29, 100, 25, 14550, 363750, 38),
(30, 100, 25, 14550, 363750, 39);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `elementosremision`
--

CREATE TABLE `elementosremision` (
  `id` int(11) NOT NULL,
  `idRemision` int(11) NOT NULL,
  `idVenta` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleados`
--

CREATE TABLE `empleados` (
  `id` int(11) NOT NULL,
  `usuario` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `nombreCompleto` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `cargo` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `tipoPermiso` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `estado` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `clave` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(100) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `empleados`
--

INSERT INTO `empleados` (`id`, `usuario`, `nombreCompleto`, `cargo`, `tipoPermiso`, `estado`, `clave`, `registradoPor`) VALUES
(1, 'Erwin', 'Erwin Perez Alean', 'Gerente General', 'Gerente', 'Activo', '1', 'Erwin Perez Alean'),
(2, 'Luis', 'Luis Correa Salcedo', 'Asistente Administrativo', 'Asistente', 'Activo', '1', 'Keyla Polo Jaramillo'),
(3, 'Keyla', 'Keyla Polo Jaramillo', 'Administradora', 'Administrador', 'Activo', '2', 'Erwin Perez Alean'),
(4, 'Ana', 'Ana Arias', 'Administradora', 'Administrador', 'Activo', '1', 'Erwin Perez Alean');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `facturas`
--

CREATE TABLE `facturas` (
  `idFactura` int(11) NOT NULL,
  `fechaFactura` date NOT NULL,
  `idCliente` int(11) NOT NULL,
  `condiciondePago` text COLLATE utf8_unicode_ci NOT NULL,
  `codigoSeguridad` int(11) DEFAULT NULL,
  `estadoPago` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fechaSaldado` date DEFAULT NULL,
  `valorFacturado` int(11) NOT NULL,
  `saldo` float DEFAULT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `facturas`
--

INSERT INTO `facturas` (`idFactura`, `fechaFactura`, `idCliente`, `condiciondePago`, `codigoSeguridad`, `estadoPago`, `fechaSaldado`, `valorFacturado`, `saldo`, `registradoPor`) VALUES
(34, '2021-05-10', 30, '30 dias', 5863641, 'Saldado', '2021-05-21', 450000, 450000, 'Erwin'),
(35, '2021-05-10', 30, '45 dias', 92199269, 'Saldado', '2021-05-16', 3250000, 3250000, 'Erwin'),
(36, '2021-05-12', 30, '45 dias', 96363993, 'Pendiente', NULL, 95000, 95000, 'null'),
(37, '2021-05-23', 30, '50 DIAS', 87508947, 'Pendiente', NULL, 822500, 822500, 'null'),
(38, '2021-05-23', 30, 'GEGE', 57625138, 'Pendiente', NULL, 363750, 363750, 'null'),
(39, '2021-05-26', 30, '60 dias FF', 56036161, 'Saldado', '2021-05-26', 363750, 363750, 'Erwin Perez Alean');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `gastospresupuestos`
--

CREATE TABLE `gastospresupuestos` (
  `id` int(11) NOT NULL,
  `fechaGasto` date DEFAULT NULL,
  `idPrespuesto` int(11) NOT NULL,
  `idConcepto` int(11) NOT NULL,
  `valor` int(11) NOT NULL,
  `observaciones` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `estado` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `observAutoriza` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `factura` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(100) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `gastospresupuestos`
--

INSERT INTO `gastospresupuestos` (`id`, `fechaGasto`, `idPrespuesto`, `idConcepto`, `valor`, `observaciones`, `estado`, `observAutoriza`, `factura`, `registradoPor`) VALUES
(16, '2021-05-20', 10, 1, 400000, 'Arriendo oficina Jireh', 'Registrado', NULL, '', 'Erwin'),
(17, '2021-05-20', 10, 3, 1000000, 'Adelanto Nomina', 'Registrado', 'Se autoriza por que Rody vendio mas este mes', '', 'Erwin'),
(18, '2021-05-20', 10, 14, 50000, 'eeewtww', 'Registrado', NULL, '', 'Erwin'),
(19, '2021-05-20', 10, 37, 500000, 'papel propalcote', 'Registrado', NULL, '', 'Erwin'),
(20, '2021-05-20', 10, 37, 560000, 'master', 'Registrado', NULL, '', 'Erwin'),
(26, '2021-05-21', 11, 1, 150000, 'XXXXX', 'Registrado', 'Se autoriza por que iban a demandarnos', '1', 'Erwin Perez Alean'),
(27, '2021-05-21', 11, 1, 1250000, 'YYYYYYY', 'Registrado', 'Se autoriza por que iban a demandarnos', '2', 'Erwin Perez Alean'),
(28, '2021-05-21', 11, 2, 850000, 'WWWWWW', 'Registrado', 'Nomina propia', '3', 'Erwin Perez Alean'),
(29, '2021-05-21', 11, 5, 235500, 'CCCCCCC', 'Registrado', NULL, '4', 'Erwin Perez Alean');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `itemspresupuesto`
--

CREATE TABLE `itemspresupuesto` (
  `id` int(11) NOT NULL,
  `idPresupuesto` int(11) NOT NULL,
  `idGasto` int(11) NOT NULL,
  `valorPresupuestado` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `itemspresupuesto`
--

INSERT INTO `itemspresupuesto` (`id`, `idPresupuesto`, `idGasto`, `valorPresupuestado`) VALUES
(179, 10, 2, 500000),
(180, 10, 2, 0),
(181, 10, 2, 0),
(182, 10, 4, 0),
(183, 10, 5, 400000),
(184, 10, 6, 0),
(185, 10, 7, 100000),
(186, 10, 8, 779850),
(187, 10, 9, 200000),
(188, 10, 10, 20700),
(189, 10, 11, 38400),
(190, 10, 12, 70000),
(191, 10, 13, 35000),
(192, 10, 14, 70000),
(193, 10, 15, 54000),
(194, 10, 16, 25000),
(195, 10, 17, 220000),
(196, 10, 18, 50000),
(197, 10, 19, 396000),
(198, 10, 20, 260000),
(199, 10, 21, 430000),
(200, 10, 22, 64000),
(201, 10, 23, 100000),
(202, 10, 24, 0),
(203, 10, 25, 30000),
(204, 10, 26, 70000),
(205, 10, 27, 80000),
(206, 10, 28, 500000),
(207, 10, 29, 500000),
(208, 10, 30, 200000),
(209, 10, 31, 200000),
(210, 10, 32, 300000),
(211, 10, 33, 100000),
(212, 10, 34, 300000),
(213, 10, 35, 50000),
(214, 10, 36, 30000),
(215, 10, 37, 4000000),
(216, 10, 38, 100000),
(217, 10, 39, 100000),
(218, 10, 40, 30000),
(219, 10, 41, 60000),
(220, 10, 42, 15000),
(221, 10, 43, 100000),
(222, 10, 44, 150000),
(223, 10, 45, 0),
(224, 10, 46, 0),
(225, 10, 47, 0),
(226, 10, 48, 500000),
(227, 10, 49, 200000),
(228, 10, 50, 150000),
(229, 10, 52, 100000),
(230, 10, 53, 190000),
(231, 10, 54, 400000),
(232, 10, 55, 100000),
(233, 10, 56, 50000),
(234, 10, 57, 90000),
(235, 10, 58, 500000),
(236, 10, 59, 180000),
(237, 10, 62, 400000),
(238, 10, 63, 500000),
(239, 11, 1, 400000),
(240, 11, 2, 0),
(241, 11, 3, 842800),
(242, 11, 4, 0),
(243, 11, 5, 400000),
(244, 11, 6, 0),
(245, 11, 7, 100000),
(246, 11, 8, 779850),
(247, 11, 9, 200000),
(248, 11, 10, 20700),
(249, 11, 11, 38400),
(250, 11, 12, 70000),
(251, 11, 13, 35000),
(252, 11, 14, 70000),
(253, 11, 15, 54000),
(254, 11, 16, 25000),
(255, 11, 17, 220000),
(256, 11, 18, 50000),
(257, 11, 19, 396000),
(258, 11, 20, 260000),
(259, 11, 21, 430000),
(260, 11, 22, 64000),
(261, 11, 23, 100000),
(262, 11, 24, 0),
(263, 11, 25, 30000),
(264, 11, 26, 70000),
(265, 11, 27, 80000),
(266, 11, 28, 500000),
(267, 11, 29, 500000),
(268, 11, 30, 200000),
(269, 11, 31, 200000),
(270, 11, 32, 300000),
(271, 11, 33, 100000),
(272, 11, 34, 300000),
(273, 11, 35, 150000),
(274, 11, 36, 30000),
(275, 11, 37, 4000000),
(276, 11, 38, 100000),
(277, 11, 39, 100000),
(278, 11, 40, 30000),
(279, 11, 41, 60000),
(280, 11, 42, 15000),
(281, 11, 43, 100000),
(282, 11, 44, 150000),
(283, 11, 45, 56000),
(284, 11, 46, 0),
(285, 11, 47, 0),
(286, 11, 48, 500000),
(287, 11, 49, 150000),
(288, 11, 50, 150000),
(289, 11, 51, 0),
(290, 11, 52, 100000),
(291, 11, 53, 190000),
(292, 11, 54, 400000),
(293, 11, 55, 100000),
(294, 11, 56, 50000),
(295, 11, 57, 90000),
(296, 11, 58, 500000),
(297, 11, 59, 180000),
(298, 11, 60, 0),
(299, 11, 61, 0),
(300, 11, 62, 400000),
(301, 11, 63, 500000),
(302, 10, 63, 100),
(303, 10, 63, 1),
(304, 10, 62, 123),
(305, 10, 2, 150000),
(306, 10, 61, 670000);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `maestrogastos`
--

CREATE TABLE `maestrogastos` (
  `id` int(11) NOT NULL,
  `descripcion` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `idGasto` int(11) NOT NULL,
  `idRubro` int(11) NOT NULL,
  `idModalidad` int(11) DEFAULT NULL,
  `presupuestado` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `maestrogastos`
--

INSERT INTO `maestrogastos` (`id`, `descripcion`, `idGasto`, `idRubro`, `idModalidad`, `presupuestado`) VALUES
(1, 'ARRIENDO', 1, 1, 1, 400000),
(2, 'NOMINA  LUIS CORREA ', 1, 2, 1, 0),
(3, 'NOMINA RODY RAMOS', 1, 2, 1, 842800),
(4, 'NOMINA DIANA CANEDO', 1, 2, 1, 0),
(5, 'NOMINA ISAAC ENSUNCHO', 1, 2, 1, 400000),
(6, 'ISAAC ENSUNCHO LIQUIDACION GANANCIA', 1, 2, 1, 0),
(7, 'CONTRATACION DISEÑOS', 1, 3, 1, 100000),
(8, 'SEGURIDAD SOCIAL', 1, 4, 1, 779850),
(9, 'SERVICIO DE ENERGIA 4455651', 1, 7, 1, 200000),
(10, 'SERVICIO DE AGUA', 1, 7, 1, 20700),
(11, 'TELEFONICA RECEPCION', 1, 7, 1, 38400),
(12, 'TELEFONO CLARO', 1, 7, 1, 70000),
(13, 'GASOLINA MOTO/VIATICOS', 1, 9, 1, 35000),
(14, 'RECARGA INTERNET', 1, 9, 1, 70000),
(15, 'INSUMO ALGODÓN X 4 MESE APR', 2, 11, 2, 54000),
(16, 'INSUMO COLBON X 2 MESE APR', 2, 11, 2, 25000),
(17, 'INSUMO MASTER \"200\"', 2, 11, 2, 220000),
(18, 'INSUMO MOLETON', 2, 11, 2, 50000),
(19, 'INSUMO SOLUCION MENSUAL', 2, 11, 2, 396000),
(20, 'INSUMO TINTAS LITOGRAFICAS', 2, 11, 2, 260000),
(21, 'INSUMO TONER  HP 5100 X 3 MESE', 2, 11, 2, 430000),
(22, 'INSUMO VARSOL X 2 MESE', 2, 11, 2, 64000),
(23, 'NUMERADORAS Y TINTAS', 2, 12, 2, 100000),
(24, 'CAJAS Y TACOS PARA SELLOS', 2, 13, 2, 0),
(25, 'TRAB OTRAS/EM. QUEMADA MASTER', 2, 14, 2, 30000),
(26, 'TRAB OTRAS/EM. QUEMADA PLANCHA', 2, 14, 2, 70000),
(27, 'TRAB OTRAS/EM. IMPRESIÓN LASER', 2, 14, 2, 80000),
(28, 'TRAB OTRAS/EM. LITOGRAFICAS', 2, 14, 2, 500000),
(29, 'TRAB OTRAS/EM. PENDONES Y VINILO', 2, 14, 2, 500000),
(30, 'TRAB OTRAS/EM. ELABORACION DE MANILLAS', 2, 14, 2, 200000),
(31, 'TRAB OTRAS/EM. ELABORACION DE CARNET', 2, 14, 2, 200000),
(32, 'TRAB OTRAS/EM. TARJETAS PLASTIFICADAS', 2, 14, 2, 300000),
(33, 'TRAB OTRAS/EM. ELABORACION DE SELLOS', 2, 14, 2, 100000),
(34, 'TRAB OTRAS/EM. ELABORACION DE ACRILICOS', 2, 14, 2, 300000),
(35, 'BORDADOS', 2, 13, 2, 50000),
(36, 'MATERIAL PARA PENDONES', 2, 13, 2, 30000),
(37, 'MATERIA PRIMA', 2, 10, 2, 4000000),
(38, 'CORTE DE PAPEL POR FUERA', 2, 14, 2, 100000),
(39, 'GASTOS VARIOS', 2, 22, 2, 100000),
(40, 'CAFETERIA OFICINA', 3, 15, 2, 30000),
(41, 'AGUA PARA TOMAR', 3, 15, 2, 60000),
(42, 'IMPLEMENTOS DE ASEO', 3, 15, 2, 15000),
(43, 'MANTENIMIENTO MOTO', 3, 16, 2, 100000),
(44, 'FLETES Y ACARREO', 3, 8, 2, 150000),
(45, 'VACACIONES MELY', 3, 6, 3, 0),
(46, 'VACACIONES JESUS', 3, 6, 3, 0),
(47, 'VACACIONES JOSE', 3, 6, 3, 0),
(48, 'COMPRA DE EQUIPOS E INSUMOS', 3, 12, 4, 500000),
(49, 'MAN/TO EQUIPOS DE OFICINA', 3, 16, 3, 150000),
(50, 'MAN/TO MAQUINA MULTILI - ACEITE', 3, 16, 3, 150000),
(51, 'COMPRA DE TRAPOS', 3, 17, 2, 0),
(52, 'GASTOS BANCARIOS', 3, 18, 3, 100000),
(53, 'CAMARA DE COMERCIO ANUAL 8,8000 4 MESE', 3, 19, 3, 190000),
(54, 'IMPUESTOS DECLARACION DE RENTA', 3, 20, 4, 400000),
(55, 'PUBLICIDAD', 3, 21, 4, 100000),
(56, 'UTILES DE OFICINA', 3, 15, 3, 50000),
(57, 'AFILADA DE CUCHILLA GUILLOTINA', 3, 16, 3, 90000),
(58, 'LIQUIDACIONES', 3, 5, 3, 500000),
(59, 'MANTILLA 1/8', 3, 11, 3, 180000),
(60, 'GASTOS EXTRAS LUIS CORREA', 4, 22, 3, 0),
(61, 'GASTOS EXTRAS', 4, 22, 3, 0),
(62, 'GASTOS NO CONTEMPLADOS', 4, 22, 4, 400000),
(63, 'NOMINA ERWIN PEREZ', 1, 2, 1, 500000);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `modalidadgasto`
--

CREATE TABLE `modalidadgasto` (
  `idModalidad` int(11) NOT NULL,
  `Descripcion` varchar(100) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `modalidadgasto`
--

INSERT INTO `modalidadgasto` (`idModalidad`, `Descripcion`) VALUES
(1, 'FIJOS'),
(2, 'RELATIVO'),
(3, 'MUY RELATIVO'),
(4, 'DEMASIADO RELATIVO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `municipios`
--

CREATE TABLE `municipios` (
  `idMunicipio` int(11) NOT NULL,
  `municipio` varchar(150) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `municipios`
--

INSERT INTO `municipios` (`idMunicipio`, `municipio`) VALUES
(1, 'Monteria'),
(2, 'Lorica'),
(3, 'Cerete');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `papeles`
--

CREATE TABLE `papeles` (
  `idPapel` int(11) NOT NULL,
  `nombrePapel` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `papeles`
--

INSERT INTO `papeles` (`idPapel`, `nombrePapel`, `registradoPor`) VALUES
(7, 'Papel bond 75 gr', 'Luis'),
(8, 'Papel bond 90 gr', 'Luis'),
(9, 'Negro mate', NULL),
(10, 'Negro semimate', 'null'),
(11, 'Negro transp', 'Luis'),
(12, 'Negro trans', 'Luis'),
(13, 'Papel general', 'Luis'),
(14, 'Negro mate brilante', 'Erwin');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `partidaspresupuestos`
--

CREATE TABLE `partidaspresupuestos` (
  `id` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `idPresupuesto` int(11) NOT NULL,
  `concepto` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `valor` int(11) NOT NULL,
  `observaciones` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `partidaspresupuestos`
--

INSERT INTO `partidaspresupuestos` (`id`, `fecha`, `idPresupuesto`, `concepto`, `valor`, `observaciones`, `registradoPor`) VALUES
(4, '2021-05-13', 10, 'Saldo del mes anterior', 2000000, 'WIFWEFH', 'Erwin'),
(5, '2021-05-21', 10, 'Prestamo Luis', 1500000, 'Prestamos de sueldo', 'Erwin'),
(6, '2021-05-01', 11, 'Saldo del mes anterior', -300000, 'Saldo negativo', 'Erwin Perez Alean'),
(7, '2021-05-20', 11, 'Asignado por Erwin', 3000000, 'Sueldo propio', 'Erwin Perez Alean');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `presupuestos`
--

CREATE TABLE `presupuestos` (
  `idPresupuesto` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `descripcion` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `estado` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `fechaInicio` date NOT NULL,
  `fechaFin` date NOT NULL,
  `registradoPor` varchar(100) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `presupuestos`
--

INSERT INTO `presupuestos` (`idPresupuesto`, `fecha`, `descripcion`, `estado`, `fechaInicio`, `fechaFin`, `registradoPor`) VALUES
(10, '2021-05-10', 'PRESUPUESTO MES DE MAYO', 'Abierto', '2021-05-01', '2021-05-31', 'Erwin'),
(11, '2021-05-13', 'Presupuesto MayoXXXX', 'Abierto', '2021-05-01', '2021-05-31', 'Erwin Perez Alean');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `remision`
--

CREATE TABLE `remision` (
  `id` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `idCliente` int(11) NOT NULL,
  `Entrega` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `telefono` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `observaciones` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `aleatorio` int(11) NOT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rubros`
--

CREATE TABLE `rubros` (
  `IdRubro` int(11) NOT NULL,
  `Descripcion` varchar(100) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `rubros`
--

INSERT INTO `rubros` (`IdRubro`, `Descripcion`) VALUES
(1, 'ARRIENDOS'),
(2, 'NOMINA'),
(3, 'CONTRATACION SERVICIOS EXTERNOS'),
(4, 'SEGURIDAD SOCIAL'),
(5, 'LIQUIDACIONES'),
(6, 'VACACIONES'),
(7, 'SERVICIOS PUBLICOS'),
(8, 'LOGISTICA'),
(9, 'OTROS GASTOS'),
(10, 'MATERIA PRIMA'),
(11, 'INSUMOS'),
(12, 'HERRAMIENTAS Y EQUIPOS'),
(13, 'MATERIALES PARA ARMADO'),
(14, 'TRABAJOS OTRAS EMPRESAS'),
(15, 'GASTOS DE OFICINA'),
(16, 'MANTENIMIENTOS'),
(17, 'CONSUMIBLES'),
(18, 'GASTOS BANCARIOS'),
(19, 'GASTOS CAMARA DE COMERCIO'),
(20, 'IMPUESTOS'),
(21, 'PUBLICIDAD'),
(22, 'GASTOS/COSTOS VARIOS');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rutas`
--

CREATE TABLE `rutas` (
  `archivo` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `ruta` varchar(255) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `rutas`
--

INSERT INTO `rutas` (`archivo`, `ruta`) VALUES
('OT', 'C:/Users/erwin/Desktop');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipogastos`
--

CREATE TABLE `tipogastos` (
  `idGasto` int(11) NOT NULL,
  `tipoGasto` varchar(100) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `tipogastos`
--

INSERT INTO `tipogastos` (`idGasto`, `tipoGasto`) VALUES
(1, 'GASTOS FIJOS'),
(2, 'COSTOS DIRECTOS'),
(3, 'COSTOS INDIRECTOS'),
(4, 'OTROS GASTOS');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipotrabajo`
--

CREATE TABLE `tipotrabajo` (
  `idTipo` int(11) NOT NULL,
  `tipo` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `tipotrabajo`
--

INSERT INTO `tipotrabajo` (`idTipo`, `tipo`, `registradoPor`) VALUES
(4, 'Facturero', 'Luis'),
(5, 'Talonarios', 'Luis'),
(6, 'Publicidad politica', 'Luis'),
(7, 'Libros', 'Luis'),
(8, 'Botones', 'Erwin');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ventas`
--

CREATE TABLE `ventas` (
  `Idventa` int(11) NOT NULL,
  `Vendedor` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `FechaventaSistema` date NOT NULL,
  `Idcliente` int(11) NOT NULL,
  `Cantidad` int(11) NOT NULL,
  `descripcionTrabajo` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `tipoTrabajo` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `unitario` double NOT NULL,
  `precio` int(11) NOT NULL,
  `tamaño` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `fechaEntrega` date NOT NULL,
  `colorTinta` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `numeracionInicial` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `numeracionFinal` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `acabado` varchar(70) COLLATE utf8_unicode_ci NOT NULL,
  `papelOriginal` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `copia1` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `copia2` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `copia3` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `observaciones` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `motivoNoAbono` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `registradoPor` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `fechaSaldado` date DEFAULT NULL,
  `estadoCuenta` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `fechaTerminacion` date DEFAULT NULL,
  `terminadoPor` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `aleatorioSeguridadAbono` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `ventas`
--

INSERT INTO `ventas` (`Idventa`, `Vendedor`, `FechaventaSistema`, `Idcliente`, `Cantidad`, `descripcionTrabajo`, `tipoTrabajo`, `unitario`, `precio`, `tamaño`, `fechaEntrega`, `colorTinta`, `numeracionInicial`, `numeracionFinal`, `acabado`, `papelOriginal`, `copia1`, `copia2`, `copia3`, `observaciones`, `motivoNoAbono`, `registradoPor`, `fechaSaldado`, `estadoCuenta`, `fechaTerminacion`, `terminadoPor`, `aleatorioSeguridadAbono`) VALUES
(92, 'Erwin Perez Alean', '2021-05-10', 30, 100, 'Libros', 'No aplica', 35000, 3500000, '20cm', '2021-05-28', 'Negro', '', '', '', 'Papel bond 75 gr', 'No aplica', 'No aplica', 'No aplica', 'Urgente', NULL, 'Erwin', NULL, 'Pendiente', NULL, NULL, 9168844),
(93, 'Keyla Polo Jaramillo', '2021-05-10', 29, 10, 'Facturero', 'Facturero', 20000, 200000, '1/12', '2021-05-11', 'Full', '', '', '', 'No aplica', 'No aplica', 'No aplica', 'No aplica', '', 'Es virtual', 'Erwin', NULL, 'Pendiente', '2021-05-21', 'Erwin Perez Alean', 456297),
(94, 'Keyla Polo Jaramillo', '2021-05-10', 29, 100, 'Botones', 'Botones', 4500, 450000, '5cm', '2021-05-20', 'Full', '', '', '', 'No aplica', 'No aplica', 'No aplica', 'No aplica', '', NULL, 'Erwin', NULL, 'Pendiente', '2021-05-10', 'Erwin', 3443221),
(95, 'Luis Correa', '2021-05-10', 30, 50, 'Cuadernos', 'No aplica', 4000, 200000, '20cm', '2021-05-20', 'Full', '', '', '', 'Papel bond 75 gr', 'No aplica', 'No aplica', 'No aplica', '', NULL, 'Erwin', NULL, 'Pendiente', NULL, NULL, 6539451),
(96, 'Erwin Perez Alean', '2021-05-11', 30, 90, 'Yoyo', 'Talonarios', 555.5555555555555, 50000, '1cm', '2021-05-28', 'full', '', '', '', 'Papel bond 75 gr', 'Papel bond 90 gr', 'Negro mate', 'Papel bond 90 gr', '', NULL, 'Erwin', NULL, 'Pendiente', '2021-05-16', NULL, 8228560),
(97, 'Erwin Perez Alean', '2021-05-11', 30, 90, 'Yoyo', 'Talonarios', 555.5555555555555, 50000, '1cm', '2021-05-28', 'full', '', '', '', 'Papel bond 75 gr', 'Papel bond 90 gr', 'Negro mate', 'Papel bond 90 gr', '', NULL, 'Erwin', NULL, 'Pendiente', NULL, NULL, 5787188),
(98, 'Luis Correa', '2021-05-11', 30, 12, 'XXXXX', 'Botones', 3750, 45000, 'XX', '2021-05-14', 'XXX', '', '', '', 'Papel bond 75 gr', 'No aplica', 'No aplica', 'No aplica', '', NULL, 'Erwin', NULL, 'Pendiente', NULL, NULL, 84381),
(99, 'Luis Correa', '2021-05-11', 30, 12, 'XXXXX', 'Botones', 3750, 45000, 'XX', '2021-05-14', 'XXX', '', '', '', 'Papel bond 75 gr', 'No aplica', 'No aplica', 'No aplica', '', NULL, 'Erwin', NULL, 'Pendiente', NULL, NULL, 2250871),
(100, 'Briceño Montiel', '2021-05-11', 30, 100, 'Correccion XXX', 'Libros', 14550, 1455000, 'Tamaño XXX', '2021-05-11', 'Color XXX', 'XXXX', 'XXXX', 'XXXX', 'Papel bond 75 gr', 'Papel bond 75 gr', 'Papel bond 75 gr', 'Papel bond 75 gr', 'Observaciones XXX', NULL, 'Erwin Perez Alean', NULL, 'Pendiente', NULL, NULL, 1535387);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ventasdiarias`
--

CREATE TABLE `ventasdiarias` (
  `Idventa` int(11) NOT NULL,
  `Vendedor` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `FechaventaSistema` date NOT NULL,
  `Idcliente` int(11) NOT NULL,
  `Cantidad` int(11) NOT NULL,
  `descripcionTrabajo` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `tipoTrabajo` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `precio` int(11) NOT NULL,
  `abonos` int(11) NOT NULL,
  `saldo` int(11) NOT NULL,
  `tamaño` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `fechaEntrega` date NOT NULL,
  `colorTinta` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `numeracionInicial` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `numeracionFinal` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `acabado` varchar(70) COLLATE utf8_unicode_ci DEFAULT NULL,
  `papelOriginal` varchar(70) COLLATE utf8_unicode_ci NOT NULL,
  `copia1` varchar(70) COLLATE utf8_unicode_ci NOT NULL,
  `copia2` varchar(70) COLLATE utf8_unicode_ci NOT NULL,
  `copia3` varchar(70) COLLATE utf8_unicode_ci NOT NULL,
  `observaciones` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `registradoPor` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `fechaSaldado` date DEFAULT NULL,
  `estadoCuenta` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `fechaTerminacion` date DEFAULT NULL,
  `fechaTerminacionSistema` date DEFAULT NULL,
  `terminadoPor` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `estadoElaboracion` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
  `aleatorioSeguridad` int(11) NOT NULL,
  `estadoFacturacion` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `factura` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `abonos`
--
ALTER TABLE `abonos`
  ADD PRIMARY KEY (`idAbono`);

--
-- Indices de la tabla `abonosfacturas`
--
ALTER TABLE `abonosfacturas`
  ADD PRIMARY KEY (`idAbono`);

--
-- Indices de la tabla `agenda`
--
ALTER TABLE `agenda`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `asignacionespresupuesto`
--
ALTER TABLE `asignacionespresupuesto`
  ADD PRIMARY KEY (`idAsignacion`);

--
-- Indices de la tabla `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`idCliente`,`nombreCliente`),
  ADD KEY `identificacion` (`identificacion`);

--
-- Indices de la tabla `comisionistas`
--
ALTER TABLE `comisionistas`
  ADD PRIMARY KEY (`idComisionista`);

--
-- Indices de la tabla `elementosfactura`
--
ALTER TABLE `elementosfactura`
  ADD PRIMARY KEY (`idRegistro`);

--
-- Indices de la tabla `elementosremision`
--
ALTER TABLE `elementosremision`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `empleados`
--
ALTER TABLE `empleados`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `facturas`
--
ALTER TABLE `facturas`
  ADD PRIMARY KEY (`idFactura`);

--
-- Indices de la tabla `gastospresupuestos`
--
ALTER TABLE `gastospresupuestos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `itemspresupuesto`
--
ALTER TABLE `itemspresupuesto`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `maestrogastos`
--
ALTER TABLE `maestrogastos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `modalidadgasto`
--
ALTER TABLE `modalidadgasto`
  ADD PRIMARY KEY (`idModalidad`);

--
-- Indices de la tabla `municipios`
--
ALTER TABLE `municipios`
  ADD PRIMARY KEY (`idMunicipio`);

--
-- Indices de la tabla `papeles`
--
ALTER TABLE `papeles`
  ADD PRIMARY KEY (`idPapel`);

--
-- Indices de la tabla `partidaspresupuestos`
--
ALTER TABLE `partidaspresupuestos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `presupuestos`
--
ALTER TABLE `presupuestos`
  ADD PRIMARY KEY (`idPresupuesto`);

--
-- Indices de la tabla `remision`
--
ALTER TABLE `remision`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `rubros`
--
ALTER TABLE `rubros`
  ADD PRIMARY KEY (`IdRubro`);

--
-- Indices de la tabla `tipogastos`
--
ALTER TABLE `tipogastos`
  ADD PRIMARY KEY (`idGasto`);

--
-- Indices de la tabla `tipotrabajo`
--
ALTER TABLE `tipotrabajo`
  ADD PRIMARY KEY (`idTipo`);

--
-- Indices de la tabla `ventas`
--
ALTER TABLE `ventas`
  ADD PRIMARY KEY (`Idventa`),
  ADD KEY `Idcliente` (`Idcliente`);

--
-- Indices de la tabla `ventasdiarias`
--
ALTER TABLE `ventasdiarias`
  ADD PRIMARY KEY (`Idventa`),
  ADD KEY `Idcliente` (`Idcliente`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `abonos`
--
ALTER TABLE `abonos`
  MODIFY `idAbono` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=99;

--
-- AUTO_INCREMENT de la tabla `abonosfacturas`
--
ALTER TABLE `abonosfacturas`
  MODIFY `idAbono` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT de la tabla `agenda`
--
ALTER TABLE `agenda`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `asignacionespresupuesto`
--
ALTER TABLE `asignacionespresupuesto`
  MODIFY `idAsignacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `clientes`
--
ALTER TABLE `clientes`
  MODIFY `idCliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT de la tabla `comisionistas`
--
ALTER TABLE `comisionistas`
  MODIFY `idComisionista` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `elementosfactura`
--
ALTER TABLE `elementosfactura`
  MODIFY `idRegistro` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT de la tabla `elementosremision`
--
ALTER TABLE `elementosremision`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `empleados`
--
ALTER TABLE `empleados`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `facturas`
--
ALTER TABLE `facturas`
  MODIFY `idFactura` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT de la tabla `gastospresupuestos`
--
ALTER TABLE `gastospresupuestos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT de la tabla `itemspresupuesto`
--
ALTER TABLE `itemspresupuesto`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=307;

--
-- AUTO_INCREMENT de la tabla `maestrogastos`
--
ALTER TABLE `maestrogastos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=64;

--
-- AUTO_INCREMENT de la tabla `modalidadgasto`
--
ALTER TABLE `modalidadgasto`
  MODIFY `idModalidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `municipios`
--
ALTER TABLE `municipios`
  MODIFY `idMunicipio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `papeles`
--
ALTER TABLE `papeles`
  MODIFY `idPapel` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `partidaspresupuestos`
--
ALTER TABLE `partidaspresupuestos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `presupuestos`
--
ALTER TABLE `presupuestos`
  MODIFY `idPresupuesto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `remision`
--
ALTER TABLE `remision`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `rubros`
--
ALTER TABLE `rubros`
  MODIFY `IdRubro` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT de la tabla `tipogastos`
--
ALTER TABLE `tipogastos`
  MODIFY `idGasto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `tipotrabajo`
--
ALTER TABLE `tipotrabajo`
  MODIFY `idTipo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `ventas`
--
ALTER TABLE `ventas`
  MODIFY `Idventa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=101;

--
-- AUTO_INCREMENT de la tabla `ventasdiarias`
--
ALTER TABLE `ventasdiarias`
  MODIFY `Idventa` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
