
DROP TABLE "DB"."ODCS"."EXEC_DATAUNIT_INFO";
DROP TABLE "DB"."ODCS"."EXEC_CONTEXT_DPU";
DROP TABLE "DB"."ODCS"."EXEC_RECORD";
DROP TABLE "DB"."ODCS"."PPL_OPEN_EVENT";
DROP TABLE "DB"."ODCS"."EXEC_PIPELINE";
DROP TABLE "DB"."ODCS"."EXEC_CONTEXT_PIPELINE";
DROP TABLE "DB"."ODCS"."EXEC_SCHEDULE_AFTER";
DROP TABLE "DB"."ODCS"."PPL_EDGE";
DROP TABLE "DB"."ODCS"."PPL_NODE";
DROP TABLE "DB"."ODCS"."PPL_GRAPH";
DROP TABLE "DB"."ODCS"."SCH_SCH_NOTIFICATION_EMAIL";
DROP TABLE "DB"."ODCS"."SCH_SCH_NOTIFICATION";
DROP TABLE "DB"."ODCS"."SCH_USR_NOTIFICATION_EMAIL";
DROP TABLE "DB"."ODCS"."SCH_USR_NOTIFICATION";
DROP TABLE "DB"."ODCS"."EXEC_SCHEDULE";
DROP TABLE "DB"."ODCS"."PPL_PPL_CONFLICTS";
DROP TABLE "DB"."ODCS"."PPL_MODEL";
DROP TABLE "DB"."ODCS"."DPU_INSTANCE";
DROP TABLE "DB"."ODCS"."DPU_TEMPLATE";
DROP TABLE "DB"."ODCS"."PPL_POSITION";
DROP TABLE "DB"."ODCS"."USR_USER_ROLE";
DROP TABLE "DB"."ODCS"."USR_USER";
DROP TABLE "DB"."ODCS"."SCH_EMAIL";
DROP TABLE "DB"."ODCS"."RDF_NS_PREFIX";

-- dev. note:
-- when updating size of limits for fields update also the limitations
-- in given class as well in commons.app.constants.LenghtLimits


sequence_set('seq_dpu_record', 100, 1); -- shared sequence for both dpu_instance and dpu_template
CREATE TABLE "DB"."ODCS"."DPU_INSTANCE"
(
-- DPURecord
  "id" INTEGER IDENTITY,
  "name" VARCHAR(1024),
  "use_dpu_description" SMALLINT,
  "description" LONG VARCHAR,
  "configuration" LONG NVARCHAR,
  "config_valid" SMALLINT,
-- DPUInstaceRecord
  "dpu_id" INTEGER,
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_DPU_INSTANCE_dpu_id" ON "DB"."ODCS"."DPU_INSTANCE" ("dpu_id");

CREATE TABLE "DB"."ODCS"."DPU_TEMPLATE"
(
-- DPURecord
  "id" INTEGER IDENTITY,
  "name" VARCHAR(1024),
  "use_dpu_description" SMALLINT,
  "description" LONG VARCHAR,  
  "configuration" LONG NVARCHAR,
  "parent_id" INTEGER,
  "config_valid" SMALLINT NOT NULL,
-- DPUTemplateRecord
  "user_id" INTEGER,
  "visibility" SMALLINT,
  "type" SMALLINT,
  "jar_directory" VARCHAR(255),
  "jar_name" VARCHAR(255),
  "jar_description" VARCHAR(1024),  
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_DPU_TEMPLATE_jar_directory" ON "DB"."ODCS"."DPU_TEMPLATE" ("jar_directory");
CREATE INDEX "ix_DPU_TEMPLATE_parent_id" ON "DB"."ODCS"."DPU_TEMPLATE" ("parent_id");
CREATE INDEX "ix_DPU_TEMPLATE_user_id" ON "DB"."ODCS"."DPU_TEMPLATE" ("user_id");
CREATE INDEX "ix_DPU_TEMPLATE_visibility" ON "DB"."ODCS"."DPU_TEMPLATE" ("visibility");

sequence_set('seq_exec_dataunit_info', 100, 1);
CREATE TABLE "DB"."ODCS"."EXEC_DATAUNIT_INFO"
(
  "id" INTEGER IDENTITY,
  "name" VARCHAR(2048),
  "idx" INTEGER,
  "type" SMALLINT,
  "is_input" SMALLINT,
  "exec_context_dpu_id" INTEGER,
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_EXEC_DATAUNIT_INFO_exec_context_dpu_id" ON "DB"."ODCS"."EXEC_DATAUNIT_INFO" ("exec_context_dpu_id");

sequence_set('seq_exec_context_pipeline', 100, 1);
CREATE TABLE "DB"."ODCS"."EXEC_CONTEXT_PIPELINE"
(
  "id" INTEGER IDENTITY,
  "dummy" SMALLINT, -- remove if table contains a column without default value
  PRIMARY KEY ("id")
);

sequence_set('seq_exec_context_dpu', 100, 1);
CREATE TABLE "DB"."ODCS"."EXEC_CONTEXT_DPU"
(
  "id" INTEGER IDENTITY,
  "exec_context_pipeline_id" INTEGER,
  "dpu_instance_id" INTEGER,
  "state" SMALLINT,
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_EXEC_CONTEXT_DPU_exec_context_pipeline_id" ON "DB"."ODCS"."EXEC_CONTEXT_DPU" ("exec_context_pipeline_id");
CREATE INDEX "ix_EXEC_CONTEXT_DPU_dpu_instance_id" ON "DB"."ODCS"."EXEC_CONTEXT_DPU" ("dpu_instance_id");

sequence_set('seq_exec_record', 100, 1);
CREATE TABLE "DB"."ODCS"."EXEC_RECORD"
(
  "id" INTEGER IDENTITY,
  "r_time" DATETIME,
  "r_type" SMALLINT,
  "dpu_id" INTEGER,
  "execution_id" INTEGER,
  "short_message" VARCHAR(128),
  "full_message" LONG VARCHAR,
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_EXEC_RECORD_r_time" ON "DB"."ODCS"."EXEC_RECORD" ("r_time");
CREATE INDEX "ix_EXEC_RECORD_r_type" ON "DB"."ODCS"."EXEC_RECORD" ("r_type");
CREATE INDEX "ix_EXEC_RECORD_dpu_id" ON "DB"."ODCS"."EXEC_RECORD" ("dpu_id");
CREATE INDEX "ix_EXEC_RECORD_execution_id" ON "DB"."ODCS"."EXEC_RECORD" ("execution_id");

sequence_set('seq_exec_pipeline', 100, 1);
CREATE TABLE "DB"."ODCS"."EXEC_PIPELINE"
(
  "id" INTEGER IDENTITY,
  "status" INTEGER,
  "pipeline_id" INTEGER,
  "debug_mode" SMALLINT,
  "t_start" DATETIME,
  "t_end" DATETIME,
  "context_id" INTEGER,
  "schedule_id" INTEGER,
  "silent_mode" SMALLINT,
  "debugnode_id" INTEGER,
  "stop" SMALLINT,
  "t_last_change" DATETIME,
  "owner_id" INTEGER,
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_EXEC_PIPELINE_status" ON "DB"."ODCS"."EXEC_PIPELINE" ("status");
CREATE INDEX "ix_EXEC_PIPELINE_pipeline_id" ON "DB"."ODCS"."EXEC_PIPELINE" ("pipeline_id");
CREATE INDEX "ix_EXEC_PIPELINE_debug_mode" ON "DB"."ODCS"."EXEC_PIPELINE" ("debug_mode");
-- Virtuoso 7 cannot handle the following index for some reason, see GH-952.
-- CREATE INDEX "ix_EXEC_PIPELINE_t_start" ON "DB"."ODCS"."EXEC_PIPELINE" ("t_start");
CREATE INDEX "ix_EXEC_PIPELINE_context_id" ON "DB"."ODCS"."EXEC_PIPELINE" ("context_id");
CREATE INDEX "ix_EXEC_PIPELINE_schedule_id" ON "DB"."ODCS"."EXEC_PIPELINE" ("schedule_id");
CREATE INDEX "ix_EXEC_PIPELINE_owner_id" ON "DB"."ODCS"."EXEC_PIPELINE" ("owner_id");

sequence_set('seq_exec_schedule', 100, 1);
CREATE TABLE "DB"."ODCS"."EXEC_SCHEDULE"
(
  "id" INTEGER IDENTITY,
  "description" LONG VARCHAR,
  "pipeline_id" INTEGER NOT NULL,
  "user_id" INTEGER, -- TODO set NOT NULL when users are implemented in frontend
  "just_once" SMALLINT,
  "enabled" SMALLINT,
  "type" SMALLINT,
  "first_exec" DATETIME,
  "last_exec" DATETIME,
  "time_period" INTEGER,
  "period_unit" SMALLINT,
  "strict_timing" SMALLINT,
  "strict_tolerance" INTEGER,
  PRIMARY KEY ("id")
);
-- composite index to optimize fetching schedules following pipeline
CREATE INDEX "ix_EXEC_SCHEDULE_pipeline_id_type" ON "DB"."ODCS"."EXEC_SCHEDULE" ("pipeline_id", "type");
CREATE INDEX "ix_EXEC_SCHEDULE_user_id" ON "DB"."ODCS"."EXEC_SCHEDULE" ("user_id");
CREATE INDEX "ix_EXEC_SCHEDULE_enabled" ON "DB"."ODCS"."EXEC_SCHEDULE" ("enabled");
CREATE INDEX "ix_EXEC_SCHEDULE_type" ON "DB"."ODCS"."EXEC_SCHEDULE" ("type");

CREATE TABLE "DB"."ODCS"."EXEC_SCHEDULE_AFTER"
(
  "schedule_id" INTEGER,
  "pipeline_id" INTEGER,
  PRIMARY KEY ("schedule_id", "pipeline_id")
);

sequence_set('seq_ppl_model', 100, 1);
CREATE TABLE "DB"."ODCS"."PPL_MODEL"
(
  "id" INTEGER IDENTITY,
  "name" VARCHAR(1024),
  "description" LONG VARCHAR,
  "user_id" INTEGER,
  "visibility" SMALLINT,
  "last_change" DATETIME,
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_PPL_MODEL_user_id" ON "DB"."ODCS"."PPL_MODEL" ("user_id");

CREATE TABLE "DB"."ODCS"."PPL_PPL_CONFLICTS"
(
  "pipeline_id" INTEGER IDENTITY,
  "pipeline_conflict_id" INTEGER,
  PRIMARY KEY ("pipeline_id", "pipeline_conflict_id")
);
CREATE INDEX "ix_PPL_PPL_CONFLICTS_pipeline_id" ON "DB"."ODCS"."PPL_PPL_CONFLICTS" ("pipeline_id");

sequence_set('seq_ppl_edge', 100, 1);
CREATE TABLE "DB"."ODCS"."PPL_EDGE"
(
  "id" INTEGER IDENTITY,
  "graph_id" INTEGER,
  "node_from_id" INTEGER,
  "node_to_id" INTEGER,
  "data_unit_name" VARCHAR(2048),
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_PPL_EDGE_graph_id" ON "DB"."ODCS"."PPL_EDGE" ("graph_id");
CREATE INDEX "ix_PPL_EDGE_node_from_id" ON "DB"."ODCS"."PPL_EDGE" ("node_from_id");
CREATE INDEX "ix_PPL_EDGE_node_to_id" ON "DB"."ODCS"."PPL_EDGE" ("node_to_id");

sequence_set('seq_ppl_node', 100, 1);
CREATE TABLE "DB"."ODCS"."PPL_NODE"
(
  "id" INTEGER IDENTITY,
  "graph_id" INTEGER,
  "instance_id" INTEGER,
  "position_id" INTEGER,
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_PPL_NODE_graph_id" ON "DB"."ODCS"."PPL_NODE" ("graph_id");
CREATE INDEX "ix_PPL_NODE_instance_id" ON "DB"."ODCS"."PPL_NODE" ("instance_id");

sequence_set('seq_ppl_graph', 100, 1);
CREATE TABLE "DB"."ODCS"."PPL_GRAPH"
(
  "id" INTEGER IDENTITY,
  "pipeline_id" INTEGER,
  PRIMARY KEY ("id"),
  UNIQUE (pipeline_id)
);
CREATE INDEX "ix_PPL_GRAPH_pipeline_id" ON "DB"."ODCS"."PPL_GRAPH" ("pipeline_id");

sequence_set('seq_ppl_position', 100, 1);
CREATE TABLE "DB"."ODCS"."PPL_POSITION"
(
  "id" INTEGER IDENTITY,
  "pos_x" INTEGER,
  "pos_y" INTEGER,
  PRIMARY KEY ("id")
);

sequence_set('seq_sch_notification', 100, 1); -- shared for both schedule and user notifications
CREATE TABLE "DB"."ODCS"."SCH_SCH_NOTIFICATION"
(
  "id" INTEGER IDENTITY,
  "schedule_id" INTEGER NOT NULL,
  "type_success" SMALLINT,
  "type_error" SMALLINT,
  PRIMARY KEY ("id"),
  UNIQUE (schedule_id)
);

CREATE TABLE "DB"."ODCS"."SCH_USR_NOTIFICATION"
(
  "id" INTEGER IDENTITY,
  "user_id" INTEGER NOT NULL,
  "type_success" SMALLINT,
  "type_error" SMALLINT,
  PRIMARY KEY ("id"),
  UNIQUE (user_id)
);
CREATE INDEX "ix_SCH_USR_NOTIFICATION_user_id" ON "DB"."ODCS"."SCH_USR_NOTIFICATION" ("user_id");

sequence_set('seq_sch_email', 100, 1);
CREATE TABLE "DB"."ODCS"."SCH_EMAIL"
(
  "id" INTEGER IDENTITY,
  "email" VARCHAR(255),
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_SCH_EMAIL_email" ON "DB"."ODCS"."SCH_EMAIL" ("email");

CREATE TABLE "DB"."ODCS"."SCH_SCH_NOTIFICATION_EMAIL"
(
  "notification_id" INTEGER,
  "email_id" INTEGER,
  PRIMARY KEY ("notification_id", "email_id")
);

CREATE TABLE "DB"."ODCS"."SCH_USR_NOTIFICATION_EMAIL"
(
  "notification_id" INTEGER IDENTITY,
  "email_id" INTEGER,
  PRIMARY KEY ("notification_id", "email_id")
);
CREATE INDEX "ix_SCH_USR_NOTIFICATION_EMAIL_email_id" ON "DB"."ODCS"."SCH_USR_NOTIFICATION_EMAIL" ("email_id");

sequence_set('seq_usr_user', 100, 1);
CREATE TABLE "DB"."ODCS"."USR_USER"
(
  "id" INTEGER IDENTITY,
  "username" VARCHAR(25) NOT NULL,
  "email_id" INTEGER,
  "u_password" CHAR(142) NOT NULL,
  "full_name" VARCHAR(55),
  "table_rows" INTEGER,
  PRIMARY KEY ("id"),
  UNIQUE ("username")
);
CREATE INDEX "ix_USR_USER_email_id" ON "DB"."ODCS"."USR_USER" ("email_id");

CREATE TABLE "DB"."ODCS"."USR_USER_ROLE"
(
  "user_id" INTEGER NOT NULL,
  "role_id" INTEGER NOT NULL,
  PRIMARY KEY ("user_id", "role_id")
);

sequence_set('seq_rdf_ns_prefix', 100, 1);
CREATE TABLE "DB"."ODCS"."RDF_NS_PREFIX"
(
  "id" INTEGER IDENTITY,
  "name" VARCHAR(255) NOT NULL,
  "uri" VARCHAR(2048) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE ("name")
);

-- Table with timestamps when was the last time users opened pipelines in canvas
sequence_set('seq_ppl_open_event', 100, 1);
CREATE TABLE "DB"."ODCS"."PPL_OPEN_EVENT"
(
  "id" INTEGER IDENTITY,
  "pipeline_id" INTEGER NOT NULL,
  "user_id" INTEGER NOT NULL,
  "opened" DATETIME NOT NULL,
  PRIMARY KEY ("id")
);
CREATE INDEX "ix_PPL_OPEN_EVENT_pipeline_id" ON "DB"."ODCS"."PPL_OPEN_EVENT" ("pipeline_id");
CREATE INDEX "ix_PPL_OPEN_EVENT_user_id" ON "DB"."ODCS"."PPL_OPEN_EVENT" ("user_id");

-- CONSTRAINTS #################################################################


-- Table "DB"."ODCS"."DPU_INSTANCE"
ALTER TABLE "DB"."ODCS"."DPU_INSTANCE"
  ADD CONSTRAINT "DPU_INSTANCE_DPU_TEMPLATE_id_id" FOREIGN KEY ("dpu_id")
    REFERENCES "DB"."ODCS"."DPU_TEMPLATE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."DPU_TEMPLATE"
ALTER TABLE "DB"."ODCS"."DPU_TEMPLATE"
  ADD CONSTRAINT "DPU_TEMPLATE_DPU_TEMPLATE_id_id" FOREIGN KEY ("parent_id")
    REFERENCES "DB"."ODCS"."DPU_TEMPLATE" ("id")
	ON UPDATE CASCADE ON DELETE SET NULL;


-- Table "DB"."ODCS"."EXEC_DATAUNIT_INFO"
ALTER TABLE "DB"."ODCS"."EXEC_DATAUNIT_INFO"
  ADD CONSTRAINT "EXEC_DATAUNIT_INFO_EXEC_CONTEXT_DPU_id_id" FOREIGN KEY ("exec_context_dpu_id")
    REFERENCES "DB"."ODCS"."EXEC_CONTEXT_DPU" ("id")
	ON UPDATE CASCADE ON DELETE SET NULL;


-- Table "DB"."ODCS"."EXEC_CONTEXT_DPU"
ALTER TABLE "DB"."ODCS"."EXEC_CONTEXT_DPU"
  ADD CONSTRAINT "EXEC_CONTEXT_DPU_EXEC_CONTEXT_PIPELINE_id_id" FOREIGN KEY ("exec_context_pipeline_id")
    REFERENCES "DB"."ODCS"."EXEC_CONTEXT_PIPELINE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."EXEC_CONTEXT_DPU"
  ADD CONSTRAINT "EXEC_CONTEXT_DPU_DPU_INSTANCE_id_id" FOREIGN KEY ("dpu_instance_id")
    REFERENCES "DB"."ODCS"."DPU_INSTANCE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."EXEC_RECORD"
ALTER TABLE "DB"."ODCS"."EXEC_RECORD"
  ADD CONSTRAINT "EXEC_RECORD_DPU_INSTANCE_id_id" FOREIGN KEY ("dpu_id")
    REFERENCES "DB"."ODCS"."DPU_INSTANCE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."EXEC_RECORD"
  ADD CONSTRAINT "EXEC_RECORD_EXEC_PIPELINE_id_id" FOREIGN KEY ("execution_id")
    REFERENCES "DB"."ODCS"."EXEC_PIPELINE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."EXEC_PIPELINE"
ALTER TABLE "DB"."ODCS"."EXEC_PIPELINE"
  ADD CONSTRAINT "EXEC_PIPELINE_PPL_MODEL_id_id" FOREIGN KEY ("pipeline_id")
    REFERENCES "DB"."ODCS"."PPL_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."EXEC_PIPELINE"
  ADD CONSTRAINT "EXEC_PIPELINE_EXEC_CONTEXT_PIPELINE_id_id" FOREIGN KEY ("context_id")
    REFERENCES "DB"."ODCS"."EXEC_CONTEXT_PIPELINE" ("id")
	ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE "DB"."ODCS"."EXEC_PIPELINE"
  ADD CONSTRAINT "EXEC_PIPELINE_EXEC_SCHEDULE_id_id" FOREIGN KEY ("schedule_id")
    REFERENCES "DB"."ODCS"."EXEC_SCHEDULE" ("id")
	ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE "DB"."ODCS"."EXEC_PIPELINE"
  ADD CONSTRAINT "EXEC_PIPELINE_PPL_NODE_id_id" FOREIGN KEY ("debugnode_id")
    REFERENCES "DB"."ODCS"."PPL_NODE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."EXEC_PIPELINE"
  ADD CONSTRAINT "EXEC_PIPELINE_USR_USER_id_id" FOREIGN KEY ("owner_id")
    REFERENCES "DB"."ODCS"."USR_USER" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

-- Table "DB"."ODCS"."EXEC_SCHEDULE"
ALTER TABLE "DB"."ODCS"."EXEC_SCHEDULE"
  ADD CONSTRAINT "EXEC_SCHEDULE_PPL_MODEL_id_id" FOREIGN KEY ("pipeline_id")
    REFERENCES "DB"."ODCS"."PPL_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."EXEC_SCHEDULE"
  ADD CONSTRAINT "EXEC_SCHEDULE_USR_USER_id_id" FOREIGN KEY ("user_id")
    REFERENCES "DB"."ODCS"."USR_USER" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."EXEC_SCHEDULE_AFTER"
ALTER TABLE "DB"."ODCS"."EXEC_SCHEDULE_AFTER"
  ADD CONSTRAINT "EXEC_SCHEDULE_AFTER_EXEC_SCHEDULE_id_id" FOREIGN KEY ("schedule_id")
    REFERENCES "DB"."ODCS"."EXEC_SCHEDULE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."EXEC_SCHEDULE_AFTER"
  ADD CONSTRAINT "EXEC_SCHEDULE_AFTER_PPL_MODEL_id_id" FOREIGN KEY ("pipeline_id")
    REFERENCES "DB"."ODCS"."PPL_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."PPL_MODEL"
ALTER TABLE "DB"."ODCS"."PPL_MODEL"
  ADD CONSTRAINT "PPL_MODEL_USR_USER_id_id" FOREIGN KEY ("user_id")
    REFERENCES "DB"."ODCS"."USR_USER" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

-- Table "DB"."ODCS"."PPL_PPL_CONFLICTS"
ALTER TABLE "DB"."ODCS"."PPL_PPL_CONFLICTS"
  ADD CONSTRAINT "PPL_PPL_CONFLICTS_PPL_MODEL_id_id" FOREIGN KEY ("pipeline_id")
    REFERENCES "DB"."ODCS"."PPL_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."PPL_PPL_CONFLICTS"
  ADD CONSTRAINT "PPL_PPL_CONFLICTS_PPL_MODEL_conflict_id_id" FOREIGN KEY ("pipeline_conflict_id")
    REFERENCES "DB"."ODCS"."PPL_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."PPL_GRAPH"
ALTER TABLE "DB"."ODCS"."PPL_GRAPH"
  ADD CONSTRAINT "PPL_GRAPH_PPL_MODEL_id_id" FOREIGN KEY ("pipeline_id")
    REFERENCES "DB"."ODCS"."PPL_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."PPL_NODE"
ALTER TABLE "DB"."ODCS"."PPL_NODE"
  ADD CONSTRAINT "PPL_NODE_PPL_GRAPH_id_id" FOREIGN KEY ("graph_id")
    REFERENCES "DB"."ODCS"."PPL_GRAPH" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."PPL_NODE"
  ADD CONSTRAINT "PPL_NODE_DPU_INSTANCE_id_id" FOREIGN KEY ("instance_id")
    REFERENCES "DB"."ODCS"."DPU_INSTANCE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."PPL_NODE"
  ADD CONSTRAINT "PPL_NODE_PPL_POSITION_id_id" FOREIGN KEY ("position_id")
    REFERENCES "DB"."ODCS"."PPL_POSITION" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."PPL_EDGE"
ALTER TABLE "DB"."ODCS"."PPL_EDGE"
  ADD CONSTRAINT "PPL_EDGE_PPL_GRAPH_id_id" FOREIGN KEY ("graph_id")
    REFERENCES "DB"."ODCS"."PPL_GRAPH" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."PPL_EDGE"
  ADD CONSTRAINT "PPL_EDGE_PPL_NODE_FROM_id_id" FOREIGN KEY ("node_from_id")
    REFERENCES "DB"."ODCS"."PPL_NODE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."PPL_EDGE"
  ADD CONSTRAINT "PPL_EDGE_PPL_NODE_TO_id_id" FOREIGN KEY ("node_to_id")
    REFERENCES "DB"."ODCS"."PPL_NODE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."SCH_SCH_NOTIFICATION"
ALTER TABLE "DB"."ODCS"."SCH_SCH_NOTIFICATION"
  ADD CONSTRAINT "SCH_SCH_NOTIFICATION_EXEC_SCHEDULE_id_id" FOREIGN KEY ("schedule_id")
    REFERENCES "DB"."ODCS"."EXEC_SCHEDULE" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."SCH_SCH_NOTIFICATION_EMAIL"
ALTER TABLE "DB"."ODCS"."SCH_SCH_NOTIFICATION_EMAIL"
  ADD CONSTRAINT "SCH_SCH_NOTIFICATION_EMAIL_SCH_SCH_NOTIFICATION_id_id" FOREIGN KEY ("notification_id")
    REFERENCES "DB"."ODCS"."SCH_SCH_NOTIFICATION" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."SCH_SCH_NOTIFICATION_EMAIL"
  ADD CONSTRAINT "SCH_SCH_NOTIFICATION_EMAIL_SCH_EMAIL_id_id" FOREIGN KEY ("email_id")
    REFERENCES "DB"."ODCS"."SCH_EMAIL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."SCH_USR_NOTIFICATION"
ALTER TABLE "DB"."ODCS"."SCH_USR_NOTIFICATION"
  ADD CONSTRAINT "SCH_USR_NOTIFICATION_USR_USER_id_id" FOREIGN KEY ("user_id")
    REFERENCES "DB"."ODCS"."USR_USER" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."SCH_USR_NOTIFICATION_EMAIL"
ALTER TABLE "DB"."ODCS"."SCH_USR_NOTIFICATION_EMAIL"
  ADD CONSTRAINT "SCH_USR_NOTIFICATION_EMAIL_SCH_USR_NOTIFICATION_id_id" FOREIGN KEY ("notification_id")
    REFERENCES "DB"."ODCS"."SCH_USR_NOTIFICATION" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."SCH_USR_NOTIFICATION_EMAIL"
  ADD CONSTRAINT "SCH_USR_NOTIFICATION_EMAIL_SCH_EMAIL_id_id" FOREIGN KEY ("email_id")
    REFERENCES "DB"."ODCS"."SCH_EMAIL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."USR_USER"
ALTER TABLE "DB"."ODCS"."USR_USER"
  ADD CONSTRAINT "USR_USER_SCH_EMAIL_id_id" FOREIGN KEY ("email_id")
    REFERENCES "DB"."ODCS"."SCH_EMAIL" ("id")
	ON UPDATE CASCADE ON DELETE SET NULL;


-- Table "DB"."ODCS"."USR_USER_ROLE"
ALTER TABLE "DB"."ODCS"."USR_USER_ROLE"
  ADD CONSTRAINT "USR_USER_USR_USER_ROLE_id_id" FOREIGN KEY ("user_id")
    REFERENCES "DB"."ODCS"."USR_USER" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- Table "DB"."ODCS"."PPL_OPEN_EVENT"
ALTER TABLE "DB"."ODCS"."PPL_OPEN_EVENT"
  ADD CONSTRAINT "PPL_OPEN_EVENT_USR_USER_id_id" FOREIGN KEY ("user_id")
    REFERENCES "DB"."ODCS"."USR_USER" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "DB"."ODCS"."PPL_OPEN_EVENT"
  ADD CONSTRAINT "PPL_OPEN_EVENT_PPL_MODEL_id_id" FOREIGN KEY ("pipeline_id")
    REFERENCES "DB"."ODCS"."PPL_MODEL" ("id")
	ON UPDATE CASCADE ON DELETE CASCADE;


-- TRIGGERS      ######################################################

-- BEGIN VIRTUOSO ONLY

CREATE TRIGGER delete_instance_logs BEFORE DELETE ON "DB"."ODCS"."DPU_INSTANCE" REFERENCING old AS o
{
	DELETE FROM "DB"."ODCS"."LOGGING"
		WHERE dpu = o.id;

	DELETE FROM "DB"."ODCS"."EXEC_RECORD"
		WHERE dpu_id = o.id;


	DELETE FROM "DB"."ODCS"."EXEC_CONTEXT_DPU"
		WHERE dpu_instance_id = o.id;
};

-- workaround for bug in virtuoso's implementation of cascades on delete
-- see https://github.com/openlink/virtuoso-opensource/issues/56
CREATE TRIGGER delete_node_fix BEFORE DELETE ON "DB"."ODCS"."PPL_NODE" REFERENCING old AS n
{
	DELETE FROM ppl_edge
	 WHERE node_from_id = n.id
	  OR node_to_id = n.id;
};

CREATE TRIGGER update_last_change AFTER UPDATE ON "DB"."ODCS"."EXEC_PIPELINE" REFERENCING old AS o, new AS n
{
  SET triggers OFF;
  UPDATE "DB"."ODCS"."EXEC_PIPELINE"
    SET
      t_last_change=now()
    WHERE id = n.id;
};

-- END VIRTUOSO ONLY

-- BEGIN MYSQL ONLY
 -- all lines starting with comment in this section will be commented out for MySQL
-- CREATE TRIGGER update_last_change BEFORE UPDATE ON `exec_pipeline`
--  FOR EACH ROW SET NEW.t_last_change = NOW();
-- END MYSQL ONLY

-- TABLE FOR LOGS

DROP TABLE "DB"."ODCS"."LOGGING";
CREATE TABLE "DB"."ODCS"."LOGGING"
(
-- BEGIN VIRTUOSO ONLY
  "id" INTEGER NOT NULL IDENTITY,
-- END VIRTUOSO ONLY
-- BEGIN MYSQL ONLY
--  "id" INTEGER unsigned NOT NULL IDENTITY,
-- END MYSQL ONLY
  "logLevel" INTEGER NOT NULL,
  "timestmp" BIGINT NOT NULL,
  "logger" VARCHAR(254) NOT NULL,
  "message" LONG VARCHAR,
  "dpu" INTEGER,
  "execution" INTEGER NOT NULL,
  "stack_trace" LONG VARCHAR,
  "relative_id" INTEGER,
  PRIMARY KEY (id)
-- BEGIN VIRTUOSO ONLY
);
-- END VIRTUOSO ONLY
-- BEGIN MYSQL ONLY
-- ) ENGINE=MyISAM;
-- END MYSQL ONLY

CREATE INDEX "ix_LOGGING_dpu" ON "DB"."ODCS"."LOGGING" ("dpu");
CREATE INDEX "ix_LOGGIN_execution" ON "DB"."ODCS"."LOGGING" ("execution");
CREATE INDEX "ix_LOGGIN_relative_id" ON "DB"."ODCS"."LOGGING" ("relative_id");

-- File must end with empty line, so last query is followed by enter.
