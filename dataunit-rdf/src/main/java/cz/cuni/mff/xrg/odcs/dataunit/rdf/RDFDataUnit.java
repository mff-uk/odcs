package cz.cuni.mff.xrg.odcs.dataunit.rdf;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.rdf.model.Graph;
import cz.cuni.mff.xrg.odcs.dataunit.rdf.policy.ErrorHandler;
import cz.cuni.mff.xrg.odcs.dataunit.rdf.policy.FlowController;

/**
 * Implementation of {@link DataUnit} for working with RDF data.
 * 
 * @author Petyr
 */
public interface RDFDataUnit extends DataUnit {

	/**
	 * Provide access to {@link RDFDataUnit}'s functionality configuration.
	 */
	public interface Options {
	
		void setErrorHandler(ErrorHandler handler);
	
		ErrorHandler getErrorHandler(); 
		
		void setFlowController(FlowController contoller);
		
		FlowController getFlowController();
		
	}
	
	/**
	 * @return main graph
	 */
	Graph getgraph();

	Options getOptions();
}
