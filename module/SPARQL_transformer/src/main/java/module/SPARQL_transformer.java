package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;

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
    	RDFDataRepository intputRepository = null;
    	RDFDataRepository outputRepository = null;
    	
    	// get intput repository
    	if (context.getInputs().isEmpty()) {
    		throw new TransformException("Missing inputs!");
    	}    	
    	DataUnit dataUnit = context.getInputs().get(0);
    	if (dataUnit.getType() == DataUnitType.RDF) {
    		intputRepository = (RDFDataRepository) dataUnit;
    	} else {
    		// wrong input ..
    		throw new TransformException("Wrong input type " + dataUnit.getType().toString() + " instead of RDF.");
    	}
    	// create output repository
    	outputRepository = (RDFDataRepository)context.getDataUnitFactory().create(DataUnitType.RDF);
    	context.addOutputDataUnit(outputRepository);    	
    	    	
        final String updateQuery = getUpdateQuery();
        //repository.transformUsingSPARQL(updateQuery);
// TODO: Jirka, ask Petyr for more detail        
    }
}
