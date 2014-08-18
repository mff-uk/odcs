-- MySQL dump 10.13  Distrib 5.5.37, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: uvrelease
-- ------------------------------------------------------
-- Server version	5.5.37-0ubuntu0.13.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `dpu_instance`
--

LOCK TABLES `dpu_instance` WRITE;
/*!40000 ALTER TABLE `dpu_instance` DISABLE KEYS */;
/*!40000 ALTER TABLE `dpu_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `dpu_template`
--

LOCK TABLES `dpu_template` WRITE;
/*!40000 ALTER TABLE `dpu_template` DISABLE KEYS */;
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('e-filesFromLocal',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>cz.cuni.mff.xrg.uv.extractor.filesfromlocal.FilesFromLocalConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <source>/tmp/</source>
  </Configuration>
</object-stream>',null,0,1,1,0,'uv-e-filesFromLocal','uv-e-filesFromLocal-1.0.0.jar','Extract local file or directory.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('e-httpDownload',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.extractor.httpdownload.HttpDownloadConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <target>/file</target>
    <retryCount>-1</retryCount>
    <retryDelay>1000</retryDelay>
  </Configuration>
</object-stream>',null,0,1,1,0,'uv-e-httpDownload','uv-e-httpDownload-1.0.0.jar','Download a single file from given URL and save it with given virtualPath.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('e-rdfDataGenerator',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.extractor.httpdownloadlist.HTTPToFilesConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <connectionTimeout>2000</connectionTimeout>
    <readTimeout>2000</readTimeout>
    <symbolicNameToURIMap class="linked-hash-map"/>
    <symbolicNameToVirtualPathMap class="linked-hash-map"/>
  </Configuration>
</object-stream>',null,0,1,1,0,'uv-e-httpDownloadList','uv-e-httpDownloadList-1.3.0.jar','Downloads list of files (in form of URIs) given in a configuration and saves them to output.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('e-rdfFromSparql',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.extractor.rdffromsparql.RdfFromSparqlEndpointConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
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
    <outputGraphSymbolicName>E-RDFFromSPARQL/output78</outputGraphSymbolicName>
  </Configuration>
</object-stream>',null,0,1,1,0,'uv-e-rdfFromSparql','uv-e-rdfFromSparql-1.3.0.jar','Extracts RDF data from SPARQL Endpoint.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('e-silkLinker',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.extractor.silklinker.SilkLinkerConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <minConfirmedLinks>0.9</minConfirmedLinks>
    <minLinksToBeVerified>0.0</minLinksToBeVerified>
    <confFileLabel></confFileLabel>
  </Configuration>
</object-stream>',null,0,1,1,0,'uv-e-silkLinker','uv-e-silkLinker-1.3.0.jar','Creates links between RDF resources based on the Silk Link Specification Language (LSL),        https://www.assembla.com/spaces/silk/wiki/Link_Specification_Language.        The script may be uploaded/adjusted in the DPU configuration. Output section of such script is always ignored,        output is written to two output data units of the DPU - "links_confirmed", "links_to_be_verified".        DPU configuration may also specify thresholds for the two outputs created. Uses 2.5.3 version of Silk. Not        supporting cancelation of DPU.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('l-filesToLocalFS',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.loader.filestolocalfs.FilesToLocalFSConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <destination>/tmp</destination>
    <moveFiles>false</moveFiles>
    <replaceExisting>false</replaceExisting>
    <skipOnError>false</skipOnError>
  </Configuration>
</object-stream>',null,0,1,1,2,'uv-l-filesToLocalFS','uv-l-filesToLocalFS-1.3.0.jar','Loads files to the specified local host directory.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('l-filesToScp',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.extractor.filestoscp.FilesToScpConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <hostname></hostname>
    <port>22</port>
    <username></username>
    <password></password>
    <destination>/</destination>
    <softFail>true</softFail>
  </Configuration>
</object-stream>',null,0,1,1,2,'uv-l-filesToScp','uv-l-filesToScp-1.0.0.jar','Upload given files using scp.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('l-filesToSparql',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.loader.filestosparql.FilesToSparqlEndpointConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <queryEndpointUrl></queryEndpointUrl>
    <updateEndpointUrl></updateEndpointUrl>
    <commitSize>10000</commitSize>
    <targetContexts class="empty-set"/>
    <skipOnError>false</skipOnError>
  </Configuration>
</object-stream>',null,0,1,1,2,'uv-l-filesToSparql','uv-l-filesToSparql-1.3.0.jar','Loads RDF data stored in Files to the specified remote SPARQL endpoint.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('l-filesToVirtuoso',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.loader.filestovirtuoso.VirtuosoLoaderConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <virtuosoUrl></virtuosoUrl>
    <username></username>
    <password></password>
    <clearDestinationGraph>false</clearDestinationGraph>
    <loadDirectoryPath></loadDirectoryPath>
    <includeSubdirectories>true</includeSubdirectories>
    <loadFilePattern>%</loadFilePattern>
    <targetContext></targetContext>
    <statusUpdateInterval>60</statusUpdateInterval>
    <threadCount>1</threadCount>
    <skipOnError>false</skipOnError>
  </Configuration>
</object-stream>',null,0,1,1,2,'uv-l-filesToVirtuoso','uv-l-filesToVirtuoso-1.3.0.jar','VirtuosoLoader issues Virtuoso internal functions to load directory of RDF data.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('l-rdfToSparql',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.loader.rdftosparql.RdfToSparqlEndpointConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <SPARQL__endpoint></SPARQL__endpoint>
    <Host__name></Host__name>
    <Password></Password>
    <GraphsUri class="linked-list"/>
    <graphOption>OVERRIDE</graphOption>
    <insertOption>STOP_WHEN_BAD_PART</insertOption>
    <chunkSize>100</chunkSize>
    <validDataBefore>false</validDataBefore>
    <retryTime>60000</retryTime>
    <retrySize>5</retrySize>
    <endpointParams>
      <queryParam>update</queryParam>
      <defaultGraphParam>using-graph-uri</defaultGraphParam>
      <postType>POST_URL_ENCODER</postType>
    </endpointParams>
    <useSparqlGraphProtocol>true</useSparqlGraphProtocol>
  </Configuration>
</object-stream>',null,0,1,1,2,'uv-l-rdfToSparql','uv-l-rdfToSparql-1.3.0.jar','Loads RDF data.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-filesFilter',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.transformer.filesfilter.FilesFilterConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <predicate>FIXED_SYMBOLIC_NAME</predicate>
    <object>.*</object>
    <useRegExp>true</useRegExp>
  </Configuration>
</object-stream>',null,0,1,1,1,'uv-t-filesFilter','uv-t-filesFilter-1.0.0.jar','Filter files.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-filesRenamer',0,'','<null configuration/>',null,0,1,1,1,'uv-t-filesRenamer','uv-t-filesRenamer-1.3.0.jar','Rename files');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-filesToRdf',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.transformer.filestordft.FilesToRDFConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <symbolicNameToBaseURIMap class="linked-hash-map"/>
    <symbolicNameToFormatMap class="linked-hash-map"/>
    <commitSize>1</commitSize>
    <fatalErrorHandling>STOP_EXTRACTION</fatalErrorHandling>
    <errorHandling>SKIP_CONTINUE_THIS_FILE</errorHandling>
    <warningHandling>SKIP_CONTINUE_THIS_FILE</warningHandling>
  </Configuration>
</object-stream>',null,0,1,1,1,'uv-t-filesToRdf','uv-t-filesToRdf-1.3.0.jar','Extracts RDF data from Files (any file format) and adds them to RDF.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-metadata',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.transformer.metadata.MetadataConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <outputGraphName>http://localhost/metadata</outputGraphName>
    <datasetURI>http://linked.opendata.cz/resource/dataset/</datasetURI>
    <distroURI>http://linked.opendata.cz/resource/dataset//distribution</distroURI>
    <title__cs>Název datasetu</title__cs>
    <title__en>Dataset title</title__en>
    <desc__cs>Popis datasetu</desc__cs>
    <desc__en>Dataset description</desc__en>
    <mime>application/zip</mime>
    <authors/>
    <possibleAuthors>
      <string>http://purl.org/klimek#me</string>
      <string>http://opendata.cz/necasky#me</string>
      <string>http://mynarz.net/#jindrich</string>
    </possibleAuthors>
    <publishers>
      <string>http://opendata.cz</string>
    </publishers>
    <possiblePublishers>
      <string>http://opendata.cz</string>
    </possiblePublishers>
    <licenses>
      <string>http://opendatacommons.org/licenses/pddl/1-0/</string>
    </licenses>
    <possibleLicenses>
      <string>http://opendatacommons.org/licenses/pddl/1-0/</string>
      <string>http://creativecommons.org/licenses/by/3.0/lu/</string>
    </possibleLicenses>
    <sources/>
    <possibleSources>
      <string>http://linked.opendata.cz</string>
    </possibleSources>
    <exampleResources/>
    <possibleExampleResources/>
    <languages/>
    <possibleLanguages>
      <string>http://id.loc.gov/vocabulary/iso639-1/en</string>
      <string>http://id.loc.gov/vocabulary/iso639-1/cs</string>
    </possibleLanguages>
    <keywords/>
    <possibleKeywords/>
    <themes/>
    <possibleThemes>
      <string>http://dbpedia.org/resource/EHealth</string>
    </possibleThemes>
    <contactPoint>http://opendata.cz/contacts</contactPoint>
    <sparqlEndpoint>http://linked.opendata.cz/sparql</sparqlEndpoint>
    <dataDump>http://linked.opendata.cz/dump/</dataDump>
    <periodicity>http://purl.org/linked-data/sdmx/2009/code#freq-M</periodicity>
    <useNow>true</useNow>
    <isQb>false</isQb>
    <modified>2014-08-14 13:55:30.730 UTC</modified>
  </Configuration>
</object-stream>',null,0,1,1,1,'uv-t-metadata','uv-t-metadata-1.0.0.jar','Generates metadata on output from input');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-rdfDataValidator',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.transformer.rdfvalidator.RDFDataValidatorConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <stopExecution>false</stopExecution>
    <sometimesOutput>true</sometimesOutput>
  </Configuration>
</object-stream>',null,0,1,1,1,'uv-t-rdfDataValidator','uv-t-rdfDataValidator-1.3.0.jar','Validate RDF data and create report about that.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-rdfToFiles',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.transformer.rdftofiles.RdfToFilesConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <rdfFileFormat>Turtle</rdfFileFormat>
    <genGraphFile>true</genGraphFile>
    <mergeGraphs>true</mergeGraphs>
    <outGraphName></outGraphName>
    <graphToFileInfo class="java.util.Arrays$ArrayList">
      <a class="eu.unifiedviews.plugins.transformer.rdftofiles.RdfToFilesConfig_V1$GraphToFileInfo-array">
        <eu.unifiedviews.plugins.transformer.rdftofiles.RdfToFilesConfig__V1_-GraphToFileInfo>
          <inSymbolicName></inSymbolicName>
          <outFileName>data</outFileName>
          <outer-class reference="../../../.."/>
        </eu.unifiedviews.plugins.transformer.rdftofiles.RdfToFilesConfig__V1_-GraphToFileInfo>
      </a>
    </graphToFileInfo>
  </Configuration>
</object-stream>',null,0,1,1,1,'uv-t-rdfToFiles','uv-t-rdfToFiles-1.0.0.jar','Transform RDF graphs into files.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-sparql',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.transformer.sparql.SPARQLConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <queryPairs class="linked-list"/>
    <isConstructType>false</isConstructType>
    <outputGraphSymbolicName>T-SPARQL/output67</outputGraphSymbolicName>
  </Configuration>
</object-stream>',null,0,1,1,1,'uv-t-sparql','uv-t-sparql-1.3.0.jar','Transforms data using SPARQL');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-tabular',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.transformer.tabular.TabularConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <columnPropertyMap class="linked-hash-map"/>
    <encoding>UTF-8</encoding>
    <quoteChar>&quot;</quoteChar>
    <delimiterChar>;</delimiterChar>
    <eofSymbols>
</eofSymbols>
    <rowLimit>10000</rowLimit>
    <tableType>csv</tableType>
  </Configuration>
</object-stream>',null,0,1,1,1,'uv-t-tabular','uv-t-tabular-1.0.0.jar','Based on https://github.com/mff-uk/DPUs/blob/master/dpu-domain-specific/tabular. Temporary solution.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-unzipper',0,'','<null configuration/>',null,0,1,1,1,'uv-t-unzipper','uv-t-unzipper-1.0.0.jar','UnZip input file into files based on zip content.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-xslt',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.transformer.xslt.XSLTConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <xslTemplate></xslTemplate>
    <xslTemplateFileNameShownInDialog></xslTemplateFileNameShownInDialog>
    <skipOnError>false</skipOnError>
    <xlstParametersMapName>xlstParameters</xlstParametersMapName>
  </Configuration>
</object-stream>',null,0,1,1,1,'uv-t-xslt','uv-t-xslt-1.3.0.jar','FilesToFilesXSLT2Transformer does XSLT over Files and outputs Files.');
INSERT INTO dpu_template (name,use_dpu_description,description,configuration,parent_id,config_valid,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES ('t-zipper',0,'','<object-stream>
  <ConfigurationVersion>
    <version>1</version>
    <className>eu.unifiedviews.plugins.transformer.zipper.ZipperConfig_V1</className>
  </ConfigurationVersion>
  <Configuration>
    <zipFile>data.zip</zipFile>
  </Configuration>
</object-stream>',null,0,1,1,1,'uv-t-zipper','uv-t-zipper-1.0.0.jar','Zip input files into zip file of given name.');
/*!40000 ALTER TABLE `dpu_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `exec_context_dpu`
--

LOCK TABLES `exec_context_dpu` WRITE;
/*!40000 ALTER TABLE `exec_context_dpu` DISABLE KEYS */;
/*!40000 ALTER TABLE `exec_context_dpu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `exec_context_pipeline`
--

LOCK TABLES `exec_context_pipeline` WRITE;
/*!40000 ALTER TABLE `exec_context_pipeline` DISABLE KEYS */;
/*!40000 ALTER TABLE `exec_context_pipeline` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `exec_dataunit_info`
--

LOCK TABLES `exec_dataunit_info` WRITE;
/*!40000 ALTER TABLE `exec_dataunit_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `exec_dataunit_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `exec_pipeline`
--

LOCK TABLES `exec_pipeline` WRITE;
/*!40000 ALTER TABLE `exec_pipeline` DISABLE KEYS */;
/*!40000 ALTER TABLE `exec_pipeline` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping data for table `exec_record`
--

LOCK TABLES `exec_record` WRITE;
/*!40000 ALTER TABLE `exec_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `exec_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `exec_schedule`
--

LOCK TABLES `exec_schedule` WRITE;
/*!40000 ALTER TABLE `exec_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `exec_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `exec_schedule_after`
--

LOCK TABLES `exec_schedule_after` WRITE;
/*!40000 ALTER TABLE `exec_schedule_after` DISABLE KEYS */;
/*!40000 ALTER TABLE `exec_schedule_after` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `logging`
--

LOCK TABLES `logging` WRITE;
/*!40000 ALTER TABLE `logging` DISABLE KEYS */;
/*!40000 ALTER TABLE `logging` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ppl_edge`
--

LOCK TABLES `ppl_edge` WRITE;
/*!40000 ALTER TABLE `ppl_edge` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppl_edge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ppl_graph`
--

LOCK TABLES `ppl_graph` WRITE;
/*!40000 ALTER TABLE `ppl_graph` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppl_graph` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ppl_model`
--

LOCK TABLES `ppl_model` WRITE;
/*!40000 ALTER TABLE `ppl_model` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppl_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ppl_node`
--

LOCK TABLES `ppl_node` WRITE;
/*!40000 ALTER TABLE `ppl_node` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppl_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ppl_open_event`
--

LOCK TABLES `ppl_open_event` WRITE;
/*!40000 ALTER TABLE `ppl_open_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppl_open_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ppl_position`
--

LOCK TABLES `ppl_position` WRITE;
/*!40000 ALTER TABLE `ppl_position` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppl_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ppl_ppl_conflicts`
--

LOCK TABLES `ppl_ppl_conflicts` WRITE;
/*!40000 ALTER TABLE `ppl_ppl_conflicts` DISABLE KEYS */;
/*!40000 ALTER TABLE `ppl_ppl_conflicts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `properties`
--

LOCK TABLES `properties` WRITE;
/*!40000 ALTER TABLE `properties` DISABLE KEYS */;
INSERT INTO `properties` VALUES ('UV.Core.version','001.003.000'),('UV.Plugin-DevEnv.version','001.000.000'),('UV.Plugins.version','001.003.000');
/*!40000 ALTER TABLE `properties` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `rdf_ns_prefix`
--

LOCK TABLES `rdf_ns_prefix` WRITE;
/*!40000 ALTER TABLE `rdf_ns_prefix` DISABLE KEYS */;
/*!40000 ALTER TABLE `rdf_ns_prefix` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `sch_email`
--

LOCK TABLES `sch_email` WRITE;
/*!40000 ALTER TABLE `sch_email` DISABLE KEYS */;
INSERT INTO `sch_email` VALUES (1,'admin@example.com'),(2,'user@example.com');
/*!40000 ALTER TABLE `sch_email` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `sch_sch_notification`
--

LOCK TABLES `sch_sch_notification` WRITE;
/*!40000 ALTER TABLE `sch_sch_notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `sch_sch_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `sch_sch_notification_email`
--

LOCK TABLES `sch_sch_notification_email` WRITE;
/*!40000 ALTER TABLE `sch_sch_notification_email` DISABLE KEYS */;
/*!40000 ALTER TABLE `sch_sch_notification_email` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `sch_usr_notification`
--

LOCK TABLES `sch_usr_notification` WRITE;
/*!40000 ALTER TABLE `sch_usr_notification` DISABLE KEYS */;
INSERT INTO `sch_usr_notification` VALUES (1,1,1,1),(2,2,1,1);
/*!40000 ALTER TABLE `sch_usr_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `sch_usr_notification_email`
--

LOCK TABLES `sch_usr_notification_email` WRITE;
/*!40000 ALTER TABLE `sch_usr_notification_email` DISABLE KEYS */;
INSERT INTO `sch_usr_notification_email` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sch_usr_notification_email` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `usr_user`
--

LOCK TABLES `usr_user` WRITE;
/*!40000 ALTER TABLE `usr_user` DISABLE KEYS */;
INSERT INTO `usr_user` VALUES (1,'admin',1,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John Admin',20),(2,'user',2,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John User',20);
/*!40000 ALTER TABLE `usr_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `usr_user_role`
--

LOCK TABLES `usr_user_role` WRITE;
/*!40000 ALTER TABLE `usr_user_role` DISABLE KEYS */;
INSERT INTO `usr_user_role` VALUES (1,0),(1,1),(2,0);
/*!40000 ALTER TABLE `usr_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-07 20:38:24
