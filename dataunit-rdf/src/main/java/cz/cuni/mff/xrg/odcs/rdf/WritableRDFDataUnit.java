package cz.cuni.mff.xrg.odcs.rdf;

import org.openrdf.model.URI;

import eu.unifiedviews.dataunit.RDFData;

public interface WritableRDFDataUnit extends RDFDataUnit {
    /**
     * Get name of the context which is considered to be owned by data unit and is the only one into which
     * write operations are performed.
     * 
     * @return
     */
    URI getWriteContext();
    
    /**
     * Add all data from given DataUnit into this DataUnit.
     * The method must not modify the current parameter (unit).
     * 
     * @param unit
     *            {@link cz.cuni.mff.xrg.odcs.commons.data.DataUnit} to add from
     * @throws IllegalArgumentException
     *             if some property of an element of the
     *             specified dataunit prevents it from being added to this
     *             dataunit
     */
    void addAll(RDFData unit);
    
}
