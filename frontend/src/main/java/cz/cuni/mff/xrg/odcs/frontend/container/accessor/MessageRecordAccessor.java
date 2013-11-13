package cz.cuni.mff.xrg.odcs.frontend.container.accessor;

import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.frontend.container.ClassAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Bogo
 */
public class MessageRecordAccessor implements ClassAccessor<MessageRecord> {

	private List<String> all = Arrays.asList("time", "type", "dpuInstance.name", "shortMessage", "dpuInstance.id");
	private List<String> sortable = Arrays.asList("time", "type", "dpuInstance.name");
	private List<String> filtrable = Arrays.asList("time", "type", "dpuInstance.name", "shortMessage");

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
				return object.getDpuInstance().getName();
			case "shortMessage":
				return object.getShortMessage();
			case "dpuInstance.id":
				return object.getDpuInstance().getId();
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
