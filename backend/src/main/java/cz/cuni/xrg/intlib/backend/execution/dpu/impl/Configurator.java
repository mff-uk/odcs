package cz.cuni.xrg.intlib.backend.execution.dpu.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.backend.execution.dpu.PreExecutor;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 * Load configuration into DPU.
 * 
 * If the DPU does not implements {@link Configurable} interface 
 * immediately return true. 
 * 
 * @author Petyr
 * 
 */
@Component
class Configurator implements PreExecutor {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public boolean preAction(DPUInstanceRecord dpu,
			Object dpuInstance,
			PipelineExecution execution,
			Context context) {
		if (dpuInstance instanceof Configurable<?>) {
			// can be configured
		} else {
			// do not configure
			return true;
		}
		@SuppressWarnings("unchecked")
		Configurable<DPUConfigObject> configurable = (Configurable<DPUConfigObject>) dpuInstance;
		try {
			configurable.configure(dpu.getRawConf());
		} catch (ConfigException e) {
			eventPublisher.publishEvent(
					DPUEvent.createPreExecutorFailed(context, this, "Failed to configure DPU."));
			// stop the execution
			return false;
		}
		// continue execution
		return true;
	}

}
