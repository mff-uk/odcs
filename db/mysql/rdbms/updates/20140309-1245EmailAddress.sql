DROP INDEX `ix_SCH_EMAIL_email` on `sch_email`;

ALTER TABLE `sch_email` ADD `email` VARCHAR(255);

UPDATE `sch_email` SET email = concat(e_user, '@', e_domain);

ALTER TABLE `sch_email` DROP `e_user`;
ALTER TABLE `sch_email` DROP `e_domain`;

CREATE INDEX `ix_SCH_EMAIL_email` ON `sch_email` (`email`);
