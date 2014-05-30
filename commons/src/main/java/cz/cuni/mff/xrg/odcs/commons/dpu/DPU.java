package cz.cuni.mff.xrg.odcs.commons.dpu;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 * Interface for DPU.
 * 
 * @see DPUContext
 * @see DPUException
 * @author Petyr
 */
public interface DPU {

    /**
     * Execute the DPU. If any exception is thrown then the DPU execution is
     * considered to failed.
     * 
     * @param context
     *            DPU's context.
     * @throws DPUException
     * @throws DataUnitException
     * @throws InterruptedException
     */
    public void execute(DPUContext context)
            throws DPUException,
            DataUnitException,
            InterruptedException;

    /**
     * Is called if and only if the @{link #execute} executive thread is
     * interrupted. This method may be implemented to clean up any resources held by the DPU.
     */
    public void cleanUp();

}
