package cz.cuni.mff.xrg.odcs.frontend.gui.validator;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;

/**
 * Create commonly used validators.
 * 
 * @author Škoda Petr
 */
public class ValidatorFactory {
	
	private ValidatorFactory() { }
	
	/**
	 * Create validator that validates that the string is shorter then given
	 * value.
	 * 
	 * @param name Name of the property that is validated.
	 * @param max Max length of the value.
	 * @return 
	 */
	public static Validator CreateMaxLength(String name, int max) {
		String msg = String.format("The %s must be shorter then %d", name, max);
		return new StringLengthValidator(msg, 0, max, true);		
	}
	
}
