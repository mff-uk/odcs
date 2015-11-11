ALTER TABLE ppl_position ADD NODE_id BIGINT;
UPDATE ppl_position SET NODE_id = (SELECT ppl_node.id FROM ppl_node WHERE ppl_node.position_id=ppl_position.id);

DROP TABLE properties;
DROP VIEW pipeline_view;
DROP VIEW exec_last_view;
DROP VIEW exec_view;