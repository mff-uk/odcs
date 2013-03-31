package cz.cuni.xrg.intlib.frontend.data.pipeline;

public class DpuInstance {
	
	public int Id;
	
	public Dpu Dpu;
	
	public String Name;
	
	public String Description;
	
	private int x;
	
	private int y;
	
	public DpuInstance(Pipeline pipeline, Dpu dpu) {
		Id = pipeline.GetUniqueDpuInstanceId();
		Dpu = dpu;
		Name = dpu.name;
		Description = dpu.description;
	}

	public int getX() {
		return x;
	}
	
	public void setX(int value) {
		x = value;
	}

	public int getY() {
		return y;
	}

	public void setY(int value) {
		y = value;
	}

}
