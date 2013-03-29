package dpu;

/**
 *
 * @author Jiri Tomes
 */
public class DPU_Instance {

    private RDF_Graph inputGraph;
    private RDF_Graph outputGraph;
    /*
     * TODO - just not in constructor
     */
    private RDF_Graph knowledgeGraph = null;
    private InstanceConfiguration instanceConfig;

    public DPU_Instance(RDF_Graph inputGraph, RDF_Graph outputGraph, InstanceConfiguration instanceConfig) {
        this.inputGraph = inputGraph;
        this.outputGraph = outputGraph;
        this.instanceConfig = instanceConfig;
    }

    public RDF_Graph getKnowledgeGraph() {
        return knowledgeGraph;
    }

    public void setKnowledgeGraph(RDF_Graph newKnowledgeGraph) {
        knowledgeGraph = newKnowledgeGraph;
    }

    public RDF_Graph getInputGraph() {
        return inputGraph;
    }

    public void setInputGraph(RDF_Graph newInputGraph) {
        inputGraph = newInputGraph;
    }

    public RDF_Graph getOutputGraph() {
        return outputGraph;
    }

    public void setOutputGraph(RDF_Graph newOutputGraph) {
        outputGraph = newOutputGraph;
    }

    public InstanceConfiguration getInstanceConfiguration() {
        return instanceConfig;
    }

    public void setInstanceConfiguration(InstanceConfiguration newInstanceConfig) {
        instanceConfig = newInstanceConfig;
    }
}
