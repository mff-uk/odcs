#!/bin/bash

script=`readlink -f $0`
basedir=`dirname $script`

dumpdir="${basedir}/../virtuoso/rdbms"
config="${HOME}/.odcs/config.properties"

# parse out login info from configuration
dbuser=`grep "^database.sql.user" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbpass=`grep "^database.sql.password" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbhost=`grep "^database.sql.hostname" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbport=`grep "^database.sql.port" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`

# dump data in relational database for intlib
echo "fk_check_input_values(0);" > "${dumpdir}/data.sql"
dbdump "${dbhost}:${dbport}" "$dbuser" "$dbpass" tablename=db.odcs.% -c \
	| sed 's/^EXIT;$//' >> "${dumpdir}/data.sql"
echo "fk_check_input_values(1);" >> "${dumpdir}/data.sql"

# generate sequence setters
cat "${dumpdir}/schema.sql" | egrep "^(CREATE TABLE|sequence_set)" \
        | awk  '/^sequence_set/ { seq1=$1; seq2=$3;}
                /^CREATE TABLE/ { if(seq1) print seq1, "(SELECT COALESCE(MAX(id)+1,1) FROM "$3"),", seq2; seq1="";}' \
        > "${dumpdir}/sequences.sql"

