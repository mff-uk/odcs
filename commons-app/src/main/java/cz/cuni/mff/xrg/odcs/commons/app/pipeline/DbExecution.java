package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;

/**
 * Interface for access to {@link PipelineExecution}s. 
 * Spring does not support autowired on generic types
 *
 * @author Petyr
 */
public interface DbExecution extends DbAccess<PipelineExecution> {
	
	public PipelineExecution create(Pipeline pipeline);
	
}