package cz.cuni.mff.xrg.odcs.frontend.browser;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import static cz.cuni.mff.xrg.odcs.commons.data.DataUnitType.RDF_Local;
import static cz.cuni.mff.xrg.odcs.commons.data.DataUnitType.RDF_Virtuoso;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.rdf.GraphUrl;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.impl.LocalRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.impl.VirtuosoRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.io.File;

/**
 * Helper for RDF DataUnits.
 *
 * @author Petyr
 * @author Bogo
 *
 */
public class RDFDataUnitHelper {

	/**
	 * Return repository for specified RDF DataUnit.
	 *
	 * @param context The pipelineExecution context.
	 * @param dpuInstance Owner of DataUnit.
	 * @param dataUnitIndex Index of data unit.
	 * @return Repository or null if there is no browser for given type.
	 *
	 */
	public static RDFDataUnit getRepository(ExecutionContextInfo context,
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

				VirtuosoRDFRepo virtuosoRepository = RDFDataUnitFactory
						.createVirtuosoRDFRepo(
						hostName,
						port,
						user,
						password,
						GraphUrl.translateDataUnitId(dataUnitId),
						"",
						App.getApp().getBean(AppConfig.class).getProperties());

				return virtuosoRepository;

			default:
				return null;
		}

	}
}
