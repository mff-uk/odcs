package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import cz.cuni.mff.xrg.odcs.frontend.container.ClassAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Container accessor for {@link Log}.
 * 
 * @author Petyr
 */
public class LogNewAccessor implements ClassAccessor<Log> {
	
	private final List<String> all = Arrays.asList("id", "date", "level", "dpuInstance", "message", "source");
	private final List<String> sortable = Arrays.asList("date", "level", "dpuInstance");
	private final List<String> filtrable = Arrays.asList("date", "level", "dpuInstance", "message", "source");
	private final List<String> visible = Arrays.asList("date", "level", "dpuInstance", "message", "source");

	@Override
	public List<String> all() {
		return all;
	}

	@Override
	public List<String> sortable() {
		return sortable;
	}

	@Override
	public List<String> filtrable() {
		return filtrable;
	}

	@Override
	public List<String> visible() {
		return visible;
	}
	
	@Override
	public Class<Log> getEntityClass() {
		return Log.class;
	}

	@Override
	public String getColumnName(String id) {
		switch (id) {
			case "dpuInstance":
				return "DPU Instance";
			default:
				return id;
		}
	}

	@Override
	public Object getValue(Log object, String id) {
		switch (id) {
			case "id":
				return object.getId();
			case "date":
				return new Date(object.getTimestamp());
			case "level":
				return object.getLogLevel();
			case "dpuInstance":
				return object.getDpuInstance();
			case "message":
				return object.getMessage();
			case "source":
				return object.getSource();
			default:
				return null;
		}
	}

	@Override
	public Class<?> getType(String id) {
		switch (id) {
			case "id":
				return Long.class;
			case "date":
				return Date.class;
			case "level":
				return Integer.class;
			case "dpuInstance":
				return DPUInstanceRecord.class;
			case "message":
			case "source":
				return String.class;
			default:
				return null;
		}
	}	
	
}
