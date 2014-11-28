package cz.cuni.mff.xrg.odcs.commons.app.execution.context;

import java.io.File;
import java.util.List;

import eu.unifiedviews.dataunit.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;

/**
 * Class used to work with context for single DPU, or global context if no DPU
 * is set.
 * 
 * @author Petyr
 */
public class DpuContextInfo {

    /**
     * Prefix for DPU folder.
     */
    private static final String DPU_ID_PREFIX = "dpu_";

    /**
     * Name of DPU temporally directory.
     */
    private static final String WORKING_TMP_DIR = "tmp";

    /**
     * Binded context.
     */
    private final ExecutionContextInfo executionContext;

    /**
     * Binded instance.
     */
    private final DPUInstanceRecord dpuInstance;

    /**
     * Binded dpu context.
     */
    private final ProcessingUnitInfo dpuInfo;

    /**
     * Execution info for whole execution.
     */
    private final ExecutionInfo execInfo;

    public DpuContextInfo(ExecutionContextInfo executionContext,
            DPUInstanceRecord dpuInstance, ExecutionInfo execInfo) {
        this.executionContext = executionContext;
        this.dpuInstance = dpuInstance;
        this.dpuInfo = executionContext.getContexts().get(dpuInstance);
        this.execInfo = execInfo;
    }

    /**
     * Add info record for new input {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
     * 
     * @param name
     *            Input name.
     * @param type
     *            DataUnit type.
     * @return Index of new DataUnitInfo.
     */
    public Integer createInput(String name, ManagableDataUnit.Type type) {
        return addDataUnit(name, type, true);
    }

    /**
     * Add info record for new output {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
     * 
     * @param name
     *            Output name.
     * @param type
     *            DataUnit type.
     * @return Index of new DataUnitInfo.
     */
    public Integer createOutput(String name, ManagableDataUnit.Type type) {
        return addDataUnit(name, type, false);
    }

    /**
     * Add info record for {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
     * 
     * @param name
     *            Name of DataUnit.
     * @param type
     *            Type of DataUnit.
     * @param isInput
     *            True in case of input DataUnit.
     * @return Index of new DataUnitInfo.
     */
    private Integer addDataUnit(String name, ManagableDataUnit.Type type, boolean isInput) {
        // add information
        Integer index = 0;
        if (dpuInfo.dataUnits.isEmpty()) {
        } else {
            index = dpuInfo.dataUnits.get(dpuInfo.dataUnits.size() - 1)
                    .getIndex() + 1;
        }
        DataUnitInfo dataUnitInfo = new DataUnitInfo(index, name, type, isInput);
        dpuInfo.dataUnits.add(dataUnitInfo);
        return index;
    }

    /**
     * @return list of stored {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}s.
     */
    public List<DataUnitInfo> getDataUnits() {
        return dpuInfo.dataUnits;
    }

    /**
     * Return id for {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} of given
     * index. Does not control existence of {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
     * 
     * @param index
     *            DataUnit's index.
     * @return id for {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} of
     *         given index
     */
    public String createId(Integer index) {
        return "exec_" + executionContext.getExecution().getId().toString() + "_dpu_"
                + dpuInstance.getId().toString() + "_du_" + index.toString();
    }

    /**
     * Return relative path from execution directory to the DPU's root temp
     * directory. This directory contains as subdirectories the DPU's working
     * temp and DataUnit's temp directories. Does not create a directory!
     * 
     * @return Relative path, start but not end with separator (/, \\)
     */
    public String getTempRootPath() {
        return execInfo.getWorkingPath() + File.separatorChar + getDpuDirName();
    }

    /**
     * Return relative path from execution directory to the DPU's temp
     * directory. This directory will be deleted after the execution ends if not
     * in debug mode. Does not create a directory!
     * 
     * @return Relative path, start but not end with separator (/, \\)
     */
    public String getTempPath() {
        return getTempRootPath() + File.separatorChar + WORKING_TMP_DIR;
    }

    /**
     * Return relative path from execution directory to the DPU DataUnit's temp
     * directory. This directory will be deleted after execution ends if not in
     * debug mode. Does not create a directory!
     * 
     * @param index
     *            DataUnitInfo index.
     * @return Relative path, start but not end with separator (/, \\)
     */
    public String getTempPath(Integer index) {
        return getTempRootPath() + File.separatorChar + index.toString();
    }

    /**
     * Return relative path from execution directory to the DPU's root storage
     * directory. The storage directory can be used to store DataUnits results.
     * This directory will be deleted after execution ends if not in debug mode.
     * Does not create a directory!
     * 
     * @return Relative path, start but not end with separator (/, \\)
     */
    public String getRootStoragePath() {
        return execInfo.getStoragePath() + File.separatorChar + getDpuDirName();
    }

    /**
     * Return relative path from execution directory to the DPU DataUnit's
     * storage directory. The storage directory can be used to store DataUnits
     * results. This directory will be deleted after execution ends if not in
     * debug mode. Does not create a directory!
     * 
     * @param index
     *            DataUnitInfo index.
     * @return Relative path, start but not end with separator (/, \\)
     */
    public String getStoragePath(Integer index) {
        return getRootStoragePath() + File.separatorChar + index.toString();
    }

    /**
     * Return relative path from execution directory to the execution result
     * directory. This directory can be used to store result data and it's
     * shared by all DPU's.
     * 
     * @return Relative path, start but not end with separator (/, \\)
     */
    public String getResultPath() {
        return execInfo.getResultPath();
    }

    /**
     * @return String that should be used as a name for DPU's directory.
     */
    private String getDpuDirName() {
        StringBuilder dirName = new StringBuilder();
        dirName.append(DPU_ID_PREFIX);
        dirName.append(dpuInstance.getId().toString());
        return dirName.toString();
    }

}
