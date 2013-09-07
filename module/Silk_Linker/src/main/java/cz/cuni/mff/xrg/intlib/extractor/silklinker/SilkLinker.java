package cz.cuni.mff.xrg.intlib.extractor.silklinker;

import cz.cuni.xrg.intlib.commons.dpu.DPU;
import cz.cuni.xrg.intlib.commons.dpu.DPUContext;
import cz.cuni.xrg.intlib.commons.dpu.annotation.AsExtractor;
import cz.cuni.xrg.intlib.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.xrg.intlib.commons.message.MessageType;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;
import de.fuberlin.wiwiss.silk.Silk;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

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
    
    private static final Logger log = LoggerFactory.getLogger(
			SilkLinker.class);

    @OutputDataUnit(name="outputs_confirmed")
	public RDFDataUnit outputConfirmed;
    
    
    @OutputDataUnit(name="outputs_to_verified")
	public RDFDataUnit outputToVerify;
    
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
        //Silk.executeFile(conf, null, Silk.DefaultThreads(), true);
        //LOG.info("Silk finished");
        
        
        //TODO Tomas: dialog - uploader + context.getWorkingDir(), show the uploaded file in textarea, it may be edited
        //TODO Tomas: get outputs from the conf file (parse XML). now we suppose two output files with 
        //hardcoded names in the .xml configuration file.
        
        log.info("Silk is about to be executed");
          try {
             //logger.debug(perlIntro + "jtagger/txt2vxml.pl /tmp/jtagger/txt /tmp/jtagger/txt_source/judikatura.zakon.txt");
            Process p = Runtime.getRuntime().exec("java -DconfigFile=/Users/tomasknap/Documents/PROJECTS/ETL-SWProj/intlib/target/silk_2.5.2/be-sameAs.xml -jar /Users/tomasknap/Documents/PROJECTS/ETL-SWProj/intlib/target/silk_2.5.2/silk.jar");
            printProcessOutput(p);
           } catch (IOException ex) {
             log.error(ex.getLocalizedMessage());
              context.sendMessage(MessageType.ERROR, "Problem executing Silk: "
					+ ex.getMessage());
         }
        
        log.info("Silk was executed");
       
        
       
                    
      
        log.info("Output 'confirmed links' is being prepared");
        try {
            outputConfirmed.addFromTurtleFile(new File("/Users/tomasknap/.silk/output/confirmed.ttl"));
        } catch (RDFException ex) {
            log.error(ex.getLocalizedMessage());
            context.sendMessage(MessageType.ERROR, "RDFException: "
					+ ex.getMessage());
        }
        
        log.info("Output 'to verify links' is being prepared");
        try {
            outputConfirmed.addFromTurtleFile(new File("/Users/tomasknap/.silk/output/verify.ttl"));
        } catch (RDFException ex) {
            log.error(ex.getLocalizedMessage());
            context.sendMessage(MessageType.ERROR, "RDFException: "
					+ ex.getMessage());
        }
       
        
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
	


   private static void printProcessOutput(Process process) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
              log.debug(line);
            }
            in.close();

            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = "";
            while ((line = in.readLine()) != null) {
              log.debug(line);
            }
            in.close();
        }
        catch (Exception e) {
            log.debug("Vynimka... " + e);
        }
    }

}
  //File outputFile = new File(config.getXmlFile() + ".ttl");
        //LinkSpecification ls = new LinkSpecification(, null, null, null, null, null)
        //LinkingConfig lc = new LinkingConfig(null, null, null, null, null);
        
        //Silk.executeLinkSpec(null, null, i, true);
       
        //RuntimeConfig c = new RuntimeConfig()
        //LoadTask l = new LoadTask(null, null)