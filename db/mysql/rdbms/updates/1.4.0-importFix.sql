-- !!! this update requires: !!!
-- 1. to stop application
-- 2. to apply this sql commands
-- 3. to rename folders in directory selected in config.properties in property module.path in dpu directory !!!
--    so that the directory there is no version. example: uv-t-sparql-1.3.1 -> uv-t-sparql
-- 4. to start application

-- removes string string starts as -[0-9]
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-0', 1) where jar_directory LIKE '%-0%';
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-1', 1) where jar_directory LIKE '%-1%';
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-2', 1) where jar_directory LIKE '%-2%';
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-3', 1) where jar_directory LIKE '%-3%';
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-4', 1) where jar_directory LIKE '%-4%';
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-5', 1) where jar_directory LIKE '%-5%';
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-6', 1) where jar_directory LIKE '%-6%';
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-7', 1) where jar_directory LIKE '%-7%';
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-8', 1) where jar_directory LIKE '%-8%';
update dpu_template set jar_directory = SUBSTRING_INDEX(jar_directory, '-9', 1) where jar_directory LIKE '%-9%';