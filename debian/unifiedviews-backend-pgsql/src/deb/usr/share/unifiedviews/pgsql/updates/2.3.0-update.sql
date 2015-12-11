ALTER TABLE ppl_position ADD NODE_id BIGINT;
UPDATE ppl_position SET NODE_id = (SELECT ppl_node.id FROM ppl_node WHERE ppl_node.position_id=ppl_position.id);

ALTER TABLE dpu_instance DROP COLUMN config_valid;
ALTER TABLE dpu_template DROP COLUMN config_valid;

DROP TABLE properties;
DROP VIEW pipeline_view;
DROP VIEW exec_last_view;
DROP VIEW exec_view;