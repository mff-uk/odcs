package cz.cuni.mff.xrg.intlib.loader.rdf;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;

/**
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class RDFLoaderConfig implements DPUConfigObject {
    public String SPARQL_endpoint;
    public String Host_name;
    public String Password;
    public List<String> GraphsUri = new LinkedList<>();
    public WriteGraphType Options; 
}
