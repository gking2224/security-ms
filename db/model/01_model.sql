-- MySQL Script generated by MySQL Workbench
-- Fri Nov 11 08:53:20 2016
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema scr_lcl
-- -----------------------------------------------------
SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `User`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `User` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL,
  `password` VARCHAR(60) NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `firstName` VARCHAR(45) NOT NULL,
  `surname` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_id`))
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE UNIQUE INDEX `username_UNIQUE` ON `User` (`username` ASC);

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `Role`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Role` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `Role` (
  `role_id` BIGINT NOT NULL,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`role_id`))
ENGINE = InnoDB;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `Permission`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Permission` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `Permission` (
  `permission_id` BIGINT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `parent_id` BIGINT NULL,
  PRIMARY KEY (`permission_id`),
  CONSTRAINT `fk_permission_permission`
    FOREIGN KEY (`parent_id`)
    REFERENCES `Permission` (`permission_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE UNIQUE INDEX `name_UNIQUE` ON `Permission` (`name` ASC);

SHOW WARNINGS;
CREATE INDEX `fk_permission_permission_idx` ON `Permission` (`parent_id` ASC);

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `UserRole`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `UserRole` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `UserRole` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  CONSTRAINT `fk_user_has_role_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `User` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_role_role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `Role` (`role_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE INDEX `fk_user_has_role_role1_idx` ON `UserRole` (`role_id` ASC);

SHOW WARNINGS;
CREATE INDEX `fk_user_has_role_user1_idx` ON `UserRole` (`user_id` ASC);

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `RolePermission`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RolePermission` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `RolePermission` (
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`role_id`, `permission_id`),
  CONSTRAINT `fk_role_has_permission_role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `Role` (`role_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_role_has_permission_permission1`
    FOREIGN KEY (`permission_id`)
    REFERENCES `Permission` (`permission_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE INDEX `fk_role_has_permission_permission1_idx` ON `RolePermission` (`permission_id` ASC);

SHOW WARNINGS;
CREATE INDEX `fk_role_has_permission_role1_idx` ON `RolePermission` (`role_id` ASC);

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `UserPermission`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `UserPermission` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `UserPermission` (
  `user_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`user_id`, `permission_id`),
  CONSTRAINT `fk_user_has_permission_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `User` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_permission_permission1`
    FOREIGN KEY (`permission_id`)
    REFERENCES `Permission` (`permission_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE INDEX `fk_user_has_permission_permission1_idx` ON `UserPermission` (`permission_id` ASC);

SHOW WARNINGS;
CREATE INDEX `fk_user_has_permission_user1_idx` ON `UserPermission` (`user_id` ASC);

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `Token`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Token` ;

SHOW WARNINGS;
CREATE TABLE IF NOT EXISTS `Token` (
  `token_id` BIGINT NOT NULL AUTO_INCREMENT,
  `token` MEDIUMTEXT NULL,
  `expiry` BIGINT NOT NULL,
  `valid` TINYINT(1) NOT NULL DEFAULT 1,
  `invalidationComment` VARCHAR(255) NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`token_id`),
  CONSTRAINT `fk_token_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `User` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SHOW WARNINGS;
CREATE INDEX `fk_token_user1_idx` ON `Token` (`user_id` ASC);

SHOW WARNINGS;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `User`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `User` (`user_id`, `username`, `password`, `enabled`, `firstName`, `surname`) VALUES (-1, 'super', '\\\\$2a\\\\$10\\\\$o4O4U.WFvUV5pTG/vx2r1.zF5QrjCF.4bE6pK8cK8X3Fy8yC7Vszq', 1, 'Super', 'User');

COMMIT;


-- -----------------------------------------------------
-- Data for table `Role`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `Role` (`role_id`, `name`) VALUES (1, 'SuperUser');

COMMIT;


-- -----------------------------------------------------
-- Data for table `Permission`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (1, 'SuperUser', NULL);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (2, 'BudgetPermissions', 1);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (3, 'DeleteBudget', 2);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (4, 'CreateBudget', 3);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (5, 'EditBudget', 4);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (6, 'ViewBudgetDetail', 5);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (7, 'ViewBudgetSummary', 6);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (8, 'ProjectPermissions', 1);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (9, 'DeleteProject', 8);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (10, 'CreateProject', 9);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (11, 'EditProject', 10);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (12, 'ViewProjectDetail', 11);
INSERT INTO `Permission` (`permission_id`, `name`, `parent_id`) VALUES (13, 'ViewProjectSummary', 12);

COMMIT;


-- -----------------------------------------------------
-- Data for table `UserRole`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `UserRole` (`user_id`, `role_id`) VALUES (-1, 1);

COMMIT;


-- -----------------------------------------------------
-- Data for table `RolePermission`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `RolePermission` (`role_id`, `permission_id`, `enabled`) VALUES (1, 1, 1);

COMMIT;

