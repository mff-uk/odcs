package cz.cuni.mff.xrg.odcs.rdf.interfaces;

import cz.cuni.mff.xrg.odcs.rdf.help.TripleProblem;
import java.util.List;

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

	/**
	 * String message describes syntax problem of data validation.
	 *
	 * @return empty string, when all data are valid.
	 */
	public String getErrorMessage();

	/**
	 *
	 * @return List of TripleProblem describes invalid triples and its cause. If
	 *         all data are valid return empty list.
	 */
	public List<TripleProblem> getFindedProblems();
}
