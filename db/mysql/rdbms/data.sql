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
