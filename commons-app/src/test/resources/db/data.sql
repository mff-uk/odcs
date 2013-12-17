
-- Sample user with admin role
INSERT INTO SCH_EMAIL(id,e_user,e_domain)
 VALUES(1,'user','example.com');
INSERT INTO SCH_EMAIL(id,e_user,e_domain)
 VALUES(2,'pdoe','example.com');

INSERT INTO USR_USER(id,username,email_id,u_password,full_name)
 VALUES(1,'jdoe',1,'10:34dbe217a123a1501be647832c77571bd0af1c8b584be30404157da1111499b9:f09771bb5a73b35d6d8cd8b5dfb0cf26bf58a71f6d3f4c1a8c92e33fb263aaff','John Doe');
INSERT INTO USR_USER(id,username,email_id,u_password,full_name)
 VALUES(2,'pdoe',2,'10:34dbe217a123a1501be647832c77571bd0af1c8b584be30404157da1111499b9:f09771bb5a73b35d6d8cd8b5dfb0cf26bf58a71f6d3f4c1a8c92e33fb263aaff','Peter Doe');

INSERT INTO USR_USER_ROLE(user_id,role_id) VALUES(1,0);
INSERT INTO USR_USER_ROLE(user_id,role_id) VALUES(1,1);
INSERT INTO USR_USER_ROLE(user_id,role_id) VALUES(2,1);

-- Testing piepline (DBpedia with empty configurations)
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description)
 VALUES(1,'SPARQL Extractor',0,'Extracts RDF data.',0,'','RDF_extractor-0.0.1.jar','',NULL,1,1,'No description in manifest.');
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description)
 VALUES(2,'RDF File Extractor',0,'Extracts RDF data from a file.',0,'','File_extractor-0.0.1.jar','',NULL,1,1,'No description in manifest.');
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description)
 VALUES(3,'SPARQL Transformer',0,'SPARQL Transformer.',1,'','SPARQL_transformer-0.0.1.jar','',NULL,1,1,'No description in manifest.');
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description)
 VALUES(4,'SPARQL Loader',0,'Loads RDF data.',2,'','RDF_loader-0.0.1.jar','',NULL,1,1,'No description in manifest.');
INSERT INTO DPU_TEMPLATE(id,name,use_dpu_description,description,type,jar_directory,jar_name,configuration,parent_id,user_id,visibility,jar_description)
 VALUES(5,'RDF File Loader',0,'Loads RDF data into file.',2,'','File_loader-0.0.1.jar','',NULL,1,1,'No description in manifest.');

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

INSERT INTO EXEC_CONTEXT_PIPELINE(id,directory)
 VALUES(1,'/tmp/intlib/context-dir');

INSERT INTO EXEC_PIPELINE(id,status,pipeline_id,debug_mode,t_start,t_end,context_id,schedule_id,silent_mode,debugnode_id,stop)
 VALUES(1,5,1,0,NULL,NULL,1,NULL,1,NULL,0);

-- schedule define by times when to run pipeline
INSERT INTO EXEC_SCHEDULE(id,name,description,pipeline_id,user_id,just_once,enabled,type,first_exec,last_exec,time_period,period_unit,strict_timing,strict_tolerance)
 VALUES(1,NULL,NULL,1,1,0,1,1,'2013-07-22 19:07:48',NULL,1,3,0,NULL);

-- schedule defined by "run after pipeline"
INSERT INTO EXEC_SCHEDULE(id,name,description,pipeline_id,user_id,just_once,enabled,type,first_exec,last_exec,time_period,period_unit,strict_timing,strict_tolerance)
 VALUES(2,NULL,NULL,2,1,1,1,0,NULL,NULL,NULL,NULL,0,NULL);
-- Test 2 should run after Test 1
INSERT INTO EXEC_SCHEDULE_AFTER(schedule_id,pipeline_id) VALUES(2,1);


-- notifications for schedules
INSERT INTO SCH_SCH_NOTIFICATION(id,schedule_id,type_success,type_error)
 VALUES(1,1,1,1);
INSERT INTO SCH_SCH_NOTIFICATION(id,schedule_id,type_success,type_error)
 VALUES(2,2,1,1);
INSERT INTO SCH_EMAIL(id,e_user,e_domain)
 VALUES(3,'scheduler','example.com');
INSERT INTO SCH_SCH_NOTIFICATION_EMAIL(notification_id,email_id)
 VALUES(1,3);

INSERT INTO SCH_USR_NOTIFICATION(id,user_id,type_success,type_error)
 VALUES(1,1,1,1);
INSERT INTO SCH_USR_NOTIFICATION_EMAIL(notification_id,email_id)
 VALUES(1,1);

-- Log messages
INSERT INTO LOGGING_EVENT (timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','DEBUG','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',1);
INSERT INTO LOGGING_EVENT (timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
 VALUES(1373056400667,'Returning cached instance of singleton bean "transactionManager"','org.springframework.beans.factory.support.DefaultListableBeanFactory','DEBUG','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'AbstractBeanFactory.java','org.springframework.beans.factory.support.AbstractBeanFactory','doGetBean','245',2);
INSERT INTO LOGGING_EVENT(timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
 VALUES(1373056401201,'Returning cached instance of singleton bean "engine"','org.springframework.beans.factory.support.DefaultListableBeanFactory','DEBUG','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'AbstractBeanFactory.java','org.springframework.beans.factory.support.AbstractBeanFactory','doGetBean','245',3);
INSERT INTO LOGGING_EVENT (timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
-- Log of level DEBUG without property
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','DEBUG','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',4);
-- Log of level DEBUG with execution property
INSERT INTO LOGGING_EVENT (timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','DEBUG','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',5);
-- Log of level DEBUG with execution, dpuInstance properties
INSERT INTO LOGGING_EVENT (timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','DEBUG','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',6);
-- Log of level INFO without property
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','INFO','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',7);
-- Log of level INFO with execution property
INSERT INTO LOGGING_EVENT (timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','INFO','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',8);
-- Log of level INFO withexecution, dpuInstance properties
INSERT INTO LOGGING_EVENT (timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','INFO','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',9);
-- Log of level WARNING without property
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','WARNING','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',10);
-- Log of level WARNING with execution property
INSERT INTO LOGGING_EVENT (timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','WARNING','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',11);
-- Log of level WARNING withexecution, dpuInstance properties
INSERT INTO LOGGING_EVENT (timestmp,formatted_message,logger_name,level_string,thread_name,reference_flag,arg0,arg1,arg2,arg3,caller_filename,caller_class,caller_method,caller_line,event_id)
 VALUES(1373056400664,'Started','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','WARNING','pool-2-thread-1',0,NULL,NULL,NULL,NULL,'PipelineWorker.java','cz.cuni.xrg.intlib.backend.execution.PipelineWorker','run','213',12);

-- Log message properties
INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(1,'HOSTNAME','localhost');
INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(1,'execution','1');

INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(2,'HOSTNAME','localhost');
INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(2,'execution','1');

INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(3,'HOSTNAME','localhost');
INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(3,'dpuInstance','1');
INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(3,'execution','1');

INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(5,'execution','1');

INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(6,'dpuInstance','1');
INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(6,'execution','1');

INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(8,'execution','1');

INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(9,'dpuInstance','1');
INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(9,'execution','1');

INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(11,'execution','1');

INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(12,'dpuInstance','1');
INSERT INTO LOGGING_EVENT_PROPERTY(event_id,mapped_key,mapped_value) VALUES(12,'execution','1');

INSERT INTO LOGGING_EVENT_EXCEPTION(event_id, i, trace_line)
 VALUES (1, 1, 'Exception in thread "pool-2-thread-2" org.springframework.orm.jpa.JpaSystemException: org.hibernate.exception.LockAcquisitionException: SR172: Transaction deadlocked.');
INSERT INTO LOGGING_EVENT_EXCEPTION(event_id, i, trace_line)
 VALUES (1, 2, '    at org.springframework.orm.jpa.EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(EntityManagerFactoryUtils.java:326)');
INSERT INTO LOGGING_EVENT_EXCEPTION(event_id, i, trace_line)
 VALUES (1, 3, '    at org.springframework.orm.jpa.DefaultJpaDialect.translateExceptionIfPossible(DefaultJpaDialect.java:120)');

INSERT INTO RDF_NS_PREFIX(id, name, uri)
 VALUES (1, 'ex1', 'http://example.com/1');

INSERT INTO RDF_NS_PREFIX(id, name, uri)
 VALUES (2, 'ex2', 'http://example.com/2');

