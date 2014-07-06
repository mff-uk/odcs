package cz.cuni.mff.xrg.odcs.commons.dpu;

import eu.unifiedviews.dpu.DPUException;

public class DPUCancelledException extends DPUException {

    /**
     * 
     */
    private static final long serialVersionUID = -4329478441269458472L;

    public DPUCancelledException() {
        super();
    }

    public DPUCancelledException(Throwable cause) {
        super(cause);
    }
}
