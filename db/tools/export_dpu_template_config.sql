select concat( 'update dpu_template set configuration = ','''',configuration,'''', ' where id =  ',id,';')  AS 'test' from dpu_template ;
