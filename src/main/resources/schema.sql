-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema paymybuddy
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema paymybuddy
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `paymybuddy` DEFAULT CHARACTER SET utf8 ;
USE `paymybuddy` ;

-- -----------------------------------------------------
-- Table `paymybuddy`.`User`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`User` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `password` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB
COMMENT = 'Regroupe toutes les information d\'un utilisateur.';


-- -----------------------------------------------------
-- Table `paymybuddy`.`Transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`Transaction` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `senderId` INT NOT NULL,
  `receiverId` INT NOT NULL,
  `description` VARCHAR(45) NULL,
  `amount` DOUBLE NULL,
  PRIMARY KEY (`id`),
  INDEX `senderId [FK]_idx` (`senderId` ASC) VISIBLE,
  INDEX `recieverId [FK]_idx` (`receiverId` ASC) VISIBLE,
  CONSTRAINT `senderId [FK]`
    FOREIGN KEY (`senderId`)
    REFERENCES `paymybuddy`.`User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `recieverId [FK]`
    FOREIGN KEY (`receiverId`)
    REFERENCES `paymybuddy`.`User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `paymybuddy`.`Contact`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`Contact` (
  `userId` INT NOT NULL,
  `contactUserId` INT NOT NULL,
  PRIMARY KEY (`userId`, `contactUserId`),
  INDEX `contactUser [FK]_idx` (`contactUserId` ASC) VISIBLE,
  CONSTRAINT `userId [FK]`
    FOREIGN KEY (`userId`)
    REFERENCES `paymybuddy`.`User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `contactUser [FK]`
    FOREIGN KEY (`contactUserId`)
    REFERENCES `paymybuddy`.`User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `paymybuddy`.`Account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`Account` (
  `id` INT NOT NULL,
  `userId` INT NOT NULL,
  `balance` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  INDEX `userId [FK]_idx` (`userId` ASC) VISIBLE,
  CONSTRAINT `userId account [FK]`
    FOREIGN KEY (`userId`)
    REFERENCES `paymybuddy`.`User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
