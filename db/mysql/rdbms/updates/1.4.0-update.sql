ALTER TABLE exec_pipeline ADD order_number BIGINT  not null;
ALTER TABLE exec_schedule ADD priority BIGINT not null;

UPDATE exec_pipeline SET order_number = 1 ;
UPDATE exec_schedule SET priority = 1 ;

CREATE TABLE `runtime_properties`
(
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NULL,
  `value` VARCHAR(100) NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `runtime_properties` (name, value) VALUES ('backend.scheduledPipelines.limit', '5');
INSERT INTO `runtime_properties` (name, value) VALUES ('run.now.pipeline.priority', '1');

-- make sure that logging table use proper engine, this operation may take some time
ALTER TABLE logging ENGINE = innodb;

UPDATE `properties` SET `value` = '001.004.000' WHERE `key` = 'UV.Core.version';
UPDATE `properties` SET `value` = '001.000.001' WHERE `key` = 'UV.Plugin-DevEnv.version';
--UPDATE `properties` SET `value` = '001.004.000' WHERE `key` = 'UV.Plugins.version';
