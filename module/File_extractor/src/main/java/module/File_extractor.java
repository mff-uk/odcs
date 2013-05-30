package module;

import gui.ConfigDialog;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.module.dpu.AbstractExtractor;
import cz.cuni.xrg.intlib.commons.module.gui.AbstractConfigDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jiri Tomes
 * @author Petyr
 */
public class File_extractor extends AbstractExtractor {

    /**
     * Logger class.
     */
    private Logger logger = LoggerFactory.getLogger(File_extractor.class);    
    
    public File_extractor() {
    }

    @Override
    public void saveConfigurationDefault(Configuration configuration) {
    	configuration.setValue(Config.OnlyThisText.name(), "");
    	configuration.setValue(Config.FileSuffix.name(), ".rdf");
    	configuration.setValue(Config.Path.name(), "");
    	configuration.setValue(Config.OnlyThisSuffix.name(), false);    	
    }    
    
	@Override
	public AbstractConfigDialog createConfigurationDialog() {
		return new ConfigDialog();
	}    

    /**
     * Implementation of module functionality here.
     *
     */
    
    private String getPath() {
        String path = (String) config.getValue(Config.Path.name());
        logger.debug("Path: " + path);
        return path;
    }

    private String getFileSuffix() {
        String suffix = (String) config.getValue(Config.FileSuffix.name());
        logger.debug("FileSuffix: " + suffix);
        return suffix;
    }

    private boolean isOnlySuffixUsed() {
        boolean useSuffix = (Boolean) config.getValue(Config.OnlyThisSuffix.name());
        logger.debug("OnlyThisSuffix: " + useSuffix);
        return useSuffix;
    }

    @Override
    public void extract(ExtractContext context) throws ExtractException {
    	RDFDataRepository repository = null;
    	// create repository
    	repository = (RDFDataRepository)context.getDataUnitFactory().create(DataUnitType.RDF);
    	if (repository == null) {
    		throw new ExtractException("DataUnitFactory returned null.");
    	}
    	
    	context.addOutputDataUnit(repository);
    	
        final String baseURI = "";
        final String path = getPath();
        final String suffix = getFileSuffix();
        final boolean useOnlyThisSuffix = isOnlySuffixUsed();

        repository.extractRDFfromXMLFileToRepository(path, suffix, baseURI, useOnlyThisSuffix);
    }

}
