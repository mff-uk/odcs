package cz.cuni.xrg.intlib.commons.app.pipeline;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.repository.RDFFormatType;


/**
 * Test scenario for pipeline runs.
 * @author Petr Škoda
 * 
 * TODO resolve vaadin dependency on config dialogue and move to backend
 */
public class PipelineRunSecondTest {
	
	/**
	 * Facade for loading modules given as jar files
	 */
	private ModuleFacade moduleFacade = null;
		
	
	/**
	 * Setup OSGi
	 * TODO What are exported packages in {@link ModuleFacade#start(String)}??
	 */
	@Test
	public void testTrivialRun() {
		moduleFacade = new ModuleFacade();
		
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
		PipelineExecution run = new PipelineExecution(pipe);
		run.setModuleFacade(moduleFacade);
		run.run();		
		
		moduleFacade.stop();
		moduleFacade = null;
	}
	
	/**
	 * Create minimal pipeline graph
	 * Scenario: E -> L
	 * @param graph
	 */
	private void setupTrivialPipelineGraph(PipelineGraph graph) {
		
		DPU extractor = new DPU("File Extractor", Type.EXTRACTOR);
		DPU loader = new DPU("File Loader", Type.LOADER);

		extractor.setJarPath("File_extractor/target/File_extractor-0.0.1.jar");
		loader.setJarPath("File_loader/target/File_loader-0.0.1.jar");
		
		int eId = graph.addDpu(extractor);
		int lId = graph.addDpu(loader);
		
		graph.addEdge(eId, lId);
		
		// set configurations
		Configuration exConfig = new  Configuration();
// TODO: set your File extractor
		exConfig.setValue("Path", "http://ld.opendata.cz/tedDumps/ted4.ttl");
		exConfig.setValue("FileSuffix", ".ttl");
		exConfig.setValue("OnlyThisSuffix", true);
		
		graph.getNodeById(eId).getDpuInstance().setInstanceConfig(exConfig);

		Configuration ldConfig = new  Configuration();
                
// TODO: set your File Loader
		ldConfig.setValue("DirectoryPath", "C:\\intlib\\");
		ldConfig.setValue("FileName", "out");
		ldConfig.setValue("RDFFileFormat", RDFFormatType.AUTO);
		
		graph.getNodeById(lId).getDpuInstance().setInstanceConfig(ldConfig);		
		
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
