package cz.cuni.mff.xrg.odcs.backend.context;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;

/**
 * Delete and close data for given {@link Context} if data are not loaded then
 * load them first (use {@link ContextRestorer} and then delete them. 
 * Also delete related content from {@link ExecutionContextInfo}. The
 * context is in same state as if newly created (empty).
 * 
 * @author Petyr
 * 
 */
class ContextDeleter {

	private final static Logger LOG = LoggerFactory
			.getLogger(ContextDeleter.class);

	@Autowired
	private AppConfig appConfig;

	/**
	 * @see ContextDeleter
	 * 
	 * @param context
	 * @param preserveContextInfo
	 */
	public void delete(Context context, boolean preserveContextInfo) {
		// delete data
		delete(context.getInputsManager());
		delete(context.getOutputsManager());

		// should we delete directories ?
		if (context.isDebugging()) {
			// debugging mode .. do not delete nothing
			return;
		}
		// delete all
		final File backendWorkingDir = new File(
				appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
		final ExecutionContextInfo contextInfo = context.getContextInfo();
		final DPUInstanceRecord dpu = context.getDPU();

		final File workingDir = context.getWorkingDir();
		deleteDirectory(workingDir);
		
		// DataUnits storage directory
		final File storagePath = new File(backendWorkingDir,
				contextInfo.getDataUnitRootStoragePath(dpu));
		deleteDirectory(storagePath);

		// DataUnit temporally directory
		final File tmpPath = new File(backendWorkingDir,
				contextInfo.getDataUnitRootTmpPath(dpu));
		deleteDirectory(tmpPath);
		
		// delete execution context info
		if (preserveContextInfo) {
			// do not delete context info
		} else {
			// delete context info
			deleteContextInfo(contextInfo);
		}
	}

	/**
	 * Delete {@link ManagableDataUnit} from given {@link DataUnitManager} and
	 * delete record about them.
	 * 
	 * @param dataUnitManage
	 */
	private void delete(DataUnitManager dataUnitManage) {
		for (ManagableDataUnit dataUnit : dataUnitManage.getDataUnits()) {
			dataUnit.delete();
		}
		dataUnitManage.getDataUnits().clear();
	}

	/**
	 * Delete directory if exist. If error occur is logged and silently ignored.
	 * 
	 * @param directory
	 */
	private void deleteDirectory(File directory) {
		if (directory.exists()) {
			try {
				FileUtils.deleteDirectory(directory);
			} catch (IOException e) {
				LOG.error("Can't delete directory {}", directory.toString(), e);
			}
		}
	}
	
	/**
	 * Delete data from given {@link ExecutionContextInfo}.
	 * @param conteInfo
	 */
	private void deleteContextInfo(ExecutionContextInfo contexInfo) {
		contexInfo.reset();
	}

}
