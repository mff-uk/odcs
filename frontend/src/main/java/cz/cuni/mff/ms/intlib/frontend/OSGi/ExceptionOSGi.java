package cz.cuni.mff.ms.intlib.frontend.OSGi;

public class ExceptionOSGi extends RuntimeException {

	/**
	 * Store original exception.
	 */
	protected Exception originalExpcetion;
	
	/**
	 * 
	 * @param message Exception message.
	 * @param ex Original exception.
	 */
	public ExceptionOSGi(String message, Exception ex) {
		super(message);
		this.originalExpcetion = ex;
	}

}
