package cz.cuni.mff.xrg.odcs.extractor.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * Extracts RDF data from a file.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
@DPU.AsExtractor
public class FileExtractor extends ConfigurableBase<FileExtractorConfig>
        implements ConfigDialogProvider<FileExtractorConfig> {

    private final Logger LOG = LoggerFactory.getLogger(FileExtractor.class);

    protected final String encode = "UTF-8";

    /**
     * The repository for file extractor.
     */
    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit writableRdfDataUnit;

    public FileExtractor() {
        super(FileExtractorConfig.class);
    }

    /**
     * Execute the file extractor.
     *
     * @param context File extractor context.
     * @throws DPUException if this DPU fails.
     */
    @Override
    public void execute(DPUContext context) throws DPUException {

        final String baseURI = "";
        final FileExtractType extractType = config.getFileExtractType();
        final String path = config.getPath();
        final String fileSuffix = config.getFileSuffix();
        final boolean onlyThisSuffix = config.useOnlyThisSuffix();

        boolean useStatisticHandler = config.isUsedStatisticalHandler();
        boolean failWhenErrors = config.isFailWhenErrors();

        final HandlerExtractType handlerExtractType = HandlerExtractType
                .getHandlerType(useStatisticHandler, failWhenErrors);

        RDFFormatType formatType = config.getRDFFormatValue();
        final RDFFormat format = RDFFormatType.getRDFFormatByType(formatType);

        LOG.debug("extractType: {}", extractType);
        LOG.debug("formatType: {}", formatType);
        LOG.debug("path: {}", path);
        LOG.debug("fileSuffix: {}", fileSuffix);
        LOG.debug("baseURI: {}", baseURI);
        LOG.debug("onlyThisSuffix: {}", onlyThisSuffix);
        LOG.debug("useStatisticHandler: {}", useStatisticHandler);
        long triplesCount = 0;
        try {
            extractFromFile(extractType, format, path, fileSuffix,
                    baseURI, onlyThisSuffix, handlerExtractType, writableRdfDataUnit, context);

            if (useStatisticHandler && StatisticalHandler.hasParsingProblems()) {

                String problems = StatisticalHandler
                        .getFoundGlobalProblemsAsString();
                StatisticalHandler.clearParsingProblems();

                context.sendMessage(DPUContext.MessageType.WARNING,
                        "Statistical and error handler has found during parsing problems triples (these triples were not added)",
                        problems);
                RepositoryConnection connection = null;
                try {
                    connection = writableRdfDataUnit.getConnection();
                    triplesCount = connection.size(writableRdfDataUnit.getWriteDataGraph());
                    LOG.info("Extracted {} triples", triplesCount);
                } catch (DataUnitException ex) {
                    throw new DPUException(ex);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (RepositoryException ex) {
                            context.sendMessage(DPUContext.MessageType.WARNING,
                                    ex.getMessage(), ex.fillInStackTrace().toString());
                        }
                    }
                }
            }
        } catch (RepositoryException | DataUnitException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, e.getMessage(), e
                    .fillInStackTrace().toString());
        }
    }

    private void setErrorsListenerToParser(RDFParser parser,
            final StatisticalHandler handler) {

        if (parser == null || handler == null) {
            return;

        }

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
    }

    public RDFParser getRDFParser(RDFFormat format, TripleCountHandler handler) {
        RDFParser parser = Rio.createParser(format);
        parser.setRDFHandler(handler);

        ParserConfig tmpConfig = parser.getParserConfig();

        tmpConfig.addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);

        parser.setParserConfig(tmpConfig);

        if (handler instanceof StatisticalHandler) {
            setErrorsListenerToParser(parser, (StatisticalHandler) handler);
        }

        return parser;
    }

    /**
     * Returns the configuration dialogue for File extractor.
     *
     * @return the configuration dialogue for File extractor.
     */
    @Override
    public AbstractConfigDialog<FileExtractorConfig> getConfigurationDialog() {
        return new FileExtractorDialog();
    }

    private void parseFileUsingHandler(TripleCountHandler handler,
            RDFFormat fileFormat,
            InputStreamReader is, String baseURI) throws DPUException, DataUnitException {

        handler.setGraphContext(writableRdfDataUnit.getWriteDataGraph());
        RDFParser parser = getRDFParser(fileFormat, handler);

        try {
            parser.parse(is, baseURI);
        } catch (IOException | RDFParseException | RDFHandlerException ex) {
            throw new DPUException(ex.getMessage(), ex);
        }
    }

    private void parseFileUsingStandardHandler(RDFFormat fileFormat,
            InputStreamReader is, String baseURI,
            RepositoryConnection connection) throws DPUException, DataUnitException {

        TripleCountHandler handler = new TripleCountHandler(connection);
        parseFileUsingHandler(handler, fileFormat, is, baseURI);
    }

    private void parseFileUsingStatisticalHandler(RDFFormat fileFormat,
            InputStreamReader is, String baseURI,
            RepositoryConnection connection, boolean failWhenErrors) throws DPUException, DataUnitException {

        StatisticalHandler handler = new StatisticalHandler(connection);
        parseFileUsingHandler(handler, fileFormat, is, baseURI);

        if (handler.hasFindedProblems()) {
            String problems = handler.getFindedProblemsAsString();

            LOG.error(problems);
            if (failWhenErrors) {
                throw new DPUException(problems);
            }
        }
    }

    private void extractDataFileFromHTTPSource(String path, RDFFormat format,
            String baseURI,
            HandlerExtractType handlerExtractType, RDFDataUnit repo, DPUContext context)
            throws DPUException, DataUnitException {

        URL urlPath;
        try {
            urlPath = new URL(path);
        } catch (MalformedURLException ex) {
            throw new DPUException(ex.getMessage(), ex);
        }

        try {
            RepositoryConnection connection = null;
            try (InputStreamReader inputStreamReader = new InputStreamReader(
                    urlPath.openStream(), Charset.forName(encode))) {

                //in case that RDF format is AUTO or not fixed.
                if (format == null) {
                    format = RDFFormat.forFileName(path, RDFFormat.RDFXML);
                }
                connection = repo.getConnection();
                connection.begin();

                switch (handlerExtractType) {
                    case STANDARD_HANDLER:
                        parseFileUsingStandardHandler(format, inputStreamReader,
                                baseURI, connection);
                        break;
                    case ERROR_HANDLER_CONTINUE_WHEN_MISTAKE:
                        parseFileUsingStatisticalHandler(format,
                                inputStreamReader,
                                baseURI, connection, false);
                        break;
                    case ERROR_HANDLER_FAIL_WHEN_MISTAKE:
                        parseFileUsingStatisticalHandler(format,
                                inputStreamReader,
                                baseURI, connection, true);
                        break;
                }
                connection.commit();
            } catch (RepositoryException e) {
                throw new DPUException(e.getMessage(), e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (RepositoryException ex) {
                        context.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                    }
                }
            }

        } catch (IOException ex) {
            throw new DPUException(ex.getMessage(), ex);
        }
    }

    private void addFileToRepository(RDFFormat fileFormat, File dataFile,
            String baseURI,
            HandlerExtractType handlerExtractType, RepositoryConnection connection, Resource... graphs)
            throws DPUException, DataUnitException {

        //in case that RDF format is AUTO or not fixed.
        if (fileFormat == null) {
            fileFormat = RDFFormat.forFileName(dataFile.getAbsolutePath(),
                    RDFFormat.RDFXML);
        }

        try (InputStreamReader is = new InputStreamReader(new FileInputStream(
                dataFile), Charset.forName(encode))) {

            switch (handlerExtractType) {
                case STANDARD_HANDLER:
                    parseFileUsingStandardHandler(fileFormat, is, baseURI,
                            connection);
                    break;
                case ERROR_HANDLER_CONTINUE_WHEN_MISTAKE:
                    parseFileUsingStatisticalHandler(fileFormat, is, baseURI,
                            connection, false);
                    break;
                case ERROR_HANDLER_FAIL_WHEN_MISTAKE:
                    parseFileUsingStatisticalHandler(fileFormat, is, baseURI,
                            connection, true);
                    break;
            }

            //connection.commit();
        } catch (IOException ex) {
            LOG.debug(ex.getMessage(), ex);
            throw new DPUException("IO Exception: " + ex.getMessage(), ex);
        }
    }

    private File[] getFilesBySuffix(File dirFile, String suffix,
            boolean useAceptedSuffix) {

        if (useAceptedSuffix) {
            final String aceptedSuffix = suffix.toUpperCase();

            FilenameFilter acceptedFileFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.toUpperCase().endsWith(aceptedSuffix)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            return dirFile.listFiles(acceptedFileFilter);

        } else {
            return dirFile.listFiles();
        }

    }

    private void addFilesInDirectoryToRepository(RDFFormat format, File[] files,
            String baseURI,
            HandlerExtractType handlerExtractType, boolean skipFiles, RepositoryConnection connection,
            Resource... graphs)
            throws DPUException, DataUnitException {

        if (files == null) {
            return; // nothing to add
        }

        for (int i = 0; i < files.length; i++) {
            File nextFile = files[i];

            try {
                addFileToRepository(format, nextFile, baseURI,
                        handlerExtractType, connection, graphs);

            } catch (DataUnitException e) {
                if (!skipFiles) {
                    throw e;
                }
            }
            final String message = String.format(
                    "RDF data from file <%s> was skiped", nextFile
                    .getAbsolutePath());
            LOG.error(message);

        }
    }

    private void extractDataFromDirectorySource(File dirFile, String suffix,
            boolean useSuffix, RDFFormat format, String baseURI,
            HandlerExtractType handlerExtractType, boolean skipFiles, WritableRDFDataUnit repo, DPUContext context)
            throws DPUException {

        if (dirFile.isDirectory()) {
            File[] files = getFilesBySuffix(dirFile, suffix, useSuffix);

            RepositoryConnection connection = null;
            try {
                connection = repo.getConnection();
                connection.begin();
                addFilesInDirectoryToRepository(format, files, baseURI,
                        handlerExtractType, skipFiles, connection,
                        repo.getWriteDataGraph());

                connection.commit();
            } catch (RepositoryException | DataUnitException e) {
                throw new DPUException(e.getMessage(), e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (RepositoryException ex) {
                        context.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                    }
                }
            }
        } else {
            throw new DPUException(
                    "Path to directory \"" + dirFile.getAbsolutePath()
                    + "\" doesnt exist");
        }
    }

    /**
     * Extract RDF triples from RDF file to repository.
     *
     * @param extractType One of defined enum type for extraction data from
     * file.
     * @param format One of RDFFormat value for parsing triples, if value is
     * null RDFFormat is selected by filename.
     * @param path String path to file/directory
     * @param suffix String suffix of fileName (example: ".ttl", ".xml", etc)
     * @param baseURI String name of defined used URI
     * @param useSuffix boolean value, if extract files only with defined suffix
     * or not.
     * @param handlerExtractType Possibilities how to choose handler for data
     * extraction and how to solve found problems with no valid data.
     * @throws DPUException when extraction fail.
     */
    public void extractFromFile(FileExtractType extractType,
            RDFFormat format,
            String path, String suffix,
            String baseURI, boolean useSuffix,
            HandlerExtractType handlerExtractType, WritableRDFDataUnit repo, DPUContext context)
            throws DPUException, DataUnitException {

        ParamController.testNullParameter(path,
                "Mandatory target path in extractor is null.");
        ParamController.testEmptyParameter(path,
                "Mandatory target path in extractor have to be not empty.");

        File dirFile = new File(path);

        switch (extractType) {
            case HTTP_URL:
                extractDataFileFromHTTPSource(path, format, baseURI,
                        handlerExtractType, repo, context);
                break;
            case PATH_TO_DIRECTORY:
                extractDataFromDirectorySource(dirFile, suffix, useSuffix,
                        format, baseURI, handlerExtractType, false, repo, context);
                break;

            case PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES:
                extractDataFromDirectorySource(dirFile, suffix, useSuffix,
                        format, baseURI, handlerExtractType, true, repo, context);
                break;
            case PATH_TO_FILE:
            case UPLOAD_FILE:
                extractDataFromFileSource(dirFile, format, baseURI,
                        handlerExtractType, repo, context);
                break;
        }

    }

    private void extractDataFromFileSource(File dirFile, RDFFormat format,
            String baseURI, HandlerExtractType handlerExtractType, WritableRDFDataUnit repo, DPUContext context) throws DPUException {
        RepositoryConnection connection = null;
        if (dirFile.isFile()) {
            try {
                connection = repo.getConnection();
                connection.begin();
                addFileToRepository(format, dirFile, baseURI,
                        handlerExtractType,
                        connection, repo.getWriteDataGraph());
                connection.commit();
            } catch (RepositoryException | DataUnitException e) {
                throw new DPUException(e.getMessage(), e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (RepositoryException ex) {
                        context.sendMessage(DPUContext.MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                    }
                }
            }
        } else {
            throw new DPUException(
                    "Path to file \"" + dirFile.getAbsolutePath() + "\"doesnt exist");
        }

    }
}
