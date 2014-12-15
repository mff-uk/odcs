package cz.cuni.mff.xrg.odcs.commons.app.execution.context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;

/**
 * Hold and manage context for pipeline execution.
 * Complete read write interface for execution context. Enable writing data into
 * context and asking for directories. Provide methods for creating file names
 * for DataUnits.
 * The directory structure used by context is following;
 * ./working/DPU_ID/DATAUNIT_INDEX/ - DataUnit working directory
 * ./working/DPU_ID/tmp/ - DPU working directory
 * ./storage/DPU_ID/DATAUNIT_INDEX/ - storage for DataUnit results ./result/ -
 * place for DPU's files that should be accessible to the user
 *
 * @author Petyr
 */
@Entity
@Table(name = "exec_context_pipeline")
public class ExecutionContextInfo implements DataObject {

    /**
     * Name of working sub directory.
     */
    @Deprecated
    private static final String WORKING_DIR = "working";

    /**
     * Name of DPU tmp directory.
     */
    @Deprecated
    private static final String WORKING_TMP_DIR = "tmp";

    /**
     * Name of storage directory in which the DataUnits are save into.
     */
    @Deprecated
    private static final String STORAGE_DIR = "storage";

    /**
     * Directory for results.
     */
    @Deprecated
    private static final String RESULT_DIR = "result";

    /**
     * Prefix for DPU folder.
     */
    @Deprecated
    private static final String DPU_ID_PREFIX = "dpu_";

    /**
     * Unique id of pipeline execution.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Id of respective execution. Used to create relative path to the context
     * directory.
     */
    @OneToOne(mappedBy = "context", fetch = FetchType.LAZY)
    private PipelineExecution execution;

    /**
     * Dummy column, because Virtuoso cannot insert a row without specifying any
     * column values. Remove when entity has an attribute without default value.
     */
    @SuppressWarnings("unused")
    private Boolean dummy = false;

    /**
     * Contexts for DPU's. Indexed by {@link DPUInstanceRecord}.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapKeyJoinColumn(name = "dpu_instance_id", referencedColumnName = "id")
    @JoinColumn(name = "exec_context_pipeline_id")
    private Map<DPUInstanceRecord, ProcessingUnitInfo> contexts;

    /**
     * Empty constructor for JPA.
     */
    public ExecutionContextInfo() {
        contexts = new HashMap<>();
    }

    /**
     * Create info for given execution.
     *
     * @param execution
     */
    public ExecutionContextInfo(PipelineExecution execution) {
        this.contexts = new HashMap<>();
        this.execution = execution;
    }

    /**
     * Return context for given DPUInstanceRecord. Create new context if need.
     *
     * @param id
     *            DPUInstanceRecord's id.
     * @return DataProcessingUnitInfo
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    private ProcessingUnitInfo getContext(DPUInstanceRecord dpuInstance) {
        // check existence
        if (!contexts.containsKey(dpuInstance)) {
            // unknown context -> add
            ProcessingUnitInfo pui = new ProcessingUnitInfo();
            contexts.put(dpuInstance, pui);
        }
        // return data
        return contexts.get(dpuInstance);
    }

    /**
     * Add info record for new input {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
     *
     * @param dpuInstance
     *            The {@link DPUInstanceRecord} which will work with the
     *            DataUnit.
     * @param name
     *            Name of data unit.
     * @param type
     *            {@link DataUnit.Type Type} of data unit.
     * @return Index of new DataUnitInfo.
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public Integer createInput(DPUInstanceRecord dpuInstance,
            String name,
            ManagableDataUnit.Type type) {
        return getContext(dpuInstance).addDataUnit(name, type, true);
    }

    /**
     * Add info record for new output {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit}.
     *
     * @param dpuInstance
     *            The {@link DPUInstanceRecord} which will work with the
     *            DataUnit.
     * @param name
     *            Name of data unit.
     * @param type
     *            {@link DataUnit.Type Type} of data unit.
     * @return Index of new DataUnitInfo.
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public Integer createOutput(DPUInstanceRecord dpuInstance,
            String name,
            ManagableDataUnit.Type type) {
        return getContext(dpuInstance).addDataUnit(name, type, false);
    }

    /**
     * Delete all data about execution except {@link #id} Use to start execution
     * from the very beginning.
     *
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public void reset() {
        contexts.clear();
    }

    /**
     * Generate unique id for given DataUnit. If call multiple times for the
     * same dpuInstance and DataUnit's index it return the same id. The id has
     * following format: exec_{exec_id}_dpu_{dpu_id}_du_{du_id}.
     *
     * @param dpuInstance
     *            Owner of the DataUnit.
     * @param index
     *            DataUnit's index assigned to the DataUnit by context.
     * @return Unique id.
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String generateDataUnitId(DPUInstanceRecord dpuInstance,
            Integer index) {
        return "exec_" + execution.getId().toString() + "_dpu_"
                + dpuInstance.getId().toString() + "_du_" + index.toString();
    }

    public String generatePipelineId() {
        return "exec_" + String.valueOf(execution.getId());
    }

    /**
     * Return context information class {@link ProcessingUnitInfo} for given
     * DPU. If the context does not exist, then create new.
     *
     * @param dpuInstance
     *            Instance of DPU for which retrieve context info.
     * @return {@link ProcessingUnitInfo}
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public ProcessingUnitInfo createDPUInfo(DPUInstanceRecord dpuInstance) {
        return getContext(dpuInstance);
    }

    /**
     * @return set of indexes of stored DPU's execution information.
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public Set<DPUInstanceRecord> getDPUIndexes() {
        return contexts.keySet();
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Return relative path from execution directory to the DPU's tmp directory.
     * This directory will be deleted after the execution ends if not in debug
     * mode. Does not create a directory!
     *
     * @param dpuInstance
     *            The
     * @return Relative path, start but not end with separator (/, \\)
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String getDPUTmpPath(DPUInstanceRecord dpuInstance) {
        // secure DPU record existence
        getContext(dpuInstance);
        // ..
        return getWorkingPath() + File.separatorChar
                + getDpuDirectoryName(dpuInstance) + File.separatorChar
                + WORKING_TMP_DIR;
    }

    /**
     * Return relative path from execution directory to the DPU DataUnit's root
     * tmp directory. This directory will be deleted after execution ends if not
     * in debug mode. Does not create a directory!
     *
     * @param dpuInstance
     * @return Relative path, start but not end with separator (/, \\)
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String getDataUnitRootTmpPath(DPUInstanceRecord dpuInstance) {
        // secure DPU record existence
        getContext(dpuInstance);
        // ..
        return getWorkingPath() + File.separatorChar
                + getDpuDirectoryName(dpuInstance);
    }

    /**
     * Return relative path from execution directory to the DPU DataUnit's tmp
     * directory. This directory will be deleted after execution ends if not in
     * debug mode. Does not create a directory!
     *
     * @param dpuInstance
     * @param index
     *            DataUnitInfo index.
     * @return Relative path, start but not end with separator (/, \\)
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String getDataUnitTmpPath(DPUInstanceRecord dpuInstance,
            Integer index) {
        return getDataUnitRootStoragePath(dpuInstance) + File.separatorChar
                + index.toString();
    }

    /**
     * Return relative path from execution directory to the DPU DataUnit's root
     * storage directory. The storage directory can be used to store DataUnits
     * results. This directory will be deleted after execution ends if not in
     * debug mode. Does not create a directory!
     *
     * @param dpuInstance
     * @return Relative path, start but not end with separator (/, \\)
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String getDataUnitRootStoragePath(DPUInstanceRecord dpuInstance) {
        // secure DPU record existence
        getContext(dpuInstance);
        // ..
        return getStoragePath() + File.separatorChar
                + getDpuDirectoryName(dpuInstance);
    }

    /**
     * Return relative path from execution directory to the DPU DataUnit's
     * storage directory. The storage directory can be used to store DataUnits
     * results. This directory will be deleted after execution ends if not in
     * debug mode. Does not create a directory!
     *
     * @param dpuInstance
     * @param index
     *            DataUnitInfo index.
     * @return Relative path, start but not end with separator (/, \\)
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String getDataUnitStoragePath(DPUInstanceRecord dpuInstance,
            Integer index) {
        return getDataUnitRootStoragePath(dpuInstance) + File.separatorChar
                + index.toString();
    }

    /**
     * Return context information class {@link ProcessingUnitInfo} for given
     * DPU.
     *
     * @param dpuInstance
     *            Instance of DPU for which retrieve context info.
     * @return {@link ProcessingUnitInfo} or null if no records for given
     *         dpuInstance exist.
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public ProcessingUnitInfo getDPUInfo(DPUInstanceRecord dpuInstance) {
        if (contexts.containsKey(dpuInstance)) {
            return contexts.get(dpuInstance);
        } else {
            return null;
        }
    }

    /**
     * Return relative path from execution directory to the execution working
     * directory.
     *
     * @return Relative path, start but not end with separator (/, \\)
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String getWorkingPath() {
        return getRootPath() + File.separatorChar + WORKING_DIR;
    }

    /**
     * Return relative path from execution directory to the execution result
     * directory. This directory can be used to store result data.
     *
     * @return Relative path, start but not end with separator (/, \\)
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String getResultPath() {
        return getRootPath() + File.separatorChar + RESULT_DIR;
    }

    /**
     * Return relative path from execution directory to the execution storage
     * directory or data units.
     *
     * @return Relative path, start but not end with separator (/, \\)
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String getStoragePath() {
        return getRootPath() + File.separatorChar + STORAGE_DIR;
    }

    /**
     * Return relative path from execution directory to the execution root
     * directory.
     *
     * @return Relative path start but not end with separator separator (/, \\).
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public String getRootPath() {
        return File.separatorChar + execution.getId().toString();
    }

    /**
     * @return respective pipeline execution.
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    public PipelineExecution getExecution() {
        return this.execution;
    }

    /**
     * @return string that should be used as a name for DPU's directory.
     * @deprecated use {@link ExecutionInfo} instead
     */
    @Deprecated
    private String getDpuDirectoryName(DPUInstanceRecord dpuInstance) {
        StringBuilder dirName = new StringBuilder();
        dirName.append(DPU_ID_PREFIX);
        dirName.append(dpuInstance.getId().toString());
        return dirName.toString();
    }

    Map<DPUInstanceRecord, ProcessingUnitInfo> getContexts() {
        return this.contexts;
    }

    /**
     * Returns true if two objects represent the same pipeline. This holds if
     * and only if <code>this.id == null ? this == obj : this.id == o.id</code>.
     *
     * @param obj
     * @return true if both objects represent the same pipeline
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final DataObject other = (DataObject) obj;
        if (this.getId() == null) {
            return super.equals(other);
        }

        return Objects.equals(this.getId(), other.getId());
    }

    /**
     * Hashcode is compatible with {@link #equals(java.lang.Object)}.
     *
     * @return The value of hashcode.
     */
    @Override
    public int hashCode() {
        if (this.getId() == null) {
            return super.hashCode();
        }
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.getId());
        return hash;
    }

}
