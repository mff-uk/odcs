#!/bin/bash

#create dirs needed for the copy 
echo "Creating dir: $1"
ssh -p 42222 knap@odcs.xrg.cz mkdir /home/knap/tmp/$1
ssh -p 42222 knap@odcs.xrg.cz mkdir /home/knap/tmp/$1/lib


#copy backend
scp -r -P 42222 /Users/tomasknap/Documents/PROJECTS/ETL-SWProj/intlib/backend/target/*.jar knap@odcs.xrg.cz:/home/knap/tmp/$1
scp -r -P 42222  /Users/tomasknap/Documents/PROJECTS/ETL-SWProj/intlib/backend/target/lib/* knap@odcs.xrg.cz:/home/knap/tmp/$1/lib

#copy frontend
scp -r -P 42222 /Users/tomasknap/Documents/PROJECTS/ETL-SWProj/intlib/frontend/target/*.war knap@odcs.xrg.cz:/home/knap/tmp/$1/odcleanstore.war

#copy core dpus and libs
#TODO clean the dpu folder before? TODO check that the folder exists!
scp -r -P 42222 /Users/tomasknap/Documents/PROJECTS/ETL-SWProj/intlib/target/dpu/* knap@odcs.xrg.cz:/data/odcs/target/dpu
scp -r -P 42222 /Users/tomasknap/Documents/PROJECTS/ETL-SWProj/intlib/target/lib/* knap@odcs.xrg.cz:/data/odcs/target/lib

#db import
#TODO ask if import db?
scp -r -P 42222 /Users/tomasknap/Documents/PROJECTS/ETL-SWProj/intlib/db/virtuoso/rdbms/* knap@odcs.xrg.cz:/home/knap/tmp/$1
#ssh -p 42222 knap@odcs.xrg.cz /usr/local/bin/isql-v -U dba -P dba01OD -S 1119 < ~/tmp/$1/schema.sql
#ssh -p 42222 knap@odcs.xrg.cz /usr/local/bin/isql-v -U dba -P dba01OD -S 1119 < ~/tmp/$1/data.sql



#TODO copy config.properties, check that backendWorking is existing

#TODO copy backend/frontend primo na misto

