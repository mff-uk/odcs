package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.module.*;
import cz.cuni.xrg.intlib.repository.FileCannotOverwriteException;
import cz.cuni.xrg.intlib.repository.LocalRepo;
import org.openrdf.rio.RDFFormat;


/**
 * TODO Change super class to desired one, you can choose from the following:
 * GraphicalExtractor, GraphicalLoader, GraphicalTransformer
 */
public class Module implements GraphicalLoader {

    /**
     * Configuration component.
     */
    private gui.ConfigDialog configDialog = null;
    /**
     * DPU configuration.
     */
    private Configuration config = new Configuration();

    public Module() {
        // set initial configuration
        /**
         * TODO Set default (possibly empty but better valid) configuration for
         * your DPU.
         */
        this.config.setValue(Config.NameDPU.name(), "");
        this.config.setValue(Config.Description.name(), "");
        this.config.setValue(Config.FileName.name(), "");
        this.config.setValue(Config.Directory.name(), "");
        this.config.setValue(Config.RDFformat.name(), RDFFormat.RDFXML.toString());
    }

    public Type getType() {
        return Type.LOADER;

    }

    public CustomComponent getConfigurationComponent() {
        // does dialog exist?
        if (this.configDialog == null) {
            // create it
            this.configDialog = new ConfigDialog();
            this.configDialog.setConfiguration(this.config);
        }
        return this.configDialog;
    }

    public Configuration getSettings() throws ConfigurationException {
        if (this.configDialog == null) {
        } else {
            // get configuration from dialog
            Configuration conf = this.configDialog.getConfiguration();
            if (conf == null) {
                // in dialog is invalid configuration .. 
                return null;
            } else {
                this.config = conf;
            }
        }
        return this.config;
    }

    public void setSettings(Configuration configuration) {
        this.config = configuration;
        if (this.configDialog == null) {
        } else {
            // also set configuration for dialog
            this.configDialog.setConfiguration(this.config);
        }
    }

    /**
     * Implementation of module functionality here.
     *
     */
    public void load(LoadContext context) throws LoadException {
        try {
            String path = "C:\\intlib\\Output_Test_Files\\";
            String fileName = "RDF_output.rdf";
            RDFFormat format = RDFFormat.RDFXML;
            boolean canFileOverwritte = true;

            LocalRepo repository = LocalRepo.createLocalRepo();
            repository.loadRDFfromRepositoryToXMLFile(path, fileName, format, canFileOverwritte);
        } catch (FileCannotOverwriteException ex) {
        }
    }
}
