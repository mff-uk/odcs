package cz.cuni.xrg.intlib.frontend.browser;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import java.io.File;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import static cz.cuni.xrg.intlib.commons.data.DataUnitType.RDF_Local;
import static cz.cuni.xrg.intlib.commons.data.DataUnitType.RDF_Virtuoso;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

/**
 * Factory for DataUnitBrowsers.
 *
 * @author Petyr
 *
 */
public class DataUnitBrowserFactory {

	/**
	 * Return browser for specified DataUnit.
	 *
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
			throws DataUnitNotFoundException, BrowserInitFailedException {
		// get type and directory

		if (info == null) {
			// the context doesn't exist
			throw new DataUnitNotFoundException();
		}

		// 
		String dataUnitId = context.generateDataUnitId(
				context.generateDPUId(execution.getId(), dpuInstance.getId()), info.getIndex());

		switch (info.getType()) {
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

	/**
	 * Return repository for specified DataUnit.
	 * 
	 * @param context The pipelineExecution context.
	 * @param execution Respective PipelineExecution.
	 * @param dpuInstance Owner of DataUnit.
	 * @param dataUnitIndex Index of data unit.
	 * @return Repository or null if there is no browser for given type.
	 * 
	 */
	public static LocalRDFRepo getRepository(ExecutionContextInfo context, PipelineExecution execution,
			DPUInstanceRecord dpuInstance, DataUnitInfo info) {

		// get type and directory

		if (info == null) {
			// the context doesn't exist
			return null;
		}

		switch (info.getType()) {
			case RDF_Local:
				try {
					// get storage directory for DataUnit
					File dpuStorage = context.getDataUnitStorage(dpuInstance, info.getIndex());
					LocalRDFRepo repository = LocalRDFRepo.createLocalRepo("");
					// load data from stora
					repository.load(dpuStorage);
					return repository;

				} catch (Exception e) {
					return null;
				}
				
			case RDF_Virtuoso:
				AppConfig appConfig = App.getAppConfig();

				// load configuration from appConfig
				final String hostName =
						appConfig.getString(ConfigProperty.VIRTUOSO_HOSTNAME);
				final String port =
						appConfig.getString(ConfigProperty.VIRTUOSO_PORT);
				final String user =
						appConfig.getString(ConfigProperty.VIRTUOSO_USER);
				final String password =
						appConfig.getString(ConfigProperty.VIRTUOSO_PASSWORD);
				final String defautGraph =
						appConfig.getString(ConfigProperty.VIRTUOSO_DEFAULT_GRAPH);

				String dataUnitId = context.generateDataUnitId(
						context.generateDPUId(execution.getId(), dpuInstance.getId()), info.getIndex());

				VirtuosoRDFRepo virtuosoRepository = VirtuosoRDFRepo
						.createVirtuosoRDFRepo(hostName, port, user, password, defautGraph, "");
				virtuosoRepository.setDataGraph("http://" + dataUnitId);
				return virtuosoRepository;
				
			default:
				return null;
		}

	}
}
