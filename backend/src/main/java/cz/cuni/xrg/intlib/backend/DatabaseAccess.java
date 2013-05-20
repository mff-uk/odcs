package cz.cuni.xrg.intlib.backend;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class provide access to database through provided facades.
 * @author Petyr
 *
 */
public class DatabaseAccess {

	/**
	 * Facade for working with DPUs.
	 */
	@Autowired
	private DPUFacade dpu;
	
	/**
	 * Facade for working with pipelines.
	 */
	@Autowired
	private PipelineFacade pipeline;

	public DPUFacade getDpu() {
		return dpu;
	}

	public PipelineFacade getPipeline() {
		return pipeline;
	}
}
