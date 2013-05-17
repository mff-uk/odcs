package cz.cuni.xrg.intlib.backend.data;

import java.io.File;

import cz.cuni.xrg.intlib.backend.data.rdf.LocalRDFRepo;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextWriter;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitFactory;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Implementation of DataUnitFactory.
 *  
 * @author Petyr
 *
 */
public class DataUnitFactoryImpl implements DataUnitFactory {

	/**
	 * Related context id, is unique.
	 */	
	private String id;
	
	/**
	 * Manage mapping context into execution's directory. 
	 */
	private ExecutionContextWriter contextWriter;
	
	/**
	 * Instance of DPU for which is this DataUnitFactory.
	 */
	private DPUInstance dpuInstance;	
	
	/**
	 * Counter, can be use when generating sub folder names for new DataUnits.
	 */
	private int counter;
	
	/**
	 * Base constructor..
	 * @param id Unique id (Context id.)
	 * @param storageDirectory The folder does not have to exist.
	 */
	public DataUnitFactoryImpl(String id, ExecutionContextWriter contextWriter, DPUInstance dpuInstance) {
		this.id = id;
		this.contextWriter = contextWriter;
		this.dpuInstance = dpuInstance;
		this.counter = 0;
	}
	
	@Override
	public DataUnit create(DataUnitType type) {
		return create(type, false);
	}
	
	@Override
	public DataUnit create(DataUnitType type, boolean mergePrepare) {
		// increase counter
		++counter;
		// based on type ..
		switch(type) {
			case RDF: {
				LocalRDFRepo repository = new LocalRDFRepo();
				// get directory
				File workingDirectory = contextWriter.createDirForDataUnit(dpuInstance, type, counter);
				repository.createNew(id, workingDirectory, mergePrepare);			
				return repository;
			}
		}
		return null;
	}

}
