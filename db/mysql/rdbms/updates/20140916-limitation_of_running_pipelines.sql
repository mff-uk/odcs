ALTER TABLE exec_pipeline ADD order_number BIGINT  not null DEFAULT -1;

ALTER TABLE exec_pipeline ADD created_epoch BIGINT not null;

ALTER TABLE exec_schedule ADD priority BIGINT not null;