
-- Testing piepline (DBpedia with empty configurations)
INSERT INTO DPU_TEMPLATE(id,name,description,type,jar_path,configuration,parent_id,visibility,jar_description)
 VALUES(1,'SPARQL Extractor','Extracts RDF data.',0,'RDF_extractor-0.0.1.jar','',NULL,1,'No description in manifest.');
INSERT INTO DPU_TEMPLATE(id,name,description,type,jar_path,configuration,parent_id,visibility,jar_description)
 VALUES(2,'RDF File Extractor','Extracts RDF data from a file.',0,'File_extractor-0.0.1.jar','',NULL,1,'No description in manifest.');
INSERT INTO DPU_TEMPLATE(id,name,description,type,jar_path,configuration,parent_id,visibility,jar_description)
 VALUES(3,'SPARQL Transformer','SPARQL Transformer.',1,'SPARQL_transformer-0.0.1.jar','',NULL,1,'No description in manifest.');
INSERT INTO DPU_TEMPLATE(id,name,description,type,jar_path,configuration,parent_id,visibility,jar_description)
 VALUES(4,'SPARQL Loader','Loads RDF data.',2,'RDF_loader-0.0.1.jar','',NULL,1,'No description in manifest.');
INSERT INTO DPU_TEMPLATE(id,name,description,type,jar_path,configuration,parent_id,visibility,jar_description)
 VALUES(5,'RDF File Loader','Loads RDF data into file.',2,'File_loader-0.0.1.jar','',NULL,1,'No description in manifest.');

INSERT INTO DPU_INSTANCE(id,name,description,type,jar_path,configuration,dpu_id)
 VALUES(1,'SPARQL Extractor','Extracts RDF data.',0,'RDF_extractor-0.0.1.jar',NULL,1);
INSERT INTO DPU_INSTANCE(id,name,description,type,jar_path,configuration,dpu_id)
 VALUES(2,'RDF File Loader','Loads RDF data into file.',2,'File_loader-0.0.1.jar',NULL,5);

INSERT INTO PPL_MODEL(id,name,description) VALUES(1,'DBpedia','Loads 100 triples from DBpedia.');

INSERT INTO PPL_GRAPH(id,pipeline_id) VALUES(1,1);

INSERT INTO PPL_POSITION(id,pos_x,pos_y) VALUES(1,138,52);
INSERT INTO PPL_POSITION(id,pos_x,pos_y) VALUES(2,487,132);

INSERT INTO PPL_NODE(id,graph_id,instance_id,position_id) VALUES(1,1,1,1);
INSERT INTO PPL_NODE(id,graph_id,instance_id,position_id) VALUES(2,1,2,2);

INSERT INTO PPL_EDGE(id,graph_id,node_from_id,node_to_id,data_unit_name) VALUES(1,1,1,2,NULL);

INSERT INTO EXEC_PIPELINE(id,status,pipeline_id,debug_mode,t_start,t_end,context_id,schedule_id,silent_mode,debugnode_id) VALUES(1,5,1,0,NULL,NULL,NULL,NULL,1,NULL);

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