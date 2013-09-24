package cz.cuni.xrg.intlib.backend.spring;

import static org.mockito.Mockito.mock;

import java.io.File;

import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.commons.data.ManagableDataUnit;

/**
 * Dummy {@link DataUnitFactory}. Does not create any real data unit.
 * @author Petyr
 *
 */
public class DummyDataUnitFactory extends DataUnitFactory {

	@Override
	public ManagableDataUnit create(DataUnitType type,
			String id,
			String name,
			File directory) throws DataUnitCreateException {
		// just return mocked object
		return mock(ManagableDataUnit.class);
	}
	
}
