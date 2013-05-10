package cz.cuni.xrg.intlib.commons.app.dpu.execution;

import java.util.Date;

import cz.cuni.xrg.intlib.commons.DPUExecutive;

/**
 * Represent a single message created during DPU execution. 
 * 
 * @author Petyr
 * @author Bogo
 *
 */
public class Record {

	/**
	 * Unique id.
	 */
	private int id;
	
	/**
	 * Time of creation.
	 */
	private Date time;
	
	/**
	 * Type of record.
	 */
	private RecordType type;
	
	/**
	 * Source of message.
	 */
	private DPUExecutive source;
	
	/**
	 * Short message, should be under 50 characters.
	 */
	private String shortMessage;
	
	/**
	 * Full message text.
	 */
	private String fullMessage;
	
	public Record(Date time, RecordType type, DPUExecutive source, String shortMessage, String fullMessage ) {
		this.time = time;
		this.type = type;
		this.source = source;
		this.shortMessage = shortMessage;
		this.fullMessage = fullMessage;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public RecordType getType() {
		return type;
	}

	public DPUExecutive getSource() {
		return source;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public String getFullMessage() {
		return fullMessage;
	}	
}
