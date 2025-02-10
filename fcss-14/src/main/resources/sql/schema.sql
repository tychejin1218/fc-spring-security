CREATE TABLE IF NOT EXISTS `sample`.`token` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `identifier` VARCHAR(50) NOT NULL,
    `token` TEXT NULL,
    PRIMARY KEY(`id`)
);
