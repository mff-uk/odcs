CREATE TABLE `runtime_properties`
(
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NULL,
  `value` VARCHAR(100) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO runtime_properties (name, value) VALUES ('backend.scheduledPipelines.limit', '5');