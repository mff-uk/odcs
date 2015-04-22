-- Permission changes
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.definePipelineDependencies', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
UPDATE permission SET rwonly = true WHERE name = 'pipeline.schedule';
UPDATE permission SET rwonly = true WHERE name = 'pipeline.runDebug';
UPDATE permission SET rwonly = true WHERE name = 'pipeline.run';
DELETE FROM permission WHERE name = 'pipelineExecution.downloadAllLogs';
DELETE FROM permission WHERE name = 'pipelineExecution.readDpuInputOutputData';
DELETE FROM permission WHERE name = 'pipelineExecution.readEvent';
DELETE FROM permission WHERE name = 'pipelineExecution.readLog';
DELETE FROM permission WHERE name = 'pipelineExecution.sparqlDpuInputOutputData';
DELETE FROM permission WHERE name = 'scheduleRule.disable';
DELETE FROM permission WHERE name = 'scheduleRule.enable';
UPDATE permission SET rwonly = true WHERE name = 'scheduleRule.execute';
DELETE FROM permission WHERE name = 'deleteDebugResources';
DELETE FROM permission WHERE name = 'dpuTemplate.save';
DELETE FROM permission WHERE name = 'dpuTemplate.import';
INSERT INTO permission VALUES (nextval('seq_permission'), 'dpuTemplate.createFromInstance', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-PO'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.setVisibilityPublicRw', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), (select id from "permission" where name='pipeline.setVisibilityAtCreate'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), (select id from "permission" where name='pipeline.setVisibilityAtCreate'));
INSERT INTO "user_role_permission" values((select id from "role" where name='MOD-R-TRANSA'), (select id from "permission" where name='pipeline.setVisibilityAtCreate'));