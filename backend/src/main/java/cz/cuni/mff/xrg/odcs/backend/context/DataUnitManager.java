package cz.cuni.mff.xrg.odcs.backend.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;

/**
 * Class provide functionality pro manage list of {@link ManagableDataUnit}s.
 * 
 * @author Petyr
 * 
 */
final class DataUnitManager {

	/**
	 * Store outputs.
	 */
	private List<ManagableDataUnit> dataUnits;

	/**
	 * Mapping from {@link outputs} to indexes.
	 */
	private Map<ManagableDataUnit, Integer> indexes;

	/**
	 * DPUInstanceRecord as owner of this context.
	 */
	private DPUInstanceRecord dpuInstance;

	/**
	 * Used factory.
	 */
	private DataUnitFactory dataUnitFactory;

	/**
	 * Manage mapping context into execution's directory.
	 */
	private ExecutionContextInfo context;

	/**
	 * Execution working directory.
	 */
	private File workingDir;

	/**
	 * Application configuration.
	 */
	private AppConfig appConfig;

	/**
	 * True if used for inputs.
	 */
	private boolean isInput;

	private static Logger LOG = LoggerFactory.getLogger(DataUnitManager.class);

	/**
	 * Create manager for input {@link DataUnit}s.
	 * 
	 * @param dpuInstance
	 * @param dataUnitFactory
	 * @param context
	 * @param workingDir General working directory.
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
	 * @param workingDir General working directory.
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
	 * Check required type based on application configuration and return
	 * {@link DataUnitType} that should be created. Can thrown
	 * {@link DataUnitCreateException} in case of unknown {@link DataUnitType}.
	 * 
	 * @param type Required type.
	 * @return Type to create.
	 * @throws DataUnitCreateException
	 */
	private DataUnitType checkType(DataUnitType type)
			throws DataUnitCreateException {
		if (type == DataUnitType.RDF) {
			// select other DataUnit based on configuration
			String defRdfRepo = appConfig
					.getString(ConfigProperty.BACKEND_DEFAULTRDF);
			if (defRdfRepo == null) {
				// use local
				type = DataUnitType.RDF_Local;
			} else {
				// choose based on value in appConfig
				if (defRdfRepo.compareToIgnoreCase("virtuoso") == 0) {
					// use virtuoso
					type = DataUnitType.RDF_Virtuoso;
				} else if (defRdfRepo.compareToIgnoreCase("localRDF") == 0) {
					// use local
					type = DataUnitType.RDF_Local;
				} else {
					throw new DataUnitCreateException(
							"The data unit type is unknown."
									+ "Check the value of the parameter "
									+ "backend.defaultRDF in config.properties");
				}
			}
		}
		return type;
	}

	/**
	 * Save stored {@link DataUnit}s into {@link #workingDir}.
	 */
	public void save() {
		for (ManagableDataUnit item : dataUnits) {
			try {
				// get directory
				File directory = new File(workingDir,
						context.getDataUnitStoragePath(dpuInstance,
								indexes.get(item)));
				// and save into directory
				item.save(directory);
			} catch (Exception e) {
				LOG.error("Can't save DataUnit.", e);
			}
		}
	}

	/**
	 * Call delete on all stored DataUnits and them delete them from 
	 * this instance.
	 */
	@Deprecated
	public void delete() {
		for (ManagableDataUnit item : dataUnits) {
			item.delete();
		}
		dataUnits.clear();
	}
	
	/**
	 * Call release on all stored DataUnit and them delete them from
	 * this instance.
	 */
	@Deprecated
	public void release() {
		for (ManagableDataUnit item : dataUnits) {
			item.release();
		}
		dataUnits.clear();
	}
	
	/**
	 * Check context and create DataUnits that are in context but
	 * are not instantiated in DataUnitManager. Does not delete or release
	 * existing DataUnits.
	 * 
	 * DataUnit load's failures are silently ignored.
	 * 
	 * @throws DataUnitException 
	 */
	public void reload() throws DataUnitException {
		ProcessingUnitInfo dpuInfo = context.getDPUInfo(dpuInstance);
		if (dpuInfo == null) {
			// no data for this DPU
			return;
		}
		List<DataUnitInfo> dataUnitsInfo = dpuInfo.getDataUnits();
		// check every DataUnit in contextInfo
		for (DataUnitInfo info : dataUnitsInfo) {
			if (indexes.containsKey(info.getIndex())) {
				// DataUnit is already presented
			} else {
				// create new DataUnit
				Integer index = info.getIndex();
				String id = 
						context.generateDataUnitId(dpuInstance, index);
				File directory = new File(workingDir,
						context.getDataUnitTmpPath(dpuInstance, index));				
				ManagableDataUnit dataUnit = dataUnitFactory.create(info.getType(), 
						id, info.getName(), directory);
				// add into DataUnitManager
				dataUnits.add(dataUnit);
				indexes.put(dataUnit, index);
				// check for existence of result directory, if exist load
				File storageDirectory = new File(workingDir,
						context.getDataUnitStoragePath(dpuInstance, index));
				if (storageDirectory.exists()) {
					// load data from directory
					try {
						dataUnit.load(storageDirectory);
					} catch (FileNotFoundException | RuntimeException e) {
						LOG.error("Failed to load data for DataUnit", e);
					}
				}
			}
		}
	}
	
	/**
	 * Request creating a new DataUnit of given type. If the requested
	 * {@link DataUnit} can't be created from any reason the 
	 * {@link DataUnitCreateException} is thrown.
	 * The DataUnit's name can be further changed.
	 * 
	 * @param type Type of DataUnit.
	 * @param name DataUnit's name.
	 * @return Created DataUnit.
	 * @throw DataUnitCreateException
	 */	
	public ManagableDataUnit addDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException {
		// check for type changes only for outputs
		if (!isInput) {
			type = checkType(type);
		}
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
		ManagableDataUnit dataUnit = dataUnitFactory.create(type, id, name, directory);
		// add to storage
		dataUnits.add(dataUnit);
		indexes.put(dataUnit, index);
		//
		return dataUnit;
	}
	
	/**
	 * Return access to all stored DataUnits.
	 * @return
	 */
	public List<ManagableDataUnit> getDataUnits() {
		return dataUnits;
	}
}
