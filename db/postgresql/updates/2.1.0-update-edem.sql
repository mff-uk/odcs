-- ##############################################################
-- ##    Database update script for eDemo UV installation       #
-- ##    Update from v. 2.0.x to 2.1.0                          #
-- ##############################################################

-- Execute update script
\ir 2.1.0-update-core.sql

-- Execute permissions script
\ir ../data-permissions-edem.sql