package cz.cuni.xrg.intlib.commons.app.dpu;

/**
 * Represent imported DPUExecution in database.
 * @author Petyr
 * @author Bogo
 *
 */
public class DPU {

    private int id;
	private String name;
	private String description = "";

	public DPU(int id, String name) {
		this.id = id;
		this.name = name;
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
}
