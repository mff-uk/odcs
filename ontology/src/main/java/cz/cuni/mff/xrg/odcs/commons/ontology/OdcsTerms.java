package cz.cuni.mff.xrg.odcs.commons.ontology;

/**
 * Representation of the odcs ontology
 * 
 * @author tomasknap
 */
public class OdcsTerms {

    /**
     * Predicate for holding text value of a subject (e.g., content of a file in case of subject URI identifying that file)
     */
    public static final String DATA_UNIT_TEXT_VALUE_PREDICATE = "http://linked.opendata.cz/ontology/odcs/textValue";

    /**
     * Predicate for holding XML value of a subject (e.g., content of an XML file in case of subject URI identifying that file)
     */
    public static final String DATA_UNIT_XML_VALUE_PREDICATE = "http://linked.opendata.cz/ontology/odcs/xmlValue";

    /**
     * Predicate holding the URI which should represent the given file in the RDF data unit. Used e.g. in XSLT DPU when producing results as RDF triples.
     */
    public static final String DATA_UNIT_FILE_URI_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/file/fileURI";

    /**
     * Predicate holding path to a file (with certain URI) in a file data unit. File path is used as a file identifier in file data unit.
     */
    public static final String DATA_UNIT_FILE_PATH_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/file/filePath";

    
    public static final String DATA_UNIT_STORE_GRAPH = "http://linked.opendata.cz/ontology/odcs/dataunit";
    public static final String DATA_UNIT_RDF_CONTAINSGRAPH_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/rdf/containsGraph";
    public static final String DATA_UNIT_RDF_WRITEGRAPH_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/rdf/writeGraph";

    public static final String DATA_UNIT_FILELIST_SYMBOLIC_NAME_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/filelist/symbolicName";

    /**
     * Predicate to associate certain subject (URI representing certain file) with an XSLT parameter
     */
    public static final String XSLT_PARAM_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dpu/xslt/param";

    /**
     * XSLT parameter's name
     */
    public static final String XSLT_PARAM_NAME_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dpu/xslt/param/name";

    /**
     * XSLT parameter's value
     */
    public static final String XSLT_PARAM_VALUE_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dpu/xslt/param/value";

}
