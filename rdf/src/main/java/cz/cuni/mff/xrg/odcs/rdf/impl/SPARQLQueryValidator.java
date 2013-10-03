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

	private String getQueryWithoutPrexices(String myQuery) {

		String regex = ".*prefix\\s+[\\w]+[:]?\\s+[<]?http://[\\w:/\\.#]+[>]?\\s+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(myQuery.toLowerCase());

		boolean hasResult = matcher.find();

		if (hasResult) {

			int index = matcher.end();

			while (matcher.find()) {
				index = matcher.end();
			}

			String result = myQuery.substring(index, myQuery.length())
					.toLowerCase();

			return result;


		}

		return myQuery.trim().toLowerCase();
	}

	private SPARQLQueryType getSPARQLQueryType() {
		String myQyery = getQueryWithoutPrexices(query);

		SPARQLQueryType myType = SPARQLQueryType.UNKNOWN;

		if (myQyery.startsWith("select")) {
			myType = SPARQLQueryType.SELECT;
		} else if (myQyery.startsWith("construct")) {
			myType = SPARQLQueryType.CONSTRUCT;
		}
		return myType;
	}

	/*
	 * If query has required type returns true if type of the given query and required type are the same, false otherwise. 
	 * If no required query type is set returns true. 
	 */
	public boolean hasSameType() {
		if (requireSPARQLType) {
			SPARQLQueryType queryType = getSPARQLQueryType();
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
			SPARQLQueryType myType = getSPARQLQueryType();
			if (!isSameType(myType, requiredType)) {
				message = requiredType.toString() + " SPARQL type is required.";
				return false;
			}
		}

		boolean isValid = true;

		LocalRDFRepo emptyRepo = RDFDataUnitFactory.createLocalRDFRepo("");
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
