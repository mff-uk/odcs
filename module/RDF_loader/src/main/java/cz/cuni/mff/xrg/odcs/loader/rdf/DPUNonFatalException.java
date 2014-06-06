package cz.cuni.mff.xrg.odcs.loader.rdf;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;

public class DPUNonFatalException extends DPUException {

    public DPUNonFatalException(Throwable ex) {
        super(ex);
    }

    public DPUNonFatalException(String string) {
        super(string);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -8222702309837301080L;

}
