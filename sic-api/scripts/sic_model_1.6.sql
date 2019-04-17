CREATE DATABASE  IF NOT EXISTS `sic` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
USE `sic`;
-- MySQL dump 10.13  Distrib 5.5.44, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: sic
-- ------------------------------------------------------
-- Server version	5.5.44-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cliente` (
  `id_Cliente` bigint(20) NOT NULL AUTO_INCREMENT,
  `razonSocial` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `nombreFantasia` varchar(255) COLLATE utf8_unicode_ci DEFAULT '',
  `direccion` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_CondicionIVA` bigint(20) NOT NULL,
  `id_Fiscal` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telPrimario` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telSecundario` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_Localidad` bigint(20) NOT NULL,
  `contacto` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fechaAlta` datetime DEFAULT NULL,
  `id_Empresa` bigint(20) NOT NULL,
  `eliminado` bit(1) DEFAULT NULL,
  `predeterminado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Cliente`),
  KEY `FK96841DDADCB3D948` (`id_Empresa`),
  KEY `FK96841DDAA1BB0388` (`id_Localidad`),
  KEY `FK96841DDAD801A27A` (`id_CondicionIVA`),
  CONSTRAINT `FK96841DDAA1BB0388` FOREIGN KEY (`id_Localidad`) REFERENCES `localidad` (`id_Localidad`),
  CONSTRAINT `FK96841DDAD801A27A` FOREIGN KEY (`id_CondicionIVA`) REFERENCES `condicioniva` (`id_CondicionIVA`),
  CONSTRAINT `FK96841DDADCB3D948` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `condicioniva`
--

DROP TABLE IF EXISTS `condicioniva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `condicioniva` (
  `id_CondicionIVA` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `discriminaIVA` bit(1) DEFAULT NULL,
  `eliminada` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_CondicionIVA`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `configuraciondelsistema`
--

DROP TABLE IF EXISTS `configuraciondelsistema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configuraciondelsistema` (
  `id_ConfiguracionDelSistema` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidadMaximaDeRenglonesEnFactura` int(11) NOT NULL,
  `usarFacturaVentaPreImpresa` tinyint(1) NOT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_ConfiguracionDelSistema`),
  KEY `FKD426557E6813BD87` (`id_Empresa`),
  CONSTRAINT `FKD426557E6813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `empresa`
--

DROP TABLE IF EXISTS `empresa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `empresa` (
  `id_Empresa` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `lema` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cuip` bigint(20) DEFAULT NULL,
  `ingresosBrutos` bigint(20) DEFAULT NULL,
  `fechaInicioActividad` datetime DEFAULT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telefono` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `logo` longblob,
  `id_CondicionIVA` bigint(20) NOT NULL,
  `id_Localidad` bigint(20) NOT NULL,
  `eliminada` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Empresa`),
  KEY `FK26DD869A1BB0388` (`id_Localidad`),
  KEY `FK26DD869D801A27A` (`id_CondicionIVA`),
  CONSTRAINT `FK26DD869A1BB0388` FOREIGN KEY (`id_Localidad`) REFERENCES `localidad` (`id_Localidad`),
  CONSTRAINT `FK26DD869D801A27A` FOREIGN KEY (`id_CondicionIVA`) REFERENCES `condicioniva` (`id_CondicionIVA`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `factura`
--

DROP TABLE IF EXISTS `factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `factura` (
  `id_Factura` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha` datetime DEFAULT NULL,
  `tipoFactura` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `numSerie` bigint(20) DEFAULT NULL,
  `numFactura` bigint(20) DEFAULT NULL,
  `id_FormaDePago` bigint(20) NOT NULL,
  `fechaVencimiento` datetime DEFAULT NULL,
  `id_Transportista` bigint(20) DEFAULT NULL,
  `subTotal` double DEFAULT NULL,
  `recargo_porcentaje` double DEFAULT NULL,
  `recargo_neto` double DEFAULT NULL,
  `descuento_porcentaje` double DEFAULT NULL,
  `descuento_neto` double DEFAULT NULL,
  `subTotal_neto` double DEFAULT NULL,
  `iva_105_neto` double DEFAULT NULL,
  `iva_21_neto` double DEFAULT NULL,
  `impuestoInterno_neto` double DEFAULT NULL,
  `total` double DEFAULT NULL,
  `observaciones` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pagada` bit(1) DEFAULT NULL,
  `id_Empresa` bigint(20) NOT NULL,
  `eliminada` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Factura`),
  KEY `FK2223DF58DCB3D948` (`id_Empresa`),
  KEY `FK2223DF58A23D3F36` (`id_Transportista`),
  KEY `FK2223DF586E17BF64` (`id_FormaDePago`),
  CONSTRAINT `FK2223DF586E17BF64` FOREIGN KEY (`id_FormaDePago`) REFERENCES `formadepago` (`id_FormaDePago`) ON DELETE CASCADE,
  CONSTRAINT `FK2223DF58A23D3F36` FOREIGN KEY (`id_Transportista`) REFERENCES `transportista` (`id_Transportista`),
  CONSTRAINT `FK2223DF58DCB3D948` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=224 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `facturacompra`
--

DROP TABLE IF EXISTS `facturacompra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facturacompra` (
  `id_Factura` bigint(20) NOT NULL,
  `id_Proveedor` bigint(20) NOT NULL,
  PRIMARY KEY (`id_Factura`),
  KEY `FKF1491EF6EF9DB872` (`id_Proveedor`),
  KEY `FKF1491EF61C1FE726` (`id_Factura`),
  CONSTRAINT `FKF1491EF61C1FE726` FOREIGN KEY (`id_Factura`) REFERENCES `factura` (`id_Factura`),
  CONSTRAINT `FKF1491EF6EF9DB872` FOREIGN KEY (`id_Proveedor`) REFERENCES `proveedor` (`id_Proveedor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `facturaventa`
--

DROP TABLE IF EXISTS `facturaventa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facturaventa` (
  `id_Factura` bigint(20) NOT NULL,
  `id_Cliente` bigint(20) NOT NULL,
  `id_Usuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id_Factura`),
  KEY `FKDF8571D44E0642A` (`id_Cliente`),
  KEY `FKDF8571D48E733E92` (`id_Usuario`),
  KEY `FKDF8571D41C1FE726` (`id_Factura`),
  CONSTRAINT `FKDF8571D41C1FE726` FOREIGN KEY (`id_Factura`) REFERENCES `factura` (`id_Factura`),
  CONSTRAINT `FKDF8571D44E0642A` FOREIGN KEY (`id_Cliente`) REFERENCES `cliente` (`id_Cliente`),
  CONSTRAINT `FKDF8571D48E733E92` FOREIGN KEY (`id_Usuario`) REFERENCES `usuario` (`id_Usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `formadepago`
--

DROP TABLE IF EXISTS `formadepago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `formadepago` (
  `id_FormaDePago` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `afectaCaja` bit(1) DEFAULT NULL,
  `predeterminado` bit(1) DEFAULT NULL,
  `id_Empresa` bigint(20) NOT NULL,
  `eliminada` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_FormaDePago`),
  KEY `FK68B59117DCB3D948` (`id_Empresa`),
  CONSTRAINT `FK68B59117DCB3D948` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `localidad`
--

DROP TABLE IF EXISTS `localidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `localidad` (
  `id_Localidad` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `codigoPostal` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_Provincia` bigint(20) NOT NULL,
  `eliminada` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Localidad`),
  KEY `FK2FA59049F0169A10` (`id_Provincia`),
  CONSTRAINT `FK2FA59049F0169A10` FOREIGN KEY (`id_Provincia`) REFERENCES `provincia` (`id_Provincia`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `medida`
--

DROP TABLE IF EXISTS `medida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medida` (
  `id_Medida` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_Empresa` bigint(20) NOT NULL,
  `eliminada` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Medida`),
  KEY `FK8923797ADCB3D948` (`id_Empresa`),
  CONSTRAINT `FK8923797ADCB3D948` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pagofacturacompra`
--

DROP TABLE IF EXISTS `pagofacturacompra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pagofacturacompra` (
  `id_PagoFacturaCompra` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha` datetime DEFAULT NULL,
  `monto` double DEFAULT NULL,
  `nota` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_Factura` bigint(20) NOT NULL,
  `eliminado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_PagoFacturaCompra`),
  KEY `FKB515BCDDA66C9C2` (`id_Factura`),
  CONSTRAINT `FKB515BCDDA66C9C2` FOREIGN KEY (`id_Factura`) REFERENCES `facturacompra` (`id_Factura`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pais`
--

DROP TABLE IF EXISTS `pais`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pais` (
  `id_Pais` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eliminado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Pais`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `producto` (
  `id_Producto` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `descripcion` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cantidad` double DEFAULT NULL,
  `cantMinima` double DEFAULT NULL,
  `id_Medida` bigint(20) NOT NULL,
  `precioCosto` double DEFAULT NULL,
  `ganancia_porcentaje` double DEFAULT NULL,
  `ganancia_neto` double DEFAULT NULL,
  `precioVentaPublico` double DEFAULT NULL,
  `iva_porcentaje` double DEFAULT NULL,
  `iva_neto` double DEFAULT NULL,
  `impuestoInterno_porcentaje` double DEFAULT NULL,
  `impuestoInterno_neto` double DEFAULT NULL,
  `precioLista` double DEFAULT NULL,
  `id_Rubro` bigint(20) NOT NULL,
  `ilimitado` bit(1) DEFAULT NULL,
  `fechaUltimaModificacion` datetime DEFAULT NULL,
  `estanteria` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `estante` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_Proveedor` bigint(20) NOT NULL,
  `nota` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fechaAlta` datetime DEFAULT NULL,
  `fechaVencimiento` datetime DEFAULT NULL,
  `id_Empresa` bigint(20) NOT NULL,
  `eliminado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Producto`),
  KEY `FKC8063580DCB3D948` (`id_Empresa`),
  KEY `FKC8063580EF9DB872` (`id_Proveedor`),
  KEY `FKC8063580F70837AE` (`id_Rubro`),
  KEY `FKC8063580D72CE3BE` (`id_Medida`),
  CONSTRAINT `FKC8063580D72CE3BE` FOREIGN KEY (`id_Medida`) REFERENCES `medida` (`id_Medida`),
  CONSTRAINT `FKC8063580DCB3D948` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`) ON DELETE CASCADE,
  CONSTRAINT `FKC8063580EF9DB872` FOREIGN KEY (`id_Proveedor`) REFERENCES `proveedor` (`id_Proveedor`) ON DELETE CASCADE,
  CONSTRAINT `FKC8063580F70837AE` FOREIGN KEY (`id_Rubro`) REFERENCES `rubro` (`id_Rubro`)
) ENGINE=InnoDB AUTO_INCREMENT=1915 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proveedor`
--

DROP TABLE IF EXISTS `proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proveedor` (
  `id_Proveedor` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `razonSocial` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_CondicionIVA` bigint(20) NOT NULL,
  `id_Fiscal` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telPrimario` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telSecundario` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `contacto` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `web` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_Localidad` bigint(20) NOT NULL,
  `id_Empresa` bigint(20) NOT NULL,
  `eliminado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Proveedor`),
  KEY `FK5696EABEDCB3D948` (`id_Empresa`),
  KEY `FK5696EABEA1BB0388` (`id_Localidad`),
  KEY `FK5696EABED801A27A` (`id_CondicionIVA`),
  CONSTRAINT `FK5696EABEA1BB0388` FOREIGN KEY (`id_Localidad`) REFERENCES `localidad` (`id_Localidad`),
  CONSTRAINT `FK5696EABED801A27A` FOREIGN KEY (`id_CondicionIVA`) REFERENCES `condicioniva` (`id_CondicionIVA`),
  CONSTRAINT `FK5696EABEDCB3D948` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `provincia`
--

DROP TABLE IF EXISTS `provincia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provincia` (
  `id_Provincia` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_Pais` bigint(20) NOT NULL,
  `eliminada` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Provincia`),
  KEY `FK56D35B8DDEAB4940` (`id_Pais`),
  CONSTRAINT `FK56D35B8DDEAB4940` FOREIGN KEY (`id_Pais`) REFERENCES `pais` (`id_Pais`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renglonfactura`
--

DROP TABLE IF EXISTS `renglonfactura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renglonfactura` (
  `id_RenglonFactura` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_Factura` bigint(20) NOT NULL,
  `id_ProductoItem` bigint(20) DEFAULT NULL,
  `codigoItem` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `descripcionItem` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `medidaItem` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cantidad` double DEFAULT NULL,
  `precioUnitario` double DEFAULT NULL,
  `descuento_neto` double DEFAULT NULL,
  `descuento_porcentaje` double DEFAULT NULL,
  `iva_porcentaje` double DEFAULT NULL,
  `iva_neto` double DEFAULT NULL,
  `impuesto_porcentaje` double DEFAULT NULL,
  `impuesto_neto` double DEFAULT NULL,
  `ganancia_porcentaje` double DEFAULT NULL,
  `ganancia_neto` double DEFAULT NULL,
  `importe` double DEFAULT NULL,
  PRIMARY KEY (`id_RenglonFactura`),
  KEY `FKC6A221F91C1FE726` (`id_Factura`),
  CONSTRAINT `FKC6A221F91C1FE726` FOREIGN KEY (`id_Factura`) REFERENCES `factura` (`id_Factura`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1265 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rubro`
--

DROP TABLE IF EXISTS `rubro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rubro` (
  `id_Rubro` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_Empresa` bigint(20) NOT NULL,
  `eliminado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Rubro`),
  KEY `FK4BA34DCDCB3D948` (`id_Empresa`),
  CONSTRAINT `FK4BA34DCDCB3D948` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transportista`
--

DROP TABLE IF EXISTS `transportista`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transportista` (
  `id_Transportista` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `telefono` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `web` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_Localidad` bigint(20) NOT NULL,
  `id_Empresa` bigint(20) NOT NULL,
  `eliminado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Transportista`),
  KEY `FKD881E3A0DCB3D948` (`id_Empresa`),
  KEY `FKD881E3A0A1BB0388` (`id_Localidad`),
  CONSTRAINT `FKD881E3A0A1BB0388` FOREIGN KEY (`id_Localidad`) REFERENCES `localidad` (`id_Localidad`),
  CONSTRAINT `FKD881E3A0DCB3D948` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `id_Usuario` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `permisosAdministrador` bit(1) DEFAULT NULL,
  `eliminado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_Usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-02 11:17:13
