package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ClassAccessor;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import cz.cuni.mff.xrg.odcs.frontend.i18n.Messages;
import eu.unifiedviews.commons.dao.view.ExecutionView;

/**
 * Accessor for {@link Pipeline}s.
 * 
 * @author Škoda Petr
 */
public class ExecutionViewAccessor implements ClassAccessor<ExecutionView> {

    private final List<String> all = Arrays.asList("id", "start", "pipelineName", "duration", "status", "isDebugging", "schedule", "pipelineId", "ownerName");

    private final List<String> visible = Arrays.asList("status", "pipelineName", "start", "duration", "isDebugging", "schedule", "ownerName");

    private final List<String> sortable = Arrays.asList("pipelineName", "status", "start", "isDebugging", "schedule", "ownerName");

    private final List<String> filterable = Arrays.asList("pipelineName", "status", "start", "isDebugging", "schedule", "ownerName");

    private final List<String> toFetch = new LinkedList<>();

    @Override
    public List<String> all() {
        return all;
    }

    @Override
    public List<String> sortable() {
        return sortable;
    }

    @Override
    public List<String> filterable() {
        return filterable;
    }

    @Override
    public List<String> visible() {
        return visible;
    }

    @Override
    public List<String> toFetch() {
        return toFetch;
    }

    @Override
    public Class<ExecutionView> getEntityClass() {
        return ExecutionView.class;
    }

    @Override
    public String getColumnName(String id) {
        switch (id) {
            case "id":
                return Messages.getString("ExecutionViewAccessor.id");
            case "start":
                return Messages.getString("ExecutionViewAccessor.started");
            case "pipelineName":
                return Messages.getString("ExecutionViewAccessor.pipeline");
            case "duration":
                return Messages.getString("ExecutionViewAccessor.duration");
            case "ownerName":
                return Messages.getString("ExecutionViewAccessor.owner");
            case "status":
                return Messages.getString("ExecutionViewAccessor.status");
            case "isDebugging":
                return Messages.getString("ExecutionViewAccessor.isDebugging");
            case "lastChange":
                return Messages.getString("ExecutionViewAccessor.lastChange");
            case "schedule":
                return Messages.getString("ExecutionViewAccessor.schedule");
            default:
                return null;
        }
    }

    @Override
    public Object getValue(ExecutionView execution, String id) {
        switch (id) {
            case "id":
                return execution.getId();
            case "start":
                return execution.getStart();
            case "pipelineId":
                return execution.getPipelineId();
            case "pipelineName":
                String name = execution.getPipelineName();
                return name.length() > Utils.getColumnMaxLenght() ? name.substring(0, Utils.getColumnMaxLenght() - 3) + "..." : name;
            case "ownerName":
                return getPipelineCreatedByDisplayName(execution);
            case "duration":
                return execution.getDuration();
            case "status":
                PipelineExecutionStatus status = execution.getStatus();
                return execution.isStop() && status == PipelineExecutionStatus.RUNNING ? PipelineExecutionStatus.CANCELLING : status;
            case "isDebugging":
                return execution.isDebugging();
            case "lastChange":
                return execution.getLastChange();
            case "schedule":
                return execution.getSchedule() != null;
            default:
                return null;
        }
    }

    private String getPipelineCreatedByDisplayName(ExecutionView execution) {
        String executionOwnerName = (execution.getOwnerFullName() != null) ? execution.getOwnerFullName() : execution.getOwnerName();
        if (execution.getUserActorName() != null) {
            return executionOwnerName + " (" + execution.getUserActorName() + ")";
        }
        return executionOwnerName;
    }

    @Override
    public Class<?> getType(String id) {
        switch (id) {
            case "id":
            case "pipelineId":
                return Long.class;
            case "start":
                return Timestamp.class;
            case "pipelineName":
                return String.class;
            case "duration":
                return Long.class;
            case "status":
                return PipelineExecutionStatus.class;
            case "isDebugging":
                return Boolean.class;
            case "lastChange":
                return Timestamp.class;
            case "schedule":
                return Boolean.class;
            case "ownerName":
                return String.class;
            default:
                return null;
        }
    }

}
