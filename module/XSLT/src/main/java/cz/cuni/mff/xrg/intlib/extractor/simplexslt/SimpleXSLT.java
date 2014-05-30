package cz.cuni.mff.xrg.intlib.extractor.simplexslt;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.*;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

/**
 * Simple XSLT Extractor
 * 
 * @author tomasknap
 */
public class SimpleXSLT extends ConfigurableBase<SimpleXSLTConfig> implements Extract, ConfigDialogProvider<SimpleXSLTConfig> {

    /**
     * DPU's configuration.
     */
//    private SimpleXSLTConfig config;

    @Override
    public AbstractConfigDialog<SimpleXSLTConfig> getConfigurationDialog() {
        return new SimpleXSLTDialog();
    }

//    @Override
//    public void configure(SimpleXSLTConfig c) throws ConfigException {
//        config = c;
//    }
//
//    @Override
//    public SimpleXSLTConfig getConfiguration() {
//        return config;
//    }

    // TODO 2: Provide implementation of unimplemented methods 
    @Override
    public void extract(ExtractContext context) throws ExtractException {

        //inputs
        File stylesheet = new File(config.getXslTemplate());

        File inputFile = new File(config.getXmlFile());

        File outputFile = new File(config.getXmlFile() + ".ttl");

        //outputs
        RDFDataRepository outputRepository;
        try {
            outputRepository = (RDFDataRepository) context.addOutputDataUnit(DataUnitType.RDF, "output");
        } catch (DataUnitCreateException e) {
            throw new ExtractException("Can't create DataUnit", e);
        }
        outputRepository.extractRDFfromFileToRepository(config.getXmlFile() + ".ttl", "", "", false, false);
        ////////////

        //xslt
        Processor proc = new Processor(false);
        XsltCompiler compiler = proc.newXsltCompiler();
        XsltExecutable exp;
        try {
            exp = compiler.compile(new StreamSource(stylesheet));

            XdmNode source = proc.newDocumentBuilder().build(new StreamSource(inputFile));

            Serializer out = new Serializer();
            out.setOutputProperty(Serializer.Property.METHOD, "text");
            out.setOutputProperty(Serializer.Property.INDENT, "yes");
            out.setOutputFile(outputFile);

            XsltTransformer trans = exp.load();

            trans.setInitialContextNode(source);
            trans.setDestination(out);
            trans.transform();

        } catch (SaxonApiException ex) {
            Logger.getLogger(SimpleXSLT.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
