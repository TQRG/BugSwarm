CREATE DATABASE  IF NOT EXISTS `sic` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
USE `sic`;
-- MySQL dump 10.13  Distrib 5.5.46, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: sic
-- ------------------------------------------------------
-- Server version	5.5.46-0ubuntu0.14.04.2

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
  `contacto` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `direccion` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `eliminado` bit(1) NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `fechaAlta` datetime NOT NULL,
  `id_Fiscal` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `nombreFantasia` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `predeterminado` bit(1) NOT NULL,
  `razonSocial` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `telPrimario` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `telSecundario` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_CondicionIVA` bigint(20) DEFAULT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  `id_Localidad` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Cliente`),
  KEY `FK334B85FAD4B2D407` (`id_Localidad`),
  KEY `FK334B85FAC718D9B` (`id_CondicionIVA`),
  KEY `FK334B85FA6813BD87` (`id_Empresa`),
  CONSTRAINT `FK334B85FA6813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`),
  CONSTRAINT `FK334B85FAC718D9B` FOREIGN KEY (`id_CondicionIVA`) REFERENCES `condicioniva` (`id_CondicionIVA`),
  CONSTRAINT `FK334B85FAD4B2D407` FOREIGN KEY (`id_Localidad`) REFERENCES `localidad` (`id_Localidad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `condicioniva`
--

DROP TABLE IF EXISTS `condicioniva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `condicioniva` (
  `id_CondicionIVA` bigint(20) NOT NULL AUTO_INCREMENT,
  `discriminaIVA` bit(1) NOT NULL,
  `eliminada` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id_CondicionIVA`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
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
  `usarFacturaVentaPreImpresa` bit(1) NOT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_ConfiguracionDelSistema`),
  KEY `FK404F099E6813BD87` (`id_Empresa`),
  CONSTRAINT `FK404F099E6813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `empresa`
--

DROP TABLE IF EXISTS `empresa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `empresa` (
  `id_Empresa` bigint(20) NOT NULL AUTO_INCREMENT,
  `cuip` bigint(20) NOT NULL,
  `direccion` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `eliminada` bit(1) NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `fechaInicioActividad` datetime DEFAULT NULL,
  `ingresosBrutos` bigint(20) NOT NULL,
  `lema` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `logo` longblob,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `telefono` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_CondicionIVA` bigint(20) DEFAULT NULL,
  `id_Localidad` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Empresa`),
  KEY `FK9F354089D4B2D407` (`id_Localidad`),
  KEY `FK9F354089C718D9B` (`id_CondicionIVA`),
  CONSTRAINT `FK9F354089C718D9B` FOREIGN KEY (`id_CondicionIVA`) REFERENCES `condicioniva` (`id_CondicionIVA`),
  CONSTRAINT `FK9F354089D4B2D407` FOREIGN KEY (`id_Localidad`) REFERENCES `localidad` (`id_Localidad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `factura`
--

DROP TABLE IF EXISTS `factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `factura` (
  `id_Factura` bigint(20) NOT NULL AUTO_INCREMENT,
  `descuento_neto` double NOT NULL,
  `descuento_porcentaje` double NOT NULL,
  `eliminada` bit(1) NOT NULL,
  `fecha` datetime NOT NULL,
  `fechaVencimiento` datetime DEFAULT NULL,
  `impuestoInterno_neto` double NOT NULL,
  `iva_105_neto` double NOT NULL,
  `iva_21_neto` double NOT NULL,
  `numFactura` bigint(20) NOT NULL,
  `numSerie` bigint(20) NOT NULL,
  `observaciones` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `pagada` bit(1) NOT NULL,
  `recargo_neto` double NOT NULL,
  `recargo_porcentaje` double NOT NULL,
  `subTotal` double NOT NULL,
  `subTotal_neto` double NOT NULL,
  `tipoFactura` char(1) COLLATE utf8_unicode_ci NOT NULL,
  `total` double NOT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  `id_FormaDePago` bigint(20) DEFAULT NULL,
  `id_Pedido` bigint(20) DEFAULT NULL,
  `id_Transportista` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Factura`),
  KEY `FKBEEB4778FBCAB835` (`id_Transportista`),
  KEY `FKBEEB47786813BD87` (`id_Empresa`),
  KEY `FKBEEB4778C25D6C23` (`id_FormaDePago`),
  KEY `FKBEEB4778F66D0F75` (`id_Pedido`),
  CONSTRAINT `FKBEEB4778F66D0F75` FOREIGN KEY (`id_Pedido`) REFERENCES `pedido` (`id_Pedido`),
  CONSTRAINT `FKBEEB47786813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`),
  CONSTRAINT `FKBEEB4778C25D6C23` FOREIGN KEY (`id_FormaDePago`) REFERENCES `formadepago` (`id_FormaDePago`),
  CONSTRAINT `FKBEEB4778FBCAB835` FOREIGN KEY (`id_Transportista`) REFERENCES `transportista` (`id_Transportista`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `facturacompra`
--

DROP TABLE IF EXISTS `facturacompra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facturacompra` (
  `id_Factura` bigint(20) NOT NULL,
  `id_Proveedor` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Factura`),
  KEY `FKF84502F6A77FCB65` (`id_Factura`),
  KEY `FKF84502F6229588F1` (`id_Proveedor`),
  CONSTRAINT `FKF84502F6229588F1` FOREIGN KEY (`id_Proveedor`) REFERENCES `proveedor` (`id_Proveedor`),
  CONSTRAINT `FKF84502F6A77FCB65` FOREIGN KEY (`id_Factura`) REFERENCES `factura` (`id_Factura`)
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
  `id_Cliente` bigint(20) DEFAULT NULL,
  `id_Usuario` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Factura`),
  KEY `FKD77D0DD4A77FCB65` (`id_Factura`),
  KEY `FKD77D0DD490404869` (`id_Cliente`),
  KEY `FKD77D0DD419D322D1` (`id_Usuario`),
  CONSTRAINT `FKD77D0DD419D322D1` FOREIGN KEY (`id_Usuario`) REFERENCES `usuario` (`id_Usuario`),
  CONSTRAINT `FKD77D0DD490404869` FOREIGN KEY (`id_Cliente`) REFERENCES `cliente` (`id_Cliente`),
  CONSTRAINT `FKD77D0DD4A77FCB65` FOREIGN KEY (`id_Factura`) REFERENCES `factura` (`id_Factura`)
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
  `afectaCaja` bit(1) NOT NULL,
  `eliminada` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `predeterminado` bit(1) NOT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_FormaDePago`),
  KEY `FK3BF588F76813BD87` (`id_Empresa`),
  CONSTRAINT `FK3BF588F76813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `localidad`
--

DROP TABLE IF EXISTS `localidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `localidad` (
  `id_Localidad` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigoPostal` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `eliminada` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_Provincia` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Localidad`),
  KEY `FKB8337069230E6A8F` (`id_Provincia`),
  CONSTRAINT `FKB8337069230E6A8F` FOREIGN KEY (`id_Provincia`) REFERENCES `provincia` (`id_Provincia`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `medida`
--

DROP TABLE IF EXISTS `medida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medida` (
  `id_Medida` bigint(20) NOT NULL AUTO_INCREMENT,
  `eliminada` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Medida`),
  KEY `FKBFBE8D5A6813BD87` (`id_Empresa`),
  CONSTRAINT `FKBFBE8D5A6813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pagofacturacompra`
--

DROP TABLE IF EXISTS `pagofacturacompra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pagofacturacompra` (
  `id_PagoFacturaCompra` bigint(20) NOT NULL AUTO_INCREMENT,
  `eliminado` bit(1) NOT NULL,
  `fecha` datetime NOT NULL,
  `monto` double NOT NULL,
  `nota` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_Factura` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_PagoFacturaCompra`),
  KEY `FKD14D60FD642D9C43` (`id_Factura`),
  CONSTRAINT `FKD14D60FD642D9C43` FOREIGN KEY (`id_Factura`) REFERENCES `facturacompra` (`id_Factura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pais`
--

DROP TABLE IF EXISTS `pais`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pais` (
  `id_Pais` bigint(20) NOT NULL AUTO_INCREMENT,
  `eliminado` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id_Pais`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido` (
  `id_Pedido` bigint(20) NOT NULL AUTO_INCREMENT,
  `eliminado` bit(1) NOT NULL,
  `fecha` datetime NOT NULL,
  `fechaVencimiento` datetime DEFAULT NULL,
  `nroPedido` bigint(20) NOT NULL,
  `observaciones` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `totalActual` double NOT NULL,
  `totalEstimado` double NOT NULL,
  `id_Cliente` bigint(20) DEFAULT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  `id_Usuario` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Pedido`),
  KEY `FKC4DD174590404869` (`id_Cliente`),
  KEY `FKC4DD174519D322D1` (`id_Usuario`),
  KEY `FKC4DD17456813BD87` (`id_Empresa`),
  CONSTRAINT `FKC4DD17456813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`),
  CONSTRAINT `FKC4DD174519D322D1` FOREIGN KEY (`id_Usuario`) REFERENCES `usuario` (`id_Usuario`),
  CONSTRAINT `FKC4DD174590404869` FOREIGN KEY (`id_Cliente`) REFERENCES `cliente` (`id_Cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `producto` (
  `id_Producto` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantMinima` double NOT NULL,
  `cantidad` double NOT NULL,
  `codigo` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `eliminado` bit(1) NOT NULL,
  `estante` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `estanteria` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `fechaAlta` datetime NOT NULL,
  `fechaUltimaModificacion` datetime NOT NULL,
  `fechaVencimiento` datetime DEFAULT NULL,
  `ganancia_neto` double NOT NULL,
  `ganancia_porcentaje` double NOT NULL,
  `ilimitado` bit(1) NOT NULL,
  `impuestoInterno_neto` double NOT NULL,
  `impuestoInterno_porcentaje` double NOT NULL,
  `iva_neto` double NOT NULL,
  `iva_porcentaje` double NOT NULL,
  `nota` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `precioCosto` double NOT NULL,
  `precioLista` double NOT NULL,
  `precioVentaPublico` double NOT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  `id_Medida` bigint(20) DEFAULT NULL,
  `id_Proveedor` bigint(20) DEFAULT NULL,
  `id_Rubro` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Producto`),
  KEY `FKC42BD160229588F1` (`id_Proveedor`),
  KEY `FKC42BD1606813BD87` (`id_Empresa`),
  KEY `FKC42BD160EC2FFB9F` (`id_Medida`),
  KEY `FKC42BD16029421FAD` (`id_Rubro`),
  CONSTRAINT `FKC42BD16029421FAD` FOREIGN KEY (`id_Rubro`) REFERENCES `rubro` (`id_Rubro`),
  CONSTRAINT `FKC42BD160229588F1` FOREIGN KEY (`id_Proveedor`) REFERENCES `proveedor` (`id_Proveedor`),
  CONSTRAINT `FKC42BD1606813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`),
  CONSTRAINT `FKC42BD160EC2FFB9F` FOREIGN KEY (`id_Medida`) REFERENCES `medida` (`id_Medida`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `proveedor`
--

DROP TABLE IF EXISTS `proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proveedor` (
  `id_Proveedor` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `contacto` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `direccion` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `eliminado` bit(1) NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_Fiscal` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `razonSocial` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `telPrimario` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `telSecundario` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `web` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_CondicionIVA` bigint(20) DEFAULT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  `id_Localidad` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Proveedor`),
  KEY `FKDF24CADED4B2D407` (`id_Localidad`),
  KEY `FKDF24CADEC718D9B` (`id_CondicionIVA`),
  KEY `FKDF24CADE6813BD87` (`id_Empresa`),
  CONSTRAINT `FKDF24CADE6813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`),
  CONSTRAINT `FKDF24CADEC718D9B` FOREIGN KEY (`id_CondicionIVA`) REFERENCES `condicioniva` (`id_CondicionIVA`),
  CONSTRAINT `FKDF24CADED4B2D407` FOREIGN KEY (`id_Localidad`) REFERENCES `localidad` (`id_Localidad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `provincia`
--

DROP TABLE IF EXISTS `provincia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provincia` (
  `id_Provincia` bigint(20) NOT NULL AUTO_INCREMENT,
  `eliminada` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_Pais` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Provincia`),
  KEY `FKDF613BAD4BA4E561` (`id_Pais`),
  CONSTRAINT `FKDF613BAD4BA4E561` FOREIGN KEY (`id_Pais`) REFERENCES `pais` (`id_Pais`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renglonPedido`
--

DROP TABLE IF EXISTS `renglonPedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renglonPedido` (
  `id_RenglonPedido` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidad` double NOT NULL,
  `descuento_neto` double NOT NULL,
  `descuento_porcentaje` double NOT NULL,
  `subTotal` double NOT NULL,
  `id_Pedido` bigint(20) DEFAULT NULL,
  `id_Producto` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_RenglonPedido`),
  KEY `FK63F138C493D5F4EB` (`id_Producto`),
  KEY `FK63F138C4F66D0F75` (`id_Pedido`),
  CONSTRAINT `FK63F138C4F66D0F75` FOREIGN KEY (`id_Pedido`) REFERENCES `pedido` (`id_Pedido`),
  CONSTRAINT `FK63F138C493D5F4EB` FOREIGN KEY (`id_Producto`) REFERENCES `producto` (`id_Producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `renglonfactura`
--

DROP TABLE IF EXISTS `renglonfactura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renglonfactura` (
  `id_RenglonFactura` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidad` double NOT NULL,
  `codigoItem` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `descripcionItem` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `descuento_neto` double NOT NULL,
  `descuento_porcentaje` double NOT NULL,
  `ganancia_neto` double NOT NULL,
  `ganancia_porcentaje` double NOT NULL,
  `id_ProductoItem` bigint(20) NOT NULL,
  `importe` double NOT NULL,
  `impuesto_neto` double NOT NULL,
  `impuesto_porcentaje` double NOT NULL,
  `iva_neto` double NOT NULL,
  `iva_porcentaje` double NOT NULL,
  `medidaItem` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `precioUnitario` double NOT NULL,
  `id_Factura` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_RenglonFactura`),
  KEY `FK9F22BDF9A77FCB65` (`id_Factura`),
  CONSTRAINT `FK9F22BDF9A77FCB65` FOREIGN KEY (`id_Factura`) REFERENCES `factura` (`id_Factura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rubro`
--

DROP TABLE IF EXISTS `rubro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rubro` (
  `id_Rubro` bigint(20) NOT NULL AUTO_INCREMENT,
  `eliminado` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Rubro`),
  KEY `FK67D24FC6813BD87` (`id_Empresa`),
  CONSTRAINT `FK67D24FC6813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transportista`
--

DROP TABLE IF EXISTS `transportista`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transportista` (
  `id_Transportista` bigint(20) NOT NULL AUTO_INCREMENT,
  `direccion` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `eliminado` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `telefono` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `web` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `id_Empresa` bigint(20) DEFAULT NULL,
  `id_Localidad` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_Transportista`),
  KEY `FKA8E2B3C0D4B2D407` (`id_Localidad`),
  KEY `FKA8E2B3C06813BD87` (`id_Empresa`),
  CONSTRAINT `FKA8E2B3C06813BD87` FOREIGN KEY (`id_Empresa`) REFERENCES `empresa` (`id_Empresa`),
  CONSTRAINT `FKA8E2B3C0D4B2D407` FOREIGN KEY (`id_Localidad`) REFERENCES `localidad` (`id_Localidad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `id_Usuario` bigint(20) NOT NULL AUTO_INCREMENT,
  `eliminado` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `permisosAdministrador` bit(1) NOT NULL,
  PRIMARY KEY (`id_Usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-12-07 19:25:25
