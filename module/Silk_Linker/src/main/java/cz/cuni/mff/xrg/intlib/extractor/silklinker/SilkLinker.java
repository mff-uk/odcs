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
import cz.cuni.xrg.intlib.rdf.enums.FileExtractType;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;
import de.fuberlin.wiwiss.silk.Silk;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.stream.StreamSource;


//import de.fuberlin.wiwiss.silk.Silk;
//import de.fuberlin.wiwiss.silk.config.RuntimeConfig;
//import de.fuberlin.wiwiss.silk.LoadTask;
//import de.fuberlin.wiwiss.silk.cache.EntityCache;


/**
 * Simple XSLT Extractor
 *
 * @author tomasknap
 */
//import de.fuberlin.wiwiss.silk.config.LinkSpecification;
//import de.fuberlin.wiwiss.silk.config.LinkingConfig;
//import de.fuberlin.wiwiss.silk.config.Prefixes;
//import de.fuberlin.wiwiss.silk.datasource.Source;
//import de.fuberlin.wiwiss.silk.util.DPair;
//import de.fuberlin.wiwiss.silk.util.FileUtils;
//import de.fuberlin.wiwiss.silk.output.Output;
import org.slf4j.LoggerFactory;


public class SilkLinker implements Extract, Configurable<SilkLinkerConfig>, ConfigDialogProvider<SilkLinkerConfig> {

    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(
			SilkLinker.class);
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

        //inputs (sample config file is in the file module/Silk_Linker/be-sameAs.xml)
        File conf = new File(config.getSilkConf());
        
        //Execution of the Silk linker (xml conf is an important input!)
        //TODO Petr: solve the problem when loading XML conf
        logger.info("Silk is being launched");
        Silk.executeFile(conf, null, Silk.DefaultThreads(), true);
        
        
        //TODO Tomas: dialog - uploader + context.getWorkingDir(), show the uploaded file in textarea
        
        //TODO Tomas: get outputs from the conf file. now we suppose two output files with 
        //hardcoded names in the .xml configuration file.
                    
      
        logger.info("Output 'confirmed links' is being prepared");
        //outputs confirmed
        //the outgoing edge must be labelled as: output rename outputs_confirmed
        RDFDataRepository outputRepositoryConfirmed;
        try {
            outputRepositoryConfirmed = (RDFDataRepository) context.addOutputDataUnit(DataUnitType.RDF, "outputs_confirmed");
        } catch (DataUnitCreateException e) {
            throw new ExtractException("Can't create DataUnit", e);
        }
        try {
            outputRepositoryConfirmed.extractfromFile(FileExtractType.PATH_TO_FILE, "/Users/tomasknap/.silk/confirmed.ttl", "", "", false, false);
        } catch (RDFException ex) {
            Logger.getLogger(SilkLinker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         logger.info("Output 'to verify links' is being prepared");
        //outputs to be verified
        //the outgoing edge must be labelled as: output rename outputs_to_verified
        RDFDataRepository outputRepositoryToVerify;
        try {
            outputRepositoryToVerify = (RDFDataRepository) context.addOutputDataUnit(DataUnitType.RDF, "outputs_to_verified");
        } catch (DataUnitCreateException e) {
            throw new ExtractException("Can't create DataUnit", e);
        }
        try {
            outputRepositoryToVerify.extractfromFile(FileExtractType.PATH_TO_FILE, "/Users/tomasknap/.silk/verify.ttl", "", "", false, false);
        } catch (RDFException ex) {
            Logger.getLogger(SilkLinker.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
}



  //File outputFile = new File(config.getXmlFile() + ".ttl");
        //LinkSpecification ls = new LinkSpecification(, null, null, null, null, null)
        //LinkingConfig lc = new LinkingConfig(null, null, null, null, null);
        
        //Silk.executeLinkSpec(null, null, i, true);
       
        //RuntimeConfig c = new RuntimeConfig()
        //LoadTask l = new LoadTask(null, null)