package cz.cuni.xrg.intlib.frontend.data.pipeline;

public class Dpu {
	
	public int id;
	public String name;
	public String description = "";
	
	public Dpu(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

}
