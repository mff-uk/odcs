package cz.cuni.xrg.intlib.commons.app.data;

import cz.cuni.xrg.intlib.commons.DPU;

import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.load.LoadContext;
import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.load.LoadException;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * Is responsible for loading the RDF data.
 * 
 * 
 * @author Jiri Tomes
 */
public interface Loader extends DPU {

    /**
     * Loads the RDF data of an completed extract and transform cycle to a data
     * sink.<br/>
     * For convenience and flexibility reasons the repository and the graph is
     * provided, so that loaders can query only parts of the RDF data.<br/>
     * <strong>Loaders may only read the RDF data and must not transform
     * it!</strong>
     *
     * @param repository The repository where the RDF data is cached that should
     * be loaded
     * @param graph The graph that contains the RDF data of one perticular ETL
     * cycle
     * @param context The context containing meta information about this load
     * process
     * @throws LoadException If loading fails, this exception has to be thrown
     */
    public void load(Repository repository, URI graph, LoadContext context) throws LoadException;
}
