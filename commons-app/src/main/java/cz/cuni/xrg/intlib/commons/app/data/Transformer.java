package cz.cuni.xrg.intlib.commons.app.data;

import cz.cuni.xrg.intlib.commons.DPU;
import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.transform.TransformContext;
import cz.cuni.xrg.intlib.commons.app.data.pipeline.event.transform.TransformException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * Is responsible for transforming- cleaning or enriching RDF data.
 * 
 * Transformations may include e.g.
 * <ul>
 * <li>Cleaning data</li>
 * <li>Converting to another RDF schema</li>
 * <li>Linking resources to external datasets</li>
 * </ul>
 * 
 * 
 * @author Jiri Tomes
 */
public interface Transformer extends DPU {

    /**
     * Transforms the cached RDF data in the repository.
     *
     * @param repository The repository where the RDF data is cached that should be transformed
     * @param graph The graph that contains the RDF data which was extracted
     * @param context The context containing meta information about this transformation process
     * @throws TransformException If the transformation fails, this exception has to be thrown
     */
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException;
}
