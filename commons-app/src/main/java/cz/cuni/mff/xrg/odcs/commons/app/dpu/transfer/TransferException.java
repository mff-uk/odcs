package cz.cuni.mff.xrg.odcs.commons.app.dpu.transfer;

/**
 * Exception used by transport service.
 * 
 * @author Škoda Petr
 */
public class TransferException extends Exception {

	public TransferException(String message) {
		super(message);
	}

	public TransferException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
