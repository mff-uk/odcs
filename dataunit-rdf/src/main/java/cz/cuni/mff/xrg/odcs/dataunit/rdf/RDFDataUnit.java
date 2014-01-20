package cz.cuni.mff.xrg.odcs.dataunit.rdf;

import cz.cuni.mff.xrg.odcs.dataunit.rdf.data.Graph;
import cz.cuni.mff.xrg.odcs.dataunit.rdf.data.Triple;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.rdf.policy.ErrorHandler;
import cz.cuni.mff.xrg.odcs.dataunit.rdf.policy.ProgramFlowContoller;
import java.io.File;
import java.util.Collection;

/**
 * {#link DataUnit} implementation for RDF data.
 * @author Petyr
 */
public interface RDFDataUnit extends DataUnit, Collection<Triple> {
	
	/**
	 * Return factory that can be used to create RDF's data objects.
	 * 
	 * @return 
	 */
	RDFFactory getFactory();
	
	/**
	 * Return currently used {@link ProgramFlowContoller} by 
	 * this {@link RDFDataUnit}.
	 * 
	 * @return 
	 */
	ProgramFlowContoller getFlowController();
	
	/**
	 * Set new {@link ProgramFlowContoller} used to control 
	 * the program flow in {@link RDFDataUnit}.
	 * 
	 * @param newController 
	 */
	void setFlowController(ProgramFlowContoller newController);
	
	/**
	 * Return current handler used by {@link RDFDataUnit}.
	 * 
	 * @return 
	 */
	ErrorHandler getHandler();
	
	/**
	 * Set new handler for {@link RDFDataUnit}
	 * 
	 * @param newHandler 
	 */
	void setHandler(ErrorHandler newHandler);
	
	/**
	 * Extract triples from given file. Used {@link RDFFileType} is
	 * determined based on file extension. In case of error
	 * the {@link RDFErrorHandler} is used to sanitize the problem.
	 * 
	 * @param file
	 * @throws RDFException 
	 */
	void extract(File file) throws RDFException;
	
	/**
	 * Extract triples from given file. In case of error
	 * the {@link RDFErrorHandler} is used to sanitize the problem.
	 * 
	 * @param file
	 * @param type
	 * @throws RDFException 
	 */
	void extract(File file, RDFFileType type) throws RDFException;
	
	/**
	 * Load the content of this {@link RDFDataUnit} into given file.
	 * If the file exists then the load method fails.
	 * 
	 * @param file
	 * @param type
	 * @throws RDFException 
	 */
	void load(File file, RDFFileType type) throws RDFException;
	
	/**
	 * Execute given query and return {@link Graph} that represent
	 * the result. The {@link Graph} is released together with 
	 * this {@link RDFDataUnit}.
	 * 
	 * @param query
	 * @return Null is never returned.
	 * @throws RDFException 
	 */
	Graph executeQuery(String query) throws RDFException;
	
}