package cz.cuni.xrg.intlib.backend.context.impl;

import java.util.Iterator;
import java.util.List;

import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.DataUnitMerger;
import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;

/**
 * Very primitive DataUnits merger. Just copy
 * DataUnits from the right to the left.
 * 
 * @author Petyr
 *
 */
public class PrimitiveDataUnitMerger implements DataUnitMerger {

	@Override
	public void merger(List<DataUnit> left, List<DataUnit> right, DataUnitFactory factory) throws ContextException {		
		Iterator<DataUnit> iterRight = right.iterator();

		// add the rest from right
		while (iterRight.hasNext()) {
			DataUnit rightDataUnit = iterRight.next();
			// create new data unit (in context into which we merge)
			DataUnit newDataUnit;
			try {
				// we do not store reverse mapping for inputs
				newDataUnit = factory.createInput(rightDataUnit.getType())
						.getDataUnit();
			} catch (DataUnitCreateException e) {
				throw new ContextException("Failed to create input object.", e);
			}

			// and copy the data
			try {
				newDataUnit.merge(rightDataUnit);
			} catch (IllegalArgumentException e) {
				throw new ContextException(
						"Can't merge data units, type miss match.", e);
			} catch (Exception e) {
				throw new ContextException("Can't merge data units.", e);
			}
			left.add(newDataUnit);
		}

	}

}
