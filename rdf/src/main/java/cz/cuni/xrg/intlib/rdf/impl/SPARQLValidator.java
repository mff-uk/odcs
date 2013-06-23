package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.interfaces.Validator;


/**
 * Class responsible to find out, if sparql queries are valid or not.
 *
 * @author Jiri Tomes
 *
 */
public class SPARQLValidator implements Validator {

    private String query;
    private String message;

    public SPARQLValidator(String query) {
        this.query = query;
        this.message = "";
    }

    /**
     * Method for detection right syntax of sparql query.
     *
     * @return true, if query is valid, false otherwise.
     */
    @Override
    public boolean isQueryValid() {

        boolean isValid = true;
        LocalRDFRepo emptyRepo= LocalRDFRepo.createLocalRepo();
        try {
            emptyRepo.makeQueryOverRepository(query);
        } catch (InvalidQueryException e) {
            message = e.getCause().getMessage();
            isValid = false;
        }

        return isValid;

    }

    /**
     * String message describes syntax problem of SPARQL query.
     *
     * @return empty string, when SPARQL query is valid
     */
    @Override
    public String getErrorMessage() {
        return message;
    }
}
