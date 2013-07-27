package cz.cuni.xrg.intlib.commons.app.execution;

import java.util.Date;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

/**
 * Represent a single message created during DPURecord execution.
 *
 * @author Petyr
 * @author Bogo
 *
 */
@Entity
@Table(name = "exec_record")
public class Record implements Serializable {

	/**
	 * Unique id.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
	private RecordType type;

	/**
	 * DPURecord which emitted the message.
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "dpu_id", nullable = true)
	private DPUInstanceRecord dpuInstance;

	/**
	 * Pipeline execution during which message was emitted.
	 */
	@OneToOne(optional = false)
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
	public Record() {
	}

	/**
	 * Constructor.
	 *
	 * @param time
	 * @param type
	 * @param dpuInstance
	 * @param shortMessage
	 * @param fullMessage
	 */
	public Record(Date time,
			RecordType type,
			DPUInstanceRecord dpuInstance,
			PipelineExecution execution,
			String shortMessage,
			String fullMessage) {
		this.time = time;
		this.type = type;
		this.dpuInstance = dpuInstance;
		this.execution = execution;
		this.shortMessage = shortMessage;
		this.fullMessage = fullMessage;
	}

	public Long getId() {
		return id;
	}

	public Date getTime() {
		return time;
	}

	public RecordType getType() {
		return type;
	}

	public DPUInstanceRecord getDpuInstance() {
		return dpuInstance;
	}

	public PipelineExecution getExecution() {
		return execution;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public String getFullMessage() {
		return fullMessage;
	}
	
	public Timestamp getTimestamp() {
		return new Timestamp(time.getTime());
	}
}
