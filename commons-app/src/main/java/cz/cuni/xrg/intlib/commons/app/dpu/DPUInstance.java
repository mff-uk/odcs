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
	 * Primary key
	 */
    private int id;
    
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
    
    /**
     * Connection to knowledge base
     */
    private RDFGraph knowledgeGraph;
    
    /**
     * Configuration setting for this component.
     */
    private Configuration instanceConfig;

    private String name;

    private String description;

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

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the dpu
	 */
	public DPU getDpu() {
		return dpu;
	}

	/**
	 * @param dpu the dpu to set
	 */
	public void setDpu(DPU dpu) {
		this.dpu = dpu;
	}

	/**
	 * @return the inputGraph
	 */
	public RDFGraph getInputGraph() {
		return inputGraph;
	}

	/**
	 * @param inputGraph the inputGraph to set
	 */
	public void setInputGraph(RDFGraph inputGraph) {
		this.inputGraph = inputGraph;
	}

	/**
	 * @return the outputGraph
	 */
	public RDFGraph getOutputGraph() {
		return outputGraph;
	}

	/**
	 * @param outputGraph the outputGraph to set
	 */
	public void setOutputGraph(RDFGraph outputGraph) {
		this.outputGraph = outputGraph;
	}

	/**
	 * @return the knowledgeGraph
	 */
	public RDFGraph getKnowledgeGraph() {
		return knowledgeGraph;
	}

	/**
	 * @param knowledgeGraph the knowledgeGraph to set
	 */
	public void setKnowledgeGraph(RDFGraph knowledgeGraph) {
		this.knowledgeGraph = knowledgeGraph;
	}

	/**
	 * @return the instanceConfig
	 */
	public Configuration getInstanceConfig() {
		return instanceConfig;
	}

	/**
	 * @param instanceConfig the instanceConfig to set
	 */
	public void setInstanceConfig(Configuration instanceConfig) {
		this.instanceConfig = instanceConfig;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
