package cz.cuni.mff.xrg.odcs.dpu.filestofilesxslt2transformer;

import java.io.File;
import java.io.StringReader;
import java.util.Date;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUCancelledException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesDataUnitEntry;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesIteration;
import cz.cuni.mff.xrg.odcs.files.WritableFilesDataUnit;

@AsTransformer
public class FilesToFilesXSLT2Transformer extends ConfigurableBase<FilesToFilesXSLT2TransformerConfig> implements ConfigDialogProvider<FilesToFilesXSLT2TransformerConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(FilesToFilesXSLT2Transformer.class);

    @InputDataUnit(name = "filesInput")
    public FilesDataUnit filesInput;

    @OutputDataUnit(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FilesToFilesXSLT2Transformer() {
        super(FilesToFilesXSLT2TransformerConfig.class);
    }

    @Override
    public AbstractConfigDialog<FilesToFilesXSLT2TransformerConfig> getConfigurationDialog() {
        return new FilesToFilesXSLT2TransformerConfigDialog();
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException {
        //check that XSLT is available 
        if (config.getXslTemplate().isEmpty()) {
            throw new DPUException("No XSLT template available, execution interrupted");
        }

        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.valueOf(config);
        dpuContext.sendMessage(MessageType.INFO, shortMessage, longMessage);

        //try to compile XSLT
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp;
        try {
            exp = comp.compile(new StreamSource(new StringReader(config.getXslTemplate())));
        } catch (SaxonApiException ex) {
            throw new DPUException("Cannot compile XSLT", ex);
        }
        XsltTransformer trans = exp.load();

        dpuContext.sendMessage(MessageType.INFO, "Stylesheet was compiled successully");

        FilesIteration filesIteration;
        try {
            filesIteration = filesInput.getFiles();
        } catch (DataUnitException ex) {
            throw new DPUException("Could not obtain filesInput", ex);
        }
        long filesSuccessfulCount = 0L;
        long index = 0L;

        try {
            while (filesIteration.hasNext()) {
                checkCancelled(dpuContext);

                FilesDataUnitEntry entry;
                try {
                    entry = filesIteration.next();

                    String inSymbolicName = entry.getSymbolicName();

                    String outputFilename = filesOutput.createFile(inSymbolicName);
                    File outputFile = new File(outputFilename);
                    File inputFile = new File(entry.getFilesystemURI());
                    try {
                        index++;

                        Date start = new Date();
                        if (dpuContext.isDebugging()) {
                            long inputSizeM = inputFile.length() / 1024 / 1024;
                            LOG.debug("Memory used: {}M", String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024));
                            LOG.debug("Processing {} file {} length {}M", appendNumber(index), entry, inputSizeM);

                        }
                        Serializer out = new Serializer(outputFile);

//                    DocumentBuilder builder = proc.newDocumentBuilder();
//                    builder.setTreeModel(TreeModel.TINY_TREE_CONDENSED);
//                    XdmNode source = builder.build(new StreamSource(entry.getFilesystemURI().toASCIIString()));
//                    trans.setInitialContextNode(source);

                        trans.setSource(new StreamSource(inputFile));
                        trans.setDestination(out);
                        trans.transform();
                        trans.getUnderlyingController().clearDocumentPool();

                        filesOutput.addExistingFile(inSymbolicName, outputFilename);
                        filesSuccessfulCount++;

                        if (dpuContext.isDebugging()) {
                            LOG.debug("Processed {} file in {}s", appendNumber(index), (System.currentTimeMillis() - start.getTime()) / 1000);
                            LOG.debug("Memory used: " + String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + "M");
                        }
                    } catch (SaxonApiException | DataUnitException ex) {
                        dpuContext.sendMessage(
                                config.isSkipOnError() ? MessageType.WARNING : MessageType.ERROR,
                                "Error processing " + appendNumber(index) + " file",
                                String.valueOf(entry),
                                ex);
                    }
                } catch (DataUnitException ex) {
                    dpuContext.sendMessage(
                            config.isSkipOnError() ? MessageType.WARNING : MessageType.ERROR,
                            "DataUnit exception.",
                            "",
                            ex);
                }
            }
        } catch (DataUnitException ex) {
            throw new DPUException("Error iterating filesInput.", ex);
        } finally {
            try {
                filesIteration.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error closing filesInput", ex);
            }
        }
        String message = String.format("Processed %d/%d", filesSuccessfulCount, index);
        dpuContext.sendMessage(filesSuccessfulCount < index ? MessageType.WARNING : MessageType.INFO, message);
    }

    private void checkCancelled(DPUContext dpuContext) throws DPUCancelledException {
        if (dpuContext.canceled()) {
            throw new DPUCancelledException();
        }
    }

    public static String appendNumber(long number) {
        String value = String.valueOf(number);
        if (value.length() > 1) {
            // Check for special case: 11 - 13 are all "th".
            // So if the second to last digit is 1, it is "th".
            char secondToLastDigit = value.charAt(value.length() - 2);
            if (secondToLastDigit == '1')
                return value + "th";
        }
        char lastDigit = value.charAt(value.length() - 1);
        switch (lastDigit) {
            case '1':
                return value + "st";
            case '2':
                return value + "nd";
            case '3':
                return value + "rd";
            default:
                return value + "th";
        }
    }
}
