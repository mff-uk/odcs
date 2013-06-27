package cz.cuni.xrg.intlib.backend.data;

import java.io.File;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

/**
 * Create new DataUnits.
 *
 * @author Petyr
 *
 */
public class DataUnitFactory {

	/**
	 * Related context id, is unique.
	 */
	private String id;
	
	/**
	 * DPU instance record.
	 */
	private DPUInstanceRecord instance;
	
	/**
	 * Manage mapping context into execution's directory. 
	 */
	private ExecutionContextInfo context;
	
	/**
	 * Base constructor..
	 *
	 * @param id Unique id (Context id.)
	 */
	public DataUnitFactory(String id, DPUInstanceRecord instance, ExecutionContextInfo context) {
		this.id = id;
		this.instance = instance;
		this.context = context;
	}

	/**
	 * Create input DataUnit.
	 *
	 * @param type DataUnit's type.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	public DataUnitContainer createInput(DataUnitType type) throws DataUnitCreateException {
		return create(type, true);
	}

	/**
	 * Create input DataUnit with given configuration.
	 *
	 * @param type   DataUnit's type.
	 * @param config Initial configuration.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	public DataUnitContainer createInput(DataUnitType type, Object config) throws DataUnitCreateException {
		return create(type, true, config);
	}

	/**
	 * Create output DataUnit.
	 *
	 * @param type DataUnit's type.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	public DataUnitContainer createOutput(DataUnitType type) throws DataUnitCreateException {
		return create(type, false);
	}

	/**
	 * Create output DataUnit.
	 *
	 * @param type   DataUnit's type.
	 * @param config Initial configuration.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	public DataUnitContainer createOutput(DataUnitType type, Object config) throws DataUnitCreateException {
		return create(type, false, config);
	}

	/**
	 * Create DataUnit.
	 *
	 * @param type  DataUnit's type.
	 * @param input Should be DataUnit created as input?
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	private DataUnitContainer create(DataUnitType type, boolean input) throws DataUnitCreateException {
		Integer index = context.createOutput(instance, "", type);
		File tmpDir = context.getDataUnitTmp(instance, index);
		
		switch (type) {
			case RDF:
			case RDF_Local:
				// create DataUnit
				RDFDataRepository localRepository = LocalRDFRepo
						.createLocalRepo(tmpDir.getAbsolutePath(), id);
				// create container with DataUnit and index
				return new DataUnitContainer(localRepository, index);
			case RDF_Virtuoso:
				break;
		}
		throw new DataUnitCreateException("Unknown DataUnit type.");
	}

	/**
	 * Create DataUnit.
	 *
	 * @param type  DataUnit's type. 
	 * @param input Should be DataUnit created as input?
	 * @param config COnfiguration for DataUnit.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	private DataUnitContainer create(DataUnitType type, boolean input, Object config)
			throws DataUnitCreateException {
		switch (type) {
			case RDF:
			case RDF_Local: // RDF_Local does't support non-default configuration
				throw new DataUnitCreateException("Can't create RDF_Local with configuration.");
			case RDF_Virtuoso:
				break;
		}
		throw new DataUnitCreateException("Unknown DataUnit type.");
	}
}
