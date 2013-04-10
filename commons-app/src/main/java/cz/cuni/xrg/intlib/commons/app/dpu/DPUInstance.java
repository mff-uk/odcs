package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.RDFGraph;

/**
 * For representing concrete DPU component in the pipeline.
 *
 * @author Jiri Tomes
 */
public class DPUInstance {

    /**
     * DPU
     */
    private DPU dpu;

    /**
     * All edges go to the component on the canvas.
     */
    private RDFGraph inputGraph;
    /**
     * All edges go from the component on the canvas.
     */
    private RDFGraph outputGraph;
    /*
     * TODO - just not in constructor
     */
    private RDFGraph knowledgeGraph = null;
    /**
     * Configuration setting for this component.
     */
    private InstanceConfiguration instanceConfig;

    public DPUInstance(DPU dpu) {
        this.dpu = dpu;
    }

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

    public String getName() {
        return dpu.getName();
    }

    public void setName(String value) {

    }

    public String getDescription() {
        return dpu.getDescription();
    }

    public void setDescription(String value) {

    }
}
