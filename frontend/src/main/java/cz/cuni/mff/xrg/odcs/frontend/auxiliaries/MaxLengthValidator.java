package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import com.vaadin.data.Validator;

/**
 * Validator for checking maximum length of input. Maximum length can be set in
 * constructor.
 *
 * @author Bogo
 */
public final class MaxLengthValidator implements Validator {

	public static int NAME_LENGTH = 2048;
	public static int DPU_NAME_LENGTH = 45;
	public static int DESCRIPTION_LENGTH = 512;
	private int maxLength = 1000;

	/**
	 * Constructor.
	 *
	 * @param maxLength Maximum length of input.
	 */
	public MaxLengthValidator(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * Validates value for maximum length.
	 *
	 * @param value value to validate
	 * @throws com.vaadin.data.Validator.InvalidValueException If maximum length
	 * is exceeded.
	 */
	@Override
	public void validate(Object value) throws InvalidValueException {
		if (value.getClass() == String.class) {
			String stringValue = (String) value;
			if (stringValue.length() > maxLength) {
				throw new Validator.InvalidValueException(String.format("Max length is %d characters! Current length is %d characters!", maxLength, stringValue.length()));
			}
		}
	}
}
