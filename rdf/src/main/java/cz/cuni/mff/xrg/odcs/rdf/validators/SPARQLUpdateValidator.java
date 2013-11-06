package cz.cuni.mff.xrg.odcs.rdf.validators;

import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryValidator;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Class responsible to find out, if sparql update queries are valid or not. It
 * using very often as query in transformer.
 *
 * @author Jiri Tomes
 */
public class SPARQLUpdateValidator implements QueryValidator {

	private String updateQuery;

	private String message;

	public SPARQLUpdateValidator(String updateQuery) {
		this.updateQuery = updateQuery;
		this.message = "";
	}

	/**
	 * Method for detection right syntax of query.
	 *
	 * @return true, if query is valid, false otherwise.
	 */
	@Override
	public boolean isQueryValid() {
		boolean isValid = true;

		LocalRDFRepo emptyRepo = RDFDataUnitFactory.createLocalRDFRepo("");

		RepositoryConnection connection = null;
		try {
			connection = emptyRepo.getDataRepository().getConnection();

			connection.prepareUpdate(QueryLanguage.SPARQL,
					updateQuery);

		} catch (MalformedQueryException e) {
			message = e.getCause().getMessage();
			isValid = false;

		} catch (RepositoryException ex) {
			throw new RuntimeException(
					"Connection to repository is not available. "
					+ ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					throw new RuntimeException(ex.getMessage(), ex);
				}
			}
		}

		emptyRepo.delete();

		return isValid;
	}

	/**
	 * String message describes syntax problem of validation query.
	 *
	 * @return empty string, when query is valid.
	 */
	@Override
	public String getErrorMessage() {
		return message;
	}
}
