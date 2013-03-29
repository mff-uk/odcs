package cz.cuni.intlib.commons.app.data.pipeline;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Jiri Tomes
 */
@Entity
@Table(name="pipeline_model")
public final class Pipeline implements cz.cuni.intlib.commons.app.data.Resource {

    //private State state;
    
    private String name;
    
    private String description;
    
    /**
     * Unique ID idetificator pro each pipeline
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    private String id;

    /**
     * Default ctor for javax.persistante.
     */
    public Pipeline() {
    	
    }
    
    public Pipeline(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = createUniqueID();
    }

    
    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

 /*   
    public State getState() {
        return state;
    }

    public void setState(State newState) {
        state = newState;
    }
*/
    
    public String getID() {
        return id;
    }

    /*TODO - Method for implement*/
    public String createUniqueID() {
        return "PIPELINE_UNIQUE_ID";
    }
}
