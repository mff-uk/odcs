package cz.cuni.xrg.intlib.commons.module.dpu.auxiliaries;

import java.util.List;

import cz.cuni.xrg.intlib.commons.data.DataUnit;

/**
 * Support manipulation with input DataUnits.
 * 
 * @author Petyr
 */
public class InputHelper {

	private InputHelper() { }

	/**
	 * Try to get and recast input DataUnit to required type.
	 * @param index
	 * @return
	 */
	public static <T extends DataUnit> T getInput(List<DataUnit> inputs, Integer index, Class<T> classType) {
		if (inputs.size() < index) {
			throw new IndexOutOfBoundsException();
		}
		DataUnit dataUnit = inputs.get(index);

		if ( classType.isInstance(dataUnit) ) {
			return classType.cast(dataUnit);
		} else {
			throw new ClassCastException();
		}		
	}	
	
}
