package cz.cuni.xrg.intlib.commons.app.data.pipeline.event;

import java.util.List;

import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.transformer.Transform;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * Represents a fixed workflow composed of one or several {@link Extract}s,
 * {@link Transform}s and {@link Load}s executed in a fixed order.<br/>
 * Implementations of this class are supposed to execute the components in the following order:
 * <ol>
 * <li>Execute all {@link Extract}s in the order of the {@link List}</li>
 * <li>Execute all {@link Transform}s in the order of the {@link List}</li>
 * <li>Execute all {@link Load}s in the order of the {@link List}</li>
 * </ol>
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public interface ETLPipeline extends Runnable {

    /**
     * Returns the identifier of this pipeline, will be used as named graph
     * where all the RDF data of this pipeline is cached.
     *
     * @return
     */
    public String getId();

    /**
     * Sets the identifier of this pipeline, will be used as named graph
     * where all the RDF data of this pipeline is cached.<br/>
     * <strong>The ID has to be unique for all pipelines that share an {@link Repository}</strong>
     *
     * @param id
     */
    public void setId(String id);

    /**
     * Returns the list of extractors defined for this pipeline
     *
     * @return
     */
    public List<Extract> getExtractors();

    /**
     * Sets the list of extractors defined for this pipeline
     *
     * @param extractors
     */
    public void setExtractors(List<Extract> extractors);

    /**
     * Returns the list of loaders defined for this pipeline
     *
     * @return
     */
    public List<Load> getLoaders();

    /**
     * Sets the list of loaders defined for this pipeline
     *
     * @param loaders
     */
    public void setLoaders(List<Load> loaders);

    /**
     * Returns the list of transformers defined for this pipeline
     *
     * @return
     */
    public List<Transform> getTransformers();

    /**
     * Sets the list of extractors defined for this pipeline
     *
     * @param transformers
     */
    public void setTransformers(List<Transform> transformers);

    /**
     * Returns the repository instance that will be used by the pipeline for caching RDF data.
     *
     * @return
     */
//    public Repository getRepository();

    /**
     * Sets the repository instance that will be used by the pipeline for caching RDF data.<br/>
     * <strong>Has to be capable to store named graphs!</strong>
     *
     * @param repository
     */
    public void setRepository(Repository repository);
}
