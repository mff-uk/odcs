package cz.cuni.mff.xrg.odcs.rdf.interfaces;

/**
 *
 * Is responsible for right validation of data.
 *
 * @author Jiri Tomes
 */
public interface DataValidator {

	/**
	 * Method for detection right syntax of data.
	 *
	 * @return true, if data are valid, false otherwise.
	 */
	public boolean areDataValid();
}
