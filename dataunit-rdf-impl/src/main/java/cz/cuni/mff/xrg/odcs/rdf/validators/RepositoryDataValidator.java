package cz.cuni.mff.xrg.odcs.rdf.validators;

import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;
import cz.cuni.mff.xrg.odcs.rdf.help.TripleProblem;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.DataValidator;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFRepo;
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
 * Basic idea of RDF data validation :
 *
 * All RDF data saved in {@link #input} instance are load to temp file. Than
 * thanks {@link StatisticalHandler} and {@link RDFParser} are loaded to target
 * {@link RDFDataUnit} only validate data and it´s created string report
 * {@link #message} with finding problems(no valid data) as
 * {@link #findedProblems}. This defined target {@link RDFDataUnit} is more
 * described in method {@link #getGoalRepository()}. If target is
 * {@link #output} then valid data are loaded direct into {@link #output}, if
 * it´s instance of {@link LocalRDFRepo} than is destroyed after validation
 * ending proccess - execute method {@link #areDataValid().
 * }
 *
 * @author Jiri Tomes
 */
public class RepositoryDataValidator implements DataValidator {

	private static Logger logger = Logger.getLogger(
			RepositoryDataValidator.class);

	private ManagableRdfDataUnit input;

	private ManagableRdfDataUnit output;

	private String message;

	private List<TripleProblem> findedProblems;

	/**
	 * Create new instance of {@link RepositoryDataValidator} that check data
	 * for given input.
	 *
	 * @param input source from where are data checked if are valid.
	 */
	public RepositoryDataValidator(RDFDataUnit input) {
		this.input = (ManagableRdfDataUnit)input;
		this.output = null;
		this.message = "";
		this.findedProblems = new ArrayList<>();
	}

	/**
	 * Create new instance of {@link RepositoryDataValidator} that check data
	 * for given input and valid data store to output.
	 *
	 * @param input  source from where are data checked if are valid.
	 * @param output target wher are valid data stored.
	 */
	public RepositoryDataValidator(RDFDataUnit input, RDFDataUnit output) {
		this.input = (ManagableRdfDataUnit)input;
		this.output = (ManagableRdfDataUnit)output;
		this.message = "";
		this.findedProblems = new ArrayList<>();
	}

	/**
	 * If is defined output {@link RDFDataUnit} where to load RDF data than
	 * return this {@link RDFDataUnit} instance, otherwise it´s created and
	 * return new instance {@link RDFDataUnit } as implementation of
	 * {@link LocalRDFRepo} which is used only for validation process and it´s
	 * destroyed after data validation.
	 *
	 * See method {@link #areDataValid()} for more info.
	 *
	 * @return instance of {@link RDFDataUnit} need for creating report.
	 */
	private ManagableRdfDataUnit getGoalRepository() {
		if (hasOutput()) {
			return output;
		} else {
			ManagableRdfDataUnit tempRepo = RDFDataUnitFactory.createLocalRDFRepo(
					"tempRepo");

			return tempRepo;
		}
	}

	/**
	 * If is set output as {@link RDFDataUnit} instance where to data add or
	 * not.
	 */
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
			ManagableRdfDataUnit goalRepo = null;
			try {
				tempFile = File.createTempFile("temp", "file");

				input.loadToFile(tempFile.getAbsolutePath(), RDFFormatType.N3,
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

	/**
	 * String message describes syntax problem of data validation.
	 *
	 * @return empty string, when all data are valid.
	 */
	@Override
	public String getErrorMessage() {
		return message;
	}

	/**
	 * Returns list of {@link TripleProblem} describes invalid triples and its
	 * cause. If all data are valid return empty list.
	 *
	 * @return List of {@link TripleProblem} describes invalid triples and its
	 *         cause. If all data are valid return empty list.
	 */
	@Override
	public List<TripleProblem> getFindedProblems() {
		return findedProblems;
	}
}
