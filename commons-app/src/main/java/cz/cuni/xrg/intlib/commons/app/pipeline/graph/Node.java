package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;

/**
 * Node represents DPU on the pipeline and holds information about its position
 * on the Pipeline canvas.
 *
 * @author Jiri Tomes
 * @author Bogo
 * @author Jan Vojt <jan@vojt.net>
 */
public class Node {

    private int id;
    private DPUInstance dpuInstance;
    private Position position;

    /**
     * Empty constructor for Hibernate.
     */
    public Node() {
    }

    /**
     * Constructor with corresponding DPUInstance
     *
     * @param dpuInstance
     */
    public Node(DPUInstance dpuInstance) {
        this.dpuInstance = dpuInstance;
    }

    public DPUInstance getDpuInstance() {
        return dpuInstance;
    }

    public Position getPosition() {
        return position;
    }

    public void setDpuInstance(DPUInstance dpuInstance) {
        this.dpuInstance = dpuInstance;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

//    public int getId() {
//        return id;
//    }

    /**
     * Temporary solution of id generation.
     *
     * @param GetUniqueDpuInstanceId
     */
//    void setId(int getUniqueDpuInstanceId) {
//        id = getUniqueDpuInstanceId;
//    }
}
