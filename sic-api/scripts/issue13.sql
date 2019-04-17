--Agrega nuevo campo NombreFantasia a la tabla Cliente
ALTER TABLE `sic`.`cliente` 
ADD COLUMN `nombreFantasia` VARCHAR(255) NULL DEFAULT '' AFTER `nombre`;

--Cambia el nombre del campo nombre por el de razonSocial
ALTER TABLE `sic`.`cliente` 
CHANGE COLUMN `nombre` `razonSocial` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ;