package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.StringUtils;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Represents log message loaded from database. 
 * 
 * @author Petyr
 */
@Entity
@Table(name = "logging")
public class Log implements Serializable, DataObject {
	
	/**
	 * Log property name for logging messages produced by
	 * {@link PipelineExecution}.
	 */
	public static final String MDC_EXECUTION_KEY_NAME = "execution";

	/**
	 * Log property name for logging messages produced
	 * by {@link DPUInstanceRecord}. Such logs usually contain
	 * a {@link #MDC_EXECUTION_KEY_NAME} as well.
	 */
	public static final String MDC_DPU_INSTANCE_KEY_NAME = "dpuInstance";	
	
	/**
	 * Primary key of message stored in database.
	 */
	@Id
	@Column(name = "id")
	private Long id;	
	
	/**
	 * Level as string, so it can be persisted in DB.
	 */
	@Column(name = "logLevel")
	private Integer logLevel;
	
	/**
	 * Timestamp of log message.
	 */
	@Column(name = "timestmp")
	private Long timestamp;
	
	/**
	 * Source class of log message.
	 */
	@Column(name = "logger")
	private String source;
	
	/**
	 * Text of formatted log massage.
	 */
	@Column(name = "message")
	private String message;
	
	/**
	 * Id of given DPU.
	 */
	@JoinColumn(name = "dpu")
	private Long dpu;
	
	/**
	 * Id of execution.
	 */
	@JoinColumn(name = "execution")
	private Long execution;

	/**
	 * Mapping to stack trace.
	 */
	@Column(name = "stack_trace")
	private String stackTrace;
	
	public Log() { }
	
	/**
	 * @return the id
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @return the logLevel
	 */
	public Integer getLogLevel() {
		return logLevel;
	}

	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
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
		return StringUtils.nullToEmpty(message);
	}

	/**
	 * @return the dpuInstanceId
	 */
	public Long getDpu() {
		return dpu;
	}

	/**
	 * @return the executionId
	 */
	public Long getExecution() {
		return execution;
	}
	
	/**
	 * Stack trace for given log if exist.
	 * @return 
	 */
	public String getStackTrace() {
		return StringUtils.nullToEmpty(stackTrace);
	}
	
}
