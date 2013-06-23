package cz.cuni.mff.xrg.intlib.loader.file;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.web.*;
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
			throw new LoadException("Wrong input type " + dataUnit.getType()
					.toString() + " instead of RDF.");
		}

		String directoryPath = config.DirectoryPath;
		String fileName = config.FileName;

		RDFFormat format;

		switch (config.RDFFileFormat) {
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
			repository.loadRDFfromRepositoryToXMLFile(directoryPath, fileName,
					format, canFileOverwritte, isNameUnique);
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
