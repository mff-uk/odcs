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

ALTER TABLE `dpu_instance` ADD COLUMN `use_template_config` SMALLINT NOT NULL DEFAULT 0;