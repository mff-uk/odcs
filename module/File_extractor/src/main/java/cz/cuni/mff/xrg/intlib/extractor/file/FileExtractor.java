package cz.cuni.mff.xrg.intlib.extractor.file;


import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 * @author Petyr
 */
public class FileExtractor implements Extract, 
	Configurable<FileExtractorConfig>, ConfigDialogProvider<FileExtractorConfig> {

    /**
     * Logger class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FileExtractor.class);

    private FileExtractorConfig config = new FileExtractorConfig();
    
    public FileExtractor() { }

    @Override
    public void extract(ExtractContext context) throws ExtractException {

    	RDFDataRepository repository;
		try {
			repository = (RDFDataRepository) context.addOutputDataUnit(DataUnitType.RDF);
		} catch (DataUnitCreateException e) {
			throw new ExtractException("Can't create DataUnit", e);
		}
		
        if (repository == null) {
            throw new ExtractException("DataUnitFactory returned null.");
        }

        final String baseURI = "";

        repository.extractRDFfromXMLFileToRepository(
        		config.Path, config.FileSuffix, baseURI, config.OnlyThisSuffix);
    }

	@Override
	public AbstractConfigDialog<FileExtractorConfig> getConfigurationDialog() {
		return new FileExtractorDialog();
	}

	@Override
	public void configure(FileExtractorConfig c) throws ConfigException {
		config = c;		
	}

	@Override
	public FileExtractorConfig getConfiguration() {
		return config;
	}
}
