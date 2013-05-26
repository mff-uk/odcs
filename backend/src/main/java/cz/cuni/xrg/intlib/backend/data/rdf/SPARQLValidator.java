package cz.cuni.xrg.intlib.backend.data.rdf;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ValidityReport;

/**
 * Class responsible to find out, if sparql queries are valid or not.
 *
 * @author Jiri Tomes
 *
 */
public class SPARQLValidator {

    private String query;

    public SPARQLValidator(String query) {
        this.query = query;
    }

    /**
     * Method for detection right syntax of sparql query.
     *
     * @return true, if query is valid, false otherwise.
     */
    public boolean isQueryValid() {

        /*TODO - bad jena documentation, no examples on the web. */
        Model model = ModelFactory.createDefaultModel();

        InfModel infmodel = ModelFactory.createRDFSModel(model);
        ValidityReport validity = infmodel.validate();
        return validity.isValid();

    }
}
