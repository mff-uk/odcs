-- Update version.
UPDATE `properties` SET `value` = '001.005.000' WHERE `key` = 'UV.Core.version';
UPDATE `properties` SET `value` = '001.001.000' WHERE `key` = 'UV.Plugin-DevEnv.version';

-- Add property for template shared configuration.
ALTER TABLE `dpu_instance` ADD COLUMN `use_template_config` SMALLINT NOT NULL DEFAULT 0;
