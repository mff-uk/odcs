package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.dao.StringUtils;
import java.util.Objects;
import javax.persistence.*;

import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represent imported DPU in database.
 *
 * @author Petyr
 * @author Bogo
 * @author Maria Kukhar
 *
 */
@MappedSuperclass
public abstract class DPURecord {

	private static final Logger LOG = LoggerFactory.getLogger(DPURecord.class);
	
    /**
     * Primary key of graph stored in db
     */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dpu_record")
	@SequenceGenerator(name = "seq_dpu_record", allocationSize = 1)
    private Long id;
    
    /**
     * DPURecord name, provided by user.
     */
	@Column(name="name")
    private String name;
    
	/**
	 * If true then the value of {@link #description} has been 
	 * created by DPU's dialog.  
	 */
	@Column(name="use_dpu_description")
	private boolean useDPUDescription;
	
    /**
     * DPURecord description, can be provided by user or by the DPU's dialog.
     */
	@Column(name="description", nullable = true)
    private String description;
    		
	/**
	 * DPU's configuration in serialized version.
	 */
	@Column(name="configuration", nullable = true)
	private byte[] serializedConfiguration;
	
	/**
	 * If true configuration is in valid state.
	 */
	@Column(name="config_valid")
	private boolean configValid;
	
	/**
	 * DPU instance. Created in {{@link #loadInstance(ModuleFacade)}.
	 */
	@Transient
	protected Object instance;
	
    /**
     * Allow empty constructor for JPA.
     */
    public DPURecord() { }

    /**
     * Constructor with name and type of DPURecord.
     *
     * @param name
     */
    public DPURecord(String name) {
        this.name = name;
        this.useDPUDescription = false;
    }

    /**
     * Create new DPURecord by copying the values from existing DPURecord.
     * @param dpuRecord
     */
    public DPURecord(DPURecord dpuRecord) {
    	this.name = dpuRecord.name;
    	this.useDPUDescription = dpuRecord.useDPUDescription;
    	this.description = dpuRecord.description;
    	if (dpuRecord.serializedConfiguration == null) {
    		this.serializedConfiguration = null;
    	} else {
			// deep copy
			this.serializedConfiguration = dpuRecord.serializedConfiguration.clone();
    	}
    	this.configValid = dpuRecord.configValid;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = StringUtils.secureLenght(newName, LenghtLimits.DPU_NAME);
    }

    public boolean useDPUDescription() {
    	return useDPUDescription;
    }
    
    public void setUseDPUDescription(boolean useDPUDescription) {
    	this.useDPUDescription = useDPUDescription;
    }
    
    public String getDescription() {
		return StringUtils.nullToEmpty(description);
    }

    public void setDescription(String newDescription)  {
		this.description = StringUtils.emptyToNull(newDescription);
    }

    public Long getId() {
        return id;
    }

	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Return DPU's type.
	 * @return
	 */
    public abstract DPUType getType();
    
    /**
     * Load appropriate DPU instance info {@link #instance}. The instance
     * is then accessible through the {@link #getInstance()} method.
     * @param moduleFacade
     * @throws ModuleException
     */
    public abstract void loadInstance(ModuleFacade moduleFacade) throws ModuleException;
    
    /**
     * Return full path from the DPU's jar file from DPU's directory.
     * @return
     */
    public abstract String getJarPath();

    /**
     * Get stored instance if loaded. To load instance use {@link #loadInstance}.
     * @return Stored instance.
     */
    public Object getInstance() {
    	return instance;
    }
    
	/**
	 * Return raw configuration representation.
	 * @return
	 */
	public byte[] getRawConf() {
		LOG.debug("getRawConf for: {} on: {}", this.name, this.serializedConfiguration);
		return serializedConfiguration;
	}

	/**
	 * Set raw configuration representation. Use with caution!
	 * @param conf
	 */
	public void setRawConf(byte[] conf) {
		serializedConfiguration = conf;
		LOG.debug("setRawConf for: {} on: {}", this.name, this.serializedConfiguration);
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
			hash = 83 * hash + Objects.hashCode(getJarPath());
			hash = 83 * hash + Objects.hashCode(this.serializedConfiguration);
		} else {
			hash = 83 * hash + Objects.hashCode(this.id);
		}
		return hash;
	}
	
	/**
	 * Compares DPURecord to other object. Two DPURecord instances are equal if they have
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
		final DPURecord other = (DPURecord) obj;
		
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
		if (this.getType() != other.getType()) {
			return false;
		}
		if (!Objects.equals(getJarPath(), other.getJarPath())) {
			return false;
		}
		if (!Objects.equals(this.serializedConfiguration, other.serializedConfiguration)) {
			return false;
		}
		return true;
	}
	
    @Override
    public String toString() {
        return name;
    }
}
