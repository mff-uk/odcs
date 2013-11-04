package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.Validator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	/*
	 * If query has required type returns true if type of the given query and required type are the same, false otherwise. 
	 * If no required query type is set returns true. 
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

	private static String getQueryForCaseSELECT_COUNT(String myQuery) {
		String countRegex = "count([\\s]+)?\\([\\s\\w-_\\?\\*]+\\)(([\\s]+)?as[\\s]?\\?[\\w-_\\*]+)?";
		Pattern pattern = Pattern.compile(countRegex);
		Matcher countMatcher = pattern.matcher(myQuery);

		boolean hasResult = countMatcher.find();

		String result = myQuery;

		while (hasResult) {

			String nextCount = myQuery.substring(countMatcher.start(),
					countMatcher.end());

			int start = nextCount.indexOf("(") + 1;
			int end = nextCount.lastIndexOf(")");

			boolean hasNext = (start < end) && (start != -1);

			if (hasNext) {
				String nextCountReplace = nextCount.substring(
						start, end);

				result = result.replace(nextCount, nextCountReplace);

			}

			hasResult = countMatcher.find();

		}

		return result;
	}

	private String getQueryForExtendedSPARQL() {
		QueryPart queryPart = new QueryPart(query);

		if (isSameType(queryPart.getSPARQLQueryType(), SPARQLQueryType.SELECT)) {

			String result = getQueryForCaseSELECT_COUNT(query);

			return result;
		} else {
			return query;
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

		String extendedQuery = getQueryForExtendedSPARQL();

		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();

			Query myQuery = connection.prepareQuery(QueryLanguage.SPARQL,
					extendedQuery);


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
