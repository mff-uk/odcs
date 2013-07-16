package cz.cuni.xrg.intlib.frontend.browser;

import java.io.File;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;

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
	 * @param execution Respective PipelineExecution.
	 * @param dpuInstance Owner of DataUnit.
	 * @param dataUnitIndex Index of data unit.
	 * @return Browser or null if there is no browser for given type.
	 * @throws DataUnitNotFoundException
	 * @throws BrowserInitFailedException
	 */
	public static DataUnitBrowser getBrowser(
			ExecutionContextInfo context, PipelineExecution execution, 
			DPUInstanceRecord dpuInstance, DataUnitInfo info)
		throws DataUnitNotFoundException, BrowserInitFailedException{
		// get type and directory
		
		if (info == null) {
			// the context doesn't exist
			throw new DataUnitNotFoundException();
		}

		// 
		String dataUnitId = context.generateDataUnitId(
				context.generateDPUId(execution.getId(), dpuInstance.getId()), info.getIndex());
		
		switch(info.getType()) {
		case RDF_Local:		
			LocalRdfBrowser localRdfBrowser = new LocalRdfBrowser();
			try {
				// get storage directory for DataUnit
				File dpuStorage = context.getDataUnitStorage(dpuInstance, info.getIndex());
				// load data
				localRdfBrowser.loadDataUnit(dpuStorage, dataUnitId);
			} catch (Exception e) {
				throw new BrowserInitFailedException(e);
			}
			return localRdfBrowser;
		case RDF_Virtuoso:
			VirtuosoRdfBrowser virtuosoRdfBrowser = new VirtuosoRdfBrowser();
			try {
				// get storage directory for DataUnit
				File dpuStorage = context.getDataUnitStorage(dpuInstance, info.getIndex());
				// load data
				virtuosoRdfBrowser.loadDataUnit(dpuStorage, dataUnitId);
			} catch (Exception e) {
				throw new BrowserInitFailedException(e);
			}
			return virtuosoRdfBrowser;			
		default:
			return null;
		}
	}

}
