-- ##############################################################
-- ##    Database update script for standard UV installation    #
-- ##    Update from v. 2.0.x to 2.1.0                          #
-- ##############################################################

-- Execute update script
\ir 2.1.0-update-core.sql

-- Clear permissions table and insert latest version of permissions
DELETE FROM permission;
ALTER SEQUENCE "seq_permission" RESTART WITH 1;
\ir ../data-permissions.sql