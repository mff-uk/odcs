package cz.cuni.mff.xrg.odcs.backend.execution.pipeline.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.context.ContextFacade;
import cz.cuni.mff.xrg.odcs.backend.execution.pipeline.PostExecutor;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

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

	@Autowired
	private ContextFacade contextFacade;
	
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
				// close and save data
				contextFacade.close(item);
			} else {
				// delete data ..
				contextFacade.delete(item);
			}
		}

		// prepare execution root
		File rootDir = new File(
				appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
		
		
		if (!execution.isDebugging()) {
			deleteDebugDate(rootDir, execution);
		}

		// delete result, storage if empty
		deleteIfEmpty(rootDir, execution.getContext().getResultPath());
		deleteIfEmpty(rootDir, execution.getContext().getStoragePath());
		// we delete the execution directory if it is empty
		deleteIfEmpty(rootDir, execution.getContext().getRootPath());
		
		LOG.debug("CleanUp has been finished .. ");
		return true;
	}

	private void deleteDebugDate(File executionRoot, PipelineExecution execution) {
		// delete working directory
		// the sub directories should be already deleted by DPU's
		delete(executionRoot, execution.getContext().getWorkingPath());
	}

	/**
	 * Try to delete directory in execution directory. If error occur then is
	 * logged but otherwise ignored.
	 * 
	 * @param executionRoot Path to the execution root.
	 * @param relativePath Relative sub-path from absolute path.
	 */
	private void delete(File executionRoot, String relativePath) {
		File toDelete = new File(executionRoot, relativePath);
		
		LOG.debug("Deleting: {}", toDelete.toString());
		
		try {
			FileUtils.deleteDirectory(toDelete);
		} catch (IOException e) {
			LOG.warn("Can't delete directory after execution", e);
		}
	}

	/**
	 * Delete directory if it's empty.
	 * 
	 * @param executionRoot Path to the execution root.
	 * @param relativePath Relative sub-path from absolute path.
	 */
	private void deleteIfEmpty(File executionRoot, String relativePath) {
		File toDelete = new File(executionRoot, relativePath);
		if (!toDelete.exists()) {
			// file does not exist
			return;
		}

		LOG.debug("Deleting: {}", toDelete.toString());
		
		if (!toDelete.isDirectory()) {
			LOG.warn("Directory to delete is file: {}", toDelete.toString());
			return;
		}

		// check if empty
		if (toDelete.list().length == 0) {
			// empty
			try {
				FileUtils.deleteDirectory(toDelete);
			} catch (IOException e) {
				LOG.warn("Can't delete directory after execution", e);
			}
		}

	}

}
