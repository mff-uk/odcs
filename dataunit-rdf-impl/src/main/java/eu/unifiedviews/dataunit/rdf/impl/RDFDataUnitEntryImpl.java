/**
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
 */
package eu.unifiedviews.dataunit.rdf.impl;

import org.openrdf.model.URI;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;

/**
 * Holds basic informations about a single file.
 *
 * @author Michal Klempa
 */
public class RDFDataUnitEntryImpl implements RDFDataUnit.Entry {
    
    private final String symbolicName;

    private final URI dataGraphURI;

    public RDFDataUnitEntryImpl(String symbolicName, URI dataGraphURI) {
        this.symbolicName = symbolicName;
        this.dataGraphURI = dataGraphURI;
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public URI getDataGraphURI() throws DataUnitException {
        return dataGraphURI;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[symbolicName=" + symbolicName + ",dataGraphURI=" + String.valueOf(dataGraphURI) + "]";
    }

}
