package cz.cuni.xrg.intlib.commons.app.data.pipeline;

import javax.persistence.*;
import cz.cuni.xrg.intlib.commons.app.data.Resource;

/**
 *
 * @author Jiri Tomes
 */
@Entity
@Table(name="pipeline_model")
public class Pipeline implements Resource {

    //private State state;

    private String name;

    private String description;

    @Transient
    private Graph graph;

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

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public String getID() {
        return id;
    }

    /*TODO - Method for implement*/
    public String createUniqueID() {
        return "PIPELINE_UNIQUE_ID";
    }
}
