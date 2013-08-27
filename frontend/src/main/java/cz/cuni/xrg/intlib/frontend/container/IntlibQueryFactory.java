package cz.cuni.xrg.intlib.frontend.container;

import java.io.Serializable;
import javax.persistence.EntityManager;
import org.vaadin.addons.lazyquerycontainer.EntityQueryDefinition;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

/**
 *
 * @author Bogo
 */
public class IntlibQueryFactory implements QueryFactory, Serializable {
    /**
     * Java serialization version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The JPA EntityManager.
     */
    private final EntityManager entityManager;

    /**
     * Constructor which allows setting the entity manager.
     * @param entityManager the entity manager
     */
    public IntlibQueryFactory(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * @return the entityManager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Constructs a new query according to the given QueryDefinition.
     *
     * @param queryDefinition Properties participating in the sorting.
     * @return A new query constructed according to the given sort state.
     */
    @Override
    public Query constructQuery(final QueryDefinition queryDefinition) {
        return new IntlibQuery((EntityQueryDefinition) queryDefinition, entityManager);
    }

}

