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
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.rdf.RepositoryManager;
import eu.unifiedviews.commons.rdf.repository.RDFException;

/**
 * CleanUp data after execution.
 * 
 * @author Petyr
 */
@Component
class CleanUp implements PostExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(CleanUp.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ContextFacade contextFacade;

    @Autowired
    private RDFDataUnitFactory rdfDataUnitFactory;

    @Autowired
    private RepositoryManager repositoryManager;

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
                // close the data unit
                // the data has already been saved 
                // in DPU post executor after the DPU's execution
                contextFacade.close(item);
            } else {
                // delete data ..
                // but preserve context info as it can be used to examine the 
                // execution
                contextFacade.delete(item, true);
            }
        }
        if (execution.isDebugging()) {
            rdfDataUnitFactory.release(execution.getContext().generatePipelineId());
            
            try {
                repositoryManager.release(execution.getContext().generatePipelineId());
            } catch (RDFException ex) {
                LOG.error("Can't release repository.", ex);
            }
        } else {
            rdfDataUnitFactory.clean(execution.getContext().generatePipelineId());

            try {
                repositoryManager.delete(execution.getContext().generatePipelineId());
            } catch (RDFException ex) {
                LOG.error("Can't delete repository.", ex);
            }
        }     
        // prepare execution root
        File rootDir = new File(
                appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));

        // get access to the infromation in execution context
        ExecutionInfo info = new ExecutionInfo(execution.getContext());

        if (!execution.isDebugging()) {
            // delete working directory
            // the sub directories should be already deleted by DPU's
            delete(rootDir, info.getWorkingPath());
        }

        // delete result, storage if empty

        deleteIfEmpty(rootDir, info.getResultPath());
        deleteIfEmpty(rootDir, info.getStoragePath());
        // we delete the execution directory if it is empty
        deleteIfEmpty(rootDir, info.getRootPath());

        LOG.debug("CleanUp has been finished .. ");
        return true;
    }

    /**
     * Try to delete directory in execution directory. If error occur then is
     * logged but otherwise ignored.
     * 
     * @param executionRoot
     *            Path to the execution root.
     * @param relativePath
     *            Relative sub-path from absolute path.
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
     * @param executionRoot
     *            Path to the execution root.
     * @param relativePath
     *            Relative sub-path from absolute path.
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
