package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogMessage;
import cz.cuni.mff.xrg.odcs.frontend.container.ClassAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Level;

/**
 *
 * @author Bogo
 */
public class LogAccessor implements ClassAccessor<LogMessage> {

	private List<String> all = Arrays.asList("date", "level", "dpuInstanceId", "message", "source");
	private List<String> sortable = Arrays.asList("date", "level", "dpuInstanceId");
	private List<String> filtrable = Arrays.asList("date", "level", "dpuInstanceId", "message", "source");

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
		return all;
	}
	
	@Override
	public Class<LogMessage> getEntityClass() {
		return LogMessage.class;
	}

	@Override
	public String getColumnName(String id) {
		switch (id) {
			case "dpuInstanceId":
				return "DPU Instance";
			default:
				return id;
		}
	}

	@Override
	public Object getValue(LogMessage object, String id) {
		switch (id) {
			case "id":
				return object.getId();
			case "date":
				return object.getDate();
			case "level":
				return object.getLevel();
			case "dpuInstanceId":
				return object.getDpuInstanceId();
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
				return Level.class;
			case "dpuInstanceId":
				return Long.class;
			case "message":
			case "source":
				return String.class;
			default:
				return null;
		}
	}
}
