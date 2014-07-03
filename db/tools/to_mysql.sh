#!/bin/bash
source=$1
destination=$2
# get rid of prefix DB.ODCS
# name of tables to lower-case
# remove final command EXIT
echo "sed -e s/DB\.ODCS\.//g $source > tmp; mv tmp $destination"
sed -e "s/DB\.ODCS\.//g" $source > tmp; mv tmp $destination

echo "sed -e 's/DPU_INSTANCE/dpu_instance/g  '   $destination >tmp; mv tmp $destination"
sed -e 's/DPU_INSTANCE/dpu_instance/g'   $destination >tmp; mv tmp $destination

echo "sed -e 's/DPU_TEMPLATE/dpu_template/g'  $destination >tmp; mv tmp $destination"
sed -e 's/DPU_TEMPLATE/dpu_template/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/EXEC_CONTEXT_DPU/exec_context_dpu/g'  $destination >tmp; mv tmp $destination"
sed -e 's/EXEC_CONTEXT_DPU/exec_context_dpu/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/EXEC_CONTEXT_PIPELINE/exec_context_pipeline/g'  $destination >tmp; mv tmp $destination"
sed -e 's/EXEC_CONTEXT_PIPELINE/exec_context_pipeline/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/EXEC_DATAUNIT_INFO/exec_dataunit_info/g'  $destination >tmp; mv tmp $destination"
sed -e 's/EXEC_DATAUNIT_INFO/exec_dataunit_info/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/EXEC_PIPELINE/exec_pipeline/g'  $destination >tmp; mv tmp $destination"
sed -e 's/EXEC_PIPELINE/exec_pipeline/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/EXEC_RECORD/exec_record/g'  $destination >tmp; mv tmp $destination"
sed -e 's/EXEC_RECORD/exec_record/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/EXEC_SCHEDULE_AFTER/exec_schedule_after/g'  $destination >tmp; mv tmp $destination"
sed -e 's/EXEC_SCHEDULE_AFTER/exec_schedule_after/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/EXEC_SCHEDULE/exec_schedule/g'  $destination >tmp; mv tmp $destination"
sed -e 's/EXEC_SCHEDULE/exec_schedule/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/LOGGING/logging/g'  $destination >tmp; mv tmp $destination"
sed -e 's/LOGGING/logging/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/PPL_EDGE/ppl_edge/g'  $destination >tmp; mv tmp $destination"
sed -e 's/PPL_EDGE/ppl_edge/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/PPL_GRAPH/ppl_graph/g'  $destination >tmp; mv tmp $destination"
sed -e 's/PPL_GRAPH/ppl_graph/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/PPL_MODEL/ppl_model/g'  $destination >tmp; mv tmp $destination"
sed -e 's/PPL_MODEL/ppl_model/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/PPL_NODE/ppl_node/g'  $destination >tmp; mv tmp $destination"
sed -e 's/PPL_NODE/ppl_node/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/PPL_OPEN_EVENT/ppl_open_event/g'  $destination >tmp; mv tmp $destination"
sed -e 's/PPL_OPEN_EVENT/ppl_open_event/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/PPL_POSITION/ppl_position/g'  $destination >tmp; mv tmp $destination"
sed -e 's/PPL_POSITION/ppl_position/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/PPL_PPL_CONFLICTS/ppl_ppl_conflicts/g'  $destination >tmp; mv tmp $destination"
sed -e 's/PPL_PPL_CONFLICTS/ppl_ppl_conflicts/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/RDF_NS_PREFIX/rdf_ns_prefix/g'  $destination >tmp; mv tmp $destination"
sed -e 's/RDF_NS_PREFIX/rdf_ns_prefix/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/SCH_EMAIL/sch_email/g'  $destination >tmp; mv tmp $destination"
sed -e 's/SCH_EMAIL/sch_email/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/SCH_SCH_NOTIFICATION_EMAIL/sch_sch_notification_email/g'  $destination >tmp; mv tmp $destination"
sed -e 's/SCH_SCH_NOTIFICATION_EMAIL/sch_sch_notification_email/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/SCH_SCH_NOTIFICATION/sch_sch_notification/g'  $destination >tmp; mv tmp $destination"
sed -e 's/SCH_SCH_NOTIFICATION/sch_sch_notification/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/SCH_USR_NOTIFICATION_EMAIL/sch_usr_notification_email/g'  $destination >tmp; mv tmp $destination"
sed -e 's/SCH_USR_NOTIFICATION_EMAIL/sch_usr_notification_email/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/SCH_USR_NOTIFICATION/sch_usr_notification/g'  $destination >tmp; mv tmp $destination"
sed -e 's/SCH_USR_NOTIFICATION/sch_usr_notification/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/USR_USER_ROLE/usr_user_role/g'  $destination >tmp; mv tmp $destination"
sed -e 's/USR_USER_ROLE/usr_user_role/g'  $destination >tmp; mv tmp $destination

echo "sed -e 's/USR_USER/usr_user/g'  $destination >tmp; mv tmp $destination"
sed -e 's/USR_USER/usr_user/g'  $destination >tmp; mv tmp $destination

echo "sed -e "s/EXIT//g" $destination >tmp; mv tmp $destination"
sed -e "s/EXIT//g" $destination >tmp; mv tmp $destination


