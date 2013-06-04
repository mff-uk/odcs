package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.configuration.Config;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import java.util.List;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class SPARQL_transformer implements GraphicalTransformer, DPUExecutive {

    /**
     * Config component.
     */
    private gui.ConfigDialog configDialog = null;
    /**
     * DPU configuration.
     */
    private Config config = null;

    public SPARQL_transformer() {
    }

    @Override
    public void saveConfigurationDefault(Config configuration) {
        configuration.setValue(Config.SPARQL_Update_Query.name(), "CONSTRUCT {?s ?p ?o} where {?s ?p ?o}");
    }

    @Override
    public DpuType getType() {
        return DpuType.TRANSFORMER;

    }

    @Override
    public CustomComponent getConfigurationComponent(Config configuration) {
        // does dialog exist?
        if (this.configDialog == null) {
            // create it
            this.configDialog = new ConfigDialog();
            this.configDialog.setConfiguration(configuration);
        }
        return this.configDialog;
    }

    @Override
    public void loadConfiguration(Config configuration)
            throws ConfigException {
        // 
        if (this.configDialog == null) {
        } else {
            // get configuration from dialog
            this.configDialog.setConfiguration(configuration);
        }
    }

    @Override
    public void saveConfiguration(Config configuration) {
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

            intputRepository.copyAllDataToTargetRepository(outputRepository);
            outputRepository.transformUsingSPARQL(updateQuery);

            context.addOutputDataUnit(outputRepository);

        } else {
            throw new TransformException("Transform context " + context + " is null");
        }
    }
}
