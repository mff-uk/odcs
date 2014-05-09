package cz.cuni.mff.xrg.odcs.loader.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsLoader;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.DataValidator;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.validators.RepositoryDataValidator;

/**
 * Loads RDF data into file.
 * 
 * @author Jiri Tomes
 * @author Petyr
 */
@AsLoader
public class FileLoader extends ConfigurableBase<FileLoaderConfig>
        implements ConfigDialogProvider<FileLoaderConfig> {

    private final Logger logger = LoggerFactory.getLogger(FileLoader.class);

    final String encode = "UTF-8";

    /**
     * The repository for file loader.
     */
    @InputDataUnit(name = "input")
    public RDFDataUnit rdfDataUnit;

    @OutputDataUnit(name = "validationDataUnit", description = "Never connect any data to this unit please!")
    public RDFDataUnit validationDataUnit;

    @OutputDataUnit(name = "input_redirection", optional = true)
    public RDFDataUnit inputShadow;

    public FileLoader() {
        super(FileLoaderConfig.class);
    }

    /**
     * Execute the file loader.
     * 
     * @param context
     *            File loader context.
     * @throws DataUnitException
     *             if this DPU fails.
     * @throws DPUException
     *             if this DPU fails.
     */
    @Override
    public void execute(DPUContext context) throws DPUException, DataUnitException {

        final String filePath = config.getFilePath();
        final RDFFormat formatType = config.getRDFFileFormat();
        final boolean isNameUnique = config.isDiffName();
        final boolean canFileOverwritte = true;
        final boolean validateDataBefore = config.isValidDataBefore();

        if (validateDataBefore) {
            DataValidator dataValidator = new RepositoryDataValidator(
                    rdfDataUnit, validationDataUnit);

            if (!dataValidator.areDataValid()) {
                final String message = "RDF Data to load are not valid - LOADING to File FAIL";
                logger.error(dataValidator.getErrorMessage());

                context.sendMessage(MessageType.WARNING, message, dataValidator
                        .getErrorMessage());

                throw new RDFException(message);
            } else {
                context.sendMessage(MessageType.INFO,
                        "RDF Data for loading to file are valid");
                context.sendMessage(MessageType.INFO,
                        "Loading data to file STARTS JUST NOW");
            }
        }

        RepositoryConnection connection = null;
        long triplesCount = 0;
        try {
            connection = rdfDataUnit.getConnection();
            triplesCount = connection.size(rdfDataUnit.getDataGraph());
            FileOutputStream out = new FileOutputStream(filePath);
            OutputStreamWriter os = new OutputStreamWriter(out, Charset.forName(encode));

            RDFFormat format = null;
            // if  rdfFormatValue is null then we use an option AUTO -> try to guess format according to a file extension
            if (formatType == null) {
                File file = new File(filePath);
                format = Rio.getWriterFormatForFileName(file.getName());
            } else {
                format = formatType;
            }

            RDFWriter rdfWriter = Rio.createWriter(format, os);
            connection.export(rdfWriter, rdfDataUnit.getDataGraph());

        } catch (RepositoryException e) {
            context.sendMessage(MessageType.ERROR,
                    "connection to repository broke down");
        } catch (Exception ex) {
            context.sendMessage(MessageType.ERROR, ex.getMessage(), ex
                    .fillInStackTrace().toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    context.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
                }
            }
        }

        logger.info("Loading {} triples", triplesCount);

        if (config.isPenetrable()) {
            inputShadow.addAll(rdfDataUnit);
        }

    }

    /**
     * Returns the configuration dialogue for file loader.
     * 
     * @return the configuration dialogue for file loader.
     */
    @Override
    public AbstractConfigDialog<FileLoaderConfig> getConfigurationDialog() {
        return new FileLoaderDialog();
    }
}
