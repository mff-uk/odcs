package cz.cuni.intlib.frontend.data.pipeline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pipeline {
	
	public int Id;
	
	public String Name;
	
	public String Description;
	
	private Set<DpuInstance> dpus;
	
	private Set<PipelineConnection> connections;
	
	private int dpuCounter = 0;
	
	private int connectionCounter = 0;
	
	private int CONNECTION_SEED = 100000;
	
	private int width;
	
	private int height;
	
	public Pipeline() {
		dpus = new HashSet<DpuInstance>();
		connections = new HashSet<PipelineConnection>();
	}

	public int GetUniqueDpuInstanceId() {
		return ++dpuCounter;
	}

	public int GetUniquePipelineConnectionId() {
		return ++connectionCounter + CONNECTION_SEED;
	}
	
	public int AddDpu(Dpu dpu) {
		DpuInstance dpuInstance = new DpuInstance(this, dpu);
		dpus.add(dpuInstance);
		return dpuInstance.Id;
	} 
	
	public boolean RemoveDpu(int dpuId) {
		DpuInstance dpu = getDpuInstanceById(dpuId);
		if(dpu != null) {
			return dpus.remove(dpu);
		}
		return false;
	}
	
	public int AddPipelineConnection(int fromId, int toId) {
		DpuInstance dpuFrom = getDpuInstanceById(fromId);
		DpuInstance dpuTo = getDpuInstanceById(toId);
		
		//TODO: Check if same connection doesn't exist already!
		//If it does - add to Set fails and returns false - TODO: 2.Find Id of equal existing connection
		
		PipelineConnection pc = new PipelineConnection(this, dpuFrom, dpuTo);
		boolean newElement = connections.add(pc);
		if(!newElement) {
			return 0;
		}
		return pc.Id;
	}
	
	public boolean RemovePipelineConnection(int pcId) {
		PipelineConnection pc = getPipelineConnectionById(pcId);
		if(pc != null) {
			return connections.remove(pc);
		}
		return false;
	}

	private PipelineConnection getPipelineConnectionById(int id) {
		for(PipelineConnection el : connections) {
			if(el.Id == id) {
				return el;
			}
		}
		return null;
	}

	private DpuInstance getDpuInstanceById(int id) {
		for(DpuInstance el : dpus) {
			if(el.Id == id) {
				return el;
			}
		}
		return null;
	}

	public Set<DpuInstance> getDpus() {
		return dpus;
	}

	public Set<PipelineConnection> getConnections() {
		return connections;
	}

	public void setHeight(int value) {
		height = value;
	}

	public void setWidth(int value) {
		width = value;
	}

}
