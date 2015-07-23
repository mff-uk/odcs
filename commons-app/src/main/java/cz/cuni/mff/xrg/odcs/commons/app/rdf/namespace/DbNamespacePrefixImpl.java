/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

/**
 * Interface providing access to {@link NamespacePrefix} data objects.
 * 
 * @author Jan Vojt
 * @author Petyr
 */
@Transactional(propagation = Propagation.MANDATORY)
class DbNamespacePrefixImpl extends DbAccessBase<NamespacePrefix>
        implements DbNamespacePrefix {

    public DbNamespacePrefixImpl() {
        super(NamespacePrefix.class);
    }

    @Override
    public List<NamespacePrefix> getAllPrefixes() {
        final String stringQuery = "SELECT e FROM NamespacePrefix e";
        return executeList(stringQuery);
    }

    @Override
    public NamespacePrefix getByName(String name) {
        final String stringQuery = "SELECT e FROM NamespacePrefix e WHERE e.name = :name";
        TypedQuery<NamespacePrefix> query = createTypedQuery(stringQuery);
        query.setParameter("name", name);
        return execute(query);
    }

}
