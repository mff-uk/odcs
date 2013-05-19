DROP TABLE "DB"."INTLIB"."PPL_MODEL";
CREATE TABLE "DB"."INTLIB"."PPL_MODEL"
(
  "id" INTEGER,
  "name" VARCHAR(45),
  "description" VARCHAR(255),
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."PPL_GRAPH";
CREATE TABLE "DB"."INTLIB"."PPL_GRAPH"
(
  "id" INTEGER,
  "pipeline_id" INTEGER,
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."PPL_NODE";
CREATE TABLE "DB"."INTLIB"."PPL_NODE"
(
  "id" INTEGER,
  "graph_id" INTEGER,
  "instance_id" INTEGER,
  "position_id" INTEGER,
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."PPL_EDGE";
CREATE TABLE "DB"."INTLIB"."PPL_EDGE"
(
  "id" INTEGER,
  "graph_id" INTEGER,
  "node_from_id" INTEGER,
  "node_to_id" INTEGER,
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."PPL_POSITION";
CREATE TABLE "DB"."INTLIB"."PPL_POSITION"
(
  "id" INTEGER,
  "pos_x" INTEGER,
  "pos_y" INTEGER,
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."PPL_EXECUTION";
CREATE TABLE "DB"."INTLIB"."PPL_EXECUTION"
(
  "id" INTEGER,
  "status" INTEGER,
  "pipeline_id" INTEGER,
  "debug_mode" SMALLINT,
  "execution_directory" VARCHAR(255),
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."DPU_RECORD";
CREATE TABLE "DB"."INTLIB"."DPU_RECORD"
(
  "id" INTEGER,
  "r_time" DATE,
  "r_type" SMALLINT,
  "dpu_id" INTEGER,
  "execution_id" INTEGER,
  "short_message" LONG VARCHAR,
  "full_message" LONG VARCHAR,
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."DPU_INSTANCE";
CREATE TABLE "DB"."INTLIB"."DPU_INSTANCE"
(
  "id" INTEGER,
  "name" VARCHAR(45),
  "description" VARCHAR(255),
  "dpu_id" INTEGER,
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."DPU_INSTANCE_CONFIG";
CREATE TABLE "DB"."INTLIB"."DPU_INSTANCE_CONFIG"
(
  "id" INTEGER,
  "instance_id" INTEGER,
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."DPU_TEMPLATE_CONFIG";
CREATE TABLE "DB"."INTLIB"."DPU_TEMPLATE_CONFIG"
(
  "id" INTEGER,
  "dpu_id" INTEGER,
  PRIMARY KEY ("id")
);

DROP TABLE "DB"."INTLIB"."DPU_ICONFIG_PAIRS";
CREATE TABLE "DB"."INTLIB"."DPU_ICONFIG_PAIRS"
(
  "conf_id" INTEGER,
  "c_property" VARCHAR(255),
  "c_value" LONG VARBINARY,
  PRIMARY KEY ("conf_id", "c_property")
);

DROP TABLE "DB"."INTLIB"."DPU_TCONFIG_PAIRS";
CREATE TABLE "DB"."INTLIB"."DPU_TCONFIG_PAIRS"
(
  "conf_id" INTEGER,
  "c_property" VARCHAR(255),
  "c_value" LONG VARBINARY,
  PRIMARY KEY ("conf_id", "c_property")
);

DROP TABLE "DB"."INTLIB"."DPU_MODEL";
CREATE TABLE "DB"."INTLIB"."DPU_MODEL"
(
  "id" INTEGER,
  "name" VARCHAR(45),
  "description" VARCHAR(255),
  "type" VARCHAR(11),
  "jar_path" VARCHAR(255),
  PRIMARY KEY ("id")
);

