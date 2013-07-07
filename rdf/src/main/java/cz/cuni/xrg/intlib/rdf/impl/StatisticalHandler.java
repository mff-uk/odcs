package cz.cuni.xrg.intlib.rdf.impl;

import java.util.ArrayList;
import java.util.Collection;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 *
 * Class allow monitor better the extraction process - information about loaded
 * triples and detail log.
 *
 */
public class StatisticalHandler extends RDFHandlerBase {

	private Collection<Statement> statements = new ArrayList<>();

	private long tripleCount = 0;

	private Logger logger = LoggerFactory.getLogger(StatisticalHandler.class);

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		try {
			super.handleStatement(st);
			statements.add(st);
			tripleCount++;
			
			logger.debug(
					"Added next triple n."+tripleCount+": subject:" + st.getSubject() + " predicate:"
					+ st.getPredicate() + " object:" + st.getObject());
			

		} catch (RDFHandlerException e) {
			logger.error("Error by adding next triple");
			logger.debug(
					"Successfully added total:" + tripleCount + " RDF triples");
			throw new RDFHandlerException(e.getMessage(), e);
		}
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		try {
			super.startRDF();
			logger.debug("Starting parsing - SUCCESSFUL");
		} catch (RDFHandlerException e) {
			logger.debug("Starting parsing - FAIL");
			throw new RDFHandlerException(e.getMessage(), e);
		}
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			super.endRDF();
			logger.debug("Ending parsing - SUCCESSFUL");
			logger.debug("TOTAL ADDED:" + tripleCount + " triples");
		} catch (RDFHandlerException e) {
			logger.debug("Ending parsing - FAIL");
			logger.debug("TOTAL ADDED:" + tripleCount + " triples");
			throw new RDFHandlerException(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @return count of extracted RDF triples
	 */
	public long getTripleCount() {
		return tripleCount;
	}

	/**
	 *
	 * @return collection of all extracted RDF triples
	 */
	public Collection<Statement> getStatements() {
		return statements;
	}

	/**
	 * Clean collection of contains statemensts and reset counter for RDF
	 * triples.
	 */
	public void clear() {
		statements.clear();
		tripleCount = 0;
	}
}