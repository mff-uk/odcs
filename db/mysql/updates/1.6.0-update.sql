# Update loggin table, rename column logLevel into log_level.
ALTER TABLE `uv`.`logging` CHANGE COLUMN `logLevel` `log_level` INT(11) NOT NULL  ;
