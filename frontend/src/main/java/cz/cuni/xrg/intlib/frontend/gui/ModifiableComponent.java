package cz.cuni.xrg.intlib.frontend.gui;

/**
 * Represents a stateful component which is modifiable and the modifications may
 * be saved.
 *
 * @author Jan Vojt
 */
public interface ModifiableComponent {
	
	/**
	 * Method for testing if ViewCoponent was modified since last save.
	 * 
	 * @return Is component modified?
	 */
	public boolean isModified();

	/**
	 * Handler that applies changes performed with this component.
	 * 
	 * @return success of the operation
	 */
	public boolean saveChanges();
	
}
