package module;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.commons.data.rdf.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;

import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class File_loader implements  Load, 
	Configurable<FileLoaderConfig>, ConfigDialogProvider<FileLoaderConfig> {
    
    /**
     * DPU configuration.
     */
    private FileLoaderConfig config = new FileLoaderConfig();
    
    /**
     * Logger class.
     */
    private Logger logger = LoggerFactory.getLogger(File_loader.class);

    @Override
    public void load(LoadContext context) throws LoadException {

        //input
        if (context.getInputs().isEmpty()) {
            throw new LoadException("Missing inputs!");
        }

        DataUnit dataUnit = context.getInputs().get(0);

        RDFDataRepository repository = null;

        if (dataUnit instanceof RDFDataRepository) {
            repository = (RDFDataRepository) dataUnit;
        } else {
            // wrong input
            throw new LoadException("Wrong input type " + dataUnit.getType().toString() + " instead of RDF.");
        }

        String directoryPath = config.DirectoryPath;
        String fileName = config.FileName;
        RDFFormat format = null;
        switch(config.RDFFileFormat)
        {
        default:
        case AUTO:
        case RDFXML:
        	format = RDFFormat.RDFXML;
        	break;
        case N3:
        	format = RDFFormat.N3;
        	break;
        case TRIG:
        	format = RDFFormat.TRIG;
        	break;
        case TTL:
        	format = RDFFormat.TURTLE;
        	break;
        }
        boolean isNameUnique = config.DiffName;
        boolean canFileOverwritte = true;

        try {
            repository.loadRDFfromRepositoryToXMLFile(directoryPath, fileName, format, canFileOverwritte, isNameUnique);
        } catch (CannotOverwriteFileException ex) {
            throw new LoadException(ex);
        }
    }

	
    @Override
	public AbstractConfigDialog<FileLoaderConfig> getConfigurationDialog() {
		return new ConfigDialog();
	}

	
    @Override
	public void configure(FileLoaderConfig c) throws ConfigException {
		this.config = c;		
	}

	
    @Override
	public FileLoaderConfig getConfiguration() {
		return config;
	}
}
