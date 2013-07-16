package cz.cuni.mff.xrg.intlib.loader.file;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
//import cz.cuni.xrg.intlib.commons.module.data.InputHelper;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class FileLoader implements Load,
		Configurable<FileLoaderConfig>, ConfigDialogProvider<FileLoaderConfig> {

	/**
	 * DPU configuration.
	 */
	private FileLoaderConfig config = new FileLoaderConfig();

	/**
	 * Logger class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(FileLoader.class);

	@Override
	public void load(LoadContext context) throws LoadException {

		// get input repository
		//RDFDataRepository repository = 
		//		InputHelper.getInput(context.getInputs(), 0, RDFDataRepository.class);

		RDFDataRepository repository = (RDFDataRepository) context.getInputs().get(0); 
		
		String directoryPath = config.DirectoryPath;
		String fileName = config.FileName;

		RDFFormatType formatType=config.RDFFileFormat;

		boolean isNameUnique = config.DiffName;
		boolean canFileOverwritte = true;

		try {
			repository.loadRDFfromRepositoryToFile(directoryPath, fileName,
					formatType, canFileOverwritte, isNameUnique);
		} catch (CannotOverwriteFileException ex) {
			throw new LoadException(ex);
		}
	}

	@Override
	public AbstractConfigDialog<FileLoaderConfig> getConfigurationDialog() {
		return new FileLoaderDialog();
	}

	@Override
	public void configure(FileLoaderConfig c) throws ConfigException {
		config = c;
	}

	@Override
	public FileLoaderConfig getConfiguration() {
		return config;
	}
}
