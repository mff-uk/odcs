package cz.cuni.xrg.intlib.commons.app.dpu.execution;

import java.util.Date;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;

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
public class DPURecord {

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
	private DPURecordType type;
	
	/**
	 * DPU which emmitted the message.
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "dpu_id", nullable = false)
	private DPUInstance source;
	
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
	public DPURecord() {}
	
	/**
	 * Constructor.
	 * @param time
	 * @param type
	 * @param source
	 * @param shortMessage
	 * @param fullMessage 
	 */
	public DPURecord(Date time,
					DPURecordType type,
					DPUInstance source,
					String shortMessage,
					String fullMessage ) {
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

	public DPURecordType getType() {
		return type;
	}

	public DPUInstance getSource() {
		return source;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public String getFullMessage() {
		return fullMessage;
	}	
}
