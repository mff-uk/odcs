ALTER TABLE exec_pipeline ADD order_number BIGINT  not null;
ALTER TABLE exec_pipeline ADD created_epoch BIGINT not null;
ALTER TABLE exec_schedule ADD priority BIGINT not null;

UPDATE exec_pipeline SET order_number = 1 ;
UPDATE exec_pipeline SET created_epoch = UNIX_TIMESTAMP(NOW());
UPDATE exec_schedule SET priority = 1 ;