ALTER TABLE dpu_instance MODIFY use_dpu_description tinyint(1) DEFAULT '0';
ALTER TABLE dpu_instance MODIFY use_template_config tinyint(1) NOT NULL DEFAULT '0';

ALTER TABLE dpu_template MODIFY use_dpu_description tinyint(1) DEFAULT '0';
ALTER TABLE dpu_template MODIFY visibility SMALLINT DEFAULT NULL;
ALTER TABLE dpu_template MODIFY type SMALLINT DEFAULT NULL;

ALTER TABLE exec_dataunit_info MODIFY idx int(11) DEFAULT NULL;
ALTER TABLE exec_dataunit_info MODIFY type SMALLINT DEFAULT NULL;
ALTER TABLE exec_dataunit_info MODIFY is_input tinyint(1) DEFAULT '0';

ALTER TABLE exec_context_pipeline MODIFY dummy tinyint(1) DEFAULT '0';

ALTER TABLE exec_context_dpu MODIFY state SMALLINT DEFAULT NULL;

ALTER TABLE exec_record MODIFY r_time datetime DEFAULT NULL;
ALTER TABLE exec_record MODIFY r_type int(11) DEFAULT NULL;

ALTER TABLE backend_servers MODIFY last_update timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE exec_pipeline MODIFY status int(11) DEFAULT NULL;
ALTER TABLE exec_pipeline MODIFY debug_mode tinyint(1) DEFAULT '0';
ALTER TABLE exec_pipeline MODIFY t_start datetime DEFAULT NULL;
ALTER TABLE exec_pipeline MODIFY t_end datetime DEFAULT NULL;
ALTER TABLE exec_pipeline MODIFY silent_mode tinyint(1) DEFAULT '0';
ALTER TABLE exec_pipeline MODIFY stop tinyint(1) DEFAULT '0';
ALTER TABLE exec_pipeline MODIFY t_last_change datetime DEFAULT NULL;
ALTER TABLE exec_pipeline MODIFY order_number bigint(20) DEFAULT NULL;
ALTER TABLE exec_pipeline MODIFY backend_id varchar(128) DEFAULT NULL;

ALTER TABLE exec_schedule MODIFY description text;
ALTER TABLE exec_schedule MODIFY enabled tinyint(1) DEFAULT '0';
ALTER TABLE exec_schedule MODIFY first_exec datetime DEFAULT NULL;
ALTER TABLE exec_schedule MODIFY just_once tinyint(1) DEFAULT '0';
ALTER TABLE exec_schedule MODIFY last_exec datetime DEFAULT NULL;
ALTER TABLE exec_schedule MODIFY time_period int(11) DEFAULT NULL;
ALTER TABLE exec_schedule MODIFY period_unit int(11) DEFAULT NULL;
ALTER TABLE exec_schedule MODIFY priority bigint(20) NOT NULL;
ALTER TABLE exec_schedule MODIFY strict_tolerance int(11) DEFAULT NULL;
ALTER TABLE exec_schedule MODIFY strict_timing tinyint(1) DEFAULT '0';
ALTER TABLE exec_schedule MODIFY type SMALLINT DEFAULT NULL;

ALTER TABLE ppl_model MODIFY description text;
ALTER TABLE ppl_model MODIFY last_change datetime DEFAULT NULL;
ALTER TABLE ppl_model MODIFY name varchar(255) DEFAULT NULL;
ALTER TABLE ppl_model MODIFY visibility SMALLINT DEFAULT NULL;

ALTER TABLE ppl_edge MODIFY data_unit_name varchar(2048) DEFAULT NULL;

ALTER TABLE ppl_position MODIFY pos_x int(11) DEFAULT NULL;
ALTER TABLE ppl_position MODIFY pos_y int(11) DEFAULT NULL;
ALTER TABLE ppl_position ADD NODE_id bigint(20) DEFAULT NULL;
UPDATE ppl_position SET ppl_position.NODE_id = (SELECT ppl_node.id FROM ppl_node WHERE ppl_node.position_id=ppl_position.id);

ALTER TABLE runtime_properties MODIFY name varchar(100) DEFAULT NULL;
ALTER TABLE runtime_properties MODIFY value varchar(100) DEFAULT NULL;

ALTER TABLE sch_sch_notification MODIFY type_error SMALLINT DEFAULT NULL;
ALTER TABLE sch_sch_notification MODIFY type_started SMALLINT DEFAULT NULL;
ALTER TABLE sch_sch_notification MODIFY type_success SMALLINT DEFAULT NULL;

ALTER TABLE sch_usr_notification MODIFY report_not_scheduled tinyint(1) DEFAULT '0';
ALTER TABLE sch_usr_notification MODIFY type_error SMALLINT DEFAULT NULL;
ALTER TABLE sch_usr_notification MODIFY type_started SMALLINT DEFAULT NULL;
ALTER TABLE sch_usr_notification MODIFY type_success SMALLINT DEFAULT NULL;

ALTER TABLE sch_email MODIFY email varchar(255) DEFAULT NULL;

ALTER TABLE usr_user MODIFY full_name varchar(55) DEFAULT NULL;
ALTER TABLE usr_user MODIFY u_password varchar(142) DEFAULT NULL;
ALTER TABLE usr_user MODIFY table_rows int(11) DEFAULT NULL;
ALTER TABLE usr_user MODIFY username varchar(25) NOT NULL;

ALTER TABLE permission MODIFY name varchar(142) NOT NULL;
ALTER TABLE permission MODIFY sharedentityinstancewriterequired tinyint(1) DEFAULT '0';

ALTER TABLE role MODIFY name varchar(142) NOT NULL;

ALTER TABLE user_actor MODIFY name varchar(255) NOT NULL;

ALTER TABLE rdf_ns_prefix MODIFY name varchar(255) NOT NULL;
ALTER TABLE rdf_ns_prefix MODIFY uri varchar(2048) NOT NULL;

ALTER TABLE ppl_open_event MODIFY opened datetime NOT NULL;

ALTER TABLE logging MODIFY log_level int(11) NOT NULL;
ALTER TABLE logging MODIFY message text;
ALTER TABLE logging MODIFY logger varchar(255) NOT NULL;
ALTER TABLE logging MODIFY stack_trace text;
ALTER TABLE logging MODIFY timestmp bigint(20) NOT NULL;

DROP TABLE properties;
DROP VIEW pipeline_view;
DROP VIEW exec_last_view;
DROP VIEW exec_view;