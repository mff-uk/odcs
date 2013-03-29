package pipeline;

import common.Resource;

/**
 *
 * @author Jiri Tomes
 */
public final class Pipeline implements Resource {

    private State state;
    private String name;
    private String description;
    /**
     * Unique ID idetificator pro each pipeline
     */
    private String ID;

    public Pipeline(String name, String description) {
        this.name = name;
        this.description = description;
        this.ID =createUniqueID();
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

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        state = newState;
    }

    public String getID() {
        return ID;
    }

    /*TODO - Method for implement*/
    public String createUniqueID() {
        return "PIPELINE_UNIQUE_ID";
    }
}
