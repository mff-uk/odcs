package cz.cuni.xrg.intlib.backend.execution.pipeline.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.backend.execution.pipeline.PostExecutor;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;

/**
 * CleanUp data after execution.
 * 
 * @author Petyr
 * 
 */
@Component
class CleanUp implements PostExecutor {

	private static final Logger LOG = LoggerFactory.getLogger(CleanUp.class);
	
	@Autowired
	private AppConfig appConfig;
			
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
	
	@Override
	public boolean postAction(PipelineExecution execution,
			Map<Node, Context> contexts,
			DependencyGraph graph) {
		LOG.debug("CleanUp start .. ");
		// first release contexts
		for (Context item : contexts.values()) {
			if (execution.isDebugging()) {
				// just release leave
				item.release();
			} else {
				// delete data ..
				item.delete();
			}
		}
		
		if (!execution.isDebugging()) {			
			deleteDebugDate(execution);
		}
		
		// we delete the execution directory if it is empty
		File rootDirectory = new File(
				appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR),
				execution.getContext().getRootPath());
		if (rootDirectory.isDirectory()) {
			if (rootDirectory.list().length == 0) {
				// empty
				try {
					Files.delete(rootDirectory.toPath());
				} catch (IOException e) {
					LOG.warn("Failed to delete execution root directory", e);
				}
			}
		} else {
			LOG.warn("Execution directory is not directory.");
		}
		
		LOG.debug("CleanUp has been finished .. ");
		return true;
	}
	
	private void deleteDebugDate(PipelineExecution execution) {
		// delete working directory
		// the sub directories should be already deleted by DPU's
		deleteDirectory(execution.getContext().getWorkingPath());
	}
	
	/**
	 * Try to delete directory in execution directory. If error occur then is
	 * logged but otherwise ignored.
	 * 
	 * @param directory Relative path from execution directory.
	 */
	private void deleteDirectory(String directoryPath) {
		final String generalWorking = appConfig
				.getString(ConfigProperty.GENERAL_WORKINGDIR);
		File directory = new File(generalWorking, directoryPath);
		try {
			FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
			LOG.warn("Can't delete directory after execution", e);
		}
	}
	
}
