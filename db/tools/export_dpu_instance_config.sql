select concat( 'update dpu_instance set configuration = ','''',configuration,'''', ' where id =  ',id,';')  AS 'test' from dpu_instance ;
