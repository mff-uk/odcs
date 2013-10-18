package cz.cuni.mff.xrg.odcs.backend.data;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;

/**
 * Reconstruct context and delete all created dataUnits.
 * 
 * @author Petyr
 * 
 */
public class ContextDeleter {

	private static final Logger LOG = LoggerFactory.getLogger(ContextDeleter.class);
	
	private DataUnitFactory dataUnitFactory;
	
	private AppConfig appConfig;
	
	public ContextDeleter(DataUnitFactory dataUnitFactory, AppConfig appConfig) {
		this.dataUnitFactory = dataUnitFactory;
		this.dataUnitFactory = dataUnitFactory;
	}
	
	/**
	 * Delete all dataUnits of given execution.
	 * 
	 * @param execution
	 */
	public void deleteContext(PipelineExecution execution) {
		LOG.info("Deleting context for: {}", execution.getPipeline().getName());
		ExecutionContextInfo context = execution.getContext();
		if (context == null) {
			// nothing to delete
			return;
		}

		Set<DPUInstanceRecord> instances = context.getDPUIndexes();
		for (DPUInstanceRecord dpu : instances) {
			// for each DPU
			ProcessingUnitInfo dpuInfo = context.getDPUInfo(dpu);
			deleteContext(context, dpu, dpuInfo);
		}

	}

	/**
	 * Delete dataUnits related to single DPU.
	 * 
	 * @param context
	 * @param dpuInstance
	 * @param dpuInfo
	 */
	private void deleteContext(ExecutionContextInfo context,
			DPUInstanceRecord dpuInstance,
			ProcessingUnitInfo dpuInfo) {
		LOG.info("Deleting context for dpu: {}", dpuInstance.getName());
		
		List<DataUnitInfo> dataUnits = dpuInfo.getDataUnits();
		for (DataUnitInfo dataUnitInfo : dataUnits) {
			// we need to construct the DataUnit, create it and then 
			// delete it
			int index = dataUnitInfo.getIndex();
			final DataUnitType type = dataUnitInfo.getType();
			final String id = context.generateDataUnitId(dpuInstance, index);
			final String name = dataUnitInfo.getName();
			
			final File rootDir = new File(appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
			final File directory = new File(rootDir, context.getDataUnitTmpPath(dpuInstance, index));
			// create instance
			
			try {
				ManagableDataUnit dataUnit = dataUnitFactory.create(type, id, name, directory);
				// delete data .. 
				dataUnit.delete();
			} catch (DataUnitCreateException e) {
				LOG.warn("Failed to reinstantiate dataUnit", e);
			}
		}
	}

}
