package cz.cuni.xrg.intlib.backend.context.impl;

import java.io.IOException;
import java.util.Date;

import org.springframework.context.ApplicationEventPublisher;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Context factory class.
 * 
 * @author Petyr
 * 
 */
public class ContextFactory {

	/**
	 * Can't be instantiated.
	 */
	private ContextFactory() { }

	/**
	 * Create new Context for given DPU type.
	 * 
	 * @param execution Related execution.
	 * @param dpuInstance Respective DPU.
	 * @param eventPublisher Application event publisher.
	 * @param context ExecutionContextInfo for given execution.
	 * @param type Context type ie. {@link ExtendedExtractContext},
	 *            {@link ExtendedLoadContextImpl} or
	 *            {@link ExtendedTransformContext}
	 * @param appConfig Application's configuration.
	 * @param lastSuccExec Time of last successful execution, can be null.
	 * @return Context for DPU.
	 * @throws IOException
	 * @throws ContextException For unknown context type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T create(PipelineExecution execution,
			DPUInstanceRecord dpuInstance,
			ApplicationEventPublisher eventPublisher,
			ExecutionContextInfo context,
			Class<T> type,
			AppConfig appConfig,
			Date lastSuccExec) throws IOException, ContextException {
		// ...
		if (type == ExtendedExtractContext.class) {
			return (T) new ExtendedExtractContextImpl(execution, dpuInstance,
					eventPublisher, context, appConfig, lastSuccExec);
		} else if (type == ExtendedLoadContext.class) {
			return (T) new ExtendedLoadContextImpl(execution, dpuInstance,
					eventPublisher, context, appConfig, lastSuccExec);
		} else if (type == ExtendedTransformContext.class) {
			return (T) new ExtendedTransformContextImpl(execution, dpuInstance,
					eventPublisher, context, appConfig, lastSuccExec);
		} else {
			// unknown type
			throw new ContextException("Unknown context type.");
		}
	}

}
