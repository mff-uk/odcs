package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.DpuType;
import java.util.Objects;
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

	/**
	 * Generates hash code from primary key if it is available, otherwise
	 * from the rest of the attributes.
	 * 
	 * @return 
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		if (this.id == null) {
			hash = 83 * hash + Objects.hashCode(this.name);
			hash = 83 * hash + Objects.hashCode(this.description);
			hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
			hash = 83 * hash + (this.visibility != null ? this.visibility.hashCode() : 0);
			hash = 83 * hash + Objects.hashCode(this.jarPath);
			hash = 83 * hash + Objects.hashCode(this.templateConfiguration);
		} else {
			hash = 83 * hash + Objects.hashCode(this.id);
		}
		return hash;
	}
	
	/**
	 * Compares DPU to other object. Two DPU instances are equal if they have
	 * the same non-null primary key, or if both their primary keys are
	 * <code>null</code> and their attributes are equal. Note that
	 * {@link TemplateConfiguration} is also a part ofDPUs identity, because we
	 * may want to have same DPUs that only differ in configuration (although we
	 * should ideally change DPUs name).
	 *
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DPU other = (DPU) obj;
		
		// try primary key comparison
		if (this.id != null && other.id != null) {
			// both have primary keys
			return Objects.equals(this.id, other.id);
		}
		if (this.id == null ^ other.id == null) {
			// only one has primary key
			return false;
		}
		
		// compare attributes
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.description, other.description)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		if (this.visibility != other.visibility) {
			return false;
		}
		if (!Objects.equals(this.jarPath, other.jarPath)) {
			return false;
		}
		if (!Objects.equals(this.templateConfiguration,
				other.templateConfiguration)) {
			return false;
		}
		return true;
	}
	
}
