package cz.cuni.intlib.commons.app.data.dpu;

/**
 *
 * @author Jiri Tomes
 */
public class DPUInstance {

    private RDFGraph inputGraph;
    private RDFGraph outputGraph;
    /*
     * TODO - just not in constructor
     */
    private RDFGraph knowledgeGraph = null;
    private InstanceConfiguration instanceConfig;

    public DPUInstance(RDFGraph inputGraph, RDFGraph outputGraph, InstanceConfiguration instanceConfig) {
        this.inputGraph = inputGraph;
        this.outputGraph = outputGraph;
        this.instanceConfig = instanceConfig;
    }

    public RDFGraph getKnowledgeGraph() {
        return knowledgeGraph;
    }

    public void setKnowledgeGraph(RDFGraph newKnowledgeGraph) {
        knowledgeGraph = newKnowledgeGraph;
    }

    public RDFGraph getInputGraph() {
        return inputGraph;
    }

    public void setInputGraph(RDFGraph newInputGraph) {
        inputGraph = newInputGraph;
    }

    public RDFGraph getOutputGraph() {
        return outputGraph;
    }

    public void setOutputGraph(RDFGraph newOutputGraph) {
        outputGraph = newOutputGraph;
    }

    public InstanceConfiguration getInstanceConfiguration() {
        return instanceConfig;
    }

    public void setInstanceConfiguration(InstanceConfiguration newInstanceConfig) {
        instanceConfig = newInstanceConfig;
    }
}
