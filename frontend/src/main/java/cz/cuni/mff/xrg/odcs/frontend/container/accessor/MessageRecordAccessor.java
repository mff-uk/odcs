package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ClassAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Bogo
 */
public class MessageRecordAccessor implements ClassAccessor<MessageRecord> {

	private final List<String> all = Arrays.asList("id","time", "type", "dpuInstance.name", "shortMessage", "dpuInstance.id");
	
	private final List<String> sortable = Arrays.asList("time", "type", "dpuInstance.name");
	
	private final List<String> filtrable = Arrays.asList("time", "type", "dpuInstance.name", "shortMessage");
	
	private final List<String> visible = Arrays.asList("time", "type", "dpuInstance.name", "shortMessage");
	
	private final List<String> toFetch = Arrays.asList("dpuInstance");
	
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
	public List<String> toFetch() {
		return toFetch;
	}
	
	@Override
	public Class<MessageRecord> getEntityClass() {
		return MessageRecord.class;
	}

	@Override
	public String getColumnName(String id) {
		switch (id) {
			case "time":
				return "Date";
			case "dpuInstance.name":
				return "DPU Instance";
			case "shortMessage":
				return "Short message";
			default:
				return id;
		}
	}

	@Override
	public Object getValue(MessageRecord object, String id) {
		switch (id) {
			case "id":
				return object.getId();
			case "time":
				return object.getTime();
			case "type":
				return object.getType();
			case "dpuInstance.name":
				if(object.getDpuInstance() != null) {
					return object.getDpuInstance().getName();
				} else {
					return "";
				}
			case "shortMessage":
				return object.getShortMessage();
			case "dpuInstance.id":
				if(object.getDpuInstance() != null) {
					return object.getDpuInstance().getId();
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
			case "dpuInstance.id":
				return Long.class;
			case "time":
				return Date.class;
			case "type":
				return MessageRecordType.class;
			case "dpuInstance.name":
			case "shortMessage":
				return String.class;
			default:
				return null;
		}
	}
}
