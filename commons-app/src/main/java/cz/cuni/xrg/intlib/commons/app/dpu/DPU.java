package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.Type;

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
    private Type type;

	/**
	 * Allow empty constructor.
	 */
	public DPU() {

	}

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

}
