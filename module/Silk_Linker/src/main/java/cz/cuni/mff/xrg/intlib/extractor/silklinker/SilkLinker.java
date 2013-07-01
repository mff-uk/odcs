package cz.cuni.mff.xrg.intlib.extractor.silklinker;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.stream.StreamSource;

import de.fuberlin.wiwiss.silk.Silk;
import de.fuberlin.wiwiss.silk.config.RuntimeConfig;
import de.fuberlin.wiwiss.silk.LoadTask;
import de.fuberlin.wiwiss.silk.cache.EntityCache;


/**
 * Simple XSLT Extractor
 *
 * @author tomasknap
 */
import de.fuberlin.wiwiss.silk.config.LinkSpecification;
import de.fuberlin.wiwiss.silk.config.LinkingConfig;
import de.fuberlin.wiwiss.silk.config.Prefixes;
import de.fuberlin.wiwiss.silk.datasource.Source;
import de.fuberlin.wiwiss.silk.util.DPair;
import de.fuberlin.wiwiss.silk.util.FileUtils;
import de.fuberlin.wiwiss.silk.output.Output;


public class SilkLinker implements Extract, Configurable<SilkLinkerConfig>, ConfigDialogProvider<SilkLinkerConfig> {

    /**
     * DPU's configuration.
     */
    private SilkLinkerConfig config;

    @Override
    public AbstractConfigDialog<SilkLinkerConfig> getConfigurationDialog() {
        return new SilkLinkerDialog();
    }

    @Override
    public void configure(SilkLinkerConfig c) throws ConfigException {
        config = c;
    }

    @Override
    public SilkLinkerConfig getConfiguration() {
        return config;
    }

    // TODO 2: Provide implementation of unimplemented methods 
    @Override
    public void extract(ExtractContext context) throws ExtractException {

        //inputs
        File conf = new File(config.getSilkConf());

        //TODO uploader + context.getWorkingDir()
        //TODO get outputs from the conf file. now we suppose two output files.
        //TODO configure home dir to the working dir (config.RuntimeConfig)
        
        
        
        //File outputFile = new File(config.getXmlFile() + ".ttl");
        //LinkSpecification ls = new LinkSpecification(, null, null, null, null, null)
        //LinkingConfig lc = new LinkingConfig(null, null, null, null, null);
        
        //Silk.executeLinkSpec(null, null, i, true);
        
        Silk.executeFile(conf, null, Silk.DefaultThreads(), true);
        //RuntimeConfig c = new RuntimeConfig()
        //LoadTask l = new LoadTask(null, null)

        
        
        
        //outputs confirmed
        RDFDataRepository outputRepositoryConfirmed;
        try {
            outputRepositoryConfirmed = (RDFDataRepository) context.addOutputDataUnit(DataUnitType.RDF);
        } catch (DataUnitCreateException e) {
            throw new ExtractException("Can't create DataUnit", e);
        }
        outputRepositoryConfirmed.extractRDFfromFileToRepository("/Users/tomasknap/.silk/confirmed.ttl", "", "", false);
        /////////////
        
        //outputs to be verified
        RDFDataRepository outputRepositoryToVerify;
        try {
            outputRepositoryToVerify = (RDFDataRepository) context.addOutputDataUnit(DataUnitType.RDF);
        } catch (DataUnitCreateException e) {
            throw new ExtractException("Can't create DataUnit", e);
        }
        outputRepositoryToVerify.extractRDFfromFileToRepository("/Users/tomasknap/.silk/verify.ttl", "", "", false);
        /////////////
        

    }
}
