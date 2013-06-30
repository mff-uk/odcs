/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.commons.app.execution;

import java.util.Date;
import java.util.logging.Level;

/**
 *
 * @author Bogo
 */
public class LogMessage {
	
	private Long id;
	
	private Level level;
	
	private Date date;
	
	private String thread;
	
	private String source;
	
	private String message;

	public LogMessage(Long id, Level lvl, Date dt, String thr, String src, String msg) {
		this.id = id;
		level = lvl;
		date = dt;
		thread = thr;
		source = src;
		message = msg;
	}
	
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the thread
	 */
	public String getThread() {
		return thread;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	

	
}
