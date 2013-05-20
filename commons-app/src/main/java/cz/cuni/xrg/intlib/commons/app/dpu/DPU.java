package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.DpuType;
import javax.persistence.*;


/**
 * Represent imported DPUExecution in database.
 *
 * @author Petyr
 * @author Bogo
 * @author Maria Kukhar
 *
 */
@Entity
@Table(name="dpu_model")
public class DPU {

    /**
     * Primary key of graph stored in db
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    /**
     * DPU name, provided by user.
     */
	@Column(name="name")
    private String name;
    
    /**
     * DPU description, provided by user.
     */
	@Column(name="description")
    private String description;
    
    /**
     * DPU type, determined by associated jar file.
     */
	@Enumerated(EnumType.ORDINAL)
    private DpuType type;
    
    /**
     * VIsibility.
     */
	@Enumerated(EnumType.ORDINAL)
    private VisibilityType visibility;
    
    /**
     * Path to the jar file. The path is relative to the 
     * AppConfiguration.dpuDirectory.
     * @see AppConfiguration
     */
	@Column(name="jar_path")
    private String jarPath;
	
	/**
	 * Default configuration for this DPU.
	 * When {@link DPUInstance} is created, its {@link InstanceConfiguration} is
	 * automatically created as an exact copy of {@link TemplateConfiguration}.
	 */
	@OneToOne(mappedBy = "dpu", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private TemplateConfiguration templateConfiguration;

    /**
     * Allow empty constructor for JPA.
     */
    public DPU() {}

    /**
     * Constructor with name and type of DPU.
     *
     * @param name
     * @param type
     */
    public DPU(String name, DpuType type) {
        //this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    public Long getId() {
        return id;
    }

    /**
     * Gets DPU type.
     *
     * @return DPU type
     */
    public DpuType getType() {
        return type;
    }

    public void setJarPath(String path) {
        jarPath = path;
    }

    public String getJarPath() {
        return jarPath;
    }

	public TemplateConfiguration getTemplateConfiguration() {
		return templateConfiguration;
	}

	public void setTemplateConfiguration(TemplateConfiguration templateConfiguration) {
		templateConfiguration.setDpu(this);
		this.templateConfiguration = templateConfiguration;
	}
	
}
