package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.i18n.LocaleContextHolder;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.frontend.FrontendMessages;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ClassAccessor;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
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
        FrontendMessages messages = new FrontendMessages(LocaleContextHolder.getLocale(), this.getClass().getClassLoader());
        switch (id) {
            case "id":
                return messages.getString("ExecutionViewAccessor.id");
            case "start":
                return messages.getString("ExecutionViewAccessor.started");
            case "pipelineName":
                return messages.getString("ExecutionViewAccessor.pipeline");
            case "duration":
                return messages.getString("ExecutionViewAccessor.duration");
            case "ownerName":
                return messages.getString("ExecutionViewAccessor.owner");
            case "status":
                return messages.getString("ExecutionViewAccessor.status");
            case "isDebugging":
                return messages.getString("ExecutionViewAccessor.isDebugging");
            case "lastChange":
                return messages.getString("ExecutionViewAccessor.lastChange");
            case "schedule":
                return messages.getString("ExecutionViewAccessor.schedule");
            default:
                return null;
        }
    }

    @Override
    public Object getValue(ExecutionView object, String id) {
        switch (id) {
            case "id":
                return object.getId();
            case "start":
                return object.getStart();
            case "pipelineId":
                return object.getPipelineId();
            case "pipelineName":
                String name = object.getPipelineName();
                return name.length() > Utils.getColumnMaxLenght() ? name.substring(0, Utils.getColumnMaxLenght() - 3) + "..." : name;
            case "ownerName":
                return object.getOwnerName();
            case "duration":
                return object.getDuration();
            case "status":
                PipelineExecutionStatus status = object.getStatus();
                return object.isStop() && status == PipelineExecutionStatus.RUNNING ? PipelineExecutionStatus.CANCELLING : status;
            case "isDebugging":
                return object.isDebugging();
            case "lastChange":
                return object.getLastChange();
            case "schedule":
                return object.getSchedule() != null;
            default:
                return null;
        }
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
