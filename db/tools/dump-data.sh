#!/bin/bash

script=`readlink -f $0`
basedir=`dirname $script`

dumpdir="${basedir}/../virtuoso/rdbms"
config="${HOME}/.odcs/config.properties"

# parse out login info from configuration
dbuser=`grep "^virtuoso.rdbms.user" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbpass=`grep "^virtuoso.rdbms.password" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbhost=`grep "^virtuoso.rdbms.hostname" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbport=`grep "^virtuoso.rdbms.port" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`

# dump data in relational database for intlib
echo "fk_check_input_values(0);" > "${dumpdir}/data.sql"
dbdump "${dbhost}:${dbport}" "$dbuser" "$dbpass" tablename=db.odcs.% -c \
	| sed 's/^EXIT;$//' >> "${dumpdir}/data.sql"
echo "fk_check_input_values(1);" >> "${dumpdir}/data.sql"
