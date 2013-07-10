package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.rdf.interfaces.Validator;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 *
 * @author Jiri Tomes
 */
public class SPARQLUpdateValidator implements Validator {

	private String updateQuery;

	private String message;

	public SPARQLUpdateValidator(String updateQuery) {
		this.updateQuery = updateQuery;
		this.message = "No errors - valid query.";
	}

	@Override
	public boolean isQueryValid() {
		boolean isValid = true;

		LocalRDFRepo emptyRepo = LocalRDFRepo.createLocalRepo("");

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

		return isValid;
	}

	@Override
	public String getErrorMessage() {
		return message;
	}
}
