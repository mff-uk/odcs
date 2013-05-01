package cz.cuni.xrg.intlib.commons.event;

import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import java.util.List;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * Represents a fixed workflow composed of one or several {@link Extract}s,
 * {@link Transform}s and {@link Load}s executed in a fixed order.<br/>
 * Implementations of this class are supposed to execute the components in the following order:
 * <ol>
 * <li>Execute all {@link Extract}s in the order of the {@link List}</li>
 * <li>Execute all {@link Transform}s in the order of the {@link List}</li>
 * <li>Execute all {@link Load}s in the order of the {@link List}</li>
 * </ol>
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface ETLPipeline extends Runnable {
    
    /**
     * Get acyclic graph of DPUs, which represents data flow of pipeline.
     * @return
     */
    public PipelineGraph getGraph();
    
    /**
     * Set acyclic graph of DPUs, which represents data flow of pipeline.
     * @param graph
     */
    public void setGraph(PipelineGraph graph);
    
    /**
     * Runs the pipeline.
     */
    public void run();

}
