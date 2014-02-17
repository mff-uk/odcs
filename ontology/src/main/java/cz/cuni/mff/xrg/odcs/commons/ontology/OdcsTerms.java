package cz.cuni.mff.xrg.odcs.commons.ontology;

/**
 * Representation of the odcs ontology
 * @author tomasknap
 */
public class OdcsTerms {
        
     public static final String DATA_UNIT_TEXT_VALUE_PREDICATE = "http://linked.opendata.cz/ontology/odcs/textValue";
	 
     public static final String DATA_UNIT_XML_VALUE_PREDICATE = "http://linked.opendata.cz/ontology/odcs/xmlValue";
    
     //needed when XSLT DPU outputs results of the file transformations as literals. The value of this predicates holds the subject of the triples with the transformation result
     public static final String DATA_UNIT_FILE_URI_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/file/fileURI";
     
     //needed to associated certain metadata of the form (x,y,z) with file in file data unit with file path z
     public static final String DATA_UNIT_FILE_PATH_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dataunit/file/filePath";
    
      //needed to associated certain metadata of the form (x,y,z) with file in file data unit with file path z
     public static final String XSLT_PARAM_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dpu/xslt/param";
     public static final String XSLT_PARAM_NAME_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dpu/xslt/param/name";
     public static final String XSLT_PARAM_VALUE_PREDICATE = "http://linked.opendata.cz/ontology/odcs/dpu/xslt/param/value";
    
     
}