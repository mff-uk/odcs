﻿INSERT INTO "properties" VALUES ('UV.Core.version','001.006.000'),('UV.Plugin-DevEnv.version','001.002.000');
INSERT INTO "sch_email" VALUES (nextval('seq_sch_email'),'admin@example.com'),(nextval('seq_sch_email'),'user@example.com');
INSERT INTO "role" VALUES (nextval('seq_role'), 'Administrator'),(nextval('seq_role'),'User'),(nextval('seq_role'),'Povinna osoba'),(nextval('seq_role'),'Spravca transformacii'); 

--INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.definePipelineDependencies');
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'spravca.transformacii', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.delete', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.save', true);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.edit', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.export', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.exportDpuData', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.exportDpuJars', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.exportScheduleRules', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.import', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.importScheduleRules', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.importUserData', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.run', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.runDebug', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.setVisibilityAtCreate', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.delete', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.create', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.downloadAllLogs', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.readDpuInputOutputData', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.readEvent', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.readLog', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipelineExecution.sparqlDpuInputOutputData', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.create', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.delete', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.edit', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.execute', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'scheduleRule.setPriority', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.create', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.save', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.setVisibilityAtCreate', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.delete', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.edit', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.export', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.import', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'dpuTemplate.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'user.management', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'user.create', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'user.edit', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'user.login', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'user.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'user.delete', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'role.create', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'role.edit', false);
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'role.read', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'role.delete', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.create', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'pipeline.copy', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'deleteDebugResources', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'runtimeProperties.edit', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'editEmailGlobal', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));
INSERT INTO "permission" VALUES (nextval('seq_permission'), 'editNotificationFrequency', false);
INSERT INTO "user_role_permission" values((select id from "role" where name='Administrator'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='User'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Povinna osoba'), currval('seq_permission'));
INSERT INTO "user_role_permission" values((select id from "role" where name='Spravca transformacii'), currval('seq_permission'));

INSERT INTO "usr_user" VALUES (nextval('seq_usr_user'),'admin',1,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John Admin',20);
INSERT INTO "usr_extuser" VALUES (currval('seq_usr_user'), 'admin');
INSERT INTO "usr_user" VALUES (nextval('seq_usr_user'),'user',2,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John User',20);
INSERT INTO "usr_extuser" VALUES (currval('seq_usr_user'), 'user');

INSERT INTO "sch_usr_notification" VALUES (nextval('seq_sch_notification'),1,1,1),(nextval('seq_sch_notification'),2,1,1);
INSERT INTO "sch_usr_notification_email" VALUES (1,1),(2,2);
INSERT INTO "usr_user_role" VALUES (1,1),(1,2),(2,1);
INSERT INTO "runtime_properties" ("id", "name", "value") VALUES (nextval('seq_runtime_properties'), 'backend.scheduledPipelines.limit', '5');
INSERT INTO "runtime_properties" ("id", "name", "value") VALUES (nextval('seq_runtime_properties'), 'run.now.pipeline.priority', '1');
INSERT INTO "runtime_properties" ("id", "name", "value") VALUES (nextval('seq_runtime_properties'), 'locale', 'en');