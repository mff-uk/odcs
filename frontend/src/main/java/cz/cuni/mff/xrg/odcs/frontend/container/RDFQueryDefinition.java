package cz.cuni.mff.xrg.odcs.frontend.container;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;

/**
 * Modification of {@link LazyQueryDefinition} to work with RDF queries. 
 * All data needed for creating query are supplied in constructor.
 *
 * @author Bogo
 */
public class RDFQueryDefinition extends LazyQueryDefinition {

	private String query;
	private ExecutionContextInfo context;
	private DPUInstanceRecord dpu;
	private DataUnitInfo dataUnitInfo;

	public RDFQueryDefinition(int batchSize, String propertyId, String query, ExecutionContextInfo context, DPUInstanceRecord dpu, DataUnitInfo dataUnitInfo) {
		super(true, batchSize, propertyId);
		this.query = query;
		this.context = context;
		this.dpu = dpu;
		this.dataUnitInfo = dataUnitInfo;
	}

	String getBaseQuery() {
		return query;
	}

	ExecutionContextInfo getContext() {
		return context;
	}

	DPUInstanceRecord getDpu() {
		return dpu;
	}

	DataUnitInfo getDataUnit() {
		return dataUnitInfo;
	}
}
