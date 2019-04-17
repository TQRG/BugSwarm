START TRANSACTION;

CREATE TABLE `sic`.`rol` (
  `id_Usuario` BIGINT(20) NOT NULL,
  `tipo` VARCHAR(255) NULL DEFAULT NULL);

ALTER TABLE `sic`.`facturaventa` 
ADD COLUMN `id_Viajante` BIGINT(20) NULL DEFAULT NULL AFTER `id_Usuario`;

ALTER TABLE `sic`.`cliente` 
ADD COLUMN `id_Usuario_Credencial` BIGINT(20) NULL DEFAULT NULL AFTER `id_Localidad`,
ADD COLUMN `id_Usuario_Viajante` BIGINT(20) NULL DEFAULT NULL AFTER `id_Usuario_Credencial`;

COMMIT;
