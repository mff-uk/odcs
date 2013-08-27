package cz.cuni.xrg.intlib.backend.dpu.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Base abstract class for the DPURecord event.
 * 
 * @author Petyr
 * 
 */
public abstract class DPUEvent extends ApplicationEvent {

	/**
	 * Time of creation.
	 */
	protected Date time;

	/**
	 * Related context. Identify {@link PipelineExecution} as well as
	 * {@link DPUInstanceRecord}
	 */
	protected Context context;

	/**
	 * Message type.
	 */
	protected MessageRecordType type;

	/**
	 * Short event's message.
	 */
	protected String shortMessage;

	/**
	 * Long event's message.
	 */
	protected String longMessage;

	public DPUEvent(Context context, Object source) {
		super(source);
		this.time = new Date();
		this.context = context;
		this.shortMessage = "";
		this.longMessage = "";
	}

	public DPUEvent(Context context,
			Object source,
			MessageRecordType type,
			String shortMessage) {
		super(source);
		this.time = new Date();
		this.context = context;
		this.type = type;
		this.shortMessage = shortMessage;
		this.longMessage = "";
	}

	public DPUEvent(Context context,
			Object source,
			MessageRecordType type,
			String shortMessage,
			String longMessage) {
		super(source);
		this.time = new Date();
		this.context = context;
		this.type = type;
		this.shortMessage = shortMessage;
		this.longMessage = longMessage;
	}

	/**
	 * Record that describes event.
	 * 
	 * @return record
	 */
	public MessageRecord getRecord() {
		return new MessageRecord(time, type,
				context.getDpuInstance(), context.getExecution(),
				shortMessage, longMessage);

	}

	public PipelineExecution getExecution() {
		return context.getExecution();
	}

	public DPUInstanceRecord getDpuInstance() {
		return context.getDpuInstance();
	}

}
