-- ##############################################################
-- ##    Database update script for standard UV installation    #
-- ##    Update from v. 2.0.x to 2.1.0                          #
-- ##############################################################

-- Execute core update script
\. ./2.1.0-update-core.sql

-- Execute permissions script
\. ../data-permissions.sql
