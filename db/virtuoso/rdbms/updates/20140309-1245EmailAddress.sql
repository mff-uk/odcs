DROP INDEX "ix_SCH_EMAIL_email";

ALTER TABLE "DB"."ODCS"."SCH_EMAIL" ADD "email" VARCHAR(255);

UPDATE "DB"."ODCS"."SCH_EMAIL" SET email = concat(e_user, '@', e_domain);

ALTER TABLE "DB"."ODCS"."SCH_EMAIL" DROP "e_user";
ALTER TABLE "DB"."ODCS"."SCH_EMAIL" DROP "e_domain";

CREATE INDEX "ix_SCH_EMAIL_email" ON "DB"."ODCS"."SCH_EMAIL" ("email");
