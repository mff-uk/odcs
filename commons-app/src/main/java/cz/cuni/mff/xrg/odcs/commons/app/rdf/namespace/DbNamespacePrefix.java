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

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;

/**
 * Interface providing access to {@link NamespacePrefix} data objects.
 * 
 * @author Jan Vojt
 */
public interface DbNamespacePrefix extends DbAccess<NamespacePrefix> {

    /**
     * Fetch all RDF namespace prefixes defined in application.
     * 
     * @return all namespace prefixes
     */
    public List<NamespacePrefix> getAllPrefixes();

    /**
     * Find prefix with given name in persistent storage.
     * 
     * @param name
     *            abbreviated name
     * @return namespace prefix for given name
     */
    public NamespacePrefix getByName(String name);

}
