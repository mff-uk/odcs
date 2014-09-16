ALTER TABLE exec_pipeline ADD order_number Long not null;

ALTER TABLE exec_pipeline ADD created_epoch Long not null;

ALTER TABLE exec_schedule ADD priority Long not null;