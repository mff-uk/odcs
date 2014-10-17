#!/bin/bash
# set where config file is located
BACKUP_CONFIG="backup.conf"

if [ -r "$BACKUP_CONFIG" ]; then
  . $BACKUP_CONFIG
fi

timestamp=$(date +"%Y-%m-%d_%H:%M:%S")

echo "start: "$timestamp

zip -r  backend.zip $BACKEND_JAR $BACKEND_CONF $BACKEND_LIB
zip frontend.zip $FRONTEND_JAR $FRONTEND_CONF
zip -r plugins.zip $PLUGINS


DB_DUMP_NAME=db_$timestamp.sql
mysqldump $MYSQL_NAME  -u $MYSQL_USER --password=$MYSQL_PASS --ignore-table=$MYSQL_NAME.logging > $DB_DUMP_NAME

BACKUP_NAME=uv_backup_$timestamp.zip
zip $BACKUP_NAME  backend.zip frontend.zip plugins.zip $DB_DUMP_NAME

if [ ! -d $OUT_DIRECTORY ]; then
    mkdir -p $OUT_DIRECTORY
fi

mv $BACKUP_NAME $OUT_DIRECTORY

rm  backend.zip frontend.zip plugins.zip  $DB_DUMP_NAME 

timestamp=$(date +"%Y-%m-%d_%H:%M:%S")

echo "successfully end: "$timestamp


