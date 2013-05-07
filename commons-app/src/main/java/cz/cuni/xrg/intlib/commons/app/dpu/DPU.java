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

    private int id;
    private String name;
    private String description;
    private Type type;
    private VisibilityType visibility;
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

    /**
     * Gets DPU type.
     *
     * @return DPU type
     */
    public Type getType() {
        return type;
    }

    public void setJarPath(String path) {
        jarPath = path;
    }

    public String getJarPath() {
        return HACK_basePath + jarPath;
    }
    public static String HACK_basePath = "file:///C:/MyGit/intlib/module/";
}
