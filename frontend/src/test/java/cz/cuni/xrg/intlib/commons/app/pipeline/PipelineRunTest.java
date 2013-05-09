package cz.cuni.xrg.intlib.commons.app.pipeline;

import org.junit.Test;

import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.InstanceConfiguration;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import java.util.LinkedList;
import java.util.List;

/**
 * Test scenario for pipeline runs.
 *
 * @author Jan Vojt <jan@vojt.net>
 *
 * TODO resolve vaadin dependency on config dialogue and move to backend
 */
public class PipelineRunTest {

    /**
     * Facade for loading modules given as jar files
     */
    private ModuleFacade moduleFacade = null;

    /**
     * Setup OSGi TODO What are exported packages in
     * {@link ModuleFacade#start(String)}??
     */
    @Test
    public void testTrivialRun() {
        moduleFacade = new ModuleFacade();

        moduleFacade.start(
				",com.vaadin.ui" +
				",com.vaadin.data" +
				",com.vaadin.data.util" +
				",com.vaadin.shared.ui.combobox" +
				",com.vaadin.server" +
				// OpenRdf
				",org.openrdf.rio"
						);

        // setup pipeline
        Pipeline pipe = createEmptyPipeline();
        setupTrivialPipelineGraph(pipe.getGraph());

        // create run model and run it
        PipelineExecution run = new PipelineExecution(pipe);
        run.setModuleFacade(moduleFacade);
        //TODO SOLVE PROBLEM run.run();

        moduleFacade.stop();
        moduleFacade = null;
    }

    /**
     * Create minimal pipeline graph Scenario: E -> L
     *
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

        // set configurations
        Configuration exConfig = new InstanceConfiguration();
// TODO: set your RDF extractor

        exConfig.setValue("SPARQL_endpoint", "http://ld.opendata.cz:8894/sparql-auth");
        exConfig.setValue("Host_name", "SPARQL");
        exConfig.setValue("Password", "nejlepsipaper");
        exConfig.setValue("GraphsUri", new LinkedList<String>());
        exConfig.setValue("SPARQL_query", "select * where {?s ?o ?p} LIMIT 10");

        graph.getNodeById(eId).getDpuInstance().setInstanceConfig(exConfig);

        Configuration ldConfig = new InstanceConfiguration();
        List<String> graphsURI=new LinkedList<String>();
        graphsURI.add("http://ld.opendata.cz/resource/myGraph/001");

// TODO: set your RDF loader
        ldConfig.setValue("SPARQL_endpoint", "http://ld.opendata.cz:8894/sparql");
        ldConfig.setValue("Host_name", "SPARQL");
        ldConfig.setValue("Password", "nejlepsipaper");
        ldConfig.setValue("GraphsUri", graphsURI);

        graph.getNodeById(lId).getDpuInstance().setInstanceConfig(ldConfig);


    }

    /**
     * Emtpy pipeline factory
     *
     * @return empty pipeline
     */
    private Pipeline createEmptyPipeline() {
        Pipeline pipe = new Pipeline();
        PipelineGraph graph = new PipelineGraph();
        pipe.setGraph(graph);

        return pipe;
    }
}
