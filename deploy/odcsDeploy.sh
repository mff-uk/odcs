#*******************************************************************************
# This file is part of UnifiedViews.
#
# UnifiedViews is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# UnifiedViews is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
#*******************************************************************************
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

