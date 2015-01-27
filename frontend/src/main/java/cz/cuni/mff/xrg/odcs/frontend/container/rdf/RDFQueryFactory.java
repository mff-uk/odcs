package cz.cuni.mff.xrg.odcs.frontend.container.rdf;

import org.springframework.context.i18n.LocaleContextHolder;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import cz.cuni.mff.xrg.odcs.frontend.FrontendMessages;

/**
 * Simple {@link QueryFactory} for constructing RDF queries.
 * 
 * @author Bogo
 */
public class RDFQueryFactory implements QueryFactory {

    /**
     * Construct query from definition.
     * 
     * @param queryDefinition
     *            Query definition.
     * @return Query.
     */
    @Override
    public Query constructQuery(QueryDefinition queryDefinition) {
        FrontendMessages messages = new FrontendMessages(LocaleContextHolder.getLocale(), this.getClass().getClassLoader());
        if (queryDefinition.getClass() != RDFQueryDefinition.class) {
            throw new UnsupportedOperationException(messages.getString("RDFQueryFactory.exception"));
        }
        return new RDFQuery((RDFQueryDefinition) queryDefinition);
    }

}
