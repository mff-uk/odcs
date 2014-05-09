package cz.cuni.mff.xrg.odcs.rdf.validator;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.DataValidator;
import cz.cuni.mff.xrg.odcs.rdf.validators.RepositoryDataValidator;

/**
 * DPU for RDF data validation and save validation report in RDF TURTLE (TTL)
 * syntax to given file.
 * 
 * @author Jiri Tomes
 */
@AsTransformer
public class RDFDataValidator extends ConfigurableBase<RDFDataValidatorConfig>
        implements ConfigDialogProvider<RDFDataValidatorConfig> {

    private static final Logger LOG = LoggerFactory
            .getLogger(RDFDataValidator.class);

    /**
     * Input RDF data repository with data we want to validate.
     */
    @InputDataUnit(name = "input")
    public RDFDataUnit dataInput;

    /**
     * Output RDF data repository with only validate triples get from input.
     */
    @OutputDataUnit(name = "Validated_Data", optional = true, description = "Output RDF data repository with only validated triples get from input.")
    public RDFDataUnit dataOutput;

    /**
     * Output RDF repository report about invalid data describe as RDF triples.
     */
    @OutputDataUnit(name = "Report", description = "Output RDF repository report about invalid data described as RDF triples.")
    public RDFDataUnit reportOutput;

    public RDFDataValidator() {
        super(RDFDataValidatorConfig.class);
    }

    /**
     * Returns the configuration dialogue for RDF Data validator.
     * 
     * @return the configuration dialogue for RDF Data validator.
     */
    @Override
    public AbstractConfigDialog<RDFDataValidatorConfig> getConfigurationDialog() {
        return new RDFDataValidatorDialog();
    }

    private void makeValidationReport(DataValidator validator,
            String graphName, DPUContext context, boolean stopExecution) throws CannotOverwriteFileException, RDFException, RepositoryException {

        context.sendMessage(MessageType.INFO,
                "Start creating VALIDATION REPORT", String.format(
                        "Start generating validation report output for graph <%s> .",
                        graphName));

        ReportCreator reporter = new ReportCreator(validator
                .getFindedProblems(), graphName);
        reporter.makeOutputReport(reportOutput);

        context.sendMessage(MessageType.INFO,
                "VALIDATION REPORT created SUCCESSFULLY", String.format(
                        "Validation report output for graph <%s> created successfully",
                        graphName));

        if (stopExecution) {
            RepositoryConnection connection = null;
            try {
                connection = dataOutput.getConnection();
                connection.clear(dataOutput.getDataGraph());
            } catch (RepositoryException ex) {
                LOG.warn("Error", ex);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (RepositoryException ex) {
                        LOG.warn("Error when closing connection", ex);
                        // eat close exception, we cannot do anything clever here
                    }
                }
            }
            throw new RDFException(
                    "RDFDataValidator found some invalid data - pipeline execution is stopped");
        }
    }

    /**
     * Execute the RDF Data validator.
     * 
     * @param context
     *            RDF Data validator context.
     * @throws DataUnitException
     *             if this DPU fails.
     * @throws DPUException
     *             if this DPU fails.
     */
    @Override
    public void execute(DPUContext context)
            throws DPUException,
            DataUnitException {

        final boolean stopExecution = config.canStopExecution();
        final boolean sometimesOutput = config.hasSometimesOutput();

        try {

            DataValidator validator = new RepositoryDataValidator(dataInput,
                    dataOutput);
            String graphName = dataInput.getDataGraph().toString();

            if (sometimesOutput) {
                if (!validator.areDataValid()) {
                    context.sendMessage(MessageType.WARNING,
                            "Validator found some INVALID DATA",
                            validator.getErrorMessage() + "\nIt will be created validation report.");
                    LOG.error(validator.getErrorMessage());

                    makeValidationReport(validator, graphName, context,
                            stopExecution);

                } else {
                    context.sendMessage(MessageType.INFO,
                            "Validation Sucessful - NO errors",
                            "All RDF data are valid. Validation report will be not created.");
                }
            } else {
                if (!validator.areDataValid()) {
                    context.sendMessage(MessageType.WARNING,
                            "Validator found some INVALID DATA",
                            "Some RDF data are invalid:\n"
                                    + validator.getErrorMessage()
                                    + " It will be created validation report");
                } else {
                    context.sendMessage(MessageType.INFO,
                            "Validation Sucessful - NO errors",
                            "All RDF data are valid. Validation report output will be empty");
                }

                makeValidationReport(validator, graphName, context,
                        stopExecution);

            }
        } catch (RDFException e) {
            context.sendMessage(MessageType.ERROR, e.getMessage(), e
                    .fillInStackTrace().toString());
        } catch (RepositoryException e) {
            context.sendMessage(MessageType.ERROR, e.getMessage(), e
                    .fillInStackTrace().toString());
        }

    }
}
