-- MySQL dump 10.13  Distrib 5.5.37, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: odcs
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
INSERT INTO `dpu_template` VALUES (1,'FilesToFilesXSLT2Transformer',0,'FilesToFilesXSLT2Transformer does XSLT over Files and outputs Files.','<object-stream>\n  <cz.cuni.mff.xrg.odcs.dpu.filestofilesxslt2transformer.FilesToFilesXSLT2TransformerConfig>\n    <xslTemplate></xslTemplate>\n    <xslTemplateFileNameShownInDialog></xslTemplateFileNameShownInDialog>\n    <skipOnError>false</skipOnError>\n  </cz.cuni.mff.xrg.odcs.dpu.filestofilesxslt2transformer.FilesToFilesXSLT2TransformerConfig>\n</object-stream>',NULL,0,1,1,1,'FilesToFilesXSLT2Transformer','FilesToFilesXSLT2Transformer-1.3.0.jar','FilesToFilesXSLT2Transformer does XSLT over Files and outputs Files.'),(2,'FilesToFileTransformer',0,'FilesToFileTransformer converts Files to FileDataUnit. Does not copy files.','<null configuration/>',NULL,0,1,1,1,'FilesToFileTransformer','FilesToFileTransformer-1.3.0.jar','FilesToFileTransformer converts Files to FileDataUnit. Does not copy files.'),(3,'FilesToRDFTransformer',0,'FilesToRDFTransformer extracts RDF data from Files (any file format) and adds them to RDF.','<object-stream>\n  <cz.cuni.mff.xrg.odcs.dpu.filestordftransformer.FilesToRDFTransformerConfig>\n    <symbolicNameToBaseURIMap class=\"linked-hash-map\"/>\n    <symbolicNameToFormatMap class=\"linked-hash-map\"/>\n    <commitSize>1</commitSize>\n    <fatalErrorHandling>STOP_EXTRACTION</fatalErrorHandling>\n    <errorHandling>SKIP_CONTINUE_THIS_FILE</errorHandling>\n    <warningHandling>SKIP_CONTINUE_THIS_FILE</warningHandling>\n  </cz.cuni.mff.xrg.odcs.dpu.filestordftransformer.FilesToRDFTransformerConfig>\n</object-stream>',NULL,0,1,1,1,'FilesToRDFTransformer','FilesToRDFTransformer-1.3.0.jar','FilesToRDFTransformer extracts RDF data from Files (any file format) and adds them to RDF.'),(4,'FilesToSPARQLLoader',0,'FilesToSPARQLLoader loads RDF data stored in Files to the specified remote SPARQL endpoint.','<object-stream>\n  <cz.cuni.mff.xrg.odcs.dpu.filestosparqlloader.FilesToSPARQLLoaderConfig>\n    <queryEndpointUrl></queryEndpointUrl>\n    <updateEndpointUrl></updateEndpointUrl>\n    <commitSize>10000</commitSize>\n    <targetContexts class=\"linked-hash-set\"/>\n    <skipOnError>false</skipOnError>\n  </cz.cuni.mff.xrg.odcs.dpu.filestosparqlloader.FilesToSPARQLLoaderConfig>\n</object-stream>',NULL,0,1,1,2,'FilesToSPARQLLoader','FilesToSPARQLLoader-1.3.0.jar','FilesToSPARQLLoader loads RDF data stored in Files to the specified remote SPARQL endpoint.'),(5,'FilesToLocalDirectoryLoader',0,'FilesToLocalDirectoryLoader loads Files to the specified local host directory.','<object-stream>\n  <cz.cuni.mff.xrg.odcs.dpu.filestolocaldirectoryloader.FilesToLocalDirectoryLoaderConfig>\n    <destination>/tmp</destination>\n    <moveFiles>false</moveFiles>\n    <replaceExisting>false</replaceExisting>\n    <skipOnError>false</skipOnError>\n  </cz.cuni.mff.xrg.odcs.dpu.filestolocaldirectoryloader.FilesToLocalDirectoryLoaderConfig>\n</object-stream>',NULL,0,1,1,2,'FilesToLocalDirectoryLoader','FilesToLocalDirectoryLoader-1.3.0.jar','FilesToLocalDirectoryLoader loads Files to the specified local host directory.'),(6,'FileToFilesTransformer',0,'FileToFilesTransformer converts FileDataUnit to Files. Does not copy files.','<null configuration/>',NULL,0,1,1,1,'FileToFilesTransformer','FileToFilesTransformer-1.3.0.jar','FileToFilesTransformer converts FileDataUnit to Files. Does not copy files.'),(7,'HTTPToFilesExtractor',0,'HTTPToFilesExtractor downloads list URIs given in configuration and saves them to Files.','<object-stream>\n  <cz.cuni.mff.xrg.odcs.dpu.httptofilesextractor.HTTPToFilesExtractorConfig>\n    <connectionTimeout>2000</connectionTimeout>\n    <readTimeout>2000</readTimeout>\n    <symbolicNameToURIMap class=\"linked-hash-map\"/>\n  </cz.cuni.mff.xrg.odcs.dpu.httptofilesextractor.HTTPToFilesExtractorConfig>\n</object-stream>',NULL,0,1,1,0,'HTTPToFilesExtractor','HTTPToFilesExtractor-1.3.0.jar','HTTPToFilesExtractor downloads list URIs given in configuration and saves them to Files.'),(8,'RDFToRDFMerger2Transformer',0,'RDFToRDFMerger2Transformer merges RDF data in no time.','<null configuration/>',NULL,0,1,1,1,'RDFToRDFMerger2Transformer','RDFToRDFMerger2Transformer-1.3.0.jar','RDFToRDFMerger2Transformer merges RDF data in no time.'),(9,'FilesToFilesMerger2Transformer',0,'FilesToFilesMerger2Transformer merges Files inputs in no time.','<null configuration/>',NULL,0,1,1,1,'FilesToFilesMerger2Transformer','FilesToFilesMerger2Transformer-1.3.0.jar','FilesToFilesMerger2Transformer merges Files inputs in no time.'),(10,'TripleGeneratorToRDFExtractor',0,'TripleGeneratorToRDFExtractor generates specified number of unique triples to RDF data.','<object-stream>\n  <cz.cuni.mff.xrg.odcs.dpu.triplegeneratortordfextractor.TripleGeneratorToRDFExtractorConfig>\n    <tripleCount>1000000</tripleCount>\n    <commitSize>50000</commitSize>\n  </cz.cuni.mff.xrg.odcs.dpu.triplegeneratortordfextractor.TripleGeneratorToRDFExtractorConfig>\n</object-stream>',NULL,0,1,1,0,'TripleGeneratorToRDFExtractor','TripleGeneratorToRDFExtractor-1.3.0.jar','TripleGeneratorToRDFExtractor generates specified number of unique triples to RDF data.'),(11,'RDF_File_Extractor',0,'','<object-stream>\n  <cz.cuni.mff.xrg.odcs.extractor.file.FileExtractorConfig>\n    <Path></Path>\n    <FileSuffix></FileSuffix>\n    <RDFFormatValue>AUTO</RDFFormatValue>\n    <fileExtractType>PATH_TO_FILE</fileExtractType>\n    <OnlyThisSuffix>false</OnlyThisSuffix>\n    <UseStatisticalHandler>true</UseStatisticalHandler>\n    <failWhenErrors>false</failWhenErrors>\n  </cz.cuni.mff.xrg.odcs.extractor.file.FileExtractorConfig>\n</object-stream>',NULL,0,1,1,0,'RDF_File_Extractor','RDF_File_Extractor-1.3.0.jar','Extracts RDF data from a file.'),(12,'RDF_File_Loader',0,'','<object-stream>\n  <cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>\n    <FilePath></FilePath>\n    <RDFFileFormat>AUTO</RDFFileFormat>\n    <DiffName>false</DiffName>\n    <validDataBefore>false</validDataBefore>\n    <penetrable>false</penetrable>\n  </cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig>\n</object-stream>',NULL,0,1,1,2,'RDF_File_Loader','RDF_File_Loader-1.3.0.jar','Loads RDF data into file.'),(13,'SPARQL_Extractor',0,'','<object-stream>\n  <cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>\n    <SPARQL__endpoint></SPARQL__endpoint>\n    <Host__name></Host__name>\n    <Password></Password>\n    <SPARQL__query></SPARQL__query>\n    <ExtractFail>true</ExtractFail>\n    <UseStatisticalHandler>true</UseStatisticalHandler>\n    <failWhenErrors>false</failWhenErrors>\n    <retryTime>1000</retryTime>\n    <retrySize>-1</retrySize>\n    <endpointParams>\n      <queryParam>query</queryParam>\n      <defaultGraphParam>default-graph-uri</defaultGraphParam>\n      <namedGraphParam>named-graph-uri</namedGraphParam>\n      <defaultGraphURI class=\"linked-list\"/>\n      <namedGraphURI class=\"linked-list\"/>\n      <requestType>POST_URL_ENCODER</requestType>\n    </endpointParams>\n    <useSplitConstruct>false</useSplitConstruct>\n    <splitConstructSize>50000</splitConstructSize>\n  </cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig>\n</object-stream>',NULL,0,1,1,0,'SPARQL_Extractor','SPARQL_Extractor-1.3.0.jar','Extracts RDF data.'),(14,'SPARQL_Loader',0,'','<object-stream>\n  <cz.cuni.mff.xrg.odcs.loader.rdf.RDFLoaderConfig>\n    <SPARQL__endpoint></SPARQL__endpoint>\n    <Host__name></Host__name>\n    <Password></Password>\n    <GraphsUri class=\"linked-list\"/>\n    <graphOption>OVERRIDE</graphOption>\n    <insertOption>STOP_WHEN_BAD_PART</insertOption>\n    <chunkSize>100</chunkSize>\n    <validDataBefore>false</validDataBefore>\n    <retryTime>60000</retryTime>\n    <retrySize>5</retrySize>\n    <endpointParams>\n      <queryParam>update</queryParam>\n      <defaultGraphParam>using-graph-uri</defaultGraphParam>\n      <postType>POST_URL_ENCODER</postType>\n    </endpointParams>\n    <useSparqlGraphProtocol>true</useSparqlGraphProtocol>\n    <penetrable>false</penetrable>\n  </cz.cuni.mff.xrg.odcs.loader.rdf.RDFLoaderConfig>\n</object-stream>',NULL,0,1,1,2,'SPARQL_Loader','SPARQL_Loader-1.3.0.jar','Loads RDF data.'),(15,'SPARQL_Transformer',0,'','<object-stream>\n  <cz.cuni.mff.xrg.odcs.transformer.SPARQL.SPARQLTransformerConfig>\n    <queryPairs class=\"linked-list\"/>\n    <isConstructType>false</isConstructType>\n  </cz.cuni.mff.xrg.odcs.transformer.SPARQL.SPARQLTransformerConfig>\n</object-stream>',NULL,0,1,1,1,'SPARQL_Transformer','SPARQL_Transformer-1.3.0.jar','SPARQL Transformer.'),(16,'RDF_Data_Validator',0,'','<object-stream>\n  <cz.cuni.mff.xrg.odcs.rdf.validator.RDFDataValidatorConfig>\n    <stopExecution>false</stopExecution>\n    <sometimesOutput>true</sometimesOutput>\n  </cz.cuni.mff.xrg.odcs.rdf.validator.RDFDataValidatorConfig>\n</object-stream>',NULL,0,1,1,1,'RDF_Data_Validator','RDF_Data_Validator-1.3.0.jar','Validate RDF data and create report about that.');
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

-- Dump completed on 2014-07-24 11:25:50
