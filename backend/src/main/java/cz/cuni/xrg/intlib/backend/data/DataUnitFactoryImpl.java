package cz.cuni.xrg.intlib.backend.data;

import java.io.File;

import cz.cuni.xrg.intlib.backend.data.rdf.LocalRDFRepo;
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
	 * Root to storage directory where DataUnit can place their data.
	 */
	private File storageDirectory;
	
	/**
	 * Counter, can be use when generating sub folder names for new DataUnits.
	 */
	private int counter;
	
	/**
	 * Base constructor..
	 * @param id Unique id (Context id.)
	 * @param storageDirectory The folder does not have to exist.
	 */
	public DataUnitFactoryImpl(String id, File storageDirectory) {
		this.id = id;
		this.storageDirectory = storageDirectory;
		this.counter = 0;
	}
	
	@Override
	public DataUnit create(DataUnitType type) {
		// If we need to use storageDirectory make sure it exist first
		
		switch(type) {
		case RDF:
			// TODO: Add some unique parameters ?
			return new LocalRDFRepo();
		}
		return null;
	}

}
