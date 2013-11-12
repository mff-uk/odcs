package cz.cuni.mff.xrg.odcs.rdf.validators;

import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;
import cz.cuni.mff.xrg.odcs.rdf.help.TripleProblem;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.DataValidator;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;

/**
 * Find out, if data in RDF repository are valid or not.
 *
 * @author Jiri Tomes
 */
public class RepositoryDataValidator implements DataValidator {

	private static Logger logger = Logger.getLogger(
			RepositoryDataValidator.class);

	private RDFDataUnit input;

	private RDFDataUnit output;

	private String message;

	private List<TripleProblem> findedProblems;

	public RepositoryDataValidator(RDFDataUnit input) {
		this.input = input;
		this.output = null;
		this.message = "";
		this.findedProblems = new ArrayList<>();
	}

	public RepositoryDataValidator(RDFDataUnit input, RDFDataUnit output) {
		this.input = input;
		this.output = output;
		this.message = "";
		this.findedProblems = new ArrayList<>();
	}

	private RDFDataUnit getGoalRepository() {
		if (hasOutput()) {
			return output;
		} else {
			RDFDataUnit tempRepo = RDFDataUnitFactory.createLocalRDFRepo(
					"tempRepo");

			return tempRepo;
		}
	}

	private boolean hasOutput() {
		if (output != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method for detection right syntax of data.
	 *
	 * @return true, if data are valid, false otherwise. If repository has no
	 *         data(is empty) return true.
	 */
	@Override
	public boolean areDataValid() {

		boolean isValid = false;

		if (input.getTripleCount() == 0) {
			isValid = true;
		} else {

			File tempFile = null;
			RDFDataUnit goalRepo = null;
			try {
				tempFile = File.createTempFile("temp", "file");
				input
						.loadToFile(tempFile.getAbsolutePath(), RDFFormatType.N3,
						true, false);

				try (InputStreamReader fileStream = new InputStreamReader(
						new FileInputStream(tempFile), Charset.forName("UTF-8"))) {

					goalRepo = getGoalRepository();

					final StatisticalHandler handler = new StatisticalHandler(
							goalRepo.getConnection(), true);

					handler.setGraphContext(goalRepo.getDataGraph());

					RDFParser parser = Rio.createParser(RDFFormat.N3);
					parser.setRDFHandler(handler);

					ParserConfig config = parser.getParserConfig();

					config.addNonFatalError(
							BasicParserSettings.VERIFY_DATATYPE_VALUES);

					parser.setParserConfig(config);

					parser.setParseErrorListener(new ParseErrorListener() {
						@Override
						public void warning(String msg, int lineNo, int colNo) {
							handler.addWarning(msg, lineNo, colNo);
						}

						@Override
						public void error(String msg, int lineNo, int colNo) {
							handler.addError(msg, lineNo, colNo);
						}

						@Override
						public void fatalError(String msg, int lineNo, int colNo) {
							handler.addError(msg, lineNo, colNo);
						}
					});

					parser.parse(fileStream, "");

					isValid = !handler.hasFindedProblems();
					message = handler.getFindedProblemsAsString();
					findedProblems = handler.getFindedProblems();
				}


			} catch (IOException | CannotOverwriteFileException | RDFException | RepositoryException e) {
				message = e.getMessage();
				logger.error(message);

			} catch (RDFParseException | RDFHandlerException e) {
				message = "Problem with data parsing :" + e.getMessage();
				logger.error(message);
			} finally {
				if (tempFile != null) {
					tempFile.delete();
				}
				if (!hasOutput() && goalRepo != null) {
					goalRepo.delete();
				}
			}
		}

		return isValid;

	}

	@Override
	public String getErrorMessage() {
		return message;
	}

	@Override
	public List<TripleProblem> getFindedProblems() {
		return findedProblems;
	}
}
