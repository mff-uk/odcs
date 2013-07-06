
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