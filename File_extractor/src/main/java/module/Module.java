package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.repository.LocalRepo;
import org.openrdf.rio.RDFFormat;

/**
 * TODO Change super class to desired one, you can choose from the following:
 * GraphicalExtractor, GraphicalLoader, GraphicalTransformer
 */
public class Module implements GraphicalExtractor {

    private LocalRepo repository = null;
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
        // ...
        this.config.setValue(Config.Path.name(), "");
        this.config.setValue(Config.FileSuffix.name(), RDFFormat.RDFXML.toString());
        this.config.setValue(Config.OnlyThisSuffix.name(), new Boolean(false));
        
    }

    public Type getType() {
        return Type.EXTRACTOR;
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
    private String getPath() {
        String path = (String) config.getValue(Config.Path.name());

        return path;
    }

    private String getFileSuffix() {
        String suffix = (String) config.getValue(Config.FileSuffix.name());

        return suffix;
    }

    private boolean isOnlySuffixUsed() {
        boolean useSuffix = (Boolean) config.getValue(Config.OnlyThisSuffix.name());

        return useSuffix;
    }

    public void extract(ExtractContext context) throws ExtractException {

        final String baseURI = "";
        final String path = getPath();
        final String suffix = getFileSuffix();
        final boolean useOnlyThisSuffix = isOnlySuffixUsed();

        repository.extractRDFfromXMLFileToRepository(path, suffix, baseURI, useOnlyThisSuffix);
    }

    public LocalRepo getLocalRepo() {
        return repository;
    }

    public void setLocalRepo(LocalRepo localRepo) {
        repository = localRepo;
    }
}
