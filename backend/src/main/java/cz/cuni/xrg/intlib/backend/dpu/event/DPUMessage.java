package cz.cuni.xrg.intlib.backend.dpu.event;

import java.util.Date;

import cz.cuni.xrg.intlib.backend.context.ExtendedContext;
import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Class for representing DPU messages send by ProcessingContext sendMessage.
 * 
 * @author Petyr
 *
 */
public class DPUMessage extends DPUEvent {

	/**
	 * Time of creation.
	 */
	private Date time;
	
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
	private Type type;
	
	/**
	 * Related pipeline execution.
	 */
	private PipelineExecution execution;
	
	/**
	 * Related dpu instance.
	 */
	private DPUInstance dpuInstance;
	
	public DPUMessage(String shortMessage, String fullMessage, Type type, ExtendedContext context, Object source) {
		super(source);
		this.time = new Date();
		this.shortMessage = shortMessage;
		this.fullMessage = fullMessage;
		this.type = type;
		this.execution = context.getPipelineExecution();
		this.dpuInstance = context.getDPUInstance();
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

	public Type getType() {
		return type;
	}

	public PipelineExecution getExecution() {
		return execution;
	}

	public DPUInstance getDpuInstance() {
		return dpuInstance;
	}

}
