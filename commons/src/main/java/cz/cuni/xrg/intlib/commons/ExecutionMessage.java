/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.commons;

import java.util.Date;

/**
 *
 * @author Bogo
 */
public class ExecutionMessage {

	private int id;

	private Date time;

	private MessageType type;

	private DPUExecutive source;

	private String shortMessage;

	private String fullMessage;

	public ExecutionMessage() {

	}

	public ExecutionMessage(int id, Date time, MessageType type, DPUExecutive source, String shortMessage, String fullMessage ) {
		this.id = id;
		this.time = time;
		this.type = type;
		this.source = source;
		this.shortMessage = shortMessage;
		this.fullMessage = fullMessage;
	}

	public int getId() {
		return id;
	}

	public Date getTime() {
		return time;
	}

	public MessageType getType() {
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
