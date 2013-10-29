-- Update DPU names for jar files
 
UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_directory ='RDF_File_Extractor'
WHERE jar_directory = 'File_extractor';

UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_directory ='RDF_File_Loader'
WHERE jar_directory = 'File_loader';

UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_directory ='SPARQL_Extractor'
WHERE jar_directory = 'RDF_extractor';

UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_directory ='SPARQL_Loader'
WHERE jar_directory = 'RDF_loader';

UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_directory ='SPARQL_Transformer'
WHERE jar_directory = 'SPARQL_transformer';

UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_name ='RDF_File_Extractor-1.0.0.jar'
WHERE jar_name = 'File_extractor-1.0.0.jar';

UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_name ='RDF_File_Loader-1.0.0.jar'
WHERE jar_name = 'File_loader-1.0.0.jar';

UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_name ='SPARQL_Extractor-1.0.0.jar'
WHERE jar_name = 'RDF_extractor-1.0.0.jar';

UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_name ='SPARQL_Loader-1.0.0.jar'
WHERE jar_name = 'RDF_loader-1.0.0.jar';

UPDATE "DB"."ODCS"."DPU_TEMPLATE" 
SET jar_name ='SPARQL_Transformer-1.0.0.jar'
WHERE jar_name = 'SPARQL_transformer-1.0.0.jar';
