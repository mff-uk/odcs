
package cz.cuni.mff.xrg.odcs.rdf.metadata;

import cz.cuni.mff.xrg.odcs.commons.ontology.OdcsTerms;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.help.OrderTupleQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.BaseRDFRepo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PreDestroy;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomasknap
 */
public class FileRDFMetadataExtractor {
    
    private RDFDataUnit rdfDataUnit;
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(
            FileRDFMetadataExtractor.class);

    public FileRDFMetadataExtractor(BaseRDFRepo aThis) {
        this.rdfDataUnit = aThis;
    }

    //returns list of mappings predicate-values
    private List<String>  getMetadataValue(String subjectURI, String predicateURI) {
 
            List<String> result = new ArrayList<String>();
         
            String query = "SELECT ?o where {<" + subjectURI + "> <" + predicateURI + "> ?o } ORDER BY ?o";
            log.debug("Query for getting information about the object value: {}", query);
            OrderTupleQueryResult objects;
            try {
                objects = rdfDataUnit.executeOrderSelectQueryAsTuples(query);
            } catch (InvalidQueryException ex) {
                log.error("Internal error - invalid query: {}", query);
                return result; //return empty map;
            }

           //process all the rdf triples
            int count = 0;
            try {
                while (objects.hasNext()) {

                    count++;
                    //process the inputs
                    BindingSet solution = objects.next();
                    Binding b = solution.getBinding("o");
                    String object = b.getValue().stringValue();
                  
                    log.debug("For subject {}, the object is: {} ", subjectURI, object);
                    result.add(object.trim());

                }
            } catch (QueryEvaluationException ex) {
                log.error("Problem evaluating the query " + query + ": " + ex.getLocalizedMessage());
                return result;
            }
        
            log.debug("Found {} values", count);
            
            return result;

     }
    
  
     public Map<String,List<String>> getMetadataForSubject(String subjectURI, List<String> optionalPredicates) {
    
           Map<String,List<String>> metadata = new HashMap<>();
         
           for (String predicate: optionalPredicates) {
                List<String> values = getMetadataValue(subjectURI, predicate);
                if (!values.isEmpty()) {
                    metadata.put(predicate, values);
                }
                else {
                    log.debug("No values for subject {} predicate {}", subjectURI, predicate);
                }
           }

            return metadata;
     
     }
     
      public Map<String,List<String>> getMetadataForFilePath(String filePath, List<String> optionalPredicates) {
    
           Map<String,List<String>> metadata = new HashMap<>();
          
          //first, we have to obtain the subject URI
            //there are some files to be processed received in the input RDF data unit.        
            String query = "SELECT ?s where {?s <" + OdcsTerms.DATA_UNIT_FILE_PATH_PREDICATE + "> \"\"\"" + filePath +  "\"\"\"  } ORDER BY ?s";
            log.debug("Query for getting information about the subject URI for file path: {}", query);
            OrderTupleQueryResult subjects;
            try {
                subjects = rdfDataUnit.executeOrderSelectQueryAsTuples(query);
            } catch (InvalidQueryException ex) {
                log.error("Internal error - invalid query: {}", query);
                return metadata; //return empty map;
            }

           //process all the rdf triples
            try {

                while (subjects.hasNext()) {

                    //log.info("Processing subject: {} ", fileNumber);

                    //process the inputs
                    BindingSet solution = subjects.next();
                    Binding b = solution.getBinding("s");
                    String subjectURI = b.getValue().stringValue();
                   

                    //adjust file name because it is in the form: http://file/name/input01.xml
                    //object = object.substring(object.lastIndexOf("/")+1);
                     //store the subjects to the map
                    log.info("The subject {} is associated with file path {}", subjectURI, filePath);
                    
                    //for the subject, try to get more metadata:
                    return getMetadataForSubject(subjectURI, optionalPredicates);
                }
            } catch (QueryEvaluationException ex) {
                log.error("Problem evaluating the query to obtain metadata " + query + ": " + ex.getLocalizedMessage());
                return metadata;
            }
            return metadata;
          
     }
    
//     public Map<String, Map<String,String>> getMetadataMap(List<String> optionalPredicates) {
//         
//         Map<String,Map<String,String>> metadata = new HashMap<>();
//         
//        //get all triples with predicate DATA_UNIT_FILE_PATH_PREDICATE to be able to associate files with its metadata. 
//        //Map<String, String> getFilePathToSubjectMappings getListOf 
//         
//         //there are some files to be processed received in the input RDF data unit.        
//            String query = "SELECT ?s ?o where {?s <" + OdcsTerms.DATA_UNIT_FILE_PATH_PREDICATE + "> ?o } ORDER BY ?s ?o";
//            log.debug("Query for getting information about the file paths: {}", query);
//            OrderTupleQueryResult subjects;
//            try {
//                subjects = rdfDataUnit.executeOrderSelectQueryAsTuples(query);
//            } catch (InvalidQueryException ex) {
//                log.error("Internal error - invalid query: {}", query);
//                return metadata; //return empty map;
//            }
//
//           //process all the rdf triples
//            int countOfSubjectsWithMetadata = 0;
//            try {
//
//                while (subjects.hasNext()) {
//
//                    countOfSubjectsWithMetadata++;
//
//                    //log.info("Processing subject: {} ", fileNumber);
//
//                    //process the inputs
//                    BindingSet solution = subjects.next();
//                    Binding b = solution.getBinding("o");
//                    String subjectURI = b.getValue().toString();
//                    String filePath = solution.getBinding("s").getValue().toString();
//
//                    //adjust file name because it is in the form: http://file/name/input01.xml
//                    //object = object.substring(object.lastIndexOf("/")+1);
//                     //store the subjects to the map
//                    log.info("The subject {} is associated with file path {}", subjectURI, filePath);
//                    
//                    //for the subject, try to get more metadata:
//                    Map<String,String> params = getMetadataForSubject(subjectURI, optionalPredicates);
//                    
//                    
//                    metadata.put(filePath, params);
//
//                }
//            } catch (QueryEvaluationException ex) {
//                log.error("Problem evaluating the query to obtain metadata " + query + ": " + ex.getLocalizedMessage());
//                return metadata;
//            }
//            log.debug("Found {} subjects for which metadata exists", countOfSubjectsWithMetadata );
//
//            return metadata;
//         
//    }

   
    
    
    
    
}
