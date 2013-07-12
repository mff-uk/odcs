package cz.cuni.xrg.intlib.commons.module.dpu.auxiliaries;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

/**
 * Helper class for easy manipulation with {@link DataUnitList} for 
 * {@link RDFDataRepository} class.
 * 
 * @see {@link DataUnitList}
 * @author Petyr
 *
 */
public class RDFDataRepositoryList {

	/**
	 * Create instance of {@link DataUnitList} for working with DataUnits
	 * that are stored in given context. The DataUnits must have type
	 * {@link RDFDataRepository}
	 * @param context
	 * @return
	 */	
	public static DataUnitList<RDFDataRepository> create(LoadContext context) {
		return new DataUnitList<DataUnit>(context.getInputs()).FilterByClass(RDFDataRepository.class);
	}	
	
	/**
	 * Create instance of {@link DataUnitList} for working with DataUnits
	 * that are stored in given context. The DataUnits must have type
	 * {@link RDFDataRepository}
	 * @param context
	 * @return
	 */		
	public static DataUnitList<RDFDataRepository> create(TransformContext context) {
		return new DataUnitList<DataUnit>(context.getInputs()).FilterByClass(RDFDataRepository.class);
	}		
	
}
