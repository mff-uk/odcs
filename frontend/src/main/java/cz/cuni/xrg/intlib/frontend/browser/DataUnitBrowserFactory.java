package cz.cuni.xrg.intlib.frontend.browser;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import java.io.File;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.DataUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.rdf.GraphUrl;
import cz.cuni.xrg.intlib.rdf.data.RDFDataUnitFactory;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

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
	 * @param context       The pipelineExecution context.
	 * @param execution     Respective PipelineExecution.
	 * @param dpuInstance   Owner of DataUnit.
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
		String dataUnitId =
				context.generateDataUnitId(dpuInstance, info.getIndex());
		// storage directory
		File dpuStorage =
				new File(App.getAppConfig().getString(
				ConfigProperty.GENERAL_WORKINGDIR),
				context.getDataUnitStoragePath(dpuInstance, info.getIndex()));

		switch (info.getType()) {
			case RDF_Local:
				LocalRdfBrowser localRdfBrowser = new LocalRdfBrowser();
				try {
					// load data
					localRdfBrowser.loadDataUnit(dpuStorage, dataUnitId);
				} catch (Exception e) {
					throw new BrowserInitFailedException(e);
				}
				return localRdfBrowser;
			case RDF_Virtuoso:
				VirtuosoRdfBrowser virtuosoRdfBrowser = new VirtuosoRdfBrowser();
				try {
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

	// TODO Bohuslav: Why is this method here? It's almost the same as the one above.  
	/**
	 * Return repository for specified DataUnit.
	 *
	 * @param context       The pipelineExecution context.
	 * @param execution     Respective PipelineExecution.
	 * @param dpuInstance   Owner of DataUnit.
	 * @param dataUnitIndex Index of data unit.
	 * @return Repository or null if there is no browser for given type.
	 *
	 */
	@Deprecated
	public static RDFDataUnit getRepository(ExecutionContextInfo context,
			PipelineExecution execution,
			DPUInstanceRecord dpuInstance, DataUnitInfo info) {

		// get type and directory
		if (info == null) {
			// the context doesn't exist
			return null;
		}

		// 
		String dataUnitId =
				context.generateDataUnitId(dpuInstance, info.getIndex());
		// storage directory
		File dpuStorage =
				new File(App.getAppConfig().getString(
				ConfigProperty.GENERAL_WORKINGDIR),
				context.getDataUnitStoragePath(dpuInstance, info.getIndex()));

		switch (info.getType()) {
			case RDF_Local:
				try {
					LocalRDFRepo repository = RDFDataUnitFactory
							.createLocalRDFRepo("");
					// load data from storage
					repository.load(dpuStorage);
					return repository;

				} catch (RuntimeException e) {
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
						appConfig.getString(
						ConfigProperty.VIRTUOSO_DEFAULT_GRAPH);

				VirtuosoRDFRepo virtuosoRepository = RDFDataUnitFactory
						.createVirtuosoRDFRepo(hostName, port, user, password,
						defautGraph, "");
				virtuosoRepository.setDataGraph(GraphUrl.translateDataUnitId(
						dataUnitId));
				return virtuosoRepository;

			default:
				return null;
		}

	}
}
