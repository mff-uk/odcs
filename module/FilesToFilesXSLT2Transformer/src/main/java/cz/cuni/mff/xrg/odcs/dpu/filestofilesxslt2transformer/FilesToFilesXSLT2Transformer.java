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

    @InputDataUnit(name = "fileInput")
    public FilesDataUnit fileInput;

    @OutputDataUnit(name = "fileOutput")
    public WritableFilesDataUnit fileOutput;

    public FilesToFilesXSLT2Transformer() {
        super(FilesToFilesXSLT2TransformerConfig.class);
    }

    @Override
    public AbstractConfigDialog<FilesToFilesXSLT2TransformerConfig> getConfigurationDialog() {
        return new FilesToFilesXSLT2TransformerConfigDialog();
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException {
        //check that XSLT is available 
        if (config.getXslTemplate().isEmpty()) {
            throw new DPUException("No XSLT available, execution interrupted");
        }

        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = "";//String.format("Configuration: files to download: %d, connectionTimeout: %d, readTimeout: %d", symbolicNameToURIMap.size(), connectionTimeout, readTimeout);
        dpuContext.sendMessage(MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);

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
        
        LOG.info("Stylesheet was compiled successully");

        FilesIteration filesIteration = fileInput.getFiles();
        int filesSuccessfulCount = 0;
        int all = 0;

        try {
            while (filesIteration.hasNext()) {
                checkCancelled(dpuContext);

                FilesDataUnitEntry entry = filesIteration.next();
                String inSymbolicName = entry.getSymbolicName();
                
                String outputFilename = fileOutput.createFile(inSymbolicName);
                File outputFile = new File(outputFilename);
                File inputFile = new File(entry.getFilesystemURI());
                try {
                    all++;

                    Date start = new Date();
                    if (dpuContext.isDebugging()) {
                        long inputSizeM = inputFile.length() / 1024 / 1024;
                        LOG.debug("Memory used: " + String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024) + "M");
                        LOG.debug("Starting "+ String.valueOf(all)+" transformation of file "+ inSymbolicName + " size " + String.valueOf(inputSizeM) + "M");
                    }
                    Serializer out = new Serializer(outputFile);
                    if (!config.getOutputXSLTMethod().isEmpty()) {
                        out.setOutputProperty(Serializer.Property.METHOD, config.getOutputXSLTMethod());
                    }
                    out.setOutputProperty(Serializer.Property.INDENT, "yes");

//                    DocumentBuilder builder = proc.newDocumentBuilder();
//                    builder.setTreeModel(TreeModel.TINY_TREE_CONDENSED);
//                    XdmNode source = builder.build(new StreamSource(entry.getFilesystemURI().toASCIIString()));
//                    trans.setInitialContextNode(source);
                    
                    trans.setSource(new StreamSource(inputFile));
                    trans.setDestination(out);
                    trans.transform();
                    trans.getUnderlyingController().clearDocumentPool();
                    
                    fileOutput.addExistingFile(inSymbolicName, outputFilename);
                    filesSuccessfulCount++;
                    
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Transformed file #{} in {}. Symbolic name {} a file URI {}", filesSuccessfulCount, (System.currentTimeMillis() - start.getTime()), inSymbolicName, entry.getFilesystemURI());
                        LOG.debug("Memory used: " + String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024) + "M");
                    }
                } catch (SaxonApiException ex) {
                    dpuContext.sendMessage(MessageType.WARNING, "Problem processing file.", "Symbolic name " + inSymbolicName + " file URI " + entry.getFilesystemURI(), ex);
                    LOG.warn("Problem processing file. Symbolic name " + inSymbolicName + " file URI " + entry.getFilesystemURI(), ex);
                }
            }
        } finally {
            filesIteration.close();
        }
        String message = String.format("Transformed %d/%d", filesSuccessfulCount, all);
        if (filesSuccessfulCount < all) {
            dpuContext.sendMessage(MessageType.WARNING, message);
            LOG.warn(message);
        } else {
            dpuContext.sendMessage(MessageType.INFO, message);
            LOG.info(message);
        }
    }

    private void checkCancelled(DPUContext dpuContext) throws DPUCancelledException {
        if (dpuContext.canceled()) {
            throw new DPUCancelledException();
        }
    }
}
