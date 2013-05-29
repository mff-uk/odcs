package cz.cuni.xrg.intlib.commons.extractor;

/**
 * Is responsible for extracting data from data source and convert it to RDF data.
 * 
 * @author Jiri Tomes
 */
public interface Extract {
	
    /**
     * Extracts data from a data source and converts it to RDF.<br/>
     *
     * @param context Context for one extraction cycle containing meta information about the extraction.
     * @throws ExtractException If any error occurs troughout the extraction cycle.
     */
    public void extract(ExtractContext context) throws ExtractException;
}
