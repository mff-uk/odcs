package module;

import cz.cuni.xrg.intlib.commons.data.rdf.RDFFormatType;
import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.commons.data.rdf.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import org.openrdf.rio.RDFFormat;

/**
 * @author Jiri Tomes
 */
public class File_loader implements GraphicalLoader {

    private RDFDataRepository repository = null; // LocalRDFRepo.createLocalRepo();
    /**
     * Configuration component.
     */
    private gui.ConfigDialog configDialog = null;
    /**
     * DPU configuration.
     */
    private Configuration config = null;

    public File_loader() {
    }

    @Override
    public void saveConfigurationDefault(Configuration configuration) {
        configuration.setValue(Config.FileName.name(), "");
        configuration.setValue(Config.DirectoryPath.name(), "");
        configuration.setValue(Config.RDFFileFormat.name(), RDFFormatType.AUTO);
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

    private RDFFormat getRDFFormat() throws NotSupporteRDFFormatException {
        RDFFormatType enumFormatType = (RDFFormatType) config.getValue(Config.RDFFileFormat.name());

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

        return path;
    }

    private String getFileName() {
        String fileName = (String) config.getValue(Config.FileName.name());

        return fileName;
    }

    private boolean hasUniqueName() {
        Boolean isNameUnique = (Boolean) config.getValue(Config.DiffName.name());

        return isNameUnique;
    }

    /**
     * Implementation of module functionality here.
     *
     */
    @Override
    public void load(LoadContext context) throws LoadException {
        try {

            String directoryPath = getDirectoryPath();
            String fileName = getFileName();
            RDFFormat format = getRDFFormat();
            boolean isNameUnique = hasUniqueName();
            boolean canFileOverwritte = true;

            repository.loadRDFfromRepositoryToXMLFile(directoryPath, fileName, format, canFileOverwritte, isNameUnique);

        } catch (CannotOverwriteFileException ex) {
            System.err.println(ex.getMessage());
        } catch (NotSupporteRDFFormatException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public RDFDataRepository getRDFRepo() {
        return repository;
    }

    @Override
    public void setRDFRepo(RDFDataRepository newRepo) {
        repository = newRepo;
    }
}
