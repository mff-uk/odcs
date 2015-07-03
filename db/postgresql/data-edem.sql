-- ###################################################################
-- ##    Database init script for eDemo specific UV installation     #
-- ###################################################################

-- Execute data core SQL script
\ir data-core.sql

-- Add eDemo specific user roles
INSERT INTO "role" VALUES (nextval('seq_role'), 'MOD-R-PO'),(nextval('seq_role'),'MOD-R-TRANSA'); 

-- Execute eDemo permissions SQL script
\ir data-permissions-edem.sql
