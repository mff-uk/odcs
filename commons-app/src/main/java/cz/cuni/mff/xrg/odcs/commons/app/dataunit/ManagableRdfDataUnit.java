package cz.cuni.mff.xrg.odcs.commons.app.dataunit;

import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 * Interface provides managable methods for working with RDF data repository.
 * 
 * @author Petyr
 * @author Jiri Tomes
 */
public interface ManagableRdfDataUnit extends RDFDataUnit, ManagableDataUnit {
    /**
     * For Browsing all data in graph return its size {count of rows}.
     * 
     * @return count of rows for browsing all data in graph.
     * @throws InvalidQueryException
     *             if query for find out count of rows in not
     *             valid.
     */
    public long getResultSizeForDataCollection() throws InvalidQueryException;

    /**
     * For given valid SELECT of CONSTRUCT query return its size {count of rows
     * returns for given query).
     * 
     * @param query
     *            Valid SELECT/CONTRUCT query for asking.
     * @return size for given valid query as long.
     * @throws InvalidQueryException
     *             if query is not valid.
     */
    public long getResultSizeForQuery(String query) throws InvalidQueryException;

}
