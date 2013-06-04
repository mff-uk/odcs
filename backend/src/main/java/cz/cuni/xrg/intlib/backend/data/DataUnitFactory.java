package cz.cuni.xrg.intlib.backend.data;

import java.io.File;

import cz.cuni.xrg.intlib.backend.data.rdf.LocalRDF;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContext;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Implementation of DataUnitFactory.
 *
 * @author Petyr
 *
 */
public class DataUnitFactory  {

	/**
	 * Related context id, is unique.
	 */
	private String id;

	/**
	 * Manage mapping context into execution's directory. 
	 */
	private ExecutionContext contextWriter;
	
	/**
	 * Instance of DPURecord for which is this DataUnitFactory.
	 */
	private DPUInstanceRecord dpuInstance;	
	
	/**
	 * Counter, can be use when generating sub folder names for new DataUnits.
	 */
	private int counter;

	/**
	 * Base constructor..
	 * @param id Unique id (Context id.)
	 * @param storageDirectory The folder does not have to exist.
	 * @param dpuInstance
	 */
	public DataUnitFactory(String id, ExecutionContext contextWriter, DPUInstanceRecord dpuInstance) {
		this.id = id;
		this.contextWriter = contextWriter;
		this.dpuInstance = dpuInstance;
		this.counter = 0;
	}

	/**
	 * Create input DataUnit.
	 * @param type DataUnit's type.
	 * @return Created data unit or null in case of failure.
	 * @throws DataUnitCreateException
	 */
	public DataUnit createInput(DataUnitType type) throws DataUnitCreateException {
		return create(type, true);
	}
	
	/**
	 * Create input DataUnit with given configuration.
	 * @param type DataUnit's type.
	 * @param config Initial configuration.
	 * @return Created data unit or null in case of failure.
	 * @throws DataUnitCreateException
	 */
	public DataUnit createInput(DataUnitType type, Object config) throws DataUnitCreateException {
		return create(type, true, config);
	}	
	
	/**
	 * Create output DataUnit.
	 * @param type DataUnit's type.
	 * @return Created data unit or null in case of failure.
	 * @throws DataUnitCreateException
	 */	
	public DataUnit createOutput(DataUnitType type) throws DataUnitCreateException {
		return create(type, false);
	}
	
	/**
	 * Create output DataUnit.
	 * @param type DataUnit's type.
	 * @param config Initial configuration.
	 * @return Created data unit or null in case of failure.
	 * @throws DataUnitCreateException
	 */		
	public DataUnit createOutput(DataUnitType type, Object config) throws DataUnitCreateException {
		return create(type, false, config);
	}		
		
	/**
	 * Create DataUnit.
	 * @param type DataUnit's type.
	 * @param input Should be DataUnit created as input?
	 * @return Created DataUnit or null in case of failure.
	 * @throws DataUnitCreateException
	 */
	private DataUnit create(DataUnitType type, boolean input) throws DataUnitCreateException {
		++counter;
		switch(type) {
		case RDF:
		case RDF_Local:
			// TODO Jirka: Check this please
			LocalRDF repository = new LocalRDF();
			File workingDirectory = contextWriter.createDirForInput(dpuInstance, DataUnitType.RDF_Local, counter);
			repository.createNew(id, workingDirectory, input);	
			break;
		case RDF_Virtuoso:
			break;
		}
		return null;
	}
	
	/**
	 * Create DataUnit.
	 * @param type DataUnit's type.
	 * @param input Should be DataUnit created as input?
	 * @return Created DataUnit or null in case of failure.
	 * @throws DataUnitCreateException
	 */	
	private DataUnit create(DataUnitType type, boolean input, Object config) throws DataUnitCreateException {
		++counter;
		switch(type) {
		case RDF:
		case RDF_Local: // RDF_Local does't support default configuration
			return null;
		case RDF_Virtuoso:
			break;
		}		
		return null;
	}
	
}
