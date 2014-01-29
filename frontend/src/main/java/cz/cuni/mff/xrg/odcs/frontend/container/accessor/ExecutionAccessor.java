package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ClassAccessor;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;

public class ExecutionAccessor implements ClassAccessor<PipelineExecution> {

	private final List<String> all = Arrays.asList("id", "start", "pipeline.name", "owner.username", "duration", "status", "isDebugging", "schedule");
	private final List<String> sortable = Arrays.asList("id", "start", "pipeline.name", "owner.username", "status", "isDebugging", "schedule");
	private final List<String> filterable = Arrays.asList("id", "start", "pipeline.name", "owner.username", "status", "isDebugging", "schedule");
	private final List<String> toFetch = Arrays.asList("pipeline", "owner");

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
		return all;
	}

	@Override
	public List<String> toFetch() {
		return toFetch;
	}

	@Override
	public Class<PipelineExecution> getEntityClass() {
		return PipelineExecution.class;
	}

	@Override
	public String getColumnName(String id) {
		switch (id) {
			case "id":
				return "id";
			case "start":
				return "start";
			case "pipeline.name":
				return "pipeline name";
			case "owner.username":
				return "author";
			case "duration":
				return "duration";
			case "status":
				return "status";
			case "isDebugging":
				return "debugging";
			case "lastChange":
				return "last modification";
			case "schedule":
				return "scheduled";
			default:
				return null;
		}
	}

	@Override
	public Object getValue(PipelineExecution object, String id) {
		switch (id) {
			case "id":
				return object.getId();
			case "start":
				return object.getStart();
			case "pipeline.name":
				String name = object.getPipeline().getName();
				return name.length() > Utils.getColumnMaxLenght() ? name.substring(0, Utils.getColumnMaxLenght() - 3) + "..." : name;
			case "owner.username":
				return object.getOwner().getUsername();
			case "duration":
				return object.getDuration();
			case "status":
				return object.getStatus();
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
				return Long.class;
			case "start":
				return Timestamp.class;
			case "pipeline.name":
				return String.class;
			case "owner.username":
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
			default:
				return null;
		}
	}
}
