#!/bin/bash

script=`readlink -f $0`
basedir=`dirname $script`

dumpdir="${basedir}/../virtuoso/rdbms"
config="${HOME}/.odcs/config.properties"

# parse out login info from configuration
dbuser=`grep "^virtuoso.user" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbpass=`grep "^virtuoso.password" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbhost=`grep "^virtuoso.hostname" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`
dbport=`grep "^virtuoso.port" $config | head -n 1 | sed -r "s/^[^=]*=\\s?//"`

# recreate schema and import data
cat "${dumpdir}/schema.sql" "${dumpdir}/data.sql" | \
	isql-v "${dbhost}:${dbport}" "$dbuser" "$dbpass"

