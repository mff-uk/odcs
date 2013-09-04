package cz.cuni.mff.xrg.intlib.extractor.silklinker;

import cz.cuni.xrg.intlib.commons.dpu.DPU;
import cz.cuni.xrg.intlib.commons.dpu.DPUContext;
import cz.cuni.xrg.intlib.commons.dpu.annotation.AsExtractor;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import de.fuberlin.wiwiss.silk.Silk;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple XSLT Extractor
 *
 * @author tomasknap
 */
@AsExtractor
public class SilkLinker extends ConfigurableBase<SilkLinkerConfig>
	implements DPU, ConfigDialogProvider<SilkLinkerConfig> {
    
    private static final Logger LOG = LoggerFactory.getLogger(
			SilkLinker.class);

    public SilkLinker() {
    	super(SilkLinkerConfig.class);
    }
    
    @Override
    public AbstractConfigDialog<SilkLinkerConfig> getConfigurationDialog() {
        return new SilkLinkerDialog();
    }

    @Override
    public void execute(DPUContext context) {
        //inputs (sample config file is in the file module/Silk_Linker/be-sameAs.xml)
        File conf = new File(config.getSilkConf());
        
        //Execution of the Silk linker (xml conf is an important input!)
        //TODO Petr: solve the problem when loading XML conf
        //LOG.info("Silk is being launched");
        Silk.executeFile(conf, null, Silk.DefaultThreads(), true);
        //LOG.info("Silk finished");
        
        //TODO Tomas: dialog - uploader + context.getWorkingDir(), show the uploaded file in textarea
        
        //TODO Tomas: get outputs from the conf file. now we suppose two output files with 
        //hardcoded names in the .xml configuration file.
                    
      
        LOG.info("Output 'confirmed links' is being prepared");
        /*
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
        */
    }

	@Override
	public void cleanUp() {	}
	
}



  //File outputFile = new File(config.getXmlFile() + ".ttl");
        //LinkSpecification ls = new LinkSpecification(, null, null, null, null, null)
        //LinkingConfig lc = new LinkingConfig(null, null, null, null, null);
        
        //Silk.executeLinkSpec(null, null, i, true);
       
        //RuntimeConfig c = new RuntimeConfig()
        //LoadTask l = new LoadTask(null, null)