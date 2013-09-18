package cz.cuni.xrg.intlib.frontend.auxiliaries.dpu;

/**
 * Exception used by {@link DPUTemplateWrap#replace(java.io.File)} 
 * to indicate that the DPU's replace failed.
 * 
 * @author Petyr
 *
 */
public class DPUReplaceException extends Exception {

	public DPUReplaceException(String message) {
		super(message);
	}
	
}
