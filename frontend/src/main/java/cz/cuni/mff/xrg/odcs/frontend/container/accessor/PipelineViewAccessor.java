package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.DecorationHelper;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ClassAccessor;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import eu.unifiedviews.commons.dao.view.PipelineView;

/**
 * Accessor for {@link Pipeline}s.
 *
 * @author Škoda Petr
 */
public class PipelineViewAccessor implements ClassAccessor<PipelineView> {

    private final List<String> all = Arrays.asList("id", "name", "duration", "lastExecTime", "lastExecStatus");

    private final List<String> visible = Arrays.asList("name", "duration", "lastExecTime", "lastExecStatus");

    private final List<String> sortable = Arrays.asList("name");

    private final List<String> filterable = Arrays.asList("name");

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
    public Class<PipelineView> getEntityClass() {
        return PipelineView.class;
    }

    @Override
    public String getColumnName(String id) {
        switch (id) {
            case "id":
                return "Id";
            case "name":
                return "Name";
            case "duration":
                return "Last run time";
            case "lastExecTime":
                return "Last execution time";
            case "lastExecStatus":
                return "Last status";
            default:
                return id;
        }
    }

    @Override
    public Object getValue(PipelineView object, String id) {
        switch (id) {
            case "id":
                return object.getId();
            case "name":
                String name = object.getName();
                return name.length() > Utils.getColumnMaxLenght() ? name.substring(0, Utils.getColumnMaxLenght() - 3) + "..." : name;
            case "duration":
                return DecorationHelper.formatDuration(object.getDuration());
            case "lastExecTime":
                return object.getStart();
            case "lastExecStatus":
                final PipelineExecutionStatus type = object.getStatus();
                if (type != null) {
                    ThemeResource img = DecorationHelper.getIconForExecutionStatus(type);
                    Embedded emb = new Embedded(type.name(), img);
                    emb.setDescription(type.name());
                    return emb;
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    @Override
    public Class<?> getType(String id) {
        switch (id) {
            case "id":
                return Integer.class;
            case "name":
            case "duration":
            case "lastExecTime":
                return String.class;
            case "lastExecStatus":
                return Embedded.class;
            default:
                return null;
        }
    }

}
