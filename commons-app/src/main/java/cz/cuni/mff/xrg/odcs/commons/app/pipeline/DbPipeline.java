package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import java.util.List;

/**
 * Interface for access to {@link Pipeline}s.
 * 
 * @author Petyr
 * @author Jan Vojt
 */
public interface DbPipeline extends DbAccess<Pipeline> {
	
	/**
	 * @return all pipelines in DB
	 * @deprecated performance intensive for many pipelines
	 */
	@Deprecated
	public List<Pipeline> getAll();
	
	/**
	 * Fetches all pipelines using given DPU template.
	 * 
	 * @param dpu
	 * @return list of pipelines
	 */
	public List<Pipeline> getPipelinesUsingDPU(DPUTemplateRecord dpu);
	
	/**
	 * @param name pipeline name
	 * @return pipeline with given name or null
	 */
	public Pipeline getPipelineByName(String name);

}