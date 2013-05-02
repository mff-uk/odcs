package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.Type;
import javax.persistence.*;

/**
 * Represent imported DPUExecution in database.
 *
 * @author Petyr
 * @author Bogo
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
	
    private String name;
    
	private String description;
    
	@Transient
	private Type type;
    
	@Column(name="jar_path")
	private String jarPath;

    /**
     * Allow empty constructor for JPA.
     */
    public DPU() {}

    public DPU(String name, Type type) {
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
        return HACK_basePath + jarPath;
    }
    public static String HACK_basePath = "file:///C:/MyGit/intlib/";
}
