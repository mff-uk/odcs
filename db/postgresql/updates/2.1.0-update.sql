-- Update permission table
ALTER TABLE permission RENAME COLUMN rwonly TO write;
-- Permission changes
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.definePipelineDependencies', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
UPDATE permission SET write = true WHERE name = 'pipeline.schedule';
UPDATE permission SET write = true WHERE name = 'pipeline.runDebug';
UPDATE permission SET write = true WHERE name = 'pipeline.run';
DELETE FROM permission WHERE name = 'pipelineExecution.downloadAllLogs';
DELETE FROM permission WHERE name = 'pipelineExecution.readDpuInputOutputData';
DELETE FROM permission WHERE name = 'pipelineExecution.readEvent';
DELETE FROM permission WHERE name = 'pipelineExecution.readLog';
DELETE FROM permission WHERE name = 'pipelineExecution.sparqlDpuInputOutputData';
DELETE FROM permission WHERE name = 'scheduleRule.disable';
DELETE FROM permission WHERE name = 'scheduleRule.enable';
UPDATE permission SET write = true WHERE name = 'scheduleRule.execute';
DELETE FROM permission WHERE name = 'deleteDebugResources';
DELETE FROM permission WHERE name = 'dpuTemplate.save';
DELETE FROM permission WHERE name = 'dpuTemplate.import';
INSERT INTO permission VALUES (nextval('seq_permission'), 'dpuTemplate.createFromInstance', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.setVisibilityPublicRw', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name='pipeline.setVisibilityAtCreate'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name='pipeline.setVisibilityAtCreate'));
-- Map existing permissions to roles
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name = 'pipeline.exportScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'pipeline.exportScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name = 'pipeline.importScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'pipeline.importScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name = 'pipeline.importUserData'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'pipeline.importUserData'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name = 'dpuTemplate.setVisibilityAtCreate'));
-- Organizations removed
DROP VIEW pipeline_view;
DROP VIEW exec_view;
ALTER TABLE dpu_template DROP COLUMN organization_id;
ALTER TABLE dpu_template DROP COLUMN exec_pipeline;
ALTER TABLE dpu_template DROP COLUMN exec_schedule;
ALTER TABLE dpu_template DROP COLUMN ppl_model;
DROP TABLE organization;

CREATE VIEW "pipeline_view" AS
SELECT ppl.id AS id, ppl.name AS name, exec.t_start AS t_start, exec.t_end AS t_end, exec.status AS status, usr.username as usr_name, ppl.visibility AS visibility FROM "ppl_model" AS ppl
LEFT JOIN "exec_last_view" AS exec ON exec.pipeline_id = ppl.id
LEFT JOIN "usr_user" AS usr ON ppl.user_id = usr.id;

CREATE VIEW "exec_view" AS
SELECT exec.id AS id, exec.status AS status, ppl.id AS pipeline_id, ppl.name AS pipeline_name, exec.debug_mode AS debug_mode, exec.t_start AS t_start, 
exec.t_end AS t_end, exec.schedule_id AS schedule_id, owner.username AS owner_name, exec.stop AS stop, exec.t_last_change AS t_last_change
FROM "exec_pipeline" AS exec
LEFT JOIN "ppl_model" AS ppl ON ppl.id = exec.pipeline_id
LEFT JOIN "usr_user" AS owner ON owner.id = exec.owner_id;