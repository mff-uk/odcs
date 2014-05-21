package cz.cuni.mff.xrg.odcs.dpu.triplegenerator;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class TripleGeneratorConfig extends DPUConfigObjectBase {

    /**
     * 
     */
    private static final long serialVersionUID = -1976290368429585223L;

    private int tripleCount;

    public int getTripleCount() {
        return tripleCount;
    }

    public void setTripleCount(int tripletCount) {
        this.tripleCount = tripletCount;
    }

}
