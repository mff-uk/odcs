package cz.cuni.xrg.intlib.backend.dpu.event;

import java.util.Date;

import cz.cuni.xrg.intlib.backend.context.ExtendedContext;
import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecordType;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * Class for representing DPU messages send by ProcessingContext sendMessage.
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
	private DPURecordType type;
	
	/**
	 * Related pipeline execution.
	 */
	private PipelineExecution execution;
		
	public DPUMessage(String shortMessage, String fullMessage, MessageType type, ExtendedContext context, Object source) {
		super(source, context.getDPUInstance(), new Date());
		this.shortMessage = shortMessage;
		this.fullMessage = fullMessage;
		this.type = DPURecordType.fromMessageType(type);
		this.execution = context.getPipelineExecution();
	}

	@Override
	public DPURecord getRecord() {
		return new DPURecord(time, type, dpuInstance, shortMessage, fullMessage);		
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

	public DPURecordType getType() {
		return type;
	}

	public PipelineExecution getExecution() {
		return execution;
	}

	public DPUInstance getDpuInstance() {
		return dpuInstance;
	}

}
