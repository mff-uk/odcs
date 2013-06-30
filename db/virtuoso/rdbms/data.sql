-- delete old data
DELETE FROM DB.INTLIB.DPU_TEMPLATE;

-- import new data
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,description,type,jar_path,visibility,jar_description) VALUES(1,'SPARQL Extractor','Extracts RDF data.',0,'RDF_extractor-0.0.1.jar',1,'No description in manifest.');
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,description,type,jar_path,visibility,jar_description) VALUES(2,'RDF File Extractor','Extracts RDF data from a file.',0,'File_extractor-0.0.1.jar',1,'No description in manifest.');
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,description,type,jar_path,visibility,jar_description) VALUES(3,'SPARQL Transformer','SPARQL Transformer.',1,'SPARQL_transformer-0.0.1.jar',1,'No description in manifest.');
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,description,type,jar_path,visibility,jar_description) VALUES(4,'SPARQL Loader','Loads RDF data.',2,'RDF_loader-0.0.1.jar',1,'No description in manifest.');
INSERT INTO DB.INTLIB.DPU_TEMPLATE(id,name,description,type,jar_path,visibility,jar_description) VALUES(5,'RDF File Loader','Loads RDF data into file.',2,'File_loader-0.0.1.jar',1,'No description in manifest.');

-- check
SELECT * FROM DB.INTLIB.DPU_TEMPLATE;
