package cz.cuni.xrg.intlib.backend.dpu.event;

import java.util.Date;

import cz.cuni.xrg.intlib.backend.context.ExtendedContext;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.execution.RecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * Class for representing DPURecord messages send by ProcessingContext sendMessage.
 * 
 * @author Petyr
 *
 */
public class DPUMessage extends DPUEvent {
	
	/**
	 * Short message.
	 */
	private String shortMessage;
	
	/**
	 * Full-long message.
	 */
	private String fullMessage;
	
	/**
	 * Type of message.
	 */
	private RecordType type;
	
	/**
	 * Related pipeline execution.
	 */
	private PipelineExecution execution;
		
	public DPUMessage(String shortMessage, String fullMessage, MessageType type, ExtendedContext context, Object source) {
		super(source, context.getDPUInstance(), context.getPipelineExecution(), new Date());
		this.shortMessage = shortMessage;
		this.fullMessage = fullMessage;
		this.type = RecordType.fromMessageType(type);
		this.execution = context.getPipelineExecution();
	}

	@Override
	public Record getRecord() {
		return new Record(time, type, dpuInstance, execution, shortMessage, fullMessage);		
	}
	
	public Date getTime() {
		return time;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public RecordType getType() {
		return type;
	}

	public PipelineExecution getExecution() {
		return execution;
	}

	public DPUInstanceRecord getDpuInstance() {
		return dpuInstance;
	}

}
