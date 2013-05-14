package cz.cuni.xrg.intlib.commons.app.execution;

import java.util.Date;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

import javax.persistence.*;

/**
 * Represent a single message created during DPU execution. 
 * 
 * @author Petyr
 * @author Bogo
 *
 */
@Entity
@Table(name = "dpu_record")
public class Record {

	/**
	 * Unique id.
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	/**
	 * Time of creation.
	 */
	@Temporal(javax.persistence.TemporalType.DATE)
	@Column(name = "r_time")
	private Date time;
	
	/**
	 * Type of record.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "r_type")
	private RecordType type;
	
	/**
	 * DPU which emmitted the message.
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "dpu_id", nullable = false)
	// TODO: Enable null values? For messages outside DPU ?
	private DPUInstance source;
	
	
	/**
	 * PipelineExecution.
	 */
	@Transient
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
	public Record() {}
	
	/**
	 * Constructor.
	 * @param time
	 * @param type
	 * @param source
	 * @param shortMessage
	 * @param fullMessage 
	 */
	public Record(Date time,
					RecordType type,
					DPUInstance source,
					PipelineExecution execution,
					String shortMessage,
					String fullMessage ) {
		this.time = time;
		this.type = type;
		this.source = source;
		this.execution = execution;
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

	public DPUInstance getSource() {
		return source;
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
}
