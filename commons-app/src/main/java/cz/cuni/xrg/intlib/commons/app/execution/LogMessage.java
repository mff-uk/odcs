package cz.cuni.xrg.intlib.commons.app.execution;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.*;

/**
 * Represents log message loaded from database. Shown in DebuggingView/Log tab
 * by LogMessageTable.
 *
 * @author Bogo
 * @author Jan Vojt
 */
@Entity
@Table(name = "logging_event")
public class LogMessage {

	/**
	 * Primary key of message stored in database.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "event_id")
	private Long id;

	/**
	 * Level as string, so it can be persisted in DB.
	 */
	@Column(name = "level_string")
	private String levelString;

	/**
	 * Level of log message.
	 */
	@Transient
	private Level level;

	/*
	 * Timestamp of log message.
	 */
	@Column(name = "timestmp")
	private Long timestamp;

	/*
	 * Source thread of log message.
	 */
	@Column(name = "thread_name")
	private String thread;

	/*
	 * Source class of log message.
	 */
	@Column(name = "logger_name")
	private String source;

	/*
	 * Text of formatted log massage.
	 */
	@Column(name = "formatted_message")
	private String message;

	/**
	 * Properties associated with this log message. They can provide additional
	 * information about the message, such as DPU instance or pipeline execution
	 * which produced and logged message.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "mapped_key")
	@Column(name = "mapped_value")
	@CollectionTable(name = "logging_event_property", joinColumns =
			@JoinColumn(name = "event_id"))
	private Map<String, String> properties = new HashMap<>();

	/**
	 * Default constructor for JPA. Not to be used.
	 */
	public LogMessage() {
	}

	public LogMessage(Long id, Level lvl, Date dt, String thr, String src,
			String msg) {
		this.id = id;
		level = lvl;
		timestamp = dt.getTime();
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
	 * Lazily creates importance {@link Level} of this log from string in
	 * {@link #levelString}. If string representation is sufficient, use
	 * {@link #getLevelString()} instead for performance reasons.
	 *
	 * @return the level
	 */
	public Level getLevel() {

		if (level == null && levelString != null) {
			switch (levelString) {
				case "SEVERE":
					level = Level.SEVERE;
					break;
				case "WARNING":
					level = Level.WARNING;
					break;
				case "INFO":
					level = Level.INFO;
					break;
				case "CONFIG":
					level = Level.CONFIG;
					break;
				case "FINE":
					level = Level.FINE;
					break;
				default:
					level = Level.ALL;
			}
		}

		return level;
	}

	/**
	 * @return the level as string
	 */
	public String getLevelString() {
		return levelString;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return new Date(timestamp);
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

	/**
	 * @return map of properties associated with this log message.
	 */
	public Map<String, String> getProperties() {
		return properties;
	}
}
