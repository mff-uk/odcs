package cz.cuni.mff.xrg.odcs.rdf.validators;

import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.query.utils.QueryPart;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryValidator;

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
public class SPARQLQueryValidator implements QueryValidator {

	private String query;

	private String message;

	private SPARQLQueryType requiredType;

	boolean requireSPARQLType;

	/**
	 * Create new instance of {@link SPARQLQueryValidator} with given SPARQL
	 * query you can validate.
	 *
	 * For SPARQL update query use {@link SPARQLUpdateValidator}.
	 *
	 * @param query SPARQL query you can validate
	 */
	public SPARQLQueryValidator(String query) {
		this.query = query;
		this.message = "";
		this.requireSPARQLType = false;
		this.requiredType = SPARQLQueryType.UNKNOWN;

	}

	/**
	 * Create new instance of {@link SPARQLQueryValidator} with given SPARQL
	 * query and it´s required {@link SPARQLQueryType} you can validate.
	 *
	 * For SPARQL update query use {@link SPARQLUpdateValidator}.
	 *
	 * @param query        SPARQL query you can validate
	 * @param requiredType Type of SPARQL query that is required to by same as
	 *                     in given query
	 */
	public SPARQLQueryValidator(String query, SPARQLQueryType requiredType) {
		this.query = query;
		this.message = "";
		this.requireSPARQLType = true;
		this.requiredType = requiredType;
	}

	private boolean isSameType(SPARQLQueryType type1, SPARQLQueryType type2) {
		return type1.equals(type2);
	}

	/**
	 * If query has required type returns true if type of the given query and
	 * required type are the same, false otherwise. If no required query type is
	 * set returns true.
	 *
	 * @return true if query has required type and type of the given query and
	 *         required type are the same, false otherwise. If no required query
	 *         type is set returns true.
	 */
	public boolean hasSameType() {
		if (requireSPARQLType) {
			QueryPart queryPart = new QueryPart(query);
			SPARQLQueryType queryType = queryPart.getSPARQLQueryType();
			if (isSameType(queryType, requiredType)) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Method for detection right syntax of sparql query.
	 *
	 * @return true, if query is valid, false otherwise.
	 */
	@Override
	public boolean isQueryValid() {

		if (requireSPARQLType) {
			QueryPart queryPart = new QueryPart(query);

			SPARQLQueryType myType = queryPart.getSPARQLQueryType();
			if (!isSameType(myType, requiredType)) {
				message = requiredType.toString() + " Unsupported SPARQL 1.1 query - the DPU expects SELECT/CONSTRUCT";
				return false;
			}
		}

		boolean isValid = true;

		LocalRDFRepo emptyRepo = RDFDataUnitFactory.createLocalRDFRepo("");
		Repository repository = emptyRepo.getDataRepository();

		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();

			connection.prepareQuery(QueryLanguage.SPARQL, query);
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

		emptyRepo.delete();

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
