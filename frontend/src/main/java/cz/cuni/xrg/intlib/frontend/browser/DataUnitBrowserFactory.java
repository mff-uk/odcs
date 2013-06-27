package cz.cuni.xrg.intlib.frontend.browser;

import java.io.File;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;

/**
 * Factory for DataUnitBrowsers.
 *
 * @author Petyr
 *
 */
public class DataUnitBrowserFactory {

	/**
	 * Return browser for specified DataUnit.
	 * @param context The pipelineExecution context.
	 * @param dpuInstance Owner of DataUnit.
	 * @param isInput True if use inputs.
	 * @param dataUnitIndex Index of data unit.
	 * @return
	 * @throws DataUnitNotFoundException
	 * @throws BrowserInitFailedException
	 */
	public static DataUnitBrowser getBrowser(
			ExecutionContextInfo context, DPUInstanceRecord dpuInstance, 
			boolean isInput, int dataUnitIndex, String dumpDirName)
		throws DataUnitNotFoundException, BrowserInitFailedException{
		// get type and directory
		DataUnitInfo info = context.getDataUnitInfo(dpuInstance, dataUnitIndex);
		
		if (info == null) {
			// the context doesn't exist
			throw new DataUnitNotFoundException();
		}
		File directory = info.getDirectory();
		// TODO Petyr : return some component like "The data unit context can't be read ... " ?
		switch(info.getType()) {
		case RDF_Local:
			DataUnitBrowser localRdfBrowser = new LocalRdfBrowser();
			try {
				localRdfBrowser.loadDataUnit(directory, dumpDirName);
			} catch (Exception e) {
				throw new BrowserInitFailedException(e);
			}
			return localRdfBrowser;
		case RDF_Virtuoso:
			return null;
		default:
			return null;
		}
	}

	// TODO Petyr provide function like getBrowser(DataUnitInfo .. )
}
