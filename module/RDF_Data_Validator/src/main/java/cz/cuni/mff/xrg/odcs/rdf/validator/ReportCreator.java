package cz.cuni.mff.xrg.odcs.rdf.validator;

import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.ParsingConfictType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.help.TripleProblem;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.io.File;
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

	private List<TripleProblem> problems;

	private String graphName;

	private static String fileName = "validationReport.ttl";

	public ReportCreator(List<TripleProblem> problems, String graphName) {
		this.problems = problems;
		this.graphName = graphName;

	}

	public void makeOutputReport(File directory) throws CannotOverwriteFileException, RDFException {

		RDFDataUnit repository = RDFDataUnitFactory.createLocalRDFRepo("report");

		setNamespaces(repository);
		addReports(repository);
		loadToFile(repository, directory);

		repository.delete();

	}

	private void loadToFile(RDFDataUnit repository, File directory) throws CannotOverwriteFileException, RDFException {
		File targetFile = new File(directory, fileName);
		repository.loadToFile(targetFile.getAbsolutePath(), RDFFormatType.AUTO,
				true, true);
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

			repository.addTriple(getSubject(), new URIImpl("rdf:type"),
					new URIImpl("odcs-val:" + conflictType));

			count++;

			repository.addTriple(getSubject(), getPredicate("subject", count),
					getObject(sub));
			repository.addTriple(getSubject(), getPredicate("predicate", count),
					getObject(pred));
			repository.addTriple(getSubject(), getPredicate("object", count),
					getObject(obj));

			repository.addTriple(getSubject(), getPredicate("reason", count),
					getObject(message));
			repository
					.addTriple(getSubject(), getPredicate("sourceLine", count),
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
			connection.setNamespace("odcs-val",
					"http://linked.opendata.cz/ontology/odcs/validation/");
			connection.setNamespace("exec-error",
					"http://linked.opendata.cz/resource/odcs/internal/pipeline/validation/error/");

		} catch (RepositoryException e) {
			final String message = "Not possible to set namespace"
					+ e.getMessage();
			logger.debug(message);
			throw new RDFException(message);
		}
	}

	private Resource getSubject() {
		return new URIImpl(graphName);
	}

	private URI getPredicate(String text, int count) {
		return new URIImpl("odcs-val:" + text + "/" + String.valueOf(count));
	}

	private Value getObject(String text) {
		return new LiteralImpl(text);
	}

	private Value getObject(int number) {
		URI datatype = new URIImpl("xsd:integer");
		return new LiteralImpl(String.valueOf(number), datatype);
	}
}
