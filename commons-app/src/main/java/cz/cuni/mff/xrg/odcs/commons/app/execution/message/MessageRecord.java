package cz.cuni.mff.xrg.odcs.commons.app.execution.message;

import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import java.util.Date;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

import java.sql.Timestamp;

import javax.persistence.*;
import org.apache.commons.lang3.StringUtils;

/**
 * Represent a single message created during DPURecord execution.
 *
 * @author Petyr
 * @author Bogo
 *
 */
@Entity
@Table(name = "exec_record")
public class MessageRecord implements DataObject {

	/**
	 * Unique id.
	 */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_exec_record")
	@SequenceGenerator(name = "seq_exec_record", allocationSize = 1)
	private Long id;

	/**
	 * Time of creation.
	 */
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "r_time")
	private Date time;

	/**
	 * Type of record.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "r_type")
	private MessageRecordType type;

	/**
	 * DPURecord which emitted the message.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "dpu_id", nullable = true)
	private DPUInstanceRecord dpuInstance;

	/**
	 * Pipeline execution during which message was emitted.
	 */
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "execution_id")
	private PipelineExecution execution;

	/**
	 * Short message, should be under 50 characters.
	 */
	@Column(name = "short_message")
	private String shortMessage;

	/**
	 * Full message text.
	 */
	@Column(name = "full_message")
	private String fullMessage;

	/**
	 * No-arg constructor for JPA. Do not use!
	 */
	public MessageRecord() {
	}

	/**
	 * Constructor.
	 *
	 * @param time
	 * @param type
	 * @param dpuInstance
	 * @param execution
	 * @param shortMessage
	 * @param fullMessage
	 */
	public MessageRecord(Date time,
			MessageRecordType type,
			DPUInstanceRecord dpuInstance,
			PipelineExecution execution,
			String shortMessage,
			String fullMessage) {
		this.time = time;
		this.type = type;
		this.dpuInstance = dpuInstance;
		this.execution = execution;
		this.shortMessage = StringUtils.abbreviate(shortMessage, LenghtLimits.SHORT_MESSAGE.limit());
		this.fullMessage = fullMessage;
	}

	@Override
	public Long getId() {
		return id;
	}

	public Date getTime() {
		return time;
	}

	public MessageRecordType getType() {
		return type;
	}

	public DPUInstanceRecord getDpuInstance() {
		return dpuInstance;
	}

	public PipelineExecution getExecution() {
		return execution;
	}

	public String getShortMessage() {
		return StringUtils.defaultString(shortMessage);
	}

	public String getFullMessage() {
		return StringUtils.defaultString(fullMessage);
	}
	
	public Timestamp getTimestamp() {
		return new Timestamp(time.getTime());
	}
}
