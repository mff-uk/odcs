package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import org.springframework.context.i18n.LocaleContextHolder;

import com.vaadin.data.Validator;

import cz.cuni.mff.xrg.odcs.frontend.FrontendMessages;

/**
 * Validator for checking maximum length of input. Maximum length can be set in
 * constructor.
 * 
 * @author Bogo
 */
public final class MaxLengthValidator implements Validator {

    /**
     * Max length of description.
     */
    public static int DESCRIPTION_LENGTH = 512;

    private int maxLength = 1000;

    /**
     * Constructor.
     * 
     * @param maxLength
     *            Maximum length of input.
     */
    public MaxLengthValidator(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Validates value for maximum length.
     * 
     * @param value
     *            value to validate
     * @throws com.vaadin.data.Validator.InvalidValueException
     *             If maximum length
     *             is exceeded.
     */
    @Override
    public void validate(Object value) throws InvalidValueException {
        FrontendMessages messages = new FrontendMessages(LocaleContextHolder.getLocale(), DecorationHelper.class.getClassLoader());
        if (value.getClass() == String.class) {
            String stringValue = (String) value;
            if (stringValue.length() > maxLength) {
                throw new Validator.InvalidValueException(messages.getString("MaxLengthValidator.exception", maxLength, stringValue.length()));
            }
        }
    }
}
