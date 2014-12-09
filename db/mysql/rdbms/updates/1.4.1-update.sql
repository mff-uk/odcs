-- Update version.
UPDATE `properties` SET `value` = '001.004.001' WHERE `key` = 'UV.Core.version';
UPDATE `properties` SET `value` = '001.000.001' WHERE `key` = 'UV.Plugin-DevEnv.version';

-- Add property for template shared configuration.
ALTER TABLE `dpu_instance` ADD COLUMN `use_template_config` SMALLINT NOT NULL DEFAULT 0;

-- Views.

# Last execution for each pipeline
CREATE VIEW `uv`.`exec_last_view` AS
SELECT id, pipeline_id, t_end, t_start, status
FROM `uv`.`exec_pipeline` AS exec
WHERE t_end = (SELECT max(t_end) FROM `uv`.`exec_pipeline` AS lastExec WHERE exec.pipeline_id = lastExec.pipeline_id)

# Pipeline list
CREATE VIEW `uv`.`pipeline_view` AS
SELECT ppl.id AS id, ppl.name AS name, exec.t_start AS t_start, exec.t_end AS t_end, exec.status AS status
FROM `uv`.`ppl_model` AS ppl
LEFT JOIN `uv`.`exec_last_view` AS exec ON exec.pipeline_id = ppl.id

# Execution list.
CREATE VIEW `uv`.`exec_view` AS
SELECT exec.id AS id, exec.status AS status, ppl.id AS pipeline_id, ppl.name AS pipeline_name, exec.debug_mode AS debug_mode, exec.t_start AS t_start, exec.t_end AS t_end, exec.schedule_id AS schedule_id, owner.username AS owner_name, exec.stop AS stop, exec.t_last_change AS t_last_change
FROM `uv`.`exec_pipeline` AS exec
JOIN `uv`.`ppl_model` AS ppl ON ppl.id = exec.pipeline_id
JOIN `uv`.`usr_user` AS owner ON owner.id = exec.owner_id
