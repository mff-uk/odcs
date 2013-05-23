package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import java.util.List;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class SPARQL_transformer implements GraphicalTransformer {

    /**
     * Configuration component.
     */
    private gui.ConfigDialog configDialog = null;
    /**
     * DPU configuration.
     */
    private Configuration config = null;

    public SPARQL_transformer() {
    }

    @Override
    public void saveConfigurationDefault(Configuration configuration) {
        configuration.setValue(Config.SPARQL_Update_Query.name(), "CONSTRUCT {?s ?p ?o} where {?s ?p ?o}");
    }

    @Override
    public DpuType getType() {
        return DpuType.TRANSFORMER;

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

    /**
     * Implementation of module functionality here.
     *
     */
    private String getUpdateQuery() {
        String query = (String) config.getValue(Config.SPARQL_Update_Query.name());

        return query;
    }

    @Override
    public void transform(TransformContext context) throws TransformException {

        if (context != null) {

            List<DataUnit> inputs = context.getInputs();
            // get intput repository
            if (inputs.isEmpty()) {
                throw new TransformException("Missing inputs!");
            }
            DataUnit dataUnit = inputs.get(0);

            RDFDataRepository intputRepository = null;
            if (dataUnit instanceof RDFDataRepository) {
            	intputRepository = (RDFDataRepository) dataUnit;
            } else {
            	throw new TransformException("Wrong input type " + dataUnit.getType().toString() + " expected RDF.");
            }

            // create output repository
            RDFDataRepository outputRepository = (RDFDataRepository) context.getDataUnitFactory().create(DataUnitType.RDF);
            if (outputRepository == null) {
            	throw new TransformException("DataUnitFactory returned null.");
            }
            
            final String updateQuery = getUpdateQuery();
  
            // intputRepository != null because otherwise in wont pas the instanceof test
            intputRepository.copyAllDataToTargetRepository(outputRepository);
            outputRepository.transformUsingSPARQL(updateQuery);
            
            context.addOutputDataUnit(outputRepository);
      
        }
    }
}
