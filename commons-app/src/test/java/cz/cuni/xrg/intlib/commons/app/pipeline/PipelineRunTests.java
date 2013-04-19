package cz.cuni.xrg.intlib.commons.app.pipeline;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DpuFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;

public class PipelineRunTests {
	
	/**
	 * Facade for loading modules given as jar files
	 */
	private ModuleFacade moduleFacade = new ModuleFacade();
	
	/**
	 * Setup OSGi
	 * TODO What are exported packages in {@link ModuleFacade#start(String)}??
	 */
//	@Test
	public void testTrivialRun() {
		
		// setup pipeline
		Pipeline pipe = createEmptyPipeline();
		setupTrivialPipelineGraph(pipe.getGraph());
		
		// create run model and run it
		PipelineExecution run = new PipelineExecution(pipe);
		run.setModuleFacade(moduleFacade);
		run.run();
	}
	
	/**
	 * Create minimal pipeline graph
	 * Scenario: E -> L
	 * @param graph
	 */
	private void setupTrivialPipelineGraph(PipelineGraph graph) {
		
		DPU extractor = new DPU("RDF Extractor", Type.EXTRACTOR);
		DPU loader = new DPU("RDF Loader", Type.LOADER);

		extractor.setJarPath(DPU.HACK_basePath + "RDF_extractor/target/RDF_extractor-0.0.1.jar");
		loader.setJarPath(DPU.HACK_basePath + "RDF_loader/target/RDF_loader-0.0.1.jar");
		
		int eId = graph.addDpu(extractor);
		int lId = graph.addDpu(loader);
		
		graph.addEdge(eId, lId);
	}
	
	/**
	 * Emtpy pipeline factory
	 * @return empty pipeline
	 */
	private Pipeline createEmptyPipeline() {
		Pipeline pipe = new Pipeline();
		PipelineGraph graph = new PipelineGraph();
		pipe.setGraph(graph);
		
		return pipe;
	}

}
