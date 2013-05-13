package cz.cuni.xrg.intlib.backend;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;

/**
 * Class provide access to database through provided facades.
 * @author Petyr
 *
 */
public class DatabaseAccess {

	/**
	 * Facade for working with DPUs.
	 */
	private DPUFacade dpu;
	
	/**
	 * Facade for working with pipelines.
	 */
	private PipelineFacade pipeline;
		
	public DatabaseAccess() {
		this.dpu = new DPUFacade();
		this.pipeline = new PipelineFacade();
	}

	public DPUFacade getDpu() {
		return dpu;
	}

	public PipelineFacade getPipeline() {
		return pipeline;
	}
}
