package cz.cuni.mff.xrg.odcs.rdf.validator;

import cz.cuni.mff.xrg.odcs.rdf.enums.ParsingConfictType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.help.TripleProblem;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.util.List;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Class responsible for creting RDF report message from given found out
 * problems and save it to report TTL file.
 *
 * @author Jiri Tomes
 */
public class ReportCreator {

	private static Logger logger = Logger.getLogger(
			ReportCreator.class);

	private static final String ODCS_VAL = "http://linked.opendata.cz/ontology/odcs/validation/";

	private static final String EXEC_ERROR = "http://linked.opendata.cz/resource/odcs/internal/pipeline/validation/error/";

	private List<TripleProblem> problems;

	private String reportPrefix;

	public ReportCreator(List<TripleProblem> problems, String graphName) {
		this.problems = problems;
		this.reportPrefix = getReportPrefix(graphName);

	}

	public void makeOutputReport(RDFDataUnit repository) throws RDFException {

		setNamespaces(repository);
		addReports(repository);
	}

	private String getReportPrefix(String graphName) {

		String prefix;

		int index = graphName.indexOf("du/");

		if (index > -1) {
			prefix = graphName.substring(0, index);
		} else {
			prefix = graphName;
		}

		return prefix + "validation/error/";
	}

	private void addReports(RDFDataUnit repository) {

		int count = 0;

		for (TripleProblem next : problems) {

			Statement st = next.getStatement();

			String sub = st.getSubject().toString();
			String pred = st.getPredicate().toString();
			String obj = st.getObject().toString();

			ParsingConfictType conflictType = next.getConflictType();
			String message = next.getMessage();
			int line = next.getLine();

			count++;

			repository.addTriple(getSubject(count), new URIImpl("rdf:type"),
					new URIImpl(ODCS_VAL + conflictType.toString()));

			repository.addTriple(getSubject(count), getPredicate("subject"),
					getObject(sub));
			repository.addTriple(getSubject(count), getPredicate("predicate"),
					getObject(pred));
			repository.addTriple(getSubject(count), getPredicate("object"),
					getObject(obj));

			repository.addTriple(getSubject(count), getPredicate("reason"),
					getObject(message));
			repository
					.addTriple(getSubject(count), getPredicate("sourceLine"),
					getObject(line));

		}
	}

	private void setNamespaces(RDFDataUnit repository) throws RDFException {
		try {

			RepositoryConnection connection = repository.getConnection();

			connection.setNamespace("rdf",
					"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			connection.setNamespace("xsd",
					"http://www.w3.org/2001/XMLSchema#");
			connection.setNamespace("odcs-val", ODCS_VAL);
			connection.setNamespace("exec-error", EXEC_ERROR);

		} catch (RepositoryException e) {
			final String message = "Not possible to set namespace"
					+ e.getMessage();
			logger.debug(message);
			throw new RDFException(message);
		}
	}

	private Resource getSubject(int count) {
		return new URIImpl(reportPrefix + String.valueOf(count));
	}

	private URI getPredicate(String text) {
		return new URIImpl(ODCS_VAL + text);
	}

	private Value getObject(String text) {
		return new LiteralImpl(text);
	}

	private Value getObject(int number) {
		URI datatype = new URIImpl("xsd:integer");
		return new LiteralImpl(String.valueOf(number), datatype);
	}
}
