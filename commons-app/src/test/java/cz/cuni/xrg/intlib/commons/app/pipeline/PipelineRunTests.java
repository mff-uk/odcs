package cz.cuni.xrg.intlib.commons.app.pipeline;

import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.app.AppConfiguration;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;

public class PipelineRunTests {
	
	/**
	 * Facade for loading modules given as jar files
	 */
	private ModuleFacade moduleFacade = null;
		
	
	/**
	 * Setup OSGi
	 * TODO What are exported packages in {@link ModuleFacade#start(String)}??
	 */
//	@Test
	public void testTrivialRun() {
		
		moduleFacade = new ModuleFacade(new AppConfiguration());
		
		moduleFacade.start(
				",com.vaadin,com.vaadin.ui," +
				"com.vaadin.data,com.vaadin.data.Property,com.vaadin.data.util," +
				"com.vaadin.event.FieldEvents," + 
				"com.vaadin.shared.ui.combobox," +
				// OpenRdf
				"org.openrdf.rio"
				);	
		
		// setup pipeline
		Pipeline pipe = createEmptyPipeline();
		setupTrivialPipelineGraph(pipe.getGraph());

		// create run model and run it
		PipelineExecution execution = new PipelineExecution(pipe);
		execution.setModuleFacade(moduleFacade);
		//TODO solve execution.run();		
		
		moduleFacade.stop();
		moduleFacade = null;
	}
	
	/**
	 * Create minimal pipeline graph
	 * Scenario: E -> L
	 * @param graph
	 */
	private void setupTrivialPipelineGraph(PipelineGraph graph) {
		
		DPU extractor = new DPU("RDF Extractor", DpuType.EXTRACTOR);
		DPU loader = new DPU("RDF Loader", DpuType.LOADER);

		extractor.setJarPath("RDF_extractor/target/RDF_extractor-0.0.1.jar");
		loader.setJarPath("RDF_loader/target/RDF_loader-0.0.1.jar");
		
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
