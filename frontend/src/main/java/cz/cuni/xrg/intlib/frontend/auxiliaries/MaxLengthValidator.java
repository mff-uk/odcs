/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.auxiliaries;

import com.vaadin.data.Validator;

/**
 *
 * @author Bogo
 */
public class MaxLengthValidator implements Validator {

	private int maxLength = 1000;

	public MaxLengthValidator(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (value.getClass() == String.class) {
			String stringValue = (String) value;
			if (stringValue.length() > maxLength) {
				throw new Validator.InvalidValueException(String.format("Max length of description is %d characters! Current length is %d characters!", maxLength, stringValue.length()));
			}
		}
	}
}
