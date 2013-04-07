package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;

import java.util.List;

/**
 *
 * @author Jiri Tomes, Bogo
 */
public class Node {

    private DPUInstance dpuInstance;

    private List<Node> neighbours;

    private Position position;

    public DPUInstance getDpuInstance() {
        return dpuInstance;
    }

    public List<Node> getNeighbours() {
        return neighbours;
    }

    public Position getPosition() {
        return position;
    }

    public void setDpuInstance(DPUInstance dpuInstance) {
        this.dpuInstance = dpuInstance;
    }

    public void setNeighbours(List<Node> neighbours) {
        this.neighbours = neighbours;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

}
