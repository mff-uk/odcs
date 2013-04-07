package cz.cuni.xrg.intlib.frontend.OSGi;

/**
 * OSGi framework exception.
 * @author Petyr
 *
 */
public class ExceptionOSGi extends RuntimeException {

	/**
	 * User-defined ID in combination with custom serialization code
	 * if the type did undergo structural changes since its first release.
	 */
	private static final long serialVersionUID = 1L;
	
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
