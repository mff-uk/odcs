package cz.cuni.xrg.intlib.auxiliaries;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.module.GraphicalExtractor;
import cz.cuni.xrg.intlib.commons.module.GraphicalLoader;
import cz.cuni.xrg.intlib.commons.module.GraphicalTransformer;

/**
 * Provide function that enable obtain Configuration dialog 
 * from module (DPU) in easy way.
 * 
 * @author Petyr
 *
 */
public class ModuleDialogGetter {

	/**
	 * Prevent from creating instance.
	 */
	private ModuleDialogGetter() {
		
	}
	
	/**
	 * Return configuration dialog for given DPU. 
	 * @param dpuExewcutive
	 * @return configuration dialog or null
	 */
	public static CustomComponent getDialog(DPUExecutive dpuExewcutive) {
		CustomComponent confComponent = null;
		// get DPU type, recast, get configuration component and return it
		switch(dpuExewcutive.getType()) {
		case EXTRACTOR:
			GraphicalExtractor graphExtract = (GraphicalExtractor)dpuExewcutive;
			confComponent = graphExtract.getConfigurationComponent();
			break;
		case LOADER:
			GraphicalLoader graphLoader = (GraphicalLoader)dpuExewcutive;
			confComponent = graphLoader.getConfigurationComponent();
			break;
		case TRANSFORMER:
			GraphicalTransformer graphTrans = (GraphicalTransformer)dpuExewcutive;
			confComponent = graphTrans.getConfigurationComponent();
			break;
		default:
			confComponent = null;
			break;
		}
		return confComponent;
	}
	
}
