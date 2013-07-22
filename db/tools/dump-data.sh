#!/bin/bash

script=`readlink -f $0`
basedir=`dirname $script`

dumpdir="${basedir}/../virtuoso/rdbms"
config="${HOME}/.intlib/config.properties"

# parse out login info from configuration
dbuser=`grep "^virtuoso.user" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbpass=`grep "^virtuoso.password" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbhost=`grep "^virtuoso.hostname" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbport=`grep "^virtuoso.port" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`

# dump data in relational database for intlib
echo "fk_check_input_values(0);" > "${dumpdir}/data.sql"
dbdump "${dbhost}:${dbport}" "$dbuser" "$dbpass" tablename=db.intlib.% -c \
	| sed 's/^EXIT;$//' >> "${dumpdir}/data.sql"
echo "fk_check_input_values(1);" >> "${dumpdir}/data.sql"
