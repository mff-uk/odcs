package cz.cuni.xrg.intlib.commons.extractor;

import cz.cuni.xrg.intlib.commons.event.ProcessingContext;

import java.util.Map;

import org.openrdf.rio.RDFHandler;

/**
 * Context used by {@link Extract}s for the extraction process.
 *
 * @see Extract
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ExtractContext extends ProcessingContext {

	// RDFHandler handler, 
	
    private long triplesExtracted;

    public ExtractContext(String id, Map<String, Object> customData) {
        super(id, customData);
    }

    /**
     * Returns the number of triples that were extracted or produced.
     *
     * @return
     */
    public long getTriplesExtracted() {
        return triplesExtracted;
    }

    /**
     * Sets the number of triples that were extracted or produced.
     *
     * @param triplesExtracted
     */
    public void setTriplesExtracted(long triplesExtracted) {
        this.triplesExtracted = triplesExtracted;
    }
}
