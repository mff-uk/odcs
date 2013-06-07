package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 * Represent a scheduler plan. 
 * A single plan execute just one pipeline.
 * 
 * @author Petyr
 *
 */
//@Entity
//@Table(name = "scheduling")
public class Plan {

    /**
     * Unique ID for each plan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)	
	private Long id;
	
    /**
     * Plan's name.
     */
    @Column
    private String name;
    
    /**
     * Plan's description.
     */
    @Column
    private String description;
    
    /**
     * Pipeline to execute.
     */
//  @ManyToOne
//	@JoinColumn(name = "pipeline_id", unique = false, nullable = false)    
    private Pipeline pipeline;
	
	/**
	 * Plan type.
	 */
	@Enumerated(EnumType.ORDINAL)
	private PlanType type;
	
	/**
	 * Plan is active for just one execution.
	 */
	@Column
	private boolean justOnce;
	
	/**
	 * Determine when start pipeline execution.
	 * Used only if {@link #type} = RunInTime.
	 */
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "t_start", nullable = true)
	private Date start;	
	
    /**
     * Pipeline after which activate this plan.
	 * Used only if {@link #type} = RunAfterPipeline.
     */
	@ManyToOne
	@JoinColumn(name = "pipeline_id", nullable = true)    
    private Pipeline predPipeline;
	
	/**
	 * Empty ctor. Used by JPA. Do not use otherwise.
	 */
	public Plan() { }
}
