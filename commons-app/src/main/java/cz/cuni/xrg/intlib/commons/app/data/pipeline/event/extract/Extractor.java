package cz.cuni.xrg.intlib.commons.app.data.pipeline.event.extract;

import org.openrdf.rio.RDFHandler;

/**
 * This interface is responsible for extracting data from a
 * data source and converting the data to RDF. <br/>
 * 
 * RDF data is produced through the {@link RDFHandler} interface of the openRDF Sesame API.
 *
 * @author Jiri Tomes
 */
public interface Extractor {

    /**
     * Extracts data from a data source and converts it to RDF.<br/>
     *
     * @param handler This handler has to be used to STORE the produced RDF statements.
     * @param context Context for one extraction process containing META INFORMATION about the extraction.
     * @throws ExtractException If any error occurs troughout the extraction process.
     */
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException;

}