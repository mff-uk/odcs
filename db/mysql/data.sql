INSERT INTO `properties` VALUES ('UV.Core.version','001.005.005'),('UV.Plugin-DevEnv.version','001.001.000');
INSERT INTO `sch_email` VALUES (1,'admin@example.com'),(2,'user@example.com');
INSERT INTO `usr_user` VALUES (1,'admin',1,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John Admin',20),(2,'user',2,'100000:3069f2086098a66ec0a859ec7872b09af7866bc7ecafe2bed3ec394454056db2:b5ab4961ae8ad7775b3b568145060fabb76d7bca41c7b535887201f79ee9788a','John User',20);
INSERT INTO `usr_user_role` VALUES (1,0),(1,1),(2,0);
INSERT INTO `sch_usr_notification` VALUES (1,1,1,1),(2,2,1,1);
INSERT INTO `sch_usr_notification_email` VALUES (1,1),(2,2);
INSERT INTO `runtime_properties` (name, value) VALUES ('backend.scheduledPipelines.limit', '5');
INSERT INTO `runtime_properties` (name, value) VALUES ('run.now.pipeline.priority', '1');
INSERT INTO `runtime_properties` (name, value) VALUES ('locale', 'en');
