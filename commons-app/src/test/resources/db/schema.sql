-- schema definition customized for H2 embedded DBMS
-- used for testing

CREATE TABLE "PPL_MODEL"
(
  id INTEGER IDENTITY,
  name VARCHAR(45),
  description VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE "PPL_GRAPH"
(
  id INTEGER IDENTITY,
  pipeline_id INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE "PPL_NODE"
(
  id INTEGER IDENTITY,
  graph_id INTEGER,
  instance_id INTEGER,
  position_id INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE "PPL_EDGE"
(
  id INTEGER IDENTITY,
  graph_id INTEGER,
  node_from_id INTEGER,
  node_to_id INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE "PPL_POSITION"
(
  id INTEGER IDENTITY,
  pos_x INTEGER,
  pos_y INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE "PPL_EXECUTION"
(
  id INTEGER IDENTITY,
  status INTEGER,
  pipeline_id INTEGER,
  debug_mode SMALLINT,
  execution_directory VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE "DPU_RECORD"
(
  id INTEGER IDENTITY,
  r_time DATE,
  r_type SMALLINT,
  dpu_id INTEGER,
  execution_id INTEGER,
  short_message LONGVARCHAR,
  full_message LONGVARCHAR,
  PRIMARY KEY (id)
);

CREATE TABLE "DPU_INSTANCE"
(
  id INTEGER IDENTITY,
  name VARCHAR(45),
  description VARCHAR(255),
  dpu_id INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE "DPU_INSTANCE_CONFIG"
(
  id INTEGER IDENTITY,
  instance_id INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE "DPU_TEMPLATE_CONFIG"
(
  id INTEGER IDENTITY,
  dpu_id INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE "DPU_MODEL"
(
  id INTEGER IDENTITY,
  name VARCHAR(45),
  description VARCHAR(255),
  type SMALLINT,
  visibility SMALLINT,
  jar_path VARCHAR(255),
  PRIMARY KEY (id)
);

