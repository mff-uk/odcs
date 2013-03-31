package cz.cuni.intlib.frontend.data.pipeline;

public class PipelineConnection {

	public int Id;

	public DpuInstance From;

	public DpuInstance To;

	public PipelineConnection(Pipeline pipeline, DpuInstance from, DpuInstance to) {
		Id = pipeline.GetUniquePipelineConnectionId();
		From = from;
		To = to;
	}

	@Override
	public boolean equals(Object other) {
		if(other.getClass() != PipelineConnection.class) {
			return false;
		}
		PipelineConnection otherConnection = (PipelineConnection)other;
		if(this.Id == otherConnection.Id) {
			return true;
		} else if(this.From.Id == otherConnection.From.Id && this.To.Id == otherConnection.To.Id) {
			return true;
		}
		return false;
	}

}
