-- Update permission table
ALTER TABLE permission RENAME COLUMN rwonly TO sharedEntityInstanceWriteRequired;
-- Admin does not have any permissions besides administrator permission
DELETE FROM "user_role_permission" WHERE role_id = (SELECT id FROM role WHERE name = 'Administrator') AND permission_id != (SELECT id FROM permission WHERE name = 'administrator');
-- Update version.
UPDATE "properties" SET "value" = '002.001.000' WHERE "key" = 'UV.Core.version';
UPDATE "properties" SET "value" = '002.000.000' WHERE "key" = 'UV.Plugin-DevEnv.version';

-- Add new columns
ALTER TABLE "dpu_instance"
ADD COLUMN "menu_name" VARCHAR(255);
ALTER TABLE "dpu_template"
ADD COLUMN "menu_name" VARCHAR(255);

-- Permission changes
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.definePipelineDependencies', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
UPDATE permission SET sharedEntityInstanceWriteRequired = true WHERE name = 'pipeline.schedule';
UPDATE permission SET sharedEntityInstanceWriteRequired = true WHERE name = 'pipeline.runDebug';
UPDATE permission SET sharedEntityInstanceWriteRequired = true WHERE name = 'pipeline.run';
UPDATE permission SET name = 'dpuTemplate.setVisibility', "sharedEntityInstanceWriteRequired" = true WHERE name = 'dpuTemplate.setVisibilityAtCreate';
UPDATE permission SET name = 'pipeline.setVisibility', "sharedEntityInstanceWriteRequired" = true WHERE name = 'pipeline.setVisibilityAtCreate';
DELETE FROM permission WHERE name = 'pipelineExecution.downloadAllLogs';
DELETE FROM permission WHERE name = 'pipelineExecution.readDpuInputOutputData';
DELETE FROM permission WHERE name = 'pipelineExecution.readEvent';
DELETE FROM permission WHERE name = 'pipelineExecution.readLog';
DELETE FROM permission WHERE name = 'pipelineExecution.sparqlDpuInputOutputData';
DELETE FROM permission WHERE name = 'scheduleRule.disable';
DELETE FROM permission WHERE name = 'scheduleRule.enable';
DELETE FROM permission WHERE name = 'role.create';
DELETE FROM permission WHERE name = 'role.edit';
DELETE FROM permission WHERE name = 'role.read';
DELETE FROM permission WHERE name = 'role.delete';
DELETE FROM permission WHERE name = 'user.create';
DELETE FROM permission WHERE name = 'user.edit';
DELETE FROM permission WHERE name = 'user.login';
DELETE FROM permission WHERE name = 'user.read';
DELETE FROM permission WHERE name = 'user.delete';
DELETE FROM permission WHERE name = 'pipeline.save';
UPDATE permission SET sharedEntityInstanceWriteRequired = true WHERE name = 'scheduleRule.execute';
DELETE FROM permission WHERE name = 'deleteDebugResources';
DELETE FROM permission WHERE name = 'dpuTemplate.save';
DELETE FROM permission WHERE name = 'dpuTemplate.import';
INSERT INTO permission VALUES (nextval('seq_permission'), 'dpuTemplate.createFromInstance', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.setVisibilityPublicRw', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name='pipeline.setVisibility'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), (select id from "permission" where name='pipeline.setVisibility'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.showScreen', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
DELETE FROM "user_role_permission" WHERE role_id = (select id from "role" where name='MOD-R-TRANSA') AND permission_id = (select id from "permission" where name='administrator');
-- Map existing permissions to roles
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'pipeline.exportScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), (select id from "permission" where name = 'pipeline.exportScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), (select id from "permission" where name = 'pipeline.exportScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'pipeline.importScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), (select id from "permission" where name = 'pipeline.importScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), (select id from "permission" where name = 'pipeline.importScheduleRules'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'pipeline.importUserData'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), (select id from "permission" where name = 'pipeline.importUserData'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), (select id from "permission" where name = 'pipeline.importUserData'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name = 'dpuTemplate.setVisibility'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), (select id from "permission" where name = 'dpuTemplate.setVisibility'));
-- Remove permissions for eDemo roles
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.export') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-PO');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.export') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-TRANSA');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.exportDpuData') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-PO');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.exportDpuData') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-TRANSA');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.exportDpuJars') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-PO');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.exportDpuJars') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-TRANSA');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.exportScheduleRules') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-PO');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.exportScheduleRules') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-TRANSA');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.import') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-PO');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.import') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-TRANSA');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.importScheduleRules') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-PO');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.importScheduleRules') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-TRANSA');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.importUserData') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-PO');
DELETE FROM "user_role_permission" WHERE permission_id = (SELECT id FROM "permission" WHERE name = 'pipeline.importUserData') AND role_id = (SELECT id FROM "role" WHERE name = 'MOD-R-TRANSA');
-- Organizations removed
DROP VIEW pipeline_view;
DROP VIEW exec_view;

CREATE SEQUENCE "seq_user_actor" START 1;
CREATE TABLE "user_actor"
(
    "id" INTEGER,
    "id_extuser" VARCHAR(256) NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    PRIMARY KEY ("id"),
    UNIQUE ("id_extuser")
);

ALTER TABLE exec_pipeline DROP COLUMN organization_id;
ALTER TABLE exec_pipeline ADD COLUMN user_actor_id INTEGER;
CREATE INDEX "ix_EXEC_PIPELINE_actor_id" ON "exec_pipeline" ("user_actor_id");
ALTER TABLE exec_schedule DROP COLUMN organization_id;
ALTER TABLE exec_schedule ADD COLUMN user_actor_id INTEGER;
CREATE INDEX "ix_EXEC_SCHEDULE_user_actor_id" ON "exec_schedule" ("user_actor_id");
ALTER TABLE ppl_model DROP COLUMN organization_id;
ALTER TABLE ppl_model ADD COLUMN user_actor_id INTEGER;
CREATE INDEX "ix_PPL_MODEL_user_actor_id" ON "ppl_model" ("user_actor_id");
DROP TABLE organization;

-- Constraints
ALTER TABLE "exec_pipeline"
ADD FOREIGN KEY ("user_actor_id")
    REFERENCES "user_actor" ("id")
    ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "exec_schedule"
ADD FOREIGN KEY ("user_actor_id")
    REFERENCES "user_actor" ("id")
    ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "ppl_model"
ADD FOREIGN KEY ("user_actor_id")
    REFERENCES "user_actor" ("id")
    ON UPDATE CASCADE ON DELETE CASCADE;

-- Recreate views
CREATE VIEW "pipeline_view" AS
SELECT ppl.id AS id, ppl.name AS name, exec.t_start AS t_start, exec.t_end AS t_end, exec.status AS status, usr.username as usr_name, usr.full_name as usr_full_name, 
ppl.visibility AS visibility, actor.name AS user_actor_name FROM "ppl_model" AS ppl
LEFT JOIN "exec_last_view" AS exec ON exec.pipeline_id = ppl.id
LEFT JOIN "usr_user" AS usr ON ppl.user_id = usr.id 
LEFT JOIN "user_actor" AS actor ON ppl.user_actor_id = actor.id;

CREATE VIEW "exec_view" AS
SELECT exec.id AS id, exec.status AS status, ppl.id AS pipeline_id, ppl.name AS pipeline_name, exec.debug_mode AS debug_mode, exec.t_start AS t_start, 
exec.t_end AS t_end, exec.schedule_id AS schedule_id, owner.username AS owner_name, owner.full_name AS owner_full_name, exec.stop AS stop, exec.t_last_change AS t_last_change, 
actor.name AS user_actor_name FROM "exec_pipeline" AS exec
LEFT JOIN "ppl_model" AS ppl ON ppl.id = exec.pipeline_id
LEFT JOIN "usr_user" AS owner ON owner.id = exec.owner_id
LEFT JOIN "user_actor" AS actor ON actor.id = exec.user_actor_id;