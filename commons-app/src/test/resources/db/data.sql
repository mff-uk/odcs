
-- Sample user with admin role
INSERT INTO SCH_EMAIL(id,email)
 VALUES(1,'user@example.com');
INSERT INTO SCH_EMAIL(id,email)
 VALUES(2,'pdoe@example.com');

INSERT INTO USR_USER(id,username,email_id,u_password,full_name)
 VALUES(1,'jdoe',1,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John Doe');
INSERT INTO USR_USER(id,username,email_id,u_password,full_name)
 VALUES(2,'pdoe',2,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','Peter Doe');

INSERT INTO USR_USER_ROLE(user_id,role_id) VALUES(1,0);
INSERT INTO USR_USER_ROLE(user_id,role_id) VALUES(1,1);
INSERT INTO USR_USER_ROLE(user_id,role_id) VALUES(2,1);

-- Testing piepline (DBpedia with empty configurations)
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description,config_valid)
 VALUES(1,'SPARQL Extractor',0,'Extracts RDF data.',0,'','RDF_extractor-0.0.1.jar','',NULL,1,1,'No description in manifest.',1);
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description,config_valid)
 VALUES(2,'RDF File Extractor',0,'Extracts RDF data from a file.',0,'','File_extractor-0.0.1.jar','',NULL,1,1,'No description in manifest.',1);
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description,config_valid)
 VALUES(3,'SPARQL Transformer',0,'SPARQL Transformer.',1,'','SPARQL_transformer-0.0.1.jar','',NULL,1,1,'No description in manifest.',1);
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description,config_valid)
 VALUES(4,'SPARQL Loader',0,'Loads RDF data.',2,'','RDF_loader-0.0.1.jar','',NULL,1,1,'No description in manifest.',1);
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description,config_valid)
 VALUES(5,'RDF File Loader',0,'Loads RDF data into file.',2,'','File_loader-0.0.1.jar','',NULL,1,1,'No description in manifest.',1);

INSERT INTO DPU_INSTANCE(id,name,use_dpu_description,description,configuration,dpu_id)
 VALUES(1,'SPARQL Extractor',0,'Extracts RDF data.',NULL,1);
INSERT INTO DPU_INSTANCE(id,name,use_dpu_description,description,configuration,dpu_id)
 VALUES(2,'RDF File Loader',0,'Loads RDF data into file.',NULL,5);

INSERT INTO PPL_MODEL(id,name,description,user_id) VALUES(1,'Test 1','Testing pipeline 1.',1);
INSERT INTO PPL_MODEL(id,name,description,user_id) VALUES(2,'Test 2','Testing pipeline 2.',1);

INSERT INTO PPL_GRAPH(id,pipeline_id) VALUES(1,1);
INSERT INTO PPL_GRAPH(id,pipeline_id) VALUES(2,2);

INSERT INTO PPL_POSITION(id,pos_x,pos_y) VALUES(1,138,52);
INSERT INTO PPL_POSITION(id,pos_x,pos_y) VALUES(2,487,132);

INSERT INTO PPL_NODE(id,graph_id,instance_id,position_id) VALUES(1,1,1,1);
INSERT INTO PPL_NODE(id,graph_id,instance_id,position_id) VALUES(2,1,2,2);

INSERT INTO PPL_EDGE(id,graph_id,node_from_id,node_to_id,data_unit_name)
 VALUES(1,1,1,2,NULL);

INSERT INTO EXEC_CONTEXT_PIPELINE(id)
 VALUES(1);

INSERT INTO EXEC_PIPELINE(id,status,pipeline_id,debug_mode,t_start,t_end,context_id,schedule_id,silent_mode,debugnode_id,stop)
 VALUES(1,5,1,0,NULL,NULL,1,NULL,1,NULL,0);

-- schedule define by times when to run pipeline
INSERT INTO EXEC_SCHEDULE(id,description,pipeline_id,user_id,just_once,enabled,type,first_exec,last_exec,time_period,period_unit,strict_timing,strict_tolerance)
 VALUES(1,NULL,1,1,0,1,1,'2013-07-22 19:07:48',NULL,1,3,0,NULL);

-- schedule defined by "run after pipeline"
INSERT INTO EXEC_SCHEDULE(id,description,pipeline_id,user_id,just_once,enabled,type,first_exec,last_exec,time_period,period_unit,strict_timing,strict_tolerance)
 VALUES(2,NULL,2,1,1,1,0,NULL,NULL,NULL,NULL,0,NULL);
-- Test 2 should run after Test 1
INSERT INTO EXEC_SCHEDULE_AFTER(schedule_id,pipeline_id) VALUES(2,1);


-- notifications for schedules
INSERT INTO SCH_SCH_NOTIFICATION(id,schedule_id,type_success,type_error)
 VALUES(1,1,1,1);
INSERT INTO SCH_SCH_NOTIFICATION(id,schedule_id,type_success,type_error)
 VALUES(2,2,1,1);
INSERT INTO SCH_EMAIL(id,email)
 VALUES(3,'scheduler@example.com');
INSERT INTO SCH_SCH_NOTIFICATION_EMAIL(notification_id,email_id)
 VALUES(1,3);

INSERT INTO SCH_USR_NOTIFICATION(id,user_id,type_success,type_error)
 VALUES(1,1,1,1);
INSERT INTO SCH_USR_NOTIFICATION_EMAIL(notification_id,email_id)
 VALUES(1,1);

-- Log messages
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,10000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,null,1);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400667,10000,'org.springframework.beans.factory.support.DefaultListableBeanFactory','Returning cached instance of singleton bean "transactionManager"',1,null,2);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056401201,10000,'org.springframework.beans.factory.support.DefaultListableBeanFactory','Returning cached instance of singleton bean "engine"',1,1,3);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,10000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,1,4);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,10000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,null,5);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,10000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,1,6);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,20000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,1,7);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,20000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,null,8);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,20000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,1,9);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,30000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,1,10);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,30000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,null,11);
INSERT INTO LOGGING(timestmp, logLevel, logger, message, execution, dpu, relative_id)
 VALUES(1373056400664,30000,'cz.cuni.xrg.intlib.backend.execution.PipelineWorker','Started',1,1,12);

INSERT INTO RDF_NS_PREFIX(id, name, uri)
 VALUES (1, 'ex1', 'http://example.com/1');

INSERT INTO RDF_NS_PREFIX(id, name, uri)
 VALUES (2, 'ex2', 'http://example.com/2');

