package cz.cuni.mff.xrg.odcs.backend.context;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import eu.unifiedviews.commons.dataunit.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.GraphUrl;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * Class provide functionality pro manage list of {@link ManagableDataUnit}s.
 * 
 * @author Petyr
 */
final class DataUnitManager {

    private static final Logger LOG = LoggerFactory.getLogger(DataUnitManager.class);

    /**
     * Store outputs.
     */
    private final List<ManagableDataUnit> dataUnits;

    /**
     * Mapping from {@link outputs} to indexes.
     */
    private final Map<ManagableDataUnit, Integer> indexes;

    /**
     * DPUInstanceRecord as owner of this context.
     */
    private final DPUInstanceRecord dpuInstance;

    /**
     * Used factory.
     */
    private final DataUnitFactory dataUnitFactory;

    /**
     * Manage mapping context into execution's directory.
     */
    private final ExecutionContextInfo context;

    /**
     * Execution working directory.
     */
    private final File workingDir;

    /**
     * Application configuration.
     */
    private final AppConfig appConfig;

    /**
     * True if used for inputs.
     */
    private final boolean isInput;

    /**
     * Create manager for input {@link DataUnit}s.
     * 
     * @param dpuInstance
     * @param dataUnitFactory
     * @param context
     * @param workingDir
     *            General working directory.
     * @param appConfig
     * @return
     */
    public static DataUnitManager createInputManager(DPUInstanceRecord dpuInstance,
            DataUnitFactory dataUnitFactory,
            ExecutionContextInfo context,
            File workingDir,
            AppConfig appConfig) {
        return new DataUnitManager(dpuInstance, dataUnitFactory, context,
                workingDir, appConfig, true);
    }

    /**
     * Create manager for input {@link DataUnit}s.
     * 
     * @param dpuInstance
     * @param dataUnitFactory
     * @param context
     * @param workingDir
     *            General working directory.
     * @param appConfig
     * @return
     */
    public static DataUnitManager createOutputManager(DPUInstanceRecord dpuInstance,
            DataUnitFactory dataUnitFactory,
            ExecutionContextInfo context,
            File workingDir,
            AppConfig appConfig) {
        return new DataUnitManager(dpuInstance, dataUnitFactory, context,
                workingDir, appConfig, false);
    }

    private DataUnitManager(DPUInstanceRecord dpuInstance,
            DataUnitFactory dataUnitFactory,
            ExecutionContextInfo context,
            File workingDir,
            AppConfig appConfig,
            boolean isInput) {
        this.dataUnits = new LinkedList<>();
        this.indexes = new HashMap<>();
        this.dpuInstance = dpuInstance;
        this.dataUnitFactory = dataUnitFactory;
        this.context = context;
        this.workingDir = workingDir;
        this.appConfig = appConfig;
        this.isInput = isInput;
    }

    /**
     * Save stored {@link DataUnit}s into {@link #workingDir}.
     */
    public void save() {
        for (ManagableDataUnit item : dataUnits) {
            if (item instanceof FileDataUnit) {
                try {
                    // get directory
                    File directory = new File(workingDir,
                            context.getDataUnitStoragePath(dpuInstance,
                                    indexes.get(item)));
                    // and save into directory
                    ((FileDataUnit) item).save(directory);
                } catch (Exception e) {
                    LOG.error("Can't save DataUnit.", e);
                }
            } else {
                try {
                    item.store();
                } catch (DataUnitException ex) {
                    LOG.error("Failed to save content of data unit.", ex);
                }
            }

        }
    }

    /**
     * Call delete on all stored DataUnits and them delete them from
     * this instance.
     */
    public void clear() {
        for (ManagableDataUnit item : dataUnits) {
            try {
                item.clear();
            } catch (DataUnitException ex) {
                LOG.error("Can't clear data unit.", ex);
            }

        }
    }

    /**
     * Call release on all stored DataUnit and them delete them from
     * this instance.
     */
    public void release() {
        for (ManagableDataUnit item : dataUnits) {
            try {
                item.release();
            } catch (DataUnitException ex) {
                LOG.error("Can't realease data unit.", ex);
            }
        }
        dataUnits.clear();
    }

    /**
     * Check context and create DataUnits that are in context but
     * are not instantiated in DataUnitManager. Does not delete or release
     * existing DataUnits.
     * DataUnit load failures are silently ignored.
     * 
     * @throws DataUnitException
     */
    public void reload() throws DataUnitException {
        ProcessingUnitInfo dpuInfo = context.getDPUInfo(dpuInstance);
        if (dpuInfo == null) {
            // no data for this DPU
            LOG.trace("dpuInfo == null, not data has been realoded");
            return;
        }

        LOG.trace("Loading dataUnits input: {}", indexes);

        List<DataUnitInfo> dataUnitsInfo = dpuInfo.getDataUnits();
        // check every DataUnit in contextInfo
        for (DataUnitInfo info : dataUnitsInfo) {
            if (indexes.containsValue(info.getIndex())) {
                // DataUnit is already presented
            } else {
                if (info.isInput() == isInput) {
                    // ok, it's ours .. 
                    LOG.trace("Loading data unit name: {}", info.getName());
                } else {
                    // we are out it's in .. orotherwise, just skip
                    LOG.trace("Skip over data unit name: {}", info.getName());
                    continue;
                }

                // create new DataUnit
                Integer index = info.getIndex();
                String id = context.generateDataUnitId(dpuInstance, index);
                File directory = new File(workingDir, context.getDataUnitTmpPath(dpuInstance, index));
                
                ManagableDataUnit dataUnit = dataUnitFactory.create(
                        info.getType(),
                        context.generatePipelineId(),
                        GraphUrl.translateDataUnitId(id),
                        info.getName(),
                        directory);
                // add into DataUnitManager
                dataUnits.add(dataUnit);
                indexes.put(dataUnit, index);
                // check for existence of result directory, if exist load
                File storageDirectory = new File(workingDir,
                        context.getDataUnitStoragePath(dpuInstance, index));
                if (storageDirectory.exists() && (dataUnit instanceof FileDataUnit)) {
                    // load data from directory
                    try {
                        ((FileDataUnit) dataUnit).load(storageDirectory);
                    } catch (RuntimeException e) {
                        LOG.error("Failed to load data for DataUnit", e);
                    }
                }
            }
        }
    }

    /**
     * Request creating a new DataUnit of given type. If the requested {@link DataUnit} can't be created from any reason the {@link DataUnitException} is
     * thrown.
     * The DataUnit's name can be further changed. If the {@link DataUnit} witch given name and type alredy exist then is returned.
     * 
     * @param type
     *            Type of DataUnit.
     * @param name
     *            DataUnit's name.
     * @return Created DataUnit.
     * @throw DataUnitCreateException
     */
    public ManagableDataUnit addDataUnit(ManagableDataUnit.Type type, String name) {
        // check if we do not already have such DataUnit
        for (ManagableDataUnit du : dataUnits) {
            if ((du.getType() == type || du.getType() == type) &&
                    du.getName().compareTo(name) == 0) {
                // the DPU already exist .. 
                LOG.trace("dataUnit with name: {} type: {} already exist", name, type.toString());
                return du;
            }
        }
        LOG.trace("new dataUnit with name: {} type: {} has been created", name, type.toString());
        // gather information for new DataUnit
        Integer index;
        if (isInput) {
            index = context.createInput(dpuInstance, name, type);
        } else {
            index = context.createOutput(dpuInstance, name, type);
        }
        String id = context.generateDataUnitId(dpuInstance, index);
        File directory = new File(workingDir, context.getDataUnitTmpPath(
                dpuInstance, index));
        // create instance
        ManagableDataUnit dataUnit;
        dataUnit = dataUnitFactory.create(type, 
                context.generatePipelineId(),
                GraphUrl.translateDataUnitId(id),
                name,
                directory);
        // add to storage
        dataUnits.add(dataUnit);
        indexes.put(dataUnit, index);
        //
        return dataUnit;
    }

    /**
     * Return access to all stored DataUnits.
     * 
     * @return
     */
    public List<ManagableDataUnit> getDataUnits() {
        return dataUnits;
    }
}
