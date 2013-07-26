package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.rdf.enums.SPARQLQueryType;
import cz.cuni.xrg.intlib.rdf.interfaces.Validator;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Class responsible to find out, if sparql queries are valid or not.
 *
 * @author Jiri Tomes
 *
 */
public class SPARQLQueryValidator implements Validator {

	private String query;

	private String message;

	private SPARQLQueryType requiredType;

	boolean requireSPARQLType;

	public SPARQLQueryValidator(String query) {
		this.query = query;
		this.message = "No errors - valid query.";
		this.requireSPARQLType = false;
		this.requiredType = SPARQLQueryType.UNKNOWN;

	}

	public SPARQLQueryValidator(String query, SPARQLQueryType requiredType) {
		this.query = query;
		this.message = "No errors - valid query.";
		this.requireSPARQLType = true;
		this.requiredType = requiredType;
	}

	private boolean isSameType(SPARQLQueryType type1, SPARQLQueryType type2) {
		return type1.equals(type2);
	}

	private SPARQLQueryType getSPARQLQueryType() {
		String myQyery = query.toLowerCase().replaceAll(" ", "");

		SPARQLQueryType myType = SPARQLQueryType.UNKNOWN;

		if (myQyery.startsWith("select")) {
			myType = SPARQLQueryType.SELECT;
		} else if (myQyery.startsWith("construct")) {
			myType = SPARQLQueryType.CONSTRUCT;
		}
		return myType;
	}

	/**
	 * Method for detection right syntax of sparql query.
	 *
	 * @return true, if query is valid, false otherwise.
	 */
	@Override
	public boolean isQueryValid() {

		if (requireSPARQLType) {
			SPARQLQueryType myType = getSPARQLQueryType();
			if (!isSameType(myType, requiredType)) {
				message = requiredType.toString() + " SPARQL type is required.";
				return false;
			}
		}

		boolean isValid = true;

		LocalRDFRepo emptyRepo = LocalRDFRepo.createLocalRepo("");
		Repository repository = emptyRepo.getDataRepository();

		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();

			Query myQuery = connection.prepareQuery(QueryLanguage.SPARQL, query);


		} catch (MalformedQueryException e) {
			message = e.getCause().getMessage();
			isValid = false;

		} catch (RepositoryException ex) {

			throw new RuntimeException("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					throw new RuntimeException(
							"Failed to close connection to RDF repository while querying."
							+ ex.getMessage(), ex);
				}
			}
		}

		emptyRepo.release();

		return isValid;

	}

	/**
	 * String message describes syntax problem of SPARQL query.
	 *
	 * @return empty string, when SPARQL query is valid
	 */
	@Override
	public String getErrorMessage() {
		return message;
	}
}
