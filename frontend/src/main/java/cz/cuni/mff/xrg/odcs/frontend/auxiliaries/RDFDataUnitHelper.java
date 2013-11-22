package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import com.vaadin.data.Container.Filter;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionInfo;
import static cz.cuni.mff.xrg.odcs.commons.data.DataUnitType.RDF_Local;
import static cz.cuni.mff.xrg.odcs.commons.data.DataUnitType.RDF_Virtuoso;
import cz.cuni.mff.xrg.odcs.frontend.container.rdf.RDFRegexFilter;
import cz.cuni.mff.xrg.odcs.rdf.GraphUrl;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.repositories.VirtuosoRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryFilterManager;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.RegexFilter;
import java.io.File;
import java.util.Collection;

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
	public static RDFDataUnit getRepository(ExecutionInfo executionInfo,
			DPUInstanceRecord dpuInstance, DataUnitInfo info) {

		// get type and directory
		if (info == null) {
			// the context doesn't exist
			return null;
		}

		// 
		String dataUnitId = executionInfo.dpu(dpuInstance).createId(info.getIndex());


		switch (info.getType()) {
			case RDF_Local:
				try {
					// storage directory
					File dpuStorage =
							new File(App.getAppConfig().getString(
							ConfigProperty.GENERAL_WORKINGDIR),
							executionInfo.dpu(dpuInstance).getStoragePath(info.getIndex()));
					LocalRDFRepo repository = RDFDataUnitFactory
							.createLocalRDFRepo("");
					// load data from storage
					repository.load(dpuStorage);
					return repository;

				} catch (RuntimeException e) {
					return null;
				}

			case RDF_Virtuoso:
				AppConfig appConfig = App.getAppConfig().getSubConfiguration(
						ConfigProperty.VIRTUOSO_RDF);

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
						appConfig.getProperties());

				return virtuosoRepository;

			default:
				return null;
		}

	}

	public static String filterRDFQuery(String query, Collection<Filter> filters) {
		if(filters == null) {
			return query;
		}
		
		QueryFilterManager filterManager = new QueryFilterManager(query);
		for (Filter filter : filters) {
			if (filter.getClass() == RDFRegexFilter.class) {
				RDFRegexFilter rdfRegexFilter = (RDFRegexFilter) filter;
				RegexFilter rf = new RegexFilter(rdfRegexFilter.getColumnName(),
						rdfRegexFilter.getRegex());
				filterManager.addFilter(rf);
			}
		}
		return filterManager.getFilteredQuery();
	}
}
