package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.VisibilityType;

/**
 * Represent imported DPUExecution in database.
 *
 * @author Petyr
 * @author Bogo
 * @author Maria Kukhar
 *
 */
public class DPU {

	/**
	 * DPU id. 
	 */
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
    private Type type;
    
    /**
     * VIsibility.
     */
    private VisibilityType visibility;
    
    /**
     * Path to the jar file. The path is relative to the 
     * AppConfiguration.dpuDirectory.
     * @see AppConfiguration
     */
    private String jarPath;

    /**
     * Allow empty constructor.
     */
    public DPU() {
        this.description = "";
    }

    /**
     * Constructor with name and type of DPU.
     *
     * @param name
     * @param type
     */
    public DPU(String name, Type type) {
        //this.id = id;
        this.name = name;
        this.type = type;
        this.description = "";
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

    public Type getType() {
        return type;
    }

    public void setJarPath(String path) {
        jarPath = path;
    }

    public String getJarPath() {
        return jarPath;
    }
}
