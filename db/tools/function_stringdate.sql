CREATE FUNCTION stringdate(var VARCHAR(255))  RETURNS DATETIME DETERMINISTIC RETURN  STR_TO_DATE(var,  '%Y.%d.%m %H:%i.%s');