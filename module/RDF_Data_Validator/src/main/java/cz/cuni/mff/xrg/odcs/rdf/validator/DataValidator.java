package cz.cuni.mff.xrg.odcs.rdf.validator;

import java.util.List;

/**
 * Validator is responsible for right data validation.
 * 
 * @author Jiri Tomes
 */
public interface DataValidator {

    /**
     * Method for detection right data syntax.
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
     * Returns list of {@link TripleProblem} describes invalid triples and its
     * cause. If all data are valid return empty list.
     * 
     * @return List of {@link TripleProblem} describes invalid triples and its
     *         cause. If all data are valid return empty list.
     */
    public List<TripleProblem> getFindedProblems();
}
