package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.RDFGraph;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;

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
    private Configuration instanceConfig;
    private int id;

    public DPUInstance(DPU dpu) {
        this.dpu = dpu;
		this.name = dpu.getName();
		this.description = dpu.getDescription();
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

    public Configuration getInstanceConfiguration() {
        return instanceConfig;
    }

    public void setInstanceConfiguration(Configuration newInstanceConfig) {
        instanceConfig = newInstanceConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        description = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        id = value;
    }

    private String name;

    private String description;

	public String getJarPath() {
		return dpu.getJarPath();
	}
}
