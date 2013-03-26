package pipeline;

/**
 *
 * @author Jiri Tomes
 */
public class Pipeline {

    private State state;
    private String name;
    private String description;

    public Pipeline(String name, String description) {
        this.name = name;
        this.description = description;
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
}
