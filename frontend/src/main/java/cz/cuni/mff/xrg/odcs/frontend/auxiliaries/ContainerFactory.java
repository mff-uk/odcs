package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;


import com.vaadin.data.Container;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbPipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.frontend.container.IntlibLazyQueryContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.PipelineAccessor;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class support creating Vaadin container from List<?>.
 *
 * @author Petyr
 * @deprecated the container should be created in respective presenter
 */
@Deprecated
@Transactional(readOnly = true)
public class ContainerFactory {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DbPipeline dbPipeline;
    
    @Autowired
    private PipelineAccessor pipelineAccessor;

    /**
     * Should be used in scheduler list.
     * @param pageLength
     * @return 
     */
    public Container createPipelinesList(int pageLength) {
        
        IntlibLazyQueryContainer container = new IntlibLazyQueryContainer(em, Pipeline.class, pageLength, "id", true, true, true);
        container.getQueryView().getQueryDefinition().setDefaultSortState(
            new Object[]{"id"}, new boolean[]{false});
        container.addContainerProperty("id", Long.class, null, true, true);
        container.addContainerProperty("name", String.class, "", true, true);
        container.addContainerProperty("description", String.class, "", true, true);

        return container;
    }
}
