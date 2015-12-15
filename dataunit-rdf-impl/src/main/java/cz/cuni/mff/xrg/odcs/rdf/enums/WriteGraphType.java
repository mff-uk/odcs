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
package cz.cuni.mff.xrg.odcs.rdf.enums;

import cz.cuni.mff.xrg.odcs.rdf.exceptions.GraphNotEmptyException;

/**
 * One of chosed way, how to load RDF data to named graph to SPARQL endpoint.
 * 
 * @author Jiri Tomes
 */
public enum WriteGraphType {

    /**
     * Old data are overriden by new added data
     */
    OVERRIDE,
    /**
     * Disjuction of sets new and old data
     */
    MERGE,
    /**
     * If target graph is not empty - throw {@link GraphNotEmptyException}.
     */
    FAIL
}
