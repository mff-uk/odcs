package cz.cuni.xrg.intlib.backend.data;

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

	@Override
	public DataUnit create(DataUnitType type) {
		switch(type) {
		case RDF:
			// TODO: Add some uniq parameters ?
			return new LocalRDFRepo();
		}
		return null;
	}

}
