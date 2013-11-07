package cz.cuni.mff.xrg.odcs.rdf.validators;

import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.DataValidator;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFRepo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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

	private RDFDataUnit dataUnit;

	private String message;

	public RepositoryDataValidator(RDFDataUnit dataUnit) {
		this.dataUnit = dataUnit;
		this.message = "";
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

		if (dataUnit.getTripleCount() == 0) {
			isValid = true;
		} else {

			File tempFile = null;
			LocalRDFRepo tempRepo = null;
			try {
				tempFile = File.createTempFile("temp", "file");
				dataUnit
						.loadToFile(tempFile.getAbsolutePath(), RDFFormatType.N3,
						true, true);

				try (InputStreamReader fileStream = new InputStreamReader(
						new FileInputStream(tempFile), Charset.forName("UTF-8"))) {

					tempRepo = RDFDataUnitFactory.createLocalRDFRepo(
							"tempRepo");

					final StatisticalHandler handler = new StatisticalHandler(
							tempRepo.getDataRepository().getConnection(), true);

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
				if (tempRepo != null) {
					tempRepo.delete();
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
