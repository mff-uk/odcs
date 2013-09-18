package cz.cuni.xrg.intlib.frontend.auxiliaries.dpu;

/**
 * Exception used by {@link DPUTemplateWrap#create(java.io.File, String)} 
 * to indicate that the import failed.
 * 
 * @author Petyr
 *
 */
public class DPUCreateException extends Exception {

	public DPUCreateException(String message) {
		super(message);
	}
	
}
