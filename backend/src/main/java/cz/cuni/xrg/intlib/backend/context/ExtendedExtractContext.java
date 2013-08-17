package cz.cuni.xrg.intlib.backend.context;

import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;

/**
 * Extended extractor context.
 * 
 * @author Petyr
 * 
 */
public class ExtendedExtractContext
	extends ExtendedContext implements ExtractContext {

	/**
	 * Manager for output DataUnits.
	 */
	private DataUnitManager dataUnitManager;	
	
	public ExtendedExtractContext() {
		super();
		this.dataUnitManager = DataUnitManager.createOutputManager(dpuInstance,
				dataUnitFactory, context, getGeneralWorkingDir(), appConfig);		
	}
	
	/**
	 * Return access to list of all output DataUnits.
	 * 
	 * @return
	 */
	public List<DataUnit> getOutputs() {
		return dataUnitManager.getDataUnits();
	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type, String name)
			throws DataUnitCreateException {
		return dataUnitManager.addDataUnit(type, name);
	}

	@Override
	public DataUnit addOutputDataUnit(DataUnitType type,
			String name,
			Object config) throws DataUnitCreateException {
		return dataUnitManager.addDataUnit(type, name, config);
	}

	@Override
	public void save() {
		dataUnitManager.save();		
	}

	@Override
	public void release() {
		dataUnitManager.release();		
	}

	@Override
	public void delete() {
		dataUnitManager.delete();
		deleteDirectories();		
	}

	@Override
	public void reload() throws DataUnitException {
		dataUnitManager.reload();		
	}

}
