package cz.cuni.mff.xrg.odcs.rdf.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;

/**
 * Find out, if data in RDF repository are valid or not.
 * Basic idea of RDF data validation :
 * All RDF data saved in {@link #input} instance are load to temp file. Than
 * thanks {@link StatisticalHandler} and {@link RDFParser} are loaded to target {@link RDFDataUnit} only validate data and it´s created string report
 * {@link #message} with finding problems(no valid data) as {@link #findedProblems}. This defined target {@link RDFDataUnit} is more
 * described in method {@link #getGoalRepository()}. If target is {@link #output} then valid data are loaded direct into {@link #output}, if
 * it´s instance of {@link LocalRDFDataUnit} than is destroyed after validation
 * ending proccess - execute method {@link #areDataValid(). * }
 * 
 * @author Jiri Tomes
 */
public class RepositoryDataValidator implements DataValidator {

    private static Logger logger = LoggerFactory.getLogger(
            RepositoryDataValidator.class);

    private RDFDataUnit input;

    private WritableRDFDataUnit goalRepo;

    private String message;

    private List<TripleProblem> findedProblems;

    final private String encode = "UTF-8";

    /**
     * Create new instance of {@link RepositoryDataValidator} that check data
     * for given input and valid data store to output.
     * 
     * @param input
     *            source from where are data checked if are valid.
     * @param output
     *            target where are valid data stored.
     */
    public RepositoryDataValidator(RDFDataUnit input, WritableRDFDataUnit output) {
        this.input = input;
        this.goalRepo = output;
        this.message = "";
        this.findedProblems = new ArrayList<>();
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
        long tripleCount = -1;
        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            tripleCount = connection.size(input.getDataGraphnames().toArray(new URI[0]));

            if (tripleCount == 0) {
                isValid = true;
            } else {

                File tempFile = null;
                RepositoryConnection goalConnection = null;
                try {
                    tempFile = File.createTempFile("temp", "file");
                    tempFile = File.createTempFile("temp", "file");
                    FileOutputStream out = new FileOutputStream(tempFile.getAbsolutePath());
                    OutputStreamWriter os = new OutputStreamWriter(out, Charset.forName(encode));
                    RDFWriter rdfWriter = Rio.createWriter(RDFFormat.N3, os);
                    connection.export(rdfWriter, input.getDataGraphnames().toArray(new URI[0]));

                    try (InputStreamReader fileStream = new InputStreamReader(
                            new FileInputStream(tempFile), Charset.forName("UTF-8"))) {
                        goalConnection = goalRepo.getConnection();
                        final StatisticalHandler handler = new StatisticalHandler(
                                goalConnection, true);

                        handler.setGraphContext(goalRepo.getWriteDataGraph());

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

                } catch (IOException | RepositoryException e) {
                    message = e.getMessage();
                    logger.error(message);

                } catch (RDFParseException | RDFHandlerException e) {
                    message = "Problem with data parsing :" + e.getMessage();
                    logger.error(message);
                } finally {
                    if (goalConnection != null) {
                        try {
                            goalConnection.close();
                        } catch (RepositoryException ex) {
                            logger.warn("Error when closing connection", ex);
                            // eat close exception, we cannot do anything clever here
                        }
                    }
                    if (tempFile != null) {
                        tempFile.delete();
                    }
                }
            }

        } catch (Exception e) {
            message = e.getMessage();
            logger.error(message);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    logger.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
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
