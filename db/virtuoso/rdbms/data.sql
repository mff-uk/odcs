fk_check_input_values(0);
-- dbdump: dumping datasource "localhost:1111", username=dba
-- tablequalifier=NULL  tableowner=NULL  tablename=is given, one or more  tabletype=NULL
-- Connected to datasource "OpenLink Virtuoso", Driver v. 06.01.3127 OpenLink Virtuoso ODBC Driver.
-- get_all_tables: tablepattern="db.odcs.%",9
-- Definitions of 27 tables were read in.
-- SELECT * FROM DB.ODCS.DPU_INSTANCE
INSERT INTO DB.ODCS.DPU_INSTANCE(id,name,use_dpu_description,description,configuration,config_valid,dpu_id) VALUES(1,'SPARQL Extractor',1,'Extract from SPARQL: http://dbpedia.org/sparql',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>
    <SPARQL__endpoint>http://dbpedia.org/sparql</SPARQL__endpoint>
    <Host__name></Host__name>
    <Password></Password>
    <GraphsUri class="linked-list">
      <string>http://dbpedia.org</string>
    </GraphsUri> 
    <SPARQL__query>CONSTRUCT {&lt;http://dbpedia.org/resource/Prague&gt; ?p ?o} where {&lt;http://dbpedia.org/resource/Prague&gt; ?p ?o } LIMIT 100</SPARQL__query>
    <ExtractFail>true</ExtractFail>
    <UseStatisticalHandler>false</UseStatisticalHandler>
  </cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>
</object-stream>',1,1);
INSERT INTO DB.ODCS.DPU_INSTANCE(id,name,use_dpu_description,description,configuration,config_valid,dpu_id) VALUES(2,'RDF File Loader',1,'Load to: /tmp/dbpedia.rdf',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>
    <FilePath>/tmp/dbpedia.rdf</FilePath>
    <RDFFileFormat>RDFXML</RDFFileFormat>
    <DiffName>false</DiffName>
  </cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>
</object-stream>',1,5);

-- Table DB.ODCS.DPU_INSTANCE 2 rows output.
-- SELECT * FROM DB.ODCS.DPU_TEMPLATE
INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(1,'SPARQL Extractor',0,'Extracts RDF triples from SPARQL endpoint',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>
    <SPARQL__endpoint></SPARQL__endpoint>
    <Host__name></Host__name>
    <Password></Password>
    <SPARQL__query></SPARQL__query>
    <ExtractFail>true</ExtractFail>
    <UseStatisticalHandler>true</UseStatisticalHandler>
    <failWhenErrors>false</failWhenErrors>
    <retryTime>1000</retryTime>
    <retrySize>-1</retrySize>
    <endpointParams>
      <queryParam>query</queryParam>
      <defaultGraphParam>default-graph-uri</defaultGraphParam>
      <namedGraphParam>named-graph-uri</namedGraphParam>
      <defaultGraphURI class="linked-list"/>
      <namedGraphURI class="linked-list"/>
      <requestType>POST_URL_ENCODER</requestType>
    </endpointParams>
    <useSplitConstruct>false</useSplitConstruct>
    <splitConstructSize>50000</splitConstructSize>
  </cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>
</object-stream>',NULL,1,1,1,0,'SPARQL_Extractor','SPARQL_Extractor-1.0.0.jar','Extracts RDF data from SPARQL.');

INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(2,'RDF File Extractor',0,'Extracts RDF data from a file',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.extractor.file.FileExtractorConfig>
    <Path></Path>
    <FileSuffix></FileSuffix>
    <RDFFormatValue>AUTO</RDFFormatValue>
    <fileExtractType>PATH_TO_FILE</fileExtractType>
    <OnlyThisSuffix>false</OnlyThisSuffix>
    <UseStatisticalHandler>true</UseStatisticalHandler>
  </cz.cuni.mff.xrg.odcs.extractor.file.FileExtractorConfig>
</object-stream>',NULL,1,1,1,0,'RDF_File_Extractor','RDF_File_Extractor-1.0.0.jar','Extracts RDF data from a file.');

INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(3,'SPARQL Transformer',0,'Transforms RDF data based on SPARQL (update) query',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.transformer.SPARQL.SPARQLTransformerConfig>
    <SPARQL__Update__Query></SPARQL__Update__Query>
    <isConstructType>false</isConstructType>
  </cz.cuni.mff.xrg.odcs.transformer.SPARQL.SPARQLTransformerConfig>
</object-stream>',NULL,1,1,1,1,'SPARQL_Transformer','SPARQL_Transformer-1.0.0.jar','SPARQL Transformer.');

INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(4,'SPARQL Loader',0,'Loads RDF data to SPARQL endpoint.',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.loader.rdf.RDFLoaderConfig>
    <SPARQL__endpoint></SPARQL__endpoint>
    <Host__name></Host__name>
    <Password></Password>
    <GraphsUri class="linked-list"/>
    <graphOption>OVERRIDE</graphOption>
    <insertOption>STOP_WHEN_BAD_PART</insertOption>
    <chunkSize>100</chunkSize>
    <validDataBefore>false</validDataBefore>
    <retryTime>1000</retryTime>
    <retrySize>-1</retrySize>
    <endpointParams>
      <queryParam>update</queryParam>
      <defaultGraphParam>using-graph-uri</defaultGraphParam>
      <postType>POST_URL_ENCODER</postType>
    </endpointParams>
  </cz.cuni.mff.xrg.odcs.loader.rdf.RDFLoaderConfig>
</object-stream>',NULL,1,1,1,2,'SPARQL_Loader','SPARQL_Loader-1.0.0.jar','Loads RDF data to SPARQL endpoint.');

INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(5,'RDF File Loader',0,'Loads RDF data into file.',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>
    <FilePath></FilePath>
    <RDFFileFormat>AUTO</RDFFileFormat>
    <DiffName>false</DiffName>
    <validDataBefore>false</validDataBefore>
  </cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>
</object-stream>',NULL,1,1,1,2,'RDF_File_Loader','RDF_File_Loader-1.0.0.jar','Loads RDF data into file.');

INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(6,'RDF Data Validator',0,'Validates RDF data and creates a validation report.',
'<object-stream>
  <cz.cuni.mff.xrg.odcs.rdf.validator.RDFDataValidatorConfig>
    <stopExecution>false</stopExecution>
    <sometimesOutput>true</sometimesOutput>
  </cz.cuni.mff.xrg.odcs.rdf.validator.RDFDataValidatorConfig>
</object-stream>',NULL,1,1,1,1,'RDF_Data_Validator','RDF_Data_Validator-1.0.0.jar','Validate RDF data and create validation report.');

-- Table DB.ODCS.DPU_TEMPLATE 5 rows output.
-- SELECT * FROM DB.ODCS.EXEC_CONTEXT_DPU
-- Table DB.ODCS.EXEC_CONTEXT_DPU 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_CONTEXT_PIPELINE
-- Table DB.ODCS.EXEC_CONTEXT_PIPELINE 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_DATAUNIT_INFO
-- Table DB.ODCS.EXEC_DATAUNIT_INFO 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_PIPELINE
-- Table DB.ODCS.EXEC_PIPELINE 0 rows output.
-- Table DB.ODCS.EXEC_RECORD has more than one blob column.
-- The column full_message of type LONG VARCHAR might not get properly inserted.
-- SELECT * FROM DB.ODCS.EXEC_RECORD
-- Table DB.ODCS.EXEC_RECORD has more than one blob column.
-- The column full_message of type LONG VARCHAR might not get properly inserted.
-- Table DB.ODCS.EXEC_RECORD 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_SCHEDULE
-- Table DB.ODCS.EXEC_SCHEDULE 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_SCHEDULE_AFTER
-- Table DB.ODCS.EXEC_SCHEDULE_AFTER 0 rows output.
-- Table DB.ODCS.LOGGING has more than one blob column.
-- The column stack_trace of type LONG VARCHAR might not get properly inserted.
-- SELECT * FROM DB.ODCS.LOGGING
-- Table DB.ODCS.LOGGING has more than one blob column.
-- The column stack_trace of type LONG VARCHAR might not get properly inserted.
-- Table DB.ODCS.LOGGING 0 rows output.
-- SELECT * FROM DB.ODCS.LOGGING_EVENT
-- Table DB.ODCS.LOGGING_EVENT 0 rows output.
-- SELECT * FROM DB.ODCS.LOGGING_EVENT_EXCEPTION
-- Table DB.ODCS.LOGGING_EVENT_EXCEPTION 0 rows output.
-- SELECT * FROM DB.ODCS.LOGGING_EVENT_PROPERTY
-- Table DB.ODCS.LOGGING_EVENT_PROPERTY 0 rows output.
-- SELECT * FROM DB.ODCS.PPL_EDGE
INSERT INTO DB.ODCS.PPL_EDGE(id,graph_id,node_from_id,node_to_id,data_unit_name) VALUES(2,1,1,2,'output -> input;');
-- Table DB.ODCS.PPL_EDGE 1 rows output.
-- SELECT * FROM DB.ODCS.PPL_GRAPH
INSERT INTO DB.ODCS.PPL_GRAPH(id,pipeline_id) VALUES(1,1);
-- Table DB.ODCS.PPL_GRAPH 1 rows output.
-- SELECT * FROM DB.ODCS.PPL_MODEL
INSERT INTO DB.ODCS.PPL_MODEL(id,name,description,user_id,visibility) VALUES(1,'DBpedia','Loads 100 triples from DBpedia.',2,2);
-- Table DB.ODCS.PPL_MODEL 1 rows output.
-- SELECT * FROM DB.ODCS.PPL_NODE
INSERT INTO DB.ODCS.PPL_NODE(id,graph_id,instance_id,position_id) VALUES(1,1,1,1);
INSERT INTO DB.ODCS.PPL_NODE(id,graph_id,instance_id,position_id) VALUES(2,1,2,2);
-- Table DB.ODCS.PPL_NODE 2 rows output.
-- SELECT * FROM DB.ODCS.PPL_POSITION
INSERT INTO DB.ODCS.PPL_POSITION(id,pos_x,pos_y) VALUES(1,138,52);
INSERT INTO DB.ODCS.PPL_POSITION(id,pos_x,pos_y) VALUES(2,487,132);
-- Table DB.ODCS.PPL_POSITION 2 rows output.
-- SELECT * FROM DB.ODCS.PPL_PPL_CONFLICTS
-- Table DB.ODCS.PPL_PPL_CONFLICTS 0 rows output.
-- SELECT * FROM DB.ODCS.RDF_NS_PREFIX
-- Table DB.ODCS.RDF_NS_PREFIX 0 rows output.
-- SELECT * FROM DB.ODCS.SCH_EMAIL
INSERT INTO DB.ODCS.SCH_EMAIL(id,email) VALUES(1,'admin@example.com');
INSERT INTO DB.ODCS.SCH_EMAIL(id,email) VALUES(2,'user@example.com');
-- Table DB.ODCS.SCH_EMAIL 2 rows output.
-- SELECT * FROM DB.ODCS.SCH_SCH_NOTIFICATION
-- Table DB.ODCS.SCH_SCH_NOTIFICATION 0 rows output.
-- SELECT * FROM DB.ODCS.SCH_SCH_NOTIFICATION_EMAIL
-- Table DB.ODCS.SCH_SCH_NOTIFICATION_EMAIL 0 rows output.
-- SELECT * FROM DB.ODCS.SCH_USR_NOTIFICATION
INSERT INTO DB.ODCS.SCH_USR_NOTIFICATION(id,user_id,type_success,type_error) VALUES(1,1,1,1);
INSERT INTO DB.ODCS.SCH_USR_NOTIFICATION(id,user_id,type_success,type_error) VALUES(2,2,1,1);
-- Table DB.ODCS.SCH_USR_NOTIFICATION 2 rows output.
-- SELECT * FROM DB.ODCS.SCH_USR_NOTIFICATION_EMAIL
INSERT INTO DB.ODCS.SCH_USR_NOTIFICATION_EMAIL(notification_id,email_id) VALUES(1,1);
INSERT INTO DB.ODCS.SCH_USR_NOTIFICATION_EMAIL(notification_id,email_id) VALUES(2,2);
-- Table DB.ODCS.SCH_USR_NOTIFICATION_EMAIL 2 rows output.
-- SELECT * FROM DB.ODCS.USR_USER
INSERT INTO DB.ODCS.USR_USER(id,username,email_id,u_password,full_name,table_rows) VALUES(1,'admin',1,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John Admin',20);
INSERT INTO DB.ODCS.USR_USER(id,username,email_id,u_password,full_name,table_rows) VALUES(2,'user',2,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John User',20);
-- Table DB.ODCS.USR_USER 2 rows output.
-- SELECT * FROM DB.ODCS.USR_USER_ROLE
INSERT INTO DB.ODCS.USR_USER_ROLE(user_id,role_id) VALUES(1,0);
INSERT INTO DB.ODCS.USR_USER_ROLE(user_id,role_id) VALUES(1,1);
INSERT INTO DB.ODCS.USR_USER_ROLE(user_id,role_id) VALUES(2,0);
-- Table DB.ODCS.USR_USER_ROLE 3 rows output.


fk_check_input_values(1);
