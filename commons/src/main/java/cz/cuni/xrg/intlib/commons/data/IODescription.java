package cz.cuni.xrg.intlib.commons.data;

import java.util.List;

/**
 * This interface can be used to give DPU possibility of describing it's 
 * inputs and outputs in terms of named {@link DataUnit}s. The implementation
 * of this interface is obligatory. 
 * 
 * @author Petyr
 *
 */
public interface IODescription {
	
	/**
	 * Return list of excepted input {@link DataUnit}'s names.
	 * @return
	 */
	public List<String> getInputNames();
	
	/**
	 * Return list of guaranteed output {@link DataUnit}'s names.
	 * @return
	 */	
	public List<String> getOutputNames();
	
}
