package module;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFFormatType;
import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
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
public class File_loader implements GraphicalLoader, DPUExecutive {

    /**
     * Configuration component.
     */
    private gui.ConfigDialog configDialog = null;
    /**
     * DPU configuration.
     */
    private Configuration config = null;
    /**
     * Logger class.
     */
    private Logger logger = LoggerFactory.getLogger(File_loader.class);

    @Override
    public void saveConfigurationDefault(Configuration configuration) {
        configuration.setValue(Config.FileName.name(), "");
        configuration.setValue(Config.DirectoryPath.name(), "");
        configuration.setValue(Config.RDFFileFormat.name(), RDFFormatType.AUTO);
        configuration.setValue(Config.DiffName.name(), (Boolean) false);
    }

    @Override
    public DpuType getType() {
        return DpuType.LOADER;

    }

    @Override
    public CustomComponent getConfigurationComponent(Configuration configuration) {
        // does dialog exist?
        if (this.configDialog == null) {
            // create it
            this.configDialog = new ConfigDialog();
            this.configDialog.setConfiguration(configuration);
        }
        return this.configDialog;
    }

    @Override
    public void loadConfiguration(Configuration configuration)
            throws ConfigurationException {
        // 
        logger.debug("Loading configuration..");
        if (this.configDialog == null) {
        } else {
            // get configuration from dialog
            this.configDialog.setConfiguration(configuration);
        }
    }

    @Override
    public void saveConfiguration(Configuration configuration) {
        this.config = configuration;
        if (this.configDialog == null) {
        } else {
            // also set configuration for dialog
            this.configDialog.getConfiguration(this.config);
        }
    }

    /**
     * Implementation of module functionality here.
     *
     */
    private RDFFormat getRDFFormat() throws NotSupporteRDFFormatException {
        RDFFormatType enumFormatType = (RDFFormatType) config.getValue(Config.RDFFileFormat.name());

        if (enumFormatType == null) {
            enumFormatType = RDFFormatType.AUTO;
        }

        logger.debug("format: " + enumFormatType.toString());

        switch (enumFormatType) {
            case AUTO: {
                String fileName = getFileName();

                RDFFormat format = RDFFormat.forFileName(fileName);
                if (format == null) {
                    format = RDFFormat.RDFXML;
                }

                return format;
            }
            case RDFXML: {
                return RDFFormat.RDFXML;
            }
            case N3: {
                return RDFFormat.N3;
            }
            case TRIG: {
                return RDFFormat.TRIG;
            }

            case TTL: {
                return RDFFormat.TURTLE;
            }

        }

        throw new NotSupporteRDFFormatException();
    }

    private String getDirectoryPath() {
        String path = (String) config.getValue(Config.DirectoryPath.name());
        logger.debug("DirectoryPath: " + path);
        return path;
    }

    private String getFileName() {
        String fileName = (String) config.getValue(Config.FileName.name());
        logger.debug("FileName: " + fileName);
        return fileName;
    }

    private boolean hasUniqueName() {
        if (config.getValue(Config.DiffName.name()) == null) {
            logger.error("DiffName: unset");
            return false;

        } else {
            Boolean isNameUnique = (Boolean) config.getValue(Config.DiffName.name());
            logger.debug("DiffName: " + isNameUnique);
            return isNameUnique;
        }
    }

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

        String directoryPath = getDirectoryPath();
        String fileName = getFileName();
        RDFFormat format = getRDFFormat();
        boolean isNameUnique = hasUniqueName();
        boolean canFileOverwritte = true;

        try {
            repository.loadRDFfromRepositoryToXMLFile(directoryPath, fileName, format, canFileOverwritte, isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            throw new LoadException(ex);
        }
    }
}
