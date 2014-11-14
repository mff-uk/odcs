package cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.virtuoso;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.RDFDataUnitFactory;

public class VirtuosoRDFDataUnitFactory implements RDFDataUnitFactory {

    private String url;

    private String user;

    private String password;

    @Override
    public ManagableRdfDataUnit create(String pipelineId, String dataUnitName, String dataGraph) {
        return new VirtuosoRDFDataUnit(url, user, password, dataUnitName, dataGraph);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void clean(String pipelineId) {
        // no-op
    }

    @Override
    public void release(String pipelineId) {
        // no-op
    }
    
}
