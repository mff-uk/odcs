package cz.cuni.xrg.intlib.commons.app.data;

import cz.cuni.xrg.intlib.commons.DPU;
import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.extract.ExtractContext;
import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.extract.ExtractException;
import org.openrdf.rio.RDFHandler;

/**
 * Is responsible for extracting data from data source and convert it to RDF data.
 * 
 * @author Jiri Tomes
 */
public interface Extractor extends DPU {

    /**
     * Extracts data from a data source and converts it to RDF.<br/>
     *
     * @param handler This handler has to be used to store the produced RDF statements.<br/>
     * @param context Context for one extraction cycle containing meta information about the extraction.
     * @throws ExtractException If any error occurs troughout the extraction cycle.
     */
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException;
}
