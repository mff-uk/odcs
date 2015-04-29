-- Update permission table
ALTER TABLE `permission` CHANGE `rwonly` `write` boolean;
-- Permission changes
INSERT INTO `permission` VALUES (NULL, 'pipeline.definePipelineDependencies', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM  `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM `permission`));
UPDATE `permission` SET `write` = true WHERE name = 'pipeline.schedule';
UPDATE `permission` SET `write` = true WHERE name = 'pipeline.runDebug';
UPDATE `permission` SET `write` = true WHERE name = 'pipeline.run';
DELETE FROM `permission` WHERE name = 'pipelineExecution.downloadAllLogs';
DELETE FROM `permission` WHERE name = 'pipelineExecution.readDpuInputOutputData';
DELETE FROM `permission` WHERE name = 'pipelineExecution.readEvent';
DELETE FROM `permission` WHERE name = 'pipelineExecution.readLog';
DELETE FROM `permission` WHERE name = 'pipelineExecution.sparqlDpuInputOutputData';
DELETE FROM `permission` WHERE name = 'scheduleRule.disable';
DELETE FROM `permission` WHERE name = 'scheduleRule.enable';
UPDATE `permission` SET `write` = true WHERE name = 'scheduleRule.execute';
DELETE FROM `permission` WHERE name = 'deleteDebugResources';
DELETE FROM `permission` WHERE name = 'dpuTemplate.save';
DELETE FROM `permission` WHERE name = 'dpuTemplate.import';
INSERT INTO `permission` VALUES (NULL, 'dpuTemplate.createFromInstance', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM `permission`));
INSERT INTO `permission` VALUES (NULL, 'pipeline.setVisibilityPublicRw', false);
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (SELECT max(id) FROM `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (SELECT max(id) FROM `permission`));
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (select id from `permission` where name='pipeline.setVisibilityAtCreate'));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (select id from `permission` where name='pipeline.setVisibilityAtCreate'));
-- Map existing permissions to roles
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (select id from `permission` where name = 'pipeline.exportScheduleRules'));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (select id from `permission` where name = 'pipeline.exportScheduleRules'));
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (select id from `permission` where name = 'pipeline.importScheduleRules'));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (select id from `permission` where name = 'pipeline.importScheduleRules'));
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (select id from `permission` where name = 'pipeline.importUserData'));
INSERT INTO `user_role_permission` values((select id from `role` where name='User'), (select id from `permission` where name = 'pipeline.importUserData'));
INSERT INTO `user_role_permission` values((select id from `role` where name='Administrator'), (select id from `permission` where name = 'dpuTemplate.setVisibilityAtCreate'));
-- Organizations removed
-- TODO: fix dropping of constraints, does not work for MySQL, only for Postgres
DROP VIEW `pipeline_view`;
DROP VIEW `exec_view`;

CREATE TABLE `user_actor`
(
  `id` INTEGER,
  `id_extuser` VARCHAR(256) NOT NULL,
  `name` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`id_extuser`)
);

ALTER TABLE `exec_schedule` DROP COLUMN organization_id;
ALTER TABLE exec_schedule ADD COLUMN user_actor_id INTEGER;
CREATE INDEX `ix_EXEC_SCHEDULE_user_actor_id` ON `exec_schedule` (`user_actor_id`);
ALTER TABLE `exec_pipeline` DROP COLUMN organization_id;
ALTER TABLE exec_pipeline ADD COLUMN user_actor_id INTEGER;
CREATE INDEX `ix_EXEC_PIPELINE_actor_id` ON `exec_pipeline` (`user_actor_id`);
ALTER TABLE `ppl_model` DROP COLUMN organization_id;
ALTER TABLE ppl_model ADD COLUMN user_actor_id INTEGER;
CREATE INDEX `ix_PPL_MODEL_user_actor_id` ON `ppl_model` (`user_actor_id`);
DROP TABLE `organization`;

-- Constraints
ALTER TABLE `exec_pipeline`
ADD FOREIGN KEY (`user_actor_id`)
    REFERENCES `user_actor` (`id`)
    ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE `exec_schedule`
ADD FOREIGN KEY (`user_actor_id`)
    REFERENCES `user_actor` (`id`)
    ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE `ppl_model`
ADD FOREIGN KEY (`user_actor_id`)
    REFERENCES `user_actor` (`id`)
    ON UPDATE CASCADE ON DELETE CASCADE;


CREATE VIEW `pipeline_view` AS
SELECT ppl.id AS id, ppl.name AS name, exec.t_start AS t_start, exec.t_end AS t_end, exec.status AS status, usr.username as usr_name, ppl.visibility AS visibility,
actor.name AS user_actor_name FROM `ppl_model` AS ppl
LEFT JOIN `exec_last_view` AS exec ON exec.pipeline_id = ppl.id
LEFT JOIN `usr_user` AS usr ON ppl.user_id = usr.id
LEFT JOIN `user_actor` AS actor ON ppl.user_actor_id = actor.id;

CREATE VIEW `exec_view` AS
SELECT exec.id AS id, exec.status AS status, ppl.id AS pipeline_id, ppl.name AS pipeline_name, exec.debug_mode AS debug_mode, exec.t_start AS t_start, 
exec.t_end AS t_end, exec.schedule_id AS schedule_id, owner.username AS owner_name, exec.stop AS stop, exec.t_last_change AS t_last_change, actor.name AS user_actor_name
FROM `exec_pipeline` AS exec
LEFT JOIN `ppl_model` AS ppl ON ppl.id = exec.pipeline_id
LEFT JOIN `usr_user` AS owner ON owner.id = exec.owner_id
LEFT JOIN `user_actor` AS actor ON actor.id = exec.user_actor_id;