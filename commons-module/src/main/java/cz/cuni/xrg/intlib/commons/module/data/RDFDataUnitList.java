package cz.cuni.xrg.intlib.commons.module.data;

import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.rdf.data.RDFDataUnit;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

/**
 * Helper class for easy manipulation with {@link DataUnitList} for 
 * {@link RDFDataRepository} class.
 * 
 * @see {@link DataUnitList}
 * @author Petyr
 *
 */
public class RDFDataUnitList {

	/**
	 * Create instance of {@link DataUnitList} for working with DataUnits
	 * that are stored in given context. The DataUnits must have type
	 * {@link RDFDataUnit}
	 * @param context
	 * @return
	 */	
	public static DataUnitList<RDFDataUnit> create(LoadContext context) {
		return new DataUnitList<>(context.getInputs()).filterByClass(RDFDataUnit.class);
	}	
	
	/**
	 * Create instance of {@link DataUnitList} for working with DataUnits
	 * that are stored in given context. The DataUnits must have type
	 * {@link RDFDataUnit}
	 * @param context
	 * @return
	 */		
	public static DataUnitList<RDFDataUnit> create(TransformContext context) {
		return new DataUnitList<>(context.getInputs()).filterByClass(RDFDataUnit.class);
	}		
	
}
