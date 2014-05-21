package cz.cuni.mff.xrg.odcs.dpu.xslt2;

import java.io.File;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.filelist.FileListDataUnit;
import cz.cuni.mff.xrg.odcs.filelist.FileListDataUnit.FileListDataUnitEntry;
import cz.cuni.mff.xrg.odcs.filelist.FileListDataUnit.FileListIteration;
import cz.cuni.mff.xrg.odcs.filelist.WritableFileListDataUnit;

@AsExtractor
public class XSLT2 extends ConfigurableBase<XSLT2Config> implements ConfigDialogProvider<XSLT2Config> {
    private static final Logger LOG = LoggerFactory.getLogger(XSLT2.class);

    @InputDataUnit(name = "fileInput")
    public FileListDataUnit fileInput;

    @OutputDataUnit(name = "fileOutput")
    public WritableFileListDataUnit fileOutput;

    public XSLT2() {
        super(XSLT2Config.class);
    }

    @Override
    public AbstractConfigDialog<XSLT2Config> getConfigurationDialog() {
        return new XSLT2ConfigDialog();
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException {
        //check that XSLT is available 
        if (config.getXslTemplate().isEmpty()) {
            throw new DPUException("No XSLT available, execution interrupted");
        }
        
        String shortMessage = this.getClass().getName() + " starting.";
        String longMessage =  "";//String.format("Configuration: files to download: %d, connectionTimeout: %d, readTimeout: %d", symbolicNameToURIMap.size(), connectionTimeout, readTimeout);
        dpuContext.sendMessage(MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);
        
      //try to compile XSLT
        TransformerFactory tfactory = new net.sf.saxon.TransformerFactoryImpl(); //TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
        Templates templates;
        try {
            templates = tfactory.newTemplates(new StreamSource(new StringReader(config.getXslTemplate())));
        } catch (TransformerConfigurationException ex) {
            throw new DPUException("Cannot compile XSLT", ex);
        }
        dpuContext.sendMessage(MessageType.INFO, "Stylesheet was compiled successully");
        LOG.info("Stylesheet was compiled successully");

        dpuContext.sendMessage(MessageType.INFO, "Processing FILE INPUT");
        LOG.info("Processing FILE INPUT");
        
        FileListIteration fileListIteration = fileInput.getFileList();
        int filesSuccessfulCount= 0;
        int all = 0;
        while (fileListIteration.hasNext()) {
            FileListDataUnitEntry entry = fileListIteration.next();
            String inSymbolicName = entry.getSymbolicName();
            
            File inputFile = new File(entry.getFilesystemURI());
            
            File outputFile = new File(fileOutput.createFile(inSymbolicName));
            try {
                all++;
                executeXSLT(templates, inputFile, outputFile, Collections.<String, String>emptyMap());
                filesSuccessfulCount++;
                
                if (dpuContext.isDebugging()) {
                    dpuContext.sendMessage(MessageType.DEBUG, "Processed file.", "Symbolic name " + inSymbolicName + " file URI " + entry.getFilesystemURI());                
                    LOG.debug("Processed file. Symbolic name " + inSymbolicName + " file URI " + entry.getFilesystemURI());
                }
            } catch (TransformerException ex) {
                dpuContext.sendMessage(MessageType.WARNING, "Problem processing file.", "Symbolic name " + inSymbolicName + " file URI " + entry.getFilesystemURI(), ex);
                LOG.warn("Problem processing file. Symbolic name " + inSymbolicName + " file URI " + entry.getFilesystemURI(), ex);
            }
        }
        String message = String.format("Processed %d/%d", filesSuccessfulCount, all);
        if (filesSuccessfulCount < all) {
            dpuContext.sendMessage(MessageType.WARNING, message);
            LOG.warn(message);
        } else {
            dpuContext.sendMessage(MessageType.INFO, message);
            LOG.info(message);
        }
    }


    /**
     * @param xslTemplate
     * @param inputFile
     * @param xsltParams
     * @param exp
     *            Compiled stylesheet
     * @return
     * @throws TransformerException 
     */
    private void executeXSLT(Templates templates, File inputFile, File outputFile, Map<String, String> xsltParams) throws TransformerException {
            Transformer transformer = templates.newTransformer();
            transformer.setParameter(OutputKeys.INDENT, "yes");
            if (!config.getOutputXSLTMethod().isEmpty()) {
                LOG.debug("Overwriting output method in XSLT from the DPU configuration to {}", config.getOutputXSLTMethod());
                transformer.setParameter(OutputKeys.METHOD, config.getOutputXSLTMethod());
            }

            //set params for the template!
            for (String s : xsltParams.keySet()) {
                //QName langParam = new QName(s);
                //transformer.setParameter(s, new XdmAtomicValue(xsltParams.get(s)));
                transformer.setParameter(s, xsltParams.get(s));
                LOG.debug("Set param {} with value {}", s, xsltParams.get(s));
            }

            Date start = new Date();
            LOG.debug("XSLT is about to be executed");
            transformer.transform(new StreamSource(inputFile),
                    new StreamResult(outputFile));

            LOG.debug("XSLT executed in {} ms", (System.currentTimeMillis() - start.getTime()));
    }
}
