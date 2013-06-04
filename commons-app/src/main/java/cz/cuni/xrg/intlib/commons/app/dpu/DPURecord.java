package cz.cuni.xrg.intlib.commons.app.dpu;

import java.util.Objects;
import javax.persistence.*;

import org.hibernate.engine.loading.internal.LoadingCollectionEntry;

import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.configuration.Config;


/**
 * Represent imported DPU in database.
 *
 * @author Petyr
 * @author Bogo
 * @author Maria Kukhar
 *
 */
@Entity
public class DPURecord {

    /**
     * Primary key of graph stored in db
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    /**
     * DPURecord name, provided by user.
     */
	@Column(name="name")
    private String name;
    
    /**
     * DPURecord description, provided by user.
     */
	@Column(name="description")
    private String description;
    
    /**
     * DPURecord type, determined by associated jar file.
     */
	@Enumerated(EnumType.ORDINAL)
    private DPUType type;
        
    /**
     * Path to the jar file. The path is relative to the 
     * AppConfig.dpuDirectory.
     * @see AppConfig
     */
	@Column(name="jar_path")
    private String jarPath;
	
	/**
	 * Instance of DPU.
	 */
	@Transient
	private Configurable<Config> instance;
	
    /**
     * Allow empty constructor for JPA.
     */
    public DPURecord() {}

    /**
     * Constructor with name and type of DPURecord.
     *
     * @param name
     * @param type
     */
    public DPURecord(String name, DPUType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Load instance from associated jar file.
     * @throws ModuleException
     */
    public void loadInstance() throws ModuleException {
    	// TODO Petyr, load DPUInstance using OSGi here .. 
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

    public Long getId() {
        return id;
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
	 * Generates hash code from primary key if it is available, otherwise
	 * from the rest of the attributes.
	 * 
	 * @return 
	 */
    @Deprecated
	@Override
	public int hashCode() {
		int hash = 7;
		if (this.id == null) {
			hash = 83 * hash + Objects.hashCode(this.name);
			hash = 83 * hash + Objects.hashCode(this.description);
			hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
			hash = 83 * hash + Objects.hashCode(this.jarPath);
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
    @Deprecated
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
		return true;
	}
	
    @Override
    public String toString() {
        return name;
    }
}
