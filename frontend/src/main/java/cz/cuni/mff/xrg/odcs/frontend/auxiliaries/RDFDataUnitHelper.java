package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container.Filter;
import com.vaadin.ui.UI;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.virtuoso.VirtuosoRDFDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DpuContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionInfo;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.container.rdf.RDFRegexFilter;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryFilterManager;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.RegexFilter;
import cz.cuni.mff.xrg.odcs.rdf.repositories.GraphUrl;

/**
 * Helper for RDF DataUnits.
 *
 * @author Petyr
 * @author Bogo
 *
 */
public class RDFDataUnitHelper {

	private static final Logger LOG = LoggerFactory.getLogger(
			RDFDataUnitHelper.class);
	
	/**
	 * Return repository for specified RDF DataUnit.
	 *
	 * @param executionInfo The pipelineExecution context.
	 * @param dpuInstance   Owner of DataUnit.
	 * @param info
	 * @return Repository or null if there is no browser for given type.
	 *
	 */
	public static ManagableRdfDataUnit getRepository(ExecutionInfo executionInfo,
			DPUInstanceRecord dpuInstance, DataUnitInfo info) {

		// get type and directory
		if (info == null) {
			// the context doesn't exist
			return null;
		}

		// 
		if (executionInfo == null) {
			LOG.error("executionInfo is null!");			
			return null;
		}
		
		DpuContextInfo dpuInfo = executionInfo.dpu(dpuInstance);
		if(dpuInfo == null) {
			LOG.error("DPU info is null!");
			return null;
		}
		String dataUnitId = dpuInfo.createId(info.getIndex());

		switch (info.getType()) {
			case RDF:
				try {
					RDFDataUnitFactory rdfDataUnitFactory =((AppEntry) UI.getCurrent()).getBean(
							RDFDataUnitFactory.class);
					
					String namedGraph = GraphUrl.translateDataUnitId(dataUnitId);

					ManagableRdfDataUnit repository =
							rdfDataUnitFactory.create(info.getName(), namedGraph);
							
					return repository;

				} catch (RuntimeException e) {
					LOG.error("Error", e);
					return null;
				}


			default:
				return null;
		}

	}

	/**
	 * Filter RDF query.
	 * 
	 * @param query Query to filter.
	 * @param filters Filters to apply.
	 * @return Filtered query.
	 */
	public static String filterRDFQuery(String query, Collection<Filter> filters) {
		if (filters == null) {
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
