package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.DpuType;
import javax.persistence.*;
import cz.cuni.xrg.intlib.commons.app.dpu.VisibilityType;

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
    private int id;
    
    /**
     * DPU name, provided by user.
     */
    private String name;
    
    /**
     * DPU description, provided by user.
     */
    private String description;
    
    /**
     * DPU type, determined by associated jar file.
     */
	@Enumerated(EnumType.STRING)
    private DpuType type;
    
    /**
     * VIsibility.
     */
	@Transient
    private VisibilityType visibility;
    
    /**
     * Path to the jar file. The path is relative to the 
     * AppConfiguration.dpuDirectory.
     * @see AppConfiguration
     */
	@Column(name="jar_path")
    private String jarPath;

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

    public int getId() {
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
}
