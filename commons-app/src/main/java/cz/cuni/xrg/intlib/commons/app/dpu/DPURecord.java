package cz.cuni.xrg.intlib.commons.app.dpu;

import java.io.FileNotFoundException;
import java.util.Objects;
import javax.persistence.*;

import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;

/**
 * Represent imported DPU in database.
 *
 * @author Petyr
 * @author Bogo
 * @author Maria Kukhar
 *
 */
@MappedSuperclass
public class DPURecord {

    /**
     * Primary key of graph stored in db
     */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dpu_record")
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
	@Column(name="description")
    private String description;
    	
    /**
     * DPURecord type, determined by associated jar file.
	 * TODO move to {@link DPUTemplateRecord}?
     */
	@Enumerated(EnumType.ORDINAL)
    private DPUType type;
        
    /**
     * Path to the jar file. The path is relative to the 
     * AppConfig.dpuDirectory.
	 * TODO move to {@link DPUTemplateRecord}?
	 * 
     * @see AppConfig
     */
	@Column(name="jar_path")
    private String jarPath;	
	
	/**
	 * DPU's configuration in serialized version.
	 */
	@Column(name="configuration", nullable = true)
	private byte[] serializedConfiguration;
		
	/**
	 * DPU instance. Created in {{@link #loadInstance(ModuleFacade)}.
	 */
	@Transient
	private Object instance;
	
    /**
     * Allow empty constructor for JPA.
     */
    public DPURecord() { }

    /**
     * Constructor with name and type of DPURecord.
     *
     * @param name
     * @param type
     */
    public DPURecord(String name, DPUType type) {
        this.name = name;
        this.type = type;
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
    	this.type = dpuRecord.type;
    	this.jarPath = dpuRecord.jarPath;
    	if (dpuRecord.serializedConfiguration == null) {
    		this.serializedConfiguration = null;
    	} else {
    		// deep copy
    		this.serializedConfiguration = 
    				dpuRecord.serializedConfiguration.clone();
    	}
    }
    
    /**
     * Load DPU's instance from associated jar file.
     * @param moduleFacade ModuleFacade used to load DPU.
     * @throws ModuleException
     * @throws FileNotFoundException 
     */
    public void loadInstance(ModuleFacade moduleFacade) throws ModuleException, FileNotFoundException {
    	instance = moduleFacade.getObject(jarPath);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean useDPUDescription() {
    	return useDPUDescription;
    }
    
    public void setUseDPUDescription(boolean useDPUDescription) {
    	this.useDPUDescription = useDPUDescription;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

	public void setId(Long id) {
		this.id = id;
	}
	
    public DPUType getType() {
        return type;
    }

    public void setJarPath(String path) {
        jarPath = path;
    }

    public String getJarPath() {
        return jarPath;
    }

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
		return serializedConfiguration;
	}

	/**
	 * Set raw configuration representation. Use with caution!
	 * @param conf
	 */
	public void setRawConf(byte[] conf) {
		serializedConfiguration = conf;
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
			hash = 83 * hash + Objects.hashCode(this.jarPath);
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
		if (this.type != other.type) {
			return false;
		}
		if (!Objects.equals(this.jarPath, other.jarPath)) {
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
