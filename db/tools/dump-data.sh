#!/bin/bash

script=`readlink -f $0`
basedir=`dirname $script`
dumpdir="${basedir}/../virtuoso/rdbms"

# TODO parse out login info from configuration
dbuser="dba"
dbpass="dba"
dbhost="localhost"
dbport="1111"

# dump data in relational database for intlib
echo "fk_check_input_values(0);" > "${dumpdir}/data.sql"
dbdump "${dbhost}:${dbport}" "$dbuser" "$dbpass" tablename=db.intlib.% -c \
	| sed 's/^EXIT;$//' >> "${dumpdir}/data.sql"
echo "fk_check_input_values(1);" >> "${dumpdir}/data.sql"
